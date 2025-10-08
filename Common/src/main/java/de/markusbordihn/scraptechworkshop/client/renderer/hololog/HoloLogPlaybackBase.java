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
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.scraptechworkshop.client.renderer.hololog;

import com.mojang.blaze3d.vertex.PoseStack;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogData;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogDisplayEntity;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogDisplayRecipe;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogLine;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogPlaybackContext;
import java.util.UUID;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class HoloLogPlaybackBase {
  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected final HoloLogData holoLogData;
  protected final HoloLogPlaybackContext context;
  protected final UUID playerId;
  protected final Runnable onComplete;

  protected PlaybackState state = PlaybackState.STOPPED;
  protected int currentLineIndex = 0;
  protected boolean endEffectsPlayed = false;
  protected boolean voiceOverStarted = false;

  protected HoloLogPlaybackBase(
      final HoloLogData holoLogData,
      final HoloLogPlaybackContext context,
      final UUID playerId,
      final Runnable onComplete) {
    this.holoLogData = holoLogData;
    this.context = context;
    this.playerId = playerId;
    this.onComplete = onComplete;
  }

  public void start() {
    state = PlaybackState.PLAYING;
    currentLineIndex = 0;
    endEffectsPlayed = false;
    voiceOverStarted = false;

    log.info(
        "Starting holo log playback: {} (lines: {}, default delay: {}s)",
        holoLogData.id(),
        holoLogData.lines().size(),
        holoLogData.lineDelay());

    HoloLogPlayerEffects.playEffects(holoLogData.start(), context, context.getEffectPosition());
    onPlaybackStart();
  }

  public void stop() {
    state = PlaybackState.STOPPED;
    HoloLogPlayerAudio.stopVoiceOver(playerId);
    onPlaybackStop();
  }

  public abstract void tick();

  protected abstract void onPlaybackStart();

  protected abstract void onPlaybackStop();

  protected void startVoiceOver(float currentTime) {
    if (!voiceOverStarted && holoLogData.hasVoiceOver()) {
      if (currentTime >= holoLogData.voiceOverDelay()) {
        HoloLogPlayerAudio.playVoiceOver(playerId, holoLogData.voiceOver(), context);
        voiceOverStarted = true;
        log.debug(
            "Started voice-over at {}s (delay: {}s)", currentTime, holoLogData.voiceOverDelay());
      }
    }
  }

  protected void completePlayback() {
    if (!endEffectsPlayed) {
      HoloLogPlayerEffects.playEffects(holoLogData.end(), context, context.getEffectPosition());
      endEffectsPlayed = true;

      if (onComplete != null) {
        log.info("Holo log playback completed: {}", holoLogData.id());
        onComplete.run();
      }
    }
    state = PlaybackState.STOPPED;
  }

  public boolean isPlaying() {
    return state == PlaybackState.PLAYING;
  }

  public boolean isStopped() {
    return state == PlaybackState.STOPPED;
  }

  public HoloLogDisplayEntity getCurrentDisplayEntity() {
    int displayLineIndex = getDisplayLineIndex();
    if (displayLineIndex >= 0 && displayLineIndex < holoLogData.lines().size()) {
      HoloLogLine line = holoLogData.lines().get(displayLineIndex);
      if (line.displayEntity() != null) {
        return line.displayEntity();
      }
    }

    return holoLogData.displayEntity();
  }

  public HoloLogDisplayRecipe getCurrentDisplayRecipe() {
    int displayLineIndex = getDisplayLineIndex();
    if (displayLineIndex >= 0 && displayLineIndex < holoLogData.lines().size()) {
      HoloLogLine line = holoLogData.lines().get(displayLineIndex);
      if (line.displayRecipe() != null) {
        return line.displayRecipe();
      }
    }

    return holoLogData.displayRecipe();
  }

  protected int getDisplayLineIndex() {
    return currentLineIndex;
  }

  protected abstract void renderEntity(
      PoseStack poseStack, ResourceLocation entityId, float partialTick, int lightLevel);

  protected abstract void renderEntity(
      final PoseStack poseStack,
      final MultiBufferSource buffer,
      final ResourceLocation entityId,
      final float partialTick,
      final int lightLevel);

  protected abstract void renderHoloEntity(
      final PoseStack poseStack,
      final HoloLogDisplayEntity displayEntity,
      final float partialTick,
      final int lightLevel);

  protected abstract void renderHoloEntity(
      final PoseStack poseStack,
      final MultiBufferSource buffer,
      final HoloLogDisplayEntity displayEntity,
      final float partialTick,
      final int lightLevel);

  protected abstract void renderBlock(
      final PoseStack poseStack, final ResourceLocation blockId, final int lightLevel);

  protected abstract void renderBlock(
      final PoseStack poseStack,
      final MultiBufferSource buffer,
      final ResourceLocation blockId,
      final int lightLevel);

  protected abstract void renderItem(
      final PoseStack poseStack, final ResourceLocation itemId, final int lightLevel);

  protected abstract void renderItem(
      final PoseStack poseStack,
      final MultiBufferSource buffer,
      final ResourceLocation itemId,
      final int lightLevel);

  public void renderDisplayEntity(
      final PoseStack poseStack, final float partialTick, final int lightLevel) {
    HoloLogDisplayEntity displayEntity = getCurrentDisplayEntity();
    if (displayEntity == null) {
      return;
    }

    try {
      switch (displayEntity.type()) {
        case ENTITY:
          if (displayEntity.id() != null) {
            renderEntity(poseStack, displayEntity.id(), partialTick, lightLevel);
          }
          break;
        case HOLO_ENTITY:
          if (displayEntity.id() != null) {
            renderHoloEntity(poseStack, displayEntity, partialTick, lightLevel);
          }
          break;
        case BLOCK:
          if (displayEntity.id() != null) {
            renderBlock(poseStack, displayEntity.id(), lightLevel);
          }
          break;
        case ITEM:
          if (displayEntity.id() != null) {
            renderItem(poseStack, displayEntity.id(), lightLevel);
          }
          break;
        case RECIPE:
          // Recipe rendering is handled separately in the screen
          break;
      }
    } catch (Exception e) {
      log.error("Failed to render display entity: {}", e.getMessage());
    }
  }

  public void renderDisplayEntity(
      final PoseStack poseStack,
      final MultiBufferSource buffer,
      final float partialTick,
      final int lightLevel) {
    HoloLogDisplayEntity displayEntity = getCurrentDisplayEntity();
    if (displayEntity == null) {
      return;
    }
    renderDisplayEntity(poseStack, buffer, displayEntity, partialTick, lightLevel);
  }

  public void renderDisplayEntity(
      final PoseStack poseStack,
      final MultiBufferSource buffer,
      final HoloLogDisplayEntity displayEntity,
      final float partialTick,
      final int lightLevel) {
    if (displayEntity == null) {
      return;
    }

    try {
      switch (displayEntity.type()) {
        case ENTITY:
          if (displayEntity.id() != null) {
            renderEntity(poseStack, buffer, displayEntity.id(), partialTick, lightLevel);
          }
          break;
        case HOLO_ENTITY:
          if (displayEntity.id() != null) {
            renderHoloEntity(poseStack, buffer, displayEntity, partialTick, lightLevel);
          }
          break;
        case BLOCK:
          if (displayEntity.id() != null) {
            renderBlock(poseStack, buffer, displayEntity.id(), lightLevel);
          }
          break;
        case ITEM:
          if (displayEntity.id() != null) {
            renderItem(poseStack, buffer, displayEntity.id(), lightLevel);
          }
          break;
        case RECIPE:
          // Recipe rendering is handled separately in the screen
          break;
      }
    } catch (Exception e) {
      log.error("Failed to render display entity: {}", e.getMessage(), e);
    }
  }

  protected enum PlaybackState {
    STOPPED,
    PLAYING
  }
}
