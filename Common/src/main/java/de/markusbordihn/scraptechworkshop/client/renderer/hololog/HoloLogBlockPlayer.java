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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HoloLogBlockPlayer extends HoloLogPlaybackBase {

  private static final int CHECKS_PER_SECOND = 10;
  private static final float CHECK_INTERVAL = 1.0f / CHECKS_PER_SECOND;

  private final Consumer<String> textDisplay;
  private final Map<ResourceLocation, Entity> entityCache = new HashMap<>();
  private final float ticksPerCheck;
  private float elapsedTime = 0.0f;
  private int ticksSinceLastCheck = 0;

  public HoloLogBlockPlayer(
      final HoloLogData holoLogData,
      final HoloLogPlaybackContext context,
      final UUID playerId,
      final Consumer<String> textDisplay,
      final Runnable onComplete) {
    super(holoLogData, context, playerId, onComplete);
    this.textDisplay = textDisplay;
    this.ticksPerCheck = CHECK_INTERVAL * 20.0f;
  }

  @Override
  protected void onPlaybackStart() {
    elapsedTime = 0.0f;
    ticksSinceLastCheck = 0;

    if (textDisplay != null) {
      String titleText = Component.literal(holoLogData.title()).getString();
      if (!holoLogData.subtitle().isEmpty()) {
        titleText += " - " + holoLogData.subtitle();
      }
      textDisplay.accept(titleText);
      log.debug("Displayed title: {}", titleText);
    }
  }

  @Override
  protected void onPlaybackStop() {}

  @Override
  protected int getDisplayLineIndex() {
    int lineIndex = -1;
    for (int i = 0; i < holoLogData.lines().size(); i++) {
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

    Level level = context.getLevel();
    if (level == null || !(level instanceof ClientLevel)) {
      log.warn("Level is null or not ClientLevel, stopping playback");
      state = PlaybackState.STOPPED;
      return;
    }

    ticksSinceLastCheck++;
    if (ticksSinceLastCheck >= ticksPerCheck) {
      elapsedTime += CHECK_INTERVAL;
      ticksSinceLastCheck = 0;

      if (currentLineIndex < holoLogData.lines().size()) {
        HoloLogLine line = holoLogData.lines().get(currentLineIndex);
        if (elapsedTime >= line.startTime()) {
          if (currentLineIndex == 0) {
            startVoiceOver();
          }
          float effectiveDelay = line.lineDelay() > 0 ? line.lineDelay() : holoLogData.lineDelay();
          log.debug(
              "Displaying line {}/{}: '{}' at {}s (start time: {}s, delay: {}s)",
              currentLineIndex + 1,
              holoLogData.lines().size(),
              line.text().isEmpty() ? "<pause>" : line.text(),
              elapsedTime,
              line.startTime(),
              effectiveDelay);

          if (textDisplay != null && !line.text().isEmpty()) {
            textDisplay.accept(line.text());
          }
          HoloLogPlayerEffects.playEffects(line.effects(), context, context.getEffectPosition());
          currentLineIndex++;
        }
      }

      if (currentLineIndex >= holoLogData.lines().size()) {
        completePlayback();
      }
    }
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
    Level level = context.getLevel();
    Entity entity = getOrCreateEntity(level, entityId);
    if (entity == null) {
      return;
    }

    Minecraft mc = Minecraft.getInstance();
    EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
    EntityRenderer<? super Entity> renderer = dispatcher.getRenderer(entity);
    if (level != null) {
      entity.tickCount = (int) level.getGameTime();
    }

    renderer.render(entity, 0.0F, 0.0F, poseStack, buffer, lightLevel);
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
    Level level = context.getLevel();
    HoloLogHumanoidEntity holoEntity = getOrCreateHoloEntity(level, displayEntity);
    if (holoEntity == null) {
      return;
    }

    Minecraft minecraft = Minecraft.getInstance();
    EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
    EntityRenderer<? super Entity> renderer = dispatcher.getRenderer(holoEntity);
    if (level != null) {
      holoEntity.tickCount = (int) level.getGameTime();
    }

    renderer.render(holoEntity, 0.0F, 0.0F, poseStack, buffer, lightLevel);
  }

  @Override
  protected void renderBlock(PoseStack poseStack, ResourceLocation blockId, int lightLevel) {
    Minecraft mc = Minecraft.getInstance();
    renderBlock(poseStack, mc.renderBuffers().bufferSource(), blockId, lightLevel);
  }

  @Override
  protected void renderBlock(
      PoseStack poseStack, MultiBufferSource buffer, ResourceLocation blockId, int lightLevel) {
    Minecraft.getInstance()
        .getBlockRenderer()
        .renderSingleBlock(
            BuiltInRegistries.BLOCK.get(blockId).defaultBlockState(),
            poseStack,
            buffer,
            lightLevel,
            OverlayTexture.NO_OVERLAY);
  }

  @Override
  protected void renderItem(PoseStack poseStack, ResourceLocation itemId, int lightLevel) {
    Minecraft mc = Minecraft.getInstance();
    renderItem(poseStack, mc.renderBuffers().bufferSource(), itemId, lightLevel);
  }

  @Override
  protected void renderItem(
      PoseStack poseStack, MultiBufferSource buffer, ResourceLocation itemId, int lightLevel) {
    Minecraft.getInstance()
        .getItemRenderer()
        .renderStatic(
            new ItemStack(BuiltInRegistries.ITEM.get(itemId)),
            ItemDisplayContext.GROUND,
            lightLevel,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            buffer,
            context.getLevel(),
            0);
  }

  private Entity getOrCreateEntity(final Level level, final ResourceLocation entityId) {
    if (level == null) {
      return null;
    }

    if (entityCache.containsKey(entityId)) {
      Entity cached = entityCache.get(entityId);
      if (cached != null && !cached.isRemoved()) {
        return cached;
      }
    }

    Optional<EntityType<?>> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId);
    if (entityType.isEmpty()) {
      log.warn("Entity type not found: {}", entityId);
      return null;
    }

    Entity entity = entityType.get().create(level);
    if (entity != null) {
      entity.setInvisible(false);
      entityCache.put(entityId, entity);
    }

    return entity;
  }

  private HoloLogHumanoidEntity getOrCreateHoloEntity(
      final Level level, final HoloLogDisplayEntity displayEntity) {
    if (level == null || HoloLogEntityRegistry.HOLO_LOG_HUMANOID_ENTITY_TYPE == null) {
      return null;
    }

    ResourceLocation cacheKey = displayEntity.texture();
    if (cacheKey == null) {
      cacheKey = new ResourceLocation(Constants.MOD_ID, "hololog_humanoid_default");
    }

    if (entityCache.containsKey(cacheKey)) {
      Entity cached = entityCache.get(cacheKey);
      if (cached instanceof HoloLogHumanoidEntity holoEntity && !cached.isRemoved()) {
        return holoEntity;
      }
    }

    HoloLogHumanoidEntity holoEntity =
        HoloLogEntityRegistry.HOLO_LOG_HUMANOID_ENTITY_TYPE.create(level);
    if (holoEntity != null) {
      holoEntity.setInvisible(false);
      if (displayEntity.texture() != null) {
        holoEntity.setTexture(displayEntity.texture());
      }
      holoEntity.setSlim(displayEntity.slim());
      entityCache.put(cacheKey, holoEntity);
    }

    return holoEntity;
  }
}
