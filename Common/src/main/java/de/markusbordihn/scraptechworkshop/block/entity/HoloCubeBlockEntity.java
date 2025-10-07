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
import de.markusbordihn.scraptechworkshop.client.renderer.hololog.HoloLogBlockPlayer;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogData;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogManager;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogPlaybackContext;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogStatus;
import de.markusbordihn.scraptechworkshop.data.hololog.WorldContext;
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
  private static final ResourceLocation DEFAULT_HOLO_LOG =
      new ResourceLocation(Constants.MOD_ID, "holologs/intro/introduction");

  private static final double MAX_PLAYER_DISTANCE = 32.0;
  private static final int AUTO_RESET_TICKS = 20;

  public static BlockEntityType<HoloCubeBlockEntity> TYPE;

  private UUID cubeUUID;
  private ResourceLocation holoLogId;
  private UUID playerUUID;
  private HoloLogBlockPlayer player;
  private HoloLogStatus lastKnownStatus = HoloLogStatus.READY;
  private long endedAtTick = -1;

  public HoloCubeBlockEntity(BlockPos pos, BlockState state) {
    super(TYPE, pos, state);
    this.cubeUUID = UUID.randomUUID();
  }

  public static void tick(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final HoloCubeBlockEntity blockEntity) {

    if (!level.isClientSide) {
      return;
    }

    HoloLogStatus status = blockState.getValue(HoloCubeBlock.STATUS);
    ResourceLocation holoLogId = blockEntity.getHoloLogId();

    if (holoLogId == null) {
      if (status != HoloLogStatus.READY) {
        log.warn(
            "[{}] Hololog ID is null but status is {}, resetting to READY",
            blockEntity.cubeUUID,
            status);
        HoloCubeBlock.updateStatus(level, blockPos, HoloLogStatus.READY);
      }
      return;
    }

    if (status == HoloLogStatus.ENDED) {
      if (blockEntity.endedAtTick < 0) {
        blockEntity.endedAtTick = level.getGameTime();
      } else if (level.getGameTime() - blockEntity.endedAtTick > AUTO_RESET_TICKS) {
        log.debug("[{}] Auto-resetting from ENDED to READY", blockEntity.cubeUUID);
        HoloCubeBlock.updateStatus(level, blockPos, HoloLogStatus.READY);
        blockEntity.endedAtTick = -1;
        blockEntity.stopPlayback();
      }
      return;
    } else {
      blockEntity.endedAtTick = -1;
    }

    boolean playerInRange = hasNonSpectatorPlayerInRange(level, blockPos);
    if (status == HoloLogStatus.PLAYING) {
      log.trace(
          "[{}] PLAYING - player exists: {}, playerInRange: {}",
          blockEntity.cubeUUID,
          blockEntity.player != null,
          playerInRange);

      if (!playerInRange && blockEntity.player != null) {
        log.debug("[{}] No player in range, stopping playback", blockEntity.cubeUUID);
        HoloCubeBlock.updateStatus(level, blockPos, HoloLogStatus.READY);
        blockEntity.stopPlayback();
        return;
      }

      if (blockEntity.player == null && playerInRange) {
        log.info("[{}] Starting playback for hololog: {}", blockEntity.cubeUUID, holoLogId);
        blockEntity.startPlayback(level, blockPos, holoLogId);
      }
    } else if (status == HoloLogStatus.READY && blockEntity.player != null) {
      blockEntity.stopPlayback();
    }

    // Tick the player if it exists and is playing
    if (blockEntity.player != null && blockEntity.player.isPlaying()) {
      blockEntity.player.tick();
    }

    blockEntity.lastKnownStatus = status;
  }

  private static boolean hasNonSpectatorPlayerInRange(final Level level, final BlockPos blockPos) {
    return level.players().stream()
        .filter(player -> !player.isSpectator())
        .anyMatch(
            player -> {
              double distance =
                  player.distanceToSqr(
                      blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
              boolean inRange = distance <= MAX_PLAYER_DISTANCE * MAX_PLAYER_DISTANCE;
              if (log.isTraceEnabled()) {
                log.trace(
                    "Player {} at distance {} blocks (max: {}): {}",
                    player.getName().getString(),
                    Math.sqrt(distance),
                    MAX_PLAYER_DISTANCE,
                    inRange ? "IN RANGE" : "out of range");
              }
              return inRange;
            });
  }

  private void startPlayback(
      final Level level, final BlockPos blockPos, final ResourceLocation holoLogId) {
    log.info("[{}] startPlayback() called for hololog: {}", cubeUUID, holoLogId);

    if (holoLogId == null) {
      log.error("[{}] Cannot start playback: holoLogId is null", cubeUUID);
      return;
    }

    if (!level.isClientSide) {
      log.error("[{}] startPlayback() called on server side - this should never happen!", cubeUUID);
      return;
    }

    Optional<HoloLogData> optionalHoloLogData = HoloLogManager.loadHoloLog(holoLogId);
    if (optionalHoloLogData.isEmpty()) {
      log.error("[{}] Failed to load hololog data for: {}", cubeUUID, holoLogId);
      HoloCubeBlock.updateStatus(level, blockPos, HoloLogStatus.READY);
      return;
    }

    HoloLogData holoLogData = optionalHoloLogData.get();
    log.info(
        "[{}] Hololog loaded successfully: {} (lines: {}, default delay: {}s)",
        cubeUUID,
        holoLogId,
        holoLogData.lines().size(),
        holoLogData.lineDelay());

    HoloLogPlaybackContext holoLogPlaybackContext = new WorldContext(level, blockPos);

    // Create player instance directly
    player =
        new HoloLogBlockPlayer(
            holoLogData,
            holoLogPlaybackContext,
            UUID.randomUUID(),
            text -> {
              var clientPlayer = Minecraft.getInstance().player;
              if (clientPlayer != null && !clientPlayer.isSpectator()) {
                clientPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§7[Hololog] §f" + text), false);
              }
            },
            () -> {
              log.info("[{}] Playback finished", cubeUUID);
              HoloCubeBlock.updateStatus(level, blockPos, HoloLogStatus.ENDED);
            });

    // Start playback
    player.start();
    log.info("[{}] Playback started", cubeUUID);
  }

  private void stopPlayback() {
    if (player != null) {
      log.debug("[{}] Stopping playback", cubeUUID);
      player.stop();
      player = null;
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

  public ResourceLocation getHoloLogId() {
    return holoLogId != null ? holoLogId : DEFAULT_HOLO_LOG;
  }

  public void setHoloLogId(final ResourceLocation holoLogId) {
    log.debug("[{}] Setting holo log ID to: {}", cubeUUID, holoLogId);
    this.holoLogId = holoLogId;
    setChanged();
  }

  @Override
  public void load(CompoundTag compoundTag) {
    super.load(compoundTag);

    if (compoundTag.contains(CUBE_UUID_TAG)) {
      cubeUUID = compoundTag.getUUID(CUBE_UUID_TAG);
    }

    if (compoundTag.contains(HOLOLOG_ID_TAG)) {
      try {
        holoLogId = new ResourceLocation(compoundTag.getString(HOLOLOG_ID_TAG));
      } catch (Exception e) {
        log.warn("[{}] Failed to load hololog ID: {}", cubeUUID, e.getMessage());
        holoLogId = DEFAULT_HOLO_LOG;
      }
    }

    if (compoundTag.contains(PLAYER_UUID_TAG)) {
      playerUUID = compoundTag.getUUID(PLAYER_UUID_TAG);
    }

    if (compoundTag.contains(LAST_STATUS_TAG)) {
      try {
        lastKnownStatus =
            HoloLogStatus.valueOf(compoundTag.getString(LAST_STATUS_TAG).toUpperCase());
      } catch (Exception e) {
        log.warn("[{}] Failed to load last status: {}", cubeUUID, e.getMessage());
        lastKnownStatus = HoloLogStatus.READY;
      }
    }

    if (compoundTag.contains(ENDED_TICK_TAG)) {
      endedAtTick = compoundTag.getLong(ENDED_TICK_TAG);
    }

    log.debug("[{}] Loaded HoloCube data", cubeUUID);
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag) {
    super.saveAdditional(compoundTag);

    compoundTag.putUUID(CUBE_UUID_TAG, cubeUUID);
    compoundTag.putString(LAST_STATUS_TAG, lastKnownStatus.name());
    compoundTag.putLong(ENDED_TICK_TAG, endedAtTick);

    if (holoLogId != null) {
      compoundTag.putString(HOLOLOG_ID_TAG, holoLogId.toString());
    }

    if (playerUUID != null) {
      compoundTag.putUUID(PLAYER_UUID_TAG, playerUUID);
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

  public HoloLogBlockPlayer getPlayer() {
    return player;
  }

  @Override
  public void setChanged() {
    super.setChanged();
    syncToClient();
  }
}
