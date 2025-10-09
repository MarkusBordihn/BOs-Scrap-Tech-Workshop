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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ContextAnalyzer {

  private static final int WATER_SEARCH_RADIUS = 4;

  public static boolean shouldPreferHoe(
      final Level level, final BlockPos blockPos, final BlockState blockState) {
    return hasAdjacentFarmland(level, blockPos) || hasNearbyWater(level, blockPos);
  }

  private static boolean hasAdjacentFarmland(final Level level, final BlockPos blockPos) {
    for (BlockPos adjacentPos :
        BlockPos.betweenClosed(blockPos.offset(-1, 0, -1), blockPos.offset(1, 0, 1))) {
      if (!adjacentPos.equals(blockPos)) {
        BlockState adjacentState = level.getBlockState(adjacentPos);
        if (adjacentState.is(Blocks.FARMLAND)) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean hasNearbyWater(final Level level, final BlockPos blockPos) {
    for (BlockPos checkPos :
        BlockPos.betweenClosed(
            blockPos.offset(-WATER_SEARCH_RADIUS, -1, -WATER_SEARCH_RADIUS),
            blockPos.offset(WATER_SEARCH_RADIUS, 1, WATER_SEARCH_RADIUS))) {
      BlockState checkState = level.getBlockState(checkPos);
      if (checkState.is(Blocks.WATER)) {
        return true;
      }
    }
    return false;
  }
}
