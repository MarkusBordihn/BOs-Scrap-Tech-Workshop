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

package de.markusbordihn.scraptechworkshop.item;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.ScrapPileBlock;
import de.markusbordihn.scraptechworkshop.data.ScrapPileVariant;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ScrapPileBlockItem extends BlockItem {

  public static final String MIXED_ID = "mixed_scrap_pile";
  public static final String METAL_ID = "metal_scrap_pile";
  public static final String TECH_ID = "tech_scrap_pile";

  private final ScrapPileVariant variant;

  public ScrapPileBlockItem(Block block, Properties properties, ScrapPileVariant variant) {
    super(block, properties);
    this.variant = variant;
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState existingState = level.getBlockState(pos);

    if (existingState.getBlock() instanceof ScrapPileBlock) {
      ScrapPileVariant existingVariant = existingState.getValue(ScrapPileBlock.VARIANT);

      if (existingVariant == this.variant) {
        int currentSize = existingState.getValue(ScrapPileBlock.SIZE);
        if (currentSize < 4) {
          if (!level.isClientSide) {
            BlockState newState = existingState.setValue(ScrapPileBlock.SIZE, currentSize + 1);
            level.setBlock(pos, newState, Block.UPDATE_ALL);

            var player = context.getPlayer();
            if (player != null && !player.getAbilities().instabuild) {
              context.getItemInHand().shrink(1);
            }
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.FAIL;
      }
    }

    return super.useOn(context);
  }

  @Override
  public InteractionResult place(BlockPlaceContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState existingState = level.getBlockState(pos);

    if (existingState.getBlock() instanceof ScrapPileBlock) {
      ScrapPileVariant existingVariant = existingState.getValue(ScrapPileBlock.VARIANT);

      if (existingVariant == this.variant) {
        int currentSize = existingState.getValue(ScrapPileBlock.SIZE);
        if (currentSize < 4) {
          BlockState newState = existingState.setValue(ScrapPileBlock.SIZE, currentSize + 1);
          level.setBlock(pos, newState, Block.UPDATE_ALL);
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
      }
      return InteractionResult.FAIL;
    }

    InteractionResult result = super.place(context);

    if (result.consumesAction() && !level.isClientSide) {
      BlockState state = level.getBlockState(pos);
      if (state.getBlock() instanceof ScrapPileBlock) {
        BlockState newState =
            state.setValue(ScrapPileBlock.SIZE, 1).setValue(ScrapPileBlock.VARIANT, this.variant);
        level.setBlock(pos, newState, Block.UPDATE_ALL);
      }
    }

    return result;
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
    tooltip.add(Component.translatable(Constants.ITEM_PREFIX + "scrap_pile.tooltip"));
    tooltip.add(Component.translatable(Constants.ITEM_PREFIX + "scrap_pile.stacking"));

    String variantKey =
        switch (variant) {
          case MIXED -> "mixed_scrap_pile";
          case METAL -> "metal_scrap_pile";
          case TECH -> "tech_scrap_pile";
        };
    tooltip.add(Component.translatable(Constants.ITEM_PREFIX + variantKey + ".tooltip"));
  }

  public ScrapPileVariant getVariant() {
    return variant;
  }
}
