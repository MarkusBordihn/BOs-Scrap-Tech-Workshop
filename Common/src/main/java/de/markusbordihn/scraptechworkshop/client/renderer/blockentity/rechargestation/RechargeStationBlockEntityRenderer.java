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

package de.markusbordihn.scraptechworkshop.client.renderer.blockentity.rechargestation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.markusbordihn.scraptechworkshop.block.entity.rechargestation.RechargeStationBlockEntity;
import de.markusbordihn.scraptechworkshop.block.rechargestation.RechargeStationBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RechargeStationBlockEntityRenderer
    implements BlockEntityRenderer<RechargeStationBlockEntity> {

  private static final float ITEM_SCALE = 0.5f;
  private static final float ITEM_Y_OFFSET = 0.5f;
  private static final float ITEM_OFFSET_FROM_CENTER = 0.15f;

  public RechargeStationBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(
      final RechargeStationBlockEntity blockEntity,
      final float partialTick,
      final PoseStack poseStack,
      final MultiBufferSource bufferSource,
      final int packedLight,
      final int packedOverlay) {

    ItemStack inputItem = blockEntity.getInputItem();
    if (inputItem.isEmpty()) {
      return;
    }

    BlockState blockState = blockEntity.getBlockState();
    Direction facing = blockState.getValue(RechargeStationBlock.FACING);

    poseStack.pushPose();

    // Calculate position based on facing direction
    float xOffset = 0.5f;
    float zOffset = 0.5f;
    float rotation = 0f;

    switch (facing) {
      case NORTH -> {
        zOffset = 0.5f - ITEM_OFFSET_FROM_CENTER;
        rotation = 0f;
      }
      case SOUTH -> {
        zOffset = 0.5f + ITEM_OFFSET_FROM_CENTER;
        rotation = 180f;
      }
      case WEST -> {
        xOffset = 0.5f - ITEM_OFFSET_FROM_CENTER;
        rotation = 90f;
      }
      case EAST -> {
        xOffset = 0.5f + ITEM_OFFSET_FROM_CENTER;
        rotation = 270f;
      }
      default -> {
        zOffset = 0.5f;
        rotation = 0f;
      }
    }

    poseStack.translate(xOffset, ITEM_Y_OFFSET, zOffset);
    poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

    Level level = blockEntity.getLevel();
    float time = level != null ? (level.getGameTime() + partialTick) * 2.0f : 0f;
    poseStack.mulPose(Axis.YP.rotationDegrees(time));

    poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    itemRenderer.renderStatic(
        inputItem,
        ItemDisplayContext.FIXED,
        packedLight,
        OverlayTexture.NO_OVERLAY,
        poseStack,
        bufferSource,
        blockEntity.getLevel(),
        0);

    poseStack.popPose();
  }
}
