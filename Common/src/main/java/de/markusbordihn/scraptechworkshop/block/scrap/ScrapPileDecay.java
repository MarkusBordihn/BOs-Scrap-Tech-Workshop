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
import de.markusbordihn.scraptechworkshop.block.ScrapPileBlock;
import de.markusbordihn.scraptechworkshop.data.ScrapPileVariant;
import de.markusbordihn.scraptechworkshop.spawner.ScrapPileSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapPileDecay {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private ScrapPileDecay() {}

  public static void handleDecay(
      final BlockState blockState,
      final ServerLevel serverLevel,
      final BlockPos blockPos,
      final RandomSource randomSource) {
    final int currentSize = blockState.getValue(ScrapPileBlock.SIZE);
    final ScrapPileVariant variant = blockState.getValue(ScrapPileBlock.VARIANT);

    if (currentSize > 1) {
      serverLevel.setBlock(
          blockPos, blockState.setValue(ScrapPileBlock.SIZE, currentSize - 1), Block.UPDATE_ALL);
      log.debug(
          "Scrap pile aged at {} in dimension {} - Size reduced from {} to {}, Variant: {}",
          blockPos,
          serverLevel.dimension().location(),
          currentSize,
          currentSize - 1,
          variant);
    } else {
      serverLevel.removeBlock(blockPos, false);
      try {
        ScrapPileSpawner.decrementChunkCount(new ChunkPos(blockPos));
      } catch (Exception e) {
        // Silent error handling
      }
      log.debug(
          "Scrap pile completely decayed at {} in dimension {} - Removed, Variant: {}",
          blockPos,
          serverLevel.dimension().location(),
          variant);
    }
  }
}
