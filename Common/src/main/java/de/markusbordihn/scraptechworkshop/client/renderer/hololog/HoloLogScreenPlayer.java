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
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogLine;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogPlaybackContext;
import de.markusbordihn.scraptechworkshop.entity.hololog.HoloLogHumanoidEntity;
import de.markusbordihn.scraptechworkshop.registry.entity.HoloLogEntityRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloLogScreenPlayer extends HoloLogPlaybackBase {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final int CHECKS_PER_SECOND = 10;
  private static final float CHECK_INTERVAL = 1.0f / CHECKS_PER_SECOND;

  private final List<String> displayedLines = new ArrayList<>();
  private final float ticksPerCheck;
  private int currentCharIndex = 0;
  private float elapsedTimeSinceLastChar = 0.0f;
  private float elapsedTime = 0.0f;
  private float endDelayTimer = 0.0f;
  private boolean waitingForEndDelay = false;
  private int ticksSinceLastCheck = 0;

  public HoloLogScreenPlayer(
      final HoloLogData holoLogData,
      final HoloLogPlaybackContext context,
      final UUID playerId,
      final Runnable onComplete) {
    super(holoLogData, context, playerId, onComplete);
    this.ticksPerCheck = CHECK_INTERVAL * 20.0f;
  }

  @Override
  protected void onPlaybackStart() {
    displayedLines.clear();
    currentCharIndex = 0;
    elapsedTimeSinceLastChar = 0.0f;
    elapsedTime = 0.0f;
    endDelayTimer = 0.0f;
    waitingForEndDelay = false;
    ticksSinceLastCheck = 0;
    log.info("[HolologScreenPlayer] Playback started: {} lines", holoLogData.lines().size());
  }

  @Override
  protected void onPlaybackStop() {
    displayedLines.clear();
  }

  @Override
  protected int getDisplayLineIndex() {
    int lineIndex = -1;

    for (int i = 0; i <= currentLineIndex && i < holoLogData.lines().size(); i++) {
      HoloLogLine line = holoLogData.lines().get(i);
      if (elapsedTime >= line.startTime()) {
        lineIndex = i;
      } else {
        break;
      }
    }

    return lineIndex;
  }

  @Override
  public void tick() {
    if (state != PlaybackState.PLAYING) {
      return;
    }

    ticksSinceLastCheck++;
    if (ticksSinceLastCheck >= ticksPerCheck) {
      elapsedTime += CHECK_INTERVAL;
      elapsedTimeSinceLastChar += CHECK_INTERVAL;
      ticksSinceLastCheck = 0;

      // Try to start voice-over if not already started
      startVoiceOver(elapsedTime);

      if (currentLineIndex < holoLogData.lines().size()) {
        float charDelay = holoLogData.charDelay();
        int charsToAdd = (int) (elapsedTimeSinceLastChar / charDelay);
        if (charsToAdd > 0) {
          elapsedTimeSinceLastChar -= charsToAdd * charDelay;

          while (charsToAdd > 0 && currentLineIndex < holoLogData.lines().size()) {
            HoloLogLine currentLine = holoLogData.lines().get(currentLineIndex);
            String lineText = currentLine.text() != null ? currentLine.text() : "";
            if (elapsedTime < currentLine.startTime()) {
              break;
            }

            if (currentCharIndex < lineText.length()) {
              currentCharIndex += charsToAdd;
              if (currentCharIndex > lineText.length()) {
                charsToAdd = currentCharIndex - lineText.length();
                currentCharIndex = lineText.length();
              } else {
                charsToAdd = 0;
              }
            } else {
              HoloLogPlayerEffects.playEffects(
                  currentLine.effects(), context, context.getEffectPosition());
              currentLineIndex++;
              currentCharIndex = 0;
              elapsedTimeSinceLastChar = 0.0f;
              if (!lineText.isEmpty()) {
                displayedLines.add(lineText);
              }
            }
          }
        }
      }

      if (currentLineIndex >= holoLogData.lines().size()) {
        if (!waitingForEndDelay) {
          waitingForEndDelay = true;
          endDelayTimer = 0.0f;
          log.debug("All lines finished, waiting {}s before end effects", holoLogData.endDelay());
        }
      }

      if (waitingForEndDelay) {
        endDelayTimer += CHECK_INTERVAL;
        if (endDelayTimer >= holoLogData.endDelay()) {
          completePlayback();
        }
      }
    }
  }

  public List<String> getDisplayedLines() {
    return displayedLines;
  }

  public String getCurrentPartialLine() {
    if (currentLineIndex >= holoLogData.lines().size()) {
      return "";
    }

    HoloLogLine currentLine = holoLogData.lines().get(currentLineIndex);
    String lineText = currentLine.text() != null ? currentLine.text() : "";

    if (currentCharIndex > 0 && currentCharIndex <= lineText.length()) {
      return lineText.substring(0, currentCharIndex);
    }

    return "";
  }

  public String getTitle() {
    return holoLogData.title();
  }

  public String getSubtitle() {
    return holoLogData.subtitle();
  }

  @Override
  protected void renderEntity(
      PoseStack poseStack, ResourceLocation entityId, float partialTick, int lightLevel) {
    Minecraft mc = Minecraft.getInstance();
    renderEntity(poseStack, mc.renderBuffers().bufferSource(), entityId, partialTick, lightLevel);
  }

  @Override
  protected void renderEntity(
      PoseStack poseStack,
      MultiBufferSource buffer,
      ResourceLocation entityId,
      float partialTick,
      int lightLevel) {
    EntityType.byString(entityId.toString())
        .ifPresent(
            entityType -> {
              Minecraft mc = Minecraft.getInstance();
              if (mc.level != null) {
                var entity = entityType.create(mc.level);
                if (entity instanceof LivingEntity livingEntity) {
                  mc.getEntityRenderDispatcher()
                      .render(livingEntity, 0, 0, 0, 0, partialTick, poseStack, buffer, lightLevel);
                  if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
                    bufferSource.endBatch();
                  }
                }
              }
            });
  }

  @Override
  protected void renderHoloEntity(
      PoseStack poseStack, HoloLogDisplayEntity displayEntity, float partialTick, int lightLevel) {
    Minecraft mc = Minecraft.getInstance();
    renderHoloEntity(
        poseStack, mc.renderBuffers().bufferSource(), displayEntity, partialTick, lightLevel);
  }

  @Override
  protected void renderHoloEntity(
      PoseStack poseStack,
      MultiBufferSource buffer,
      HoloLogDisplayEntity displayEntity,
      float partialTick,
      int lightLevel) {
    Minecraft minecraft = Minecraft.getInstance();
    Level level = minecraft.level;
    if (level == null || HoloLogEntityRegistry.HOLO_LOG_HUMANOID_ENTITY_TYPE == null) {
      return;
    }

    HoloLogHumanoidEntity holoEntity =
        HoloLogEntityRegistry.HOLO_LOG_HUMANOID_ENTITY_TYPE.create(level);
    if (holoEntity != null) {
      if (displayEntity.texture() != null) {
        holoEntity.setTexture(displayEntity.texture());
      }
      holoEntity.setSlim(displayEntity.slim());
      holoEntity.tickCount = (int) level.getGameTime();
      minecraft
          .getEntityRenderDispatcher()
          .render(holoEntity, 0, 0, 0, 0, partialTick, poseStack, buffer, lightLevel);
      if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
        bufferSource.endBatch();
      }
    }
  }

  @Override
  protected void renderBlock(PoseStack poseStack, ResourceLocation blockId, int lightLevel) {
    Minecraft mc = Minecraft.getInstance();
    renderBlock(poseStack, mc.renderBuffers().bufferSource(), blockId, lightLevel);
  }

  @Override
  protected void renderBlock(
      PoseStack poseStack, MultiBufferSource buffer, ResourceLocation blockId, int lightLevel) {
    Block block = BuiltInRegistries.BLOCK.get(blockId);
    if (block != null && block != Blocks.AIR) {
      Minecraft mc = Minecraft.getInstance();
      int fullBright = LightTexture.FULL_BRIGHT;
      mc.getBlockRenderer()
          .renderSingleBlock(
              block.defaultBlockState(), poseStack, buffer, fullBright, OverlayTexture.NO_OVERLAY);
      if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
        bufferSource.endBatch();
      }
    }
  }

  @Override
  protected void renderItem(PoseStack poseStack, ResourceLocation itemId, int lightLevel) {
    Minecraft mc = Minecraft.getInstance();
    renderItem(poseStack, mc.renderBuffers().bufferSource(), itemId, lightLevel);
  }

  @Override
  protected void renderItem(
      PoseStack poseStack, MultiBufferSource buffer, ResourceLocation itemId, int lightLevel) {
    Item item = BuiltInRegistries.ITEM.get(itemId);
    if (item != null && item != Items.AIR) {
      Minecraft mc = Minecraft.getInstance();
      ItemStack itemStack = new ItemStack(item);
      mc.getItemRenderer()
          .renderStatic(
              itemStack,
              ItemDisplayContext.FIXED,
              lightLevel,
              OverlayTexture.NO_OVERLAY,
              poseStack,
              buffer,
              mc.level,
              0);
      if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
        bufferSource.endBatch();
      }
    }
  }
}
