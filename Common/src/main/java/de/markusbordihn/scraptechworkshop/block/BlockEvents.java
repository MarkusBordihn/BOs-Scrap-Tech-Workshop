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

package de.markusbordihn.scraptechworkshop.block;

import de.markusbordihn.scraptechworkshop.drop.ScrapDropHandler;
import de.markusbordihn.scraptechworkshop.spawner.ScrapPileSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BlockEvents {

  public static void handleBlockPlaceEvent(
      Block block, BlockPos blockPos, ServerLevel serverLevel, ServerPlayer serverPlayer) {
    // Record player activity to avoid cheating
    ScrapDropHandler.handleBlockPlaced(blockPos);
  }

  public static void handleBlockBreakEvent(
      Block block, BlockPos blockPos, ServerLevel serverLevel, ServerPlayer serverPlayer) {

    if (block == Blocks.AIR || serverPlayer.isCreative()) {
      return;
    }

    // Record player activity in this chunk for intelligent spawning
    ScrapPileSpawner.recordPlayerActivity(new ChunkPos(blockPos));

    // Handle scrap drops for broken blocks
    ScrapDropHandler.handleBlockBreak(serverLevel, blockPos, block);
  }
}
