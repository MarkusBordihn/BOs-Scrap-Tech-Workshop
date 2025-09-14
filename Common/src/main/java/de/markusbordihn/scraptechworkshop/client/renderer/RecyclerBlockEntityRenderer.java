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
import de.markusbordihn.scraptechworkshop.block.entity.recycler.RecyclerBlockEntity;
import de.markusbordihn.scraptechworkshop.block.recycler.RecyclerBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RecyclerBlockEntityRenderer implements BlockEntityRenderer<RecyclerBlockEntity> {

  private final ItemRenderer itemRenderer;

  public RecyclerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(
      RecyclerBlockEntity blockEntity,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay) {

    ItemStack inputStack = blockEntity.getItem(0); // Input slot is slot 0
    if (inputStack.isEmpty()) {
      return;
    }

    Level level = blockEntity.getLevel();
    if (level == null) {
      return;
    }

    // Get processing progress (0.0 to 1.0)
    float progress = getProcessingProgress(blockEntity, partialTick);

    // Only render animation if there's progress
    if (progress <= 0.0f) {
      return;
    }

    // Prevent multiple renders per tick by checking if we're in the right render phase
    if (!level.isClientSide) {
      return;
    }

    // Add small particle effects during processing
    addSmallParticleEffects(blockEntity, level, inputStack, progress);

    poseStack.pushPose();

    // Position item above the recycler block center
    poseStack.translate(0.5, 0.85, 0.56);

    // Get the facing direction of the block
    Direction facing = blockEntity.getBlockState().getValue(RecyclerBlock.FACING);

    // Apply rotation based on block facing
    switch (facing) {
      case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
      case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
      case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
      default -> {
        // NORTH is default (0 degrees) - no additional rotation needed
      }
    }

    long gameTime = level.getGameTime();
    float animationTime = (gameTime + partialTick); // Simplified animation speed

    // Shredder-like rotation: alternate between different axes each tick
    // This simulates how items tumble in a real shredder
    int rotationAxis = (int) (gameTime % 3); // 0=X, 1=Y, 2=Z
    float rotation = (animationTime * 2.0f) % 360.0f;

    // Scale down as processing progresses
    float scale = Mth.lerp(progress, 0.4f, 0.01f);

    // Calculate downward movement based on scale reduction
    float initialScale = 0.4f;
    float scaleReduction = initialScale - scale;
    float downwardMovement = scaleReduction * 0.3f;

    // Move the item down as it shrinks
    poseStack.translate(0, -downwardMovement, 0);

    poseStack.scale(scale, scale, scale);

    // Rotate on one axis per tick like a real shredder
    switch (rotationAxis) {
      case 0 -> poseStack.mulPose(Axis.XP.rotationDegrees(rotation)); // X-axis
      case 1 -> poseStack.mulPose(Axis.YP.rotationDegrees(rotation)); // Y-axis
      case 2 -> poseStack.mulPose(Axis.ZP.rotationDegrees(rotation)); // Z-axis
      default -> poseStack.mulPose(Axis.YP.rotationDegrees(rotation)); // Fallback to Y-axis
    }

    // Render the item with proper lighting and no transparency issues
    itemRenderer.renderStatic(
        inputStack,
        ItemDisplayContext.FIXED,
        packedLight,
        packedOverlay,
        poseStack,
        bufferSource,
        level,
        (int) blockEntity.getBlockPos().asLong());

    poseStack.popPose();
  }

  private void addSmallParticleEffects(
      RecyclerBlockEntity blockEntity, Level level, ItemStack inputStack, float progress) {
    // Only spawn particles every 30 ticks to reduce particle spam
    long gameTime = level.getGameTime();
    if ((gameTime % 30) != 0) {
      return;
    }

    // Create very tiny item particles that break off from the item
    double blockX = blockEntity.getBlockPos().getX() + 0.5;
    double blockY = blockEntity.getBlockPos().getY() + 0.75;
    double blockZ = blockEntity.getBlockPos().getZ() + 0.5;

    // Spawn only 1 particle per spawn event
    int particleCount = 1;
    for (int i = 0; i < particleCount; i++) {
      // Very small offset range for tiny particles - quarter of original size
      double offsetX = (level.getRandom().nextDouble() - 0.5) * 0.0375;
      double offsetY = (level.getRandom().nextDouble() - 0.5) * 0.025;
      double offsetZ = (level.getRandom().nextDouble() - 0.5) * 0.0375;

      // Very small, slow velocities for tiny particles
      double velocityX = (level.getRandom().nextDouble() - 0.5) * 0.0025;
      double velocityY = level.getRandom().nextDouble() * 0.00125;
      double velocityZ = (level.getRandom().nextDouble() - 0.5) * 0.0025;

      // Create tiny item break particles
      ItemParticleOption particleOption = new ItemParticleOption(ParticleTypes.ITEM, inputStack);
      level.addParticle(
          particleOption,
          blockX + offsetX,
          blockY + offsetY,
          blockZ + offsetZ,
          velocityX,
          velocityY,
          velocityZ);
    }

    // Add very rare tiny dust particles - only every ~2 seconds
    if ((gameTime % 40) == 0 && level.getRandom().nextFloat() < 0.3f) {
      level.addParticle(
          ParticleTypes.SMOKE,
          blockX + (level.getRandom().nextDouble() - 0.5) * 0.025,
          blockY + 0.0125,
          blockZ + (level.getRandom().nextDouble() - 0.5) * 0.025,
          0,
          0.0025,
          0);
    }
  }

  private float getProcessingProgress(RecyclerBlockEntity blockEntity, float partialTick) {
    int progress = blockEntity.getContainerData().get(0);
    int maxProgress = blockEntity.getContainerData().get(1);

    if (maxProgress <= 0) {
      return 0.0f;
    }

    return Math.min(1.0f, (progress + partialTick) / maxProgress);
  }
}
