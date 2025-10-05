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

package de.markusbordihn.scraptechworkshop.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.entity.HoloCubeBlockEntity;
import de.markusbordihn.scraptechworkshop.block.hololog.HoloCubeBlock;
import de.markusbordihn.scraptechworkshop.client.hololog.HolologPlayer;
import de.markusbordihn.scraptechworkshop.data.hololog.HolologData;
import de.markusbordihn.scraptechworkshop.data.hololog.HolologParser;
import de.markusbordihn.scraptechworkshop.data.hololog.HolologStatus;
import de.markusbordihn.scraptechworkshop.entity.hololog.HolologHumanoidEntity;
import de.markusbordihn.scraptechworkshop.registry.entity.HolologEntityRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloCubeBlockEntityRenderer<T extends HoloCubeBlockEntity>
    implements BlockEntityRenderer<T> {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final double HOLOGRAM_CENTER_X = 0.5;
  private static final double HOLOGRAM_CENTER_Z = 0.5;
  private static final double HOLOGRAM_Y_OFFSET_ITEM = 0.7;
  private static final double HOLOGRAM_Y_OFFSET_BLOCK = 0.8;
  private static final double HOLOGRAM_Y_OFFSET_DEFAULT = 0.5;

  private static final float BLOCK_SCALE_MULTIPLIER = 0.6f;
  private static final float ENTITY_SCALE_MULTIPLIER = 0.8f;

  private static final float ENTITY_HEAD_BOB_SPEED = 0.1f;
  private static final float ENTITY_HEAD_BOB_AMOUNT = 3.0f;

  private static final int RENDER_DISTANCE = 32;

  private final Map<ResourceLocation, Entity> entityCache = new HashMap<>();

  public HoloCubeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(
      T blockEntity,
      float partialTicks,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay) {

    if (blockEntity.getBlockState().getValue(HoloCubeBlock.STATUS) != HolologStatus.PLAYING) {
      return;
    }

    ResourceLocation holologId = blockEntity.getHolologId();
    if (holologId == null) {
      return;
    }

    Optional<HolologData> holologData =
        HolologParser.getHololog(HolologParser.getLocalizedId(holologId));
    if (holologData.isEmpty()) {
      return;
    }

    HolologData.HolologDisplayEntity displayEntity = getDisplayEntity(holologData.get());
    if (displayEntity == null) {
      displayEntity = HolologData.HolologDisplayEntity.DEFAULT_VILLAGER;
    }

    try {
      switch (displayEntity.type()) {
        case ENTITY -> renderEntity(blockEntity, displayEntity, poseStack, buffer, combinedLight);
        case BLOCK ->
            renderBlock(
                blockEntity, displayEntity, poseStack, buffer, combinedLight, combinedOverlay);
        case ITEM ->
            renderItem(
                blockEntity, displayEntity, poseStack, buffer, combinedLight, combinedOverlay);
        case HOLO_ENTITY ->
            renderHoloEntity(blockEntity, displayEntity, poseStack, buffer, combinedLight);
      }
    } catch (Exception e) {
      log.error(
          "Failed to render hologram display {} for block entity at {}: {}",
          displayEntity.id(),
          blockEntity.getBlockPos(),
          e.getMessage());
    }
  }

  private HolologData.HolologDisplayEntity getDisplayEntity(HolologData hololog) {
    int currentLine = HolologPlayer.getCurrentLine(hololog.id());
    int displayLineIndex = Math.max(0, currentLine - 1);

    if (currentLine > 0 && displayLineIndex < hololog.lines().size()) {
      HolologData.HolologLine line = hololog.lines().get(displayLineIndex);
      if (line.displayEntity() != null) {
        return line.displayEntity();
      }
    }

    return hololog.displayEntity();
  }

  private void renderEntity(
      T blockEntity,
      HolologData.HolologDisplayEntity displayEntity,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int combinedLight) {

    Entity entity = getOrCreateEntity(blockEntity.getLevel(), displayEntity.id());
    if (entity == null) {
      return;
    }

    EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    EntityRenderer<? super Entity> renderer = dispatcher.getRenderer(entity);

    Level level = blockEntity.getLevel();
    if (level != null) {
      entity.tickCount = (int) level.getGameTime();
    }

    poseStack.pushPose();
    prepareHologramPose(blockEntity, displayEntity, poseStack);

    renderer.render(entity, 0.0F, 0.0F, poseStack, buffer, combinedLight);

    poseStack.popPose();
  }

  private void renderHoloEntity(
      T blockEntity,
      HolologData.HolologDisplayEntity displayEntity,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int combinedLight) {

    HolologHumanoidEntity holoEntity = getOrCreateHoloEntity(blockEntity.getLevel(), displayEntity);
    if (holoEntity == null) {
      return;
    }

    EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    EntityRenderer<? super Entity> renderer = dispatcher.getRenderer(holoEntity);

    Level level = blockEntity.getLevel();
    if (level != null) {
      holoEntity.tickCount = (int) level.getGameTime();
    }

    poseStack.pushPose();
    prepareHologramPose(blockEntity, displayEntity, poseStack);

    renderer.render(holoEntity, 0.0F, 0.0F, poseStack, buffer, combinedLight);

    poseStack.popPose();
  }

  private void renderBlock(
      T blockEntity,
      HolologData.HolologDisplayEntity displayEntity,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay) {

    poseStack.pushPose();
    prepareHologramPose(blockEntity, displayEntity, poseStack);

    Minecraft.getInstance()
        .getBlockRenderer()
        .renderSingleBlock(
            BuiltInRegistries.BLOCK.get(displayEntity.id()).defaultBlockState(),
            poseStack,
            buffer,
            combinedLight,
            combinedOverlay);

    poseStack.popPose();
  }

  private void renderItem(
      T blockEntity,
      HolologData.HolologDisplayEntity displayEntity,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay) {

    poseStack.pushPose();
    prepareHologramPose(blockEntity, displayEntity, poseStack);

    Minecraft.getInstance()
        .getItemRenderer()
        .renderStatic(
            new ItemStack(BuiltInRegistries.ITEM.get(displayEntity.id())),
            ItemDisplayContext.GROUND,
            combinedLight,
            combinedOverlay,
            poseStack,
            buffer,
            blockEntity.getLevel(),
            0);

    poseStack.popPose();
  }

  private Entity getOrCreateEntity(Level level, ResourceLocation entityId) {
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

  private HolologHumanoidEntity getOrCreateHoloEntity(
      Level level, HolologData.HolologDisplayEntity displayEntity) {
    if (level == null || HolologEntityRegistry.HOLOLOG_HUMANOID_ENTITY_TYPE == null) {
      return null;
    }

    ResourceLocation cacheKey = displayEntity.texture();
    if (cacheKey == null) {
      cacheKey = new ResourceLocation(Constants.MOD_ID, "hololog_humanoid_default");
    }

    if (entityCache.containsKey(cacheKey)) {
      Entity cached = entityCache.get(cacheKey);
      if (cached instanceof HolologHumanoidEntity holoEntity && !cached.isRemoved()) {
        return holoEntity;
      }
    }

    HolologHumanoidEntity holoEntity =
        HolologEntityRegistry.HOLOLOG_HUMANOID_ENTITY_TYPE.create(level);
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

  private void prepareHologramPose(
      T blockEntity, HolologData.HolologDisplayEntity displayEntity, PoseStack poseStack) {

    double yOffset =
        displayEntity.type() == HolologData.DisplayType.ITEM
            ? HOLOGRAM_Y_OFFSET_ITEM
            : displayEntity.type() == HolologData.DisplayType.BLOCK
                ? HOLOGRAM_Y_OFFSET_BLOCK
                : HOLOGRAM_Y_OFFSET_DEFAULT;

    if (displayEntity.type() == HolologData.DisplayType.BLOCK) {
      poseStack.translate(HOLOGRAM_CENTER_X, yOffset, HOLOGRAM_CENTER_Z);

      float scale = displayEntity.scale() * BLOCK_SCALE_MULTIPLIER;
      poseStack.scale(scale, scale, scale);

      Level level = blockEntity.getLevel();
      if (level != null && displayEntity.rotationSpeed() > 0) {
        float time = level.getGameTime();
        float rotationSpeed = displayEntity.rotationSpeed();
        poseStack.mulPose(Axis.YP.rotationDegrees(time * rotationSpeed));
      }

      poseStack.translate(-0.5, -0.5, -0.5);

    } else {
      poseStack.translate(HOLOGRAM_CENTER_X, yOffset, HOLOGRAM_CENTER_Z);
    }

    if (displayEntity.type() == HolologData.DisplayType.ENTITY
        || displayEntity.type() == HolologData.DisplayType.HOLO_ENTITY) {

      // Check if custom rotation is set for HOLO_ENTITY
      boolean hasCustomRotation = false;
      if (displayEntity.type() == HolologData.DisplayType.HOLO_ENTITY) {
        hasCustomRotation =
            displayEntity.rotationX() != 0.0f
                || displayEntity.rotationY() != 0.0f
                || displayEntity.rotationZ() != 0.0f;
      }

      // Apply player-facing rotation only if no custom rotation is set
      if (!hasCustomRotation) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
          Vec3 blockCenter = Vec3.atCenterOf(blockEntity.getBlockPos()).add(0, yOffset, 0);
          Vec3 playerPos = minecraft.player.getEyePosition();
          Vec3 lookDir = playerPos.subtract(blockCenter).normalize();
          float yaw = (float) Math.toDegrees(Math.atan2(lookDir.x, lookDir.z));
          poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

          Level level = blockEntity.getLevel();
          if (level != null) {
            float time = level.getGameTime() + minecraft.getFrameTimeNs() / 1_000_000_000f;
            float headBob = (float) Math.sin(time * ENTITY_HEAD_BOB_SPEED) * ENTITY_HEAD_BOB_AMOUNT;
            poseStack.mulPose(Axis.XP.rotationDegrees(headBob));
          }
        }
      } else {
        // Apply custom rotation for HOLO_ENTITY
        poseStack.mulPose(Axis.XP.rotationDegrees(displayEntity.rotationX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(displayEntity.rotationY()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(displayEntity.rotationZ()));
      }

      float scale = displayEntity.scale() * ENTITY_SCALE_MULTIPLIER;
      poseStack.scale(scale, scale, scale);

    } else if (displayEntity.type() == HolologData.DisplayType.ITEM) {
      Level level = blockEntity.getLevel();
      if (level != null && displayEntity.rotationSpeed() > 0) {
        float time = level.getGameTime();
        float rotationSpeed = displayEntity.rotationSpeed();
        poseStack.mulPose(Axis.YP.rotationDegrees(time * rotationSpeed));
      }

      float scale = displayEntity.scale();
      poseStack.scale(scale, scale, scale);
    }
  }

  @Override
  public int getViewDistance() {
    return RENDER_DISTANCE;
  }
}
