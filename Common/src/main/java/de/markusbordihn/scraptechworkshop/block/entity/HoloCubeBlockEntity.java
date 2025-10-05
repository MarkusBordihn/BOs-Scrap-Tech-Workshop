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
import de.markusbordihn.scraptechworkshop.data.hololog.HolologPlaybackContext;
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
  private static final String CUBE_UUID_TAG = "CubeUUID";
  private static final String HOLOLOG_ID_TAG = "HolologId";
  private static final String PLAYER_UUID_TAG = "PlayerUUID";
  private static final String LAST_STATUS_TAG = "LastStatus";
  private static final String ENDED_TICK_TAG = "EndedTick";
  private static final ResourceLocation DEFAULT_HOLOLOG =
      new ResourceLocation(Constants.MOD_ID, "holologs/intro/introduction");

  private static final double MAX_PLAYER_DISTANCE = 32.0;
  private static final int AUTO_RESET_TICKS = 20;

  public static BlockEntityType<HoloCubeBlockEntity> TYPE;

  private UUID cubeUUID;
  private ResourceLocation holologId;
  private UUID playerUUID;
  private UUID currentPlayerId;
  private HolologStatus lastKnownStatus = HolologStatus.READY;
  private long endedAtTick = -1;

  public HoloCubeBlockEntity(BlockPos pos, BlockState state) {
    super(TYPE, pos, state);
    this.cubeUUID = UUID.randomUUID();
  }

  public static void tick(
      Level level, BlockPos pos, BlockState state, HoloCubeBlockEntity blockEntity) {

    if (!level.isClientSide) {
      return;
    }

    HolologStatus status = state.getValue(HoloCubeBlock.STATUS);
    ResourceLocation holologId = blockEntity.getHolologId();

    if (holologId == null) {
      if (status != HolologStatus.READY) {
        log.warn(
            "[{}] Hololog ID is null but status is {}, resetting to READY",
            blockEntity.cubeUUID,
            status);
        HoloCubeBlock.updateStatus(level, pos, HolologStatus.READY);
      }
      return;
    }

    if (status == HolologStatus.ENDED) {
      if (blockEntity.endedAtTick < 0) {
        blockEntity.endedAtTick = level.getGameTime();
      } else if (level.getGameTime() - blockEntity.endedAtTick > AUTO_RESET_TICKS) {
        log.debug("[{}] Auto-resetting from ENDED to READY", blockEntity.cubeUUID);
        HoloCubeBlock.updateStatus(level, pos, HolologStatus.READY);
        blockEntity.endedAtTick = -1;
        blockEntity.stopPlayback();
      }
      return;
    } else {
      blockEntity.endedAtTick = -1;
    }

    boolean playerInRange = hasNonSpectatorPlayerInRange(level, pos);
    if (status == HolologStatus.PLAYING) {
      log.trace(
          "[{}] PLAYING - currentPlayerId: {}, playerInRange: {}",
          blockEntity.cubeUUID,
          blockEntity.currentPlayerId,
          playerInRange);

      if (!playerInRange && blockEntity.currentPlayerId != null) {
        log.debug("[{}] No player in range, stopping playback", blockEntity.cubeUUID);
        HoloCubeBlock.updateStatus(level, pos, HolologStatus.READY);
        blockEntity.stopPlayback();
        return;
      }

      if (blockEntity.currentPlayerId == null && playerInRange) {
        log.info("[{}] Starting playback for hololog: {}", blockEntity.cubeUUID, holologId);
        blockEntity.startPlayback(level, pos, holologId);
      }
    } else if (status == HolologStatus.READY && blockEntity.currentPlayerId != null) {
      blockEntity.stopPlayback();
    }

    blockEntity.lastKnownStatus = status;
  }

  private static boolean hasNonSpectatorPlayerInRange(Level level, BlockPos pos) {
    return level.players().stream()
        .filter(player -> !player.isSpectator())
        .anyMatch(
            player -> {
              double distance =
                  player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
              boolean inRange = distance <= MAX_PLAYER_DISTANCE * MAX_PLAYER_DISTANCE;
              if (log.isTraceEnabled()) {
                log.trace(
                    "Player {} at distance {:.2f} blocks (max: {}): {}",
                    player.getName().getString(),
                    Math.sqrt(distance),
                    MAX_PLAYER_DISTANCE,
                    inRange ? "IN RANGE" : "out of range");
              }
              return inRange;
            });
  }

  private void startPlayback(Level level, BlockPos pos, ResourceLocation holologId) {
    log.info("[{}] startPlayback() called for hololog: {}", cubeUUID, holologId);

    if (holologId == null) {
      log.error("[{}] Cannot start playback: holologId is null", cubeUUID);
      return;
    }

    if (!level.isClientSide) {
      log.error("[{}] startPlayback() called on server side - this should never happen!", cubeUUID);
      return;
    }

    ResourceLocation localizedId = HolologParser.getLocalizedId(holologId);
    log.debug("[{}] Localized hololog ID: {}", cubeUUID, localizedId);

    Optional<HolologData> holologData = HolologParser.getHololog(localizedId);

    if (holologData.isEmpty()) {
      log.error(
          "[{}] Failed to load hololog data for: {} (localized: {})",
          cubeUUID,
          holologId,
          localizedId);
      HoloCubeBlock.updateStatus(level, pos, HolologStatus.READY);
      return;
    }

    HolologData data = holologData.get();
    if (data.lines().isEmpty()) {
      log.error("[{}] Hololog has no lines: {}", cubeUUID, localizedId);
      HoloCubeBlock.updateStatus(level, pos, HolologStatus.READY);
      return;
    }

    log.info(
        "[{}] Hololog loaded successfully: {} (lines: {}, default delay: {})",
        cubeUUID,
        localizedId,
        data.lines().size(),
        data.lineDelayTicks());

    HolologPlaybackContext context = new HolologPlaybackContext.WorldContext(level, pos);
    currentPlayerId =
        HolologPlayer.play(
            data,
            context,
            text -> {
              var player = Minecraft.getInstance().player;
              if (player != null && !player.isSpectator()) {
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§7[Hololog] §f" + text), false);
              }
            },
            () -> {
              log.info("[{}] Playback finished", cubeUUID);
              HoloCubeBlock.updateStatus(level, pos, HolologStatus.ENDED);
            });

    if (currentPlayerId == null) {
      log.error("[{}] HolologPlayer.play() returned null player ID!", cubeUUID);
      HoloCubeBlock.updateStatus(level, pos, HolologStatus.READY);
    } else {
      log.info("[{}] Playback started with player ID: {}", cubeUUID, currentPlayerId);
    }
  }

  private void stopPlayback() {
    if (currentPlayerId != null) {
      log.debug("[{}] Stopping playback", cubeUUID);
      HolologPlayer.stop(currentPlayerId);
      currentPlayerId = null;
    }
  }

  public void cleanup() {
    stopPlayback();
  }

  @Override
  public void setRemoved() {
    super.setRemoved();
    cleanup();
  }

  public UUID getCubeUUID() {
    return cubeUUID;
  }

  public ResourceLocation getHolologId() {
    return holologId != null ? holologId : DEFAULT_HOLOLOG;
  }

  public void setHolologId(ResourceLocation holologId) {
    log.debug("[{}] Setting hololog ID to: {}", cubeUUID, holologId);
    this.holologId = holologId;
    setChanged();
  }

  public UUID getPlayerUUID() {
    return playerUUID;
  }

  public void setPlayerUUID(UUID playerUUID) {
    log.debug("[{}] Setting player UUID to: {}", cubeUUID, playerUUID);
    this.playerUUID = playerUUID;
    setChanged();
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);

    if (tag.contains(CUBE_UUID_TAG)) {
      cubeUUID = tag.getUUID(CUBE_UUID_TAG);
    }

    if (tag.contains(HOLOLOG_ID_TAG)) {
      try {
        holologId = new ResourceLocation(tag.getString(HOLOLOG_ID_TAG));
      } catch (Exception e) {
        log.warn("[{}] Failed to load hololog ID: {}", cubeUUID, e.getMessage());
        holologId = DEFAULT_HOLOLOG;
      }
    }

    if (tag.contains(PLAYER_UUID_TAG)) {
      playerUUID = tag.getUUID(PLAYER_UUID_TAG);
    }

    if (tag.contains(LAST_STATUS_TAG)) {
      try {
        lastKnownStatus = HolologStatus.valueOf(tag.getString(LAST_STATUS_TAG).toUpperCase());
      } catch (Exception e) {
        log.warn("[{}] Failed to load last status: {}", cubeUUID, e.getMessage());
        lastKnownStatus = HolologStatus.READY;
      }
    }

    if (tag.contains(ENDED_TICK_TAG)) {
      endedAtTick = tag.getLong(ENDED_TICK_TAG);
    }

    log.debug("[{}] Loaded HoloCube data", cubeUUID);
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);

    tag.putUUID(CUBE_UUID_TAG, cubeUUID);
    tag.putString(LAST_STATUS_TAG, lastKnownStatus.name());
    tag.putLong(ENDED_TICK_TAG, endedAtTick);

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
