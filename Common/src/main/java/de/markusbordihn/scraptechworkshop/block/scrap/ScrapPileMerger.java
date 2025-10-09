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

package de.markusbordihn.scraptechworkshop.block.scrap;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.ScrapPileVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapPileMerger {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final int MAX_SIZE = 4;

  private ScrapPileMerger() {}

  public static boolean attemptMergeWithNeighbors(
      final Level level, final BlockPos pos, final BlockState state) {
    final ScrapPileVariant variant = state.getValue(ScrapPileBlock.VARIANT);
    final int currentSize = state.getValue(ScrapPileBlock.SIZE);

    for (final Direction direction : Direction.Plane.HORIZONTAL) {
      final BlockPos neighborPos = pos.relative(direction);
      final BlockState neighborState = level.getBlockState(neighborPos);

      if (neighborState.getBlock() instanceof ScrapPileBlock
          && neighborState.getValue(ScrapPileBlock.VARIANT) == variant) {

        final int neighborSize = neighborState.getValue(ScrapPileBlock.SIZE);
        final int totalSize = currentSize + neighborSize;

        if (totalSize <= MAX_SIZE) {
          level.setBlock(pos, state.setValue(ScrapPileBlock.SIZE, totalSize), Block.UPDATE_ALL);
          level.removeBlock(neighborPos, false);
          log.debug(
              "Merged scrap piles at {} and {} in dimension {} - Combined size: {}, Variant: {}",
              pos,
              neighborPos,
              level.dimension().location(),
              totalSize,
              variant);
          return true;
        } else {
          final int overflow = totalSize - MAX_SIZE;
          level.setBlock(pos, state.setValue(ScrapPileBlock.SIZE, MAX_SIZE), Block.UPDATE_ALL);
          level.setBlock(
              neighborPos, neighborState.setValue(ScrapPileBlock.SIZE, overflow), Block.UPDATE_ALL);
          log.debug(
              "Partially merged scrap piles at {} and {} in dimension {} - Main: 4, Overflow: {}, Variant: {}",
              pos,
              neighborPos,
              level.dimension().location(),
              overflow,
              variant);
          return true;
        }
      }
    }
    return false;
  }
}
