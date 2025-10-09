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

package de.markusbordihn.scraptechworkshop.spawner;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.scrap.ScrapPileBlock;
import de.markusbordihn.scraptechworkshop.config.ScrapPileConfig;
import de.markusbordihn.scraptechworkshop.data.ScrapPileVariant;
import de.markusbordihn.scraptechworkshop.registry.block.ScrapPileBlockRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapPileSpawner {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Map<ChunkPos, Integer> chunkScrapCounts = new ConcurrentHashMap<>();
  private static final Map<ChunkPos, Long> chunkCountTimestamps = new ConcurrentHashMap<>();
  private static final Map<ChunkPos, Long> chunkPlayerActivity = new ConcurrentHashMap<>();
  private static final long CACHE_VALIDITY_MS = 30000;
  private static final long ACTIVITY_VALIDITY_MS = 300000;
  private static int tickCounter = 0;

  protected ScrapPileSpawner() {}

  public static void tick(final MinecraftServer minecraftServer) {

    if (ScrapPileConfig.spawnIntervalTicks <= 0) {
      return;
    }

    if (++tickCounter >= ScrapPileConfig.spawnIntervalTicks) {
      tickCounter = 0;
      cleanupExpiredCache();
      for (ServerLevel level : minecraftServer.getAllLevels()) {
        try {
          tick(level);
        } catch (Exception e) {
          // Silent error handling to prevent server crashes
        }
      }
    }
  }

  public static void tick(final ServerLevel level) {
    if (!ScrapPileConfig.spawnEnabled || level.players().isEmpty()) {
      return;
    }

    int attemptsThisTick = getAdaptiveSpawnAttempts(level);
    for (int i = 0; i < attemptsThisTick; i++) {
      try {
        trySpawnScrapPile(level);
      } catch (Exception e) {
        break;
      }
    }
  }

  private static void cleanupExpiredCache() {
    long currentTime = System.currentTimeMillis();
    chunkCountTimestamps
        .entrySet()
        .removeIf(entry -> currentTime - entry.getValue() > CACHE_VALIDITY_MS);
    chunkScrapCounts.keySet().removeIf(chunkPos -> !chunkCountTimestamps.containsKey(chunkPos));

    chunkPlayerActivity
        .entrySet()
        .removeIf(entry -> currentTime - entry.getValue() > ACTIVITY_VALIDITY_MS);
  }

  private static int getAdaptiveSpawnAttempts(final ServerLevel level) {
    int baseAttempts = ScrapPileConfig.spawnAttemptsPerTick;
    double mspt = level.getServer().getAverageTickTime();
    if (mspt > 40.0) {
      return Math.max(1, baseAttempts / 4);
    } else if (mspt > 30.0) {
      return Math.max(1, baseAttempts / 2);
    } else if (mspt > 20.0) {
      return Math.max(1, (baseAttempts * 3) / 4);
    }

    return baseAttempts;
  }

  private static void trySpawnScrapPile(final ServerLevel level) {
    if (level.players().isEmpty()) {
      return;
    }

    var randomPlayer = level.players().get(level.random.nextInt(level.players().size()));
    ChunkPos playerChunk = new ChunkPos(randomPlayer.blockPosition());

    // First, try chunks with recent player activity (higher priority)
    for (int attempts = 0; attempts < 3; attempts++) {
      int offsetX = level.random.nextInt(17) - 8;
      int offsetZ = level.random.nextInt(17) - 8;
      ChunkPos targetChunk = new ChunkPos(playerChunk.x + offsetX, playerChunk.z + offsetZ);

      if (!level.hasChunk(targetChunk.x, targetChunk.z)) {
        continue;
      }

      if (getCachedScrapPileCount(level, targetChunk) >= ScrapPileConfig.spawnChunkCap) {
        continue;
      }

      // Prioritize chunks with recent player activity
      if (hasRecentPlayerActivity(targetChunk)) {
        BlockPos spawnPos = findSuitableSpawnLocation(level, targetChunk);
        if (spawnPos != null) {
          spawnScrapPile(level, spawnPos);
          incrementChunkCount(targetChunk);
          return;
        }
      }
    }

    // If no activity-based chunks worked, try regular spawning
    for (int attempts = 0; attempts < 3; attempts++) {
      int offsetX = level.random.nextInt(17) - 8;
      int offsetZ = level.random.nextInt(17) - 8;
      ChunkPos targetChunk = new ChunkPos(playerChunk.x + offsetX, playerChunk.z + offsetZ);

      if (!level.hasChunk(targetChunk.x, targetChunk.z)) {
        continue;
      }

      if (getCachedScrapPileCount(level, targetChunk) >= ScrapPileConfig.spawnChunkCap) {
        continue;
      }

      BlockPos spawnPos = findSuitableSpawnLocation(level, targetChunk);
      if (spawnPos != null) {
        spawnScrapPile(level, spawnPos);
        incrementChunkCount(targetChunk);
        return;
      }
    }
  }

  private static int getCachedScrapPileCount(final ServerLevel level, final ChunkPos chunkPos) {
    long currentTime = System.currentTimeMillis();
    Long cacheTime = chunkCountTimestamps.get(chunkPos);

    if (cacheTime != null && currentTime - cacheTime < CACHE_VALIDITY_MS) {
      return chunkScrapCounts.getOrDefault(chunkPos, 0);
    }

    int count = countScrapPilesInChunk(level, chunkPos);
    chunkScrapCounts.put(chunkPos, count);
    chunkCountTimestamps.put(chunkPos, currentTime);
    return count;
  }

  private static void incrementChunkCount(final ChunkPos chunkPos) {
    chunkScrapCounts.merge(chunkPos, 1, Integer::sum);
    chunkCountTimestamps.put(chunkPos, System.currentTimeMillis());
  }

  public static void decrementChunkCount(final ChunkPos chunkPos) {
    Integer current = chunkScrapCounts.get(chunkPos);
    if (current != null && current > 0) {
      chunkScrapCounts.put(chunkPos, current - 1);
      chunkCountTimestamps.put(chunkPos, System.currentTimeMillis());
    }
  }

  /** Record player activity in a chunk to influence spawning decisions */
  public static void recordPlayerActivity(final ChunkPos chunkPos) {
    chunkPlayerActivity.put(chunkPos, System.currentTimeMillis());
  }

  /** Check if a chunk has recent player activity */
  private static boolean hasRecentPlayerActivity(final ChunkPos chunkPos) {
    Long lastActivity = chunkPlayerActivity.get(chunkPos);
    if (lastActivity == null) {
      return false;
    }
    return System.currentTimeMillis() - lastActivity < ACTIVITY_VALIDITY_MS;
  }

  private static int countScrapPilesInChunk(final ServerLevel level, final ChunkPos chunkPos) {
    if (!level.hasChunk(chunkPos.x, chunkPos.z)) {
      return 0;
    }

    int count = 0;
    int startX = chunkPos.getMinBlockX();
    int startZ = chunkPos.getMinBlockZ();
    int endX = chunkPos.getMaxBlockX();
    int endZ = chunkPos.getMaxBlockZ();
    try {
      for (int x = startX; x <= endX; x += 8) {
        for (int z = startZ; z <= endZ; z += 8) {
          for (int y = level.getMinBuildHeight(); y <= ScrapPileConfig.spawnYMax; y += 8) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).getBlock() instanceof ScrapPileBlock) {
              count++;
            }
          }
        }
      }
    } catch (Exception e) {
      return 0;
    }
    return count;
  }

  private static BlockPos findSuitableSpawnLocation(
      final ServerLevel level, final ChunkPos chunkPos) {
    RandomSource random = level.random;

    for (int attempt = 0; attempt < 6; attempt++) {
      int x = chunkPos.getMinBlockX() + random.nextInt(16);
      int z = chunkPos.getMinBlockZ() + random.nextInt(16);

      int maxY = Math.min(ScrapPileConfig.spawnYMax, level.getMaxBuildHeight() - 1);
      int startY =
          random.nextInt(Math.max(1, maxY - level.getMinBuildHeight())) + level.getMinBuildHeight();

      for (int y = startY; y <= maxY; y++) {
        BlockPos pos = new BlockPos(x, y, z);
        try {
          if (isSuitableSpawnLocation(level, pos)) {
            return pos;
          }
        } catch (Exception ignored) {
        }
      }
    }
    return null;
  }

  private static boolean isSuitableSpawnLocation(final ServerLevel level, final BlockPos blockPos) {
    try {
      BlockState groundState = level.getBlockState(blockPos.below());
      BlockState airState = level.getBlockState(blockPos);

      if (!groundState.is(BlockTags.BASE_STONE_OVERWORLD)
          && !groundState.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)) {
        return false;
      }

      if (!airState.isAir() && airState.getFluidState().getType() != Fluids.WATER) {
        return false;
      }

      if (blockPos.getY() > ScrapPileConfig.spawnYMax) {
        return false;
      }

      if (ScrapPileConfig.spawnDarknessRequired) {
        if (level.getBrightness(LightLayer.BLOCK, blockPos) > 7) {
          return false;
        }
      }

      return level.players().stream()
          .noneMatch(
              player ->
                  player.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < 196);
    } catch (Exception e) {
      return false;
    }
  }

  private static void spawnScrapPile(final ServerLevel serverLevel, final BlockPos blockPos) {
    try {
      RandomSource random = serverLevel.random;
      ScrapPileVariant variant = getRandomVariant(random, serverLevel, blockPos);
      int size = random.nextFloat() < 0.75f ? 1 : (random.nextFloat() < 0.8f ? 2 : 3);
      boolean waterlogged =
          serverLevel.getBlockState(blockPos).getFluidState().getType() == Fluids.WATER;

      BlockState scrapPileState =
          ScrapPileBlockRegistry.SCRAP_PILE_BLOCK
              .defaultBlockState()
              .setValue(ScrapPileBlock.SIZE, size)
              .setValue(ScrapPileBlock.VARIANT, variant)
              .setValue(ScrapPileBlock.WATERLOGGED, waterlogged);

      serverLevel.setBlock(blockPos, scrapPileState, Block.UPDATE_ALL);

      if (log.isDebugEnabled()) {
        log.debug(
            "Spawned scrap pile at {} in dimension {} - Size: {}, Variant: {}, Waterlogged: {}",
            blockPos,
            serverLevel.dimension().location(),
            size,
            variant,
            waterlogged);
      }
    } catch (Exception e) {
      // Silent fail to prevent server disruption
    }
  }

  private static ScrapPileVariant getRandomVariant(
      final RandomSource random, final ServerLevel serverLevel, final BlockPos blockPos) {
    int depth = blockPos.getY();

    // Very deep (below Y=-32) heavily favors tech
    if (depth < -32) {
      float rand = random.nextFloat();
      if (rand < 0.2f) {
        return ScrapPileVariant.MIXED;
      } else if (rand < 0.4f) {
        return ScrapPileVariant.METAL;
      } else {
        return ScrapPileVariant.TECH;
      }
    }

    // Deep underground (below Y=0) favors metal and tech
    if (depth < 0) {
      float rand = random.nextFloat();
      if (rand < 0.3f) {
        return ScrapPileVariant.MIXED;
      } else if (rand < 0.7f) {
        return ScrapPileVariant.METAL;
      } else {
        return ScrapPileVariant.TECH;
      }
    }

    // Surface level (default behavior)
    float rand = random.nextFloat();
    if (rand < 0.6f) {
      return ScrapPileVariant.MIXED;
    } else if (rand < 0.85f) {
      return ScrapPileVariant.METAL;
    } else {
      return ScrapPileVariant.TECH;
    }
  }
}
