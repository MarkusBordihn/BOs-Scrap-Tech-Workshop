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

package de.markusbordihn.scraptechworkshop.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.entity.hololog.HoloCubeBlockEntity;
import de.markusbordihn.scraptechworkshop.block.hololog.HoloCubeBlock;
import de.markusbordihn.scraptechworkshop.client.renderer.hololog.HoloLogBlockPlayer;
import de.markusbordihn.scraptechworkshop.data.hololog.DisplayType;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogDisplayEntity;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
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

  public HoloCubeBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(
      final T blockEntity,
      final float partialTicks,
      final PoseStack poseStack,
      final MultiBufferSource buffer,
      final int combinedLight,
      final int combinedOverlay) {

    if (blockEntity.getBlockState().getValue(HoloCubeBlock.STATUS) != HoloLogStatus.PLAYING) {
      return;
    }

    HoloLogBlockPlayer player = blockEntity.getPlayer();
    if (player == null || !player.isPlaying()) {
      return;
    }

    HoloLogDisplayEntity displayEntity = player.getCurrentDisplayEntity();
    if (displayEntity == null) {
      displayEntity = HoloLogDisplayEntity.DEFAULT_VILLAGER;
    }

    try {
      poseStack.pushPose();
      prepareHologramPose(blockEntity, displayEntity, poseStack);
      player.renderDisplayEntity(poseStack, buffer, displayEntity, partialTicks, combinedLight);
      poseStack.popPose();
    } catch (Exception e) {
      log.error(
          "Failed to render hologram display {} for block entity at {}: {}",
          displayEntity.id(),
          blockEntity.getBlockPos(),
          e.getMessage());
    }
  }

  private void prepareHologramPose(
      final T blockEntity, final HoloLogDisplayEntity displayEntity, final PoseStack poseStack) {

    double yOffset =
        displayEntity.type() == DisplayType.ITEM
            ? HOLOGRAM_Y_OFFSET_ITEM
            : displayEntity.type() == DisplayType.BLOCK
                ? HOLOGRAM_Y_OFFSET_BLOCK
                : HOLOGRAM_Y_OFFSET_DEFAULT;

    if (displayEntity.type() == DisplayType.BLOCK) {
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

    if (displayEntity.type() == DisplayType.ENTITY
        || displayEntity.type() == DisplayType.HOLO_ENTITY) {

      // Check if custom rotation is set for HOLO_ENTITY
      boolean hasCustomRotation = false;
      if (displayEntity.type() == DisplayType.HOLO_ENTITY) {
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

    } else if (displayEntity.type() == DisplayType.ITEM) {
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
