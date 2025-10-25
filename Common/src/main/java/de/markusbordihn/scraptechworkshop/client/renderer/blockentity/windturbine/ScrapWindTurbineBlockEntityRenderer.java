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

package de.markusbordihn.scraptechworkshop.client.renderer.blockentity.windturbine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.markusbordihn.scraptechworkshop.block.entity.windturbine.ScrapWindTurbineBlockEntity;
import de.markusbordihn.scraptechworkshop.block.multiblock.MultiBlockStructure;
import de.markusbordihn.scraptechworkshop.block.windturbine.ScrapWindTurbineBlock;
import de.markusbordihn.scraptechworkshop.data.windturbine.ScrapWindTurbineStatus;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ScrapWindTurbineBlockEntityRenderer
    implements BlockEntityRenderer<ScrapWindTurbineBlockEntity> {

  private final ItemRenderer itemRenderer;
  private final ItemStack bladesItem;
  private final Map<ScrapWindTurbineBlockEntity, Float> rotationMap = new HashMap<>();

  public ScrapWindTurbineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    this.itemRenderer = context.getItemRenderer();
    this.bladesItem = new ItemStack(ModItems.SCRAP_WIND_TURBINE_BLADES.get());
  }

  @Override
  public void render(
      final ScrapWindTurbineBlockEntity blockEntity,
      final float partialTick,
      final PoseStack poseStack,
      final MultiBufferSource bufferSource,
      final int packedLight,
      final int packedOverlay) {

    BlockState blockState = blockEntity.getBlockState();
    if (blockState.getValue(MultiBlockStructure.HALF) == DoubleBlockHalf.UPPER) {
      return;
    }

    ScrapWindTurbineStatus status = blockState.getValue(ScrapWindTurbineBlock.STATUS);
    if (status == ScrapWindTurbineStatus.ERROR) {
      return;
    }

    poseStack.pushPose();
    poseStack.translate(0.5, 1.0, 0.5);
    poseStack.scale(2f, 2f, 2f);

    // Track rotation client-side per block entity
    float rotation = rotationMap.getOrDefault(blockEntity, 0.0f);
    if (blockEntity.getWindSpeed() > 0) {
      rotation += partialTick * blockEntity.getWindSpeed() * 0.05f;
      if (rotation > 3600.0f) {
        rotation -= 3600.0f;
      }
      rotationMap.put(blockEntity, rotation);
    }
    poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

    itemRenderer.renderStatic(
        bladesItem,
        ItemDisplayContext.FIXED,
        packedLight,
        packedOverlay,
        poseStack,
        bufferSource,
        blockEntity.getLevel(),
        0);
    poseStack.popPose();
  }
}
