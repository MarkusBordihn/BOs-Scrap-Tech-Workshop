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

import de.markusbordihn.scraptechworkshop.data.ScrapPileVariant;
import de.markusbordihn.scraptechworkshop.effects.ParticleEffects;
import de.markusbordihn.scraptechworkshop.loot.ScrapLootTables;
import de.markusbordihn.scraptechworkshop.spawner.ScrapPileSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ScrapPileCollector {
  private ScrapPileCollector() {}

  public static void handleScrapCollection(
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState,
      final Player player) {
    final int currentSize = blockState.getValue(ScrapPileBlock.SIZE);
    final ScrapPileVariant variant = blockState.getValue(ScrapPileBlock.VARIANT);

    final ItemStack scrapItem = ScrapLootTables.generateRandomScrap(variant, level.random);

    boolean itemAdded = true;
    if (!scrapItem.isEmpty()) {
      if (!player.getInventory().add(scrapItem)) {
        Block.popResource(level, blockPos, scrapItem);
        itemAdded = false;
      }
    }

    ParticleEffects.spawnScrapDustParticles(level, blockPos);

    level.playSound(
        null,
        blockPos,
        SoundEvents.ITEM_PICKUP,
        SoundSource.BLOCKS,
        0.5F,
        0.8F + level.random.nextFloat() * 0.4F);

    if (currentSize <= 1) {
      level.removeBlock(blockPos, false);
      try {
        ScrapPileSpawner.decrementChunkCount(new ChunkPos(blockPos));
      } catch (Exception e) {
        // Silent error handling
      }
      if (itemAdded) {
        level.playSound(
            null, blockPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.3F, 1.2F);
      }
    } else {
      level.setBlock(
          blockPos, blockState.setValue(ScrapPileBlock.SIZE, currentSize - 1), Block.UPDATE_ALL);
    }
  }
}
