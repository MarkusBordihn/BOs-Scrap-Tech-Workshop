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

package de.markusbordihn.scraptechworkshop.processing;

import de.markusbordihn.scraptechworkshop.data.block.StrippableBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockInteractionProcessor {

  public static InteractionResult processCycleableBlock(
      final UseOnContext context, final BlockState blockState) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    Block block = blockState.getBlock();

    // Check if it's a cycleable wood block (can add/remove bark)
    if (StrippableBlocks.isCycleableWood(block)) {
      return AxeInteractionHandler.processCycleableWood(context, blockState);
    }

    // Handle dirt/grass/farmland/path cycling
    if (block == Blocks.DIRT_PATH) {
      return DirtPathTransformer.convertToFarmland(context, level, pos);
    } else if (block == Blocks.FARMLAND) {
      return FarmlandTransformer.convertToGrass(context, level, pos);
    } else {
      boolean preferHoe = ContextAnalyzer.shouldPreferHoe(level, pos, blockState);
      if (preferHoe) {
        return HoeInteractionHandler.tillGround(context, level, pos, blockState);
      } else {
        return ShovelInteractionHandler.createPath(context, level, pos, blockState);
      }
    }
  }

  public static boolean isCycleableBlock(final Block block) {
    return block == Blocks.GRASS_BLOCK
        || block == Blocks.DIRT
        || block == Blocks.COARSE_DIRT
        || block == Blocks.ROOTED_DIRT
        || block == Blocks.FARMLAND
        || block == Blocks.DIRT_PATH
        || StrippableBlocks.isCycleableWood(block);
  }
}
