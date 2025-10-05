/*
 * Copyright 2025 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.scraptechworkshop.block.entity;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.hololog.HoloCubeBlock;
import de.markusbordihn.scraptechworkshop.client.hololog.HolologPlayer;
import de.markusbordihn.scraptechworkshop.data.hololog.HolologData;
import de.markusbordihn.scraptechworkshop.data.hololog.HolologParser;
import de.markusbordihn.scraptechworkshop.data.hololog.HolologStatus;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloCubeBlockEntity extends BlockEntity {

  public static final String ID = "holocube";
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String HOLOLOG_ID_TAG = "HolologId";
  private static final String PLAYER_UUID_TAG = "PlayerUUID";
  private static final ResourceLocation DEFAULT_HOLOLOG =
      new ResourceLocation(Constants.MOD_ID, "holologs/intro/introduction");
  public static BlockEntityType<HoloCubeBlockEntity> TYPE;
  private ResourceLocation holologId;
  private UUID playerUUID;
  private UUID currentPlayerId = null;

  public HoloCubeBlockEntity(BlockPos pos, BlockState state) {
    super(TYPE, pos, state);
  }

  public static void tick(
      Level level, BlockPos pos, BlockState state, HoloCubeBlockEntity blockEntity) {

    if (!level.isClientSide) {
      return;
    }

    HolologStatus status = state.getValue(HoloCubeBlock.STATUS);
    ResourceLocation holologId = blockEntity.getHolologId();

    if (status == HolologStatus.PLAYING && holologId != null) {
      if (blockEntity.currentPlayerId == null) {
        ResourceLocation localizedId = HolologParser.getLocalizedId(holologId);

        Optional<HolologData> holologData = HolologParser.getHololog(localizedId);
        if (holologData.isPresent()) {
          HolologPlayer.PlaybackContext context = new HolologPlayer.WorldContext(level, pos);

          UUID playerId =
              HolologPlayer.play(
                  holologData.get(),
                  context,
                  text -> {
                    if (Minecraft.getInstance().player != null) {
                      Minecraft.getInstance()
                          .player
                          .displayClientMessage(
                              net.minecraft.network.chat.Component.literal("§7[Hololog] §f" + text),
                              false);
                    }
                  },
                  () -> {
                    log.info("Hololog playback finished at {}", pos);
                    // Update block status to ENDED
                    HoloCubeBlock.updateStatus(level, pos, HolologStatus.ENDED);
                  });
          blockEntity.currentPlayerId = playerId;
          log.info("Started hololog player with ID: {} at position {}", playerId, pos);
        } else {
          log.error("Failed to load hololog data for: {}", holologId);
        }
      }
    } else if (status == HolologStatus.PAUSED) {
      if (blockEntity.currentPlayerId != null) {
        HolologPlayer.pause(blockEntity.currentPlayerId);
      }
    } else if (status == HolologStatus.READY) {
      if (blockEntity.currentPlayerId != null) {
        HolologPlayer.stop(blockEntity.currentPlayerId);
        blockEntity.currentPlayerId = null;
      }
    } else if (status == HolologStatus.ENDED) {
      if (blockEntity.currentPlayerId != null) {
        HolologPlayer.stop(blockEntity.currentPlayerId);
        blockEntity.currentPlayerId = null;
      }
    }
  }

  public void cleanup() {
    if (currentPlayerId != null) {
      HolologPlayer.stop(currentPlayerId);
      currentPlayerId = null;
    }
  }

  @Override
  public void setRemoved() {
    super.setRemoved();
    cleanup();
  }

  public ResourceLocation getHolologId() {
    return holologId != null ? holologId : DEFAULT_HOLOLOG;
  }

  public void setHolologId(ResourceLocation holologId) {
    log.debug("Setting hololog ID for HoloCube at {} to: {}", this.worldPosition, holologId);
    this.holologId = holologId;
    setChanged();
  }

  public UUID getPlayerUUID() {
    return playerUUID;
  }

  public void setPlayerUUID(UUID playerUUID) {
    this.playerUUID = playerUUID;
    setChanged();
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    if (tag.contains(HOLOLOG_ID_TAG)) {
      try {
        holologId = new ResourceLocation(tag.getString(HOLOLOG_ID_TAG));
      } catch (Exception e) {
        log.warn("Failed to load hololog ID from NBT: {}", e.getMessage());
        holologId = DEFAULT_HOLOLOG;
      }
    }
    if (tag.contains(PLAYER_UUID_TAG)) {
      playerUUID = tag.getUUID(PLAYER_UUID_TAG);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    if (holologId != null) {
      tag.putString(HOLOLOG_ID_TAG, holologId.toString());
    }
    if (playerUUID != null) {
      tag.putUUID(PLAYER_UUID_TAG, playerUUID);
    }
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag tag = super.getUpdateTag();
    saveAdditional(tag);
    return tag;
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  private void syncToClient() {
    Level currentLevel = this.level;
    if (currentLevel != null && !currentLevel.isClientSide) {
      currentLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
  }

  @Override
  public void setChanged() {
    super.setChanged();
    syncToClient();
  }
}
