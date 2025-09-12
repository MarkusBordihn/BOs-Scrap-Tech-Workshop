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

package de.markusbordihn.scraptechworkshop.drop;

import de.markusbordihn.scraptechworkshop.config.ScrapDropConfig;
import de.markusbordihn.scraptechworkshop.data.scrap.ScrapDropData;
import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ScrapDropHandler {

  private static final Random RANDOM = new Random();
  private static final Map<Block, ScrapType> BLOCK_SCRAP_TYPES = createBlockScrapTypeMap();

  private static Map<Block, ScrapType> createBlockScrapTypeMap() {
    Map<Block, ScrapType> typeMap = new HashMap<>();

    // Stone blocks - metal scrap
    typeMap.put(Blocks.STONE, ScrapType.METAL);
    typeMap.put(Blocks.DEEPSLATE, ScrapType.METAL);
    typeMap.put(Blocks.GRANITE, ScrapType.METAL);
    typeMap.put(Blocks.ANDESITE, ScrapType.METAL);
    typeMap.put(Blocks.DIORITE, ScrapType.METAL);

    // Gold blocks - gold scrap
    typeMap.put(Blocks.GOLD_ORE, ScrapType.GOLD);
    typeMap.put(Blocks.DEEPSLATE_GOLD_ORE, ScrapType.GOLD);
    typeMap.put(Blocks.GOLD_BLOCK, ScrapType.GOLD);
    typeMap.put(Blocks.ANVIL, ScrapType.GOLD);
    typeMap.put(Blocks.CHIPPED_ANVIL, ScrapType.GOLD);
    typeMap.put(Blocks.DAMAGED_ANVIL, ScrapType.GOLD);

    // Iron blocks - iron scrap
    typeMap.put(Blocks.IRON_ORE, ScrapType.IRON);
    typeMap.put(Blocks.DEEPSLATE_IRON_ORE, ScrapType.IRON);
    typeMap.put(Blocks.IRON_BLOCK, ScrapType.IRON);
    typeMap.put(Blocks.RAW_IRON_BLOCK, ScrapType.IRON);

    // Copper blocks - copper scrap
    typeMap.put(Blocks.COPPER_ORE, ScrapType.COPPER);
    typeMap.put(Blocks.DEEPSLATE_COPPER_ORE, ScrapType.COPPER);
    typeMap.put(Blocks.COPPER_BLOCK, ScrapType.COPPER);
    typeMap.put(Blocks.RAW_COPPER_BLOCK, ScrapType.COPPER);
    typeMap.put(Blocks.EXPOSED_COPPER, ScrapType.COPPER);
    typeMap.put(Blocks.WEATHERED_COPPER, ScrapType.COPPER);
    typeMap.put(Blocks.OXIDIZED_COPPER, ScrapType.COPPER);

    return typeMap;
  }

  public static void handleBlockBreak(
      ServerLevel serverLevel, BlockPos blockPos, Block brokenBlock) {
    ScrapType scrapType = BLOCK_SCRAP_TYPES.get(brokenBlock);
    if (scrapType == null) {
      return;
    }

    ScrapDropData dropData = getScrapDropDataForType(scrapType);
    if (dropData == null || !dropData.isEnabled()) {
      return;
    }

    float dropChance = dropData.dropChance() / 100.0f;
    if (RANDOM.nextFloat() < dropChance) {
      ItemStack scrapStack = new ItemStack(dropData.scrapItem(), 1);
      ItemEntity itemEntity =
          new ItemEntity(
              serverLevel,
              blockPos.getX() + 0.5,
              blockPos.getY() + 0.5,
              blockPos.getZ() + 0.5,
              scrapStack);
      serverLevel.addFreshEntity(itemEntity);

      playDropSound(serverLevel, blockPos, scrapType);
    }
  }

  private static void playDropSound(ServerLevel level, BlockPos pos, ScrapType scrapType) {
    if (!ScrapDropConfig.enableDropSounds || ScrapDropConfig.dropSoundVolume <= 0.0f) {
      return;
    }

    SoundEvent soundEvent = getSoundForScrapType(scrapType);
    float volume = Math.min(1.0f, ScrapDropConfig.dropSoundVolume);
    float pitch = 0.8f + RANDOM.nextFloat() * 0.4f;

    level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, volume, pitch);
  }

  private static SoundEvent getSoundForScrapType(ScrapType scrapType) {
    return switch (scrapType) {
      case METAL -> SoundEvents.COPPER_BREAK;
      case GOLD -> SoundEvents.ANVIL_LAND;
      case IRON -> SoundEvents.IRON_TRAPDOOR_CLOSE;
      case COPPER -> SoundEvents.COPPER_BREAK;
    };
  }

  private static ScrapDropData getScrapDropDataForType(ScrapType scrapType) {
    return switch (scrapType) {
      case METAL ->
          new ScrapDropData(ModItems.SCRAP_METAL.get(), ScrapDropConfig.metalScrapDropChance);
      case GOLD ->
          new ScrapDropData(ModItems.SCRAP_GOLD.get(), ScrapDropConfig.goldScrapDropChance);
      case IRON ->
          new ScrapDropData(ModItems.SCRAP_IRON.get(), ScrapDropConfig.ironScrapDropChance);
      case COPPER ->
          new ScrapDropData(ModItems.SCRAP_COPPER.get(), ScrapDropConfig.copperScrapDropChance);
    };
  }
}
