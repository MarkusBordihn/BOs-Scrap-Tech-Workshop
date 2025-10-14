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
import de.markusbordihn.scraptechworkshop.data.scrap.ScrapSoundType;
import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
    typeMap.put(Blocks.ANDESITE, ScrapType.METAL);
    typeMap.put(Blocks.DEEPSLATE, ScrapType.METAL);
    typeMap.put(Blocks.DIORITE, ScrapType.METAL);
    typeMap.put(Blocks.GRANITE, ScrapType.METAL);
    typeMap.put(Blocks.STONE, ScrapType.METAL);

    // Gold blocks - gold scrap
    typeMap.put(Blocks.ANVIL, ScrapType.GOLD);
    typeMap.put(Blocks.CHIPPED_ANVIL, ScrapType.GOLD);
    typeMap.put(Blocks.DAMAGED_ANVIL, ScrapType.GOLD);
    typeMap.put(Blocks.DEEPSLATE_GOLD_ORE, ScrapType.GOLD);
    typeMap.put(Blocks.GOLD_BLOCK, ScrapType.GOLD);
    typeMap.put(Blocks.GOLD_ORE, ScrapType.GOLD);

    // Iron blocks - iron scrap
    typeMap.put(Blocks.DEEPSLATE_IRON_ORE, ScrapType.IRON);
    typeMap.put(Blocks.IRON_BLOCK, ScrapType.IRON);
    typeMap.put(Blocks.IRON_ORE, ScrapType.IRON);
    typeMap.put(Blocks.RAW_IRON_BLOCK, ScrapType.IRON);

    // Copper blocks - copper scrap
    typeMap.put(Blocks.COPPER_BLOCK, ScrapType.COPPER);
    typeMap.put(Blocks.COPPER_ORE, ScrapType.COPPER);
    typeMap.put(Blocks.DEEPSLATE_COPPER_ORE, ScrapType.COPPER);
    typeMap.put(Blocks.EXPOSED_COPPER, ScrapType.COPPER);
    typeMap.put(Blocks.OXIDIZED_COPPER, ScrapType.COPPER);
    typeMap.put(Blocks.RAW_COPPER_BLOCK, ScrapType.COPPER);
    typeMap.put(Blocks.WEATHERED_COPPER, ScrapType.COPPER);

    // Alloy blocks - sophisticated alloys from ancient civilizations
    typeMap.put(Blocks.NETHERITE_BLOCK, ScrapType.ALLOY);
    typeMap.put(Blocks.END_STONE, ScrapType.ALLOY);
    typeMap.put(Blocks.PURPUR_BLOCK, ScrapType.ALLOY);
    typeMap.put(Blocks.PURPUR_PILLAR, ScrapType.ALLOY);
    typeMap.put(Blocks.END_STONE_BRICKS, ScrapType.ALLOY);

    // Insulation materials - blocks used for thermal/electrical insulation
    typeMap.put(Blocks.MAGMA_BLOCK, ScrapType.INSULATION);
    typeMap.put(Blocks.SPONGE, ScrapType.INSULATION);
    typeMap.put(Blocks.WET_SPONGE, ScrapType.INSULATION);
    typeMap.put(Blocks.WHITE_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.ORANGE_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.MAGENTA_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.LIGHT_BLUE_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.YELLOW_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.LIME_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.PINK_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.GRAY_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.LIGHT_GRAY_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.CYAN_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.PURPLE_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.BLUE_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.BROWN_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.GREEN_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.RED_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.BLACK_CONCRETE, ScrapType.INSULATION);
    typeMap.put(Blocks.WHITE_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.ORANGE_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.MAGENTA_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.LIGHT_BLUE_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.YELLOW_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.LIME_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.PINK_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.GRAY_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.LIGHT_GRAY_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.CYAN_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.PURPLE_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.BLUE_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.BROWN_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.GREEN_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.RED_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.BLACK_TERRACOTTA, ScrapType.INSULATION);
    typeMap.put(Blocks.TERRACOTTA, ScrapType.INSULATION);

    return typeMap;
  }

  public static void handleBlockBreak(
      final ServerLevel serverLevel, final BlockPos blockPos, final Block brokenBlock) {
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

  private static void playDropSound(
      final ServerLevel level, final BlockPos pos, final ScrapType scrapType) {
    if (!ScrapDropConfig.enableDropSounds || ScrapDropConfig.dropSoundVolume <= 0.0f) {
      return;
    }
    SoundEvent soundEvent = ScrapSoundType.forScrapType(scrapType).getSoundEvent();
    level.playSound(
        null,
        pos,
        soundEvent,
        SoundSource.BLOCKS,
        Math.min(1.0f, ScrapDropConfig.dropSoundVolume),
        0.8f + RANDOM.nextFloat() * 0.4f);
  }

  private static ScrapDropData getScrapDropDataForType(final ScrapType scrapType) {
    return switch (scrapType) {
      case BIO -> new ScrapDropData(ModItems.BIO_SCRAP.get(), ScrapDropConfig.bioScrapDropChance);
      case CERAMIC ->
          new ScrapDropData(ModItems.CERAMIC_SCRAP.get(), ScrapDropConfig.ceramicScrapDropChance);
      case COPPER ->
          new ScrapDropData(ModItems.COPPER_SCRAP.get(), ScrapDropConfig.copperScrapDropChance);
      case CRYSTAL ->
          new ScrapDropData(ModItems.CRYSTAL_SCRAP.get(), ScrapDropConfig.crystalScrapDropChance);
      case FASTENER ->
          new ScrapDropData(ModItems.FASTENER_SCRAP.get(), ScrapDropConfig.fastenerScrapDropChance);
      case FIBER ->
          new ScrapDropData(ModItems.FIBER_SCRAP.get(), ScrapDropConfig.fiberScrapDropChance);
      case GLASS ->
          new ScrapDropData(ModItems.GLASS_SCRAP.get(), ScrapDropConfig.glassScrapDropChance);
      case GOLD ->
          new ScrapDropData(ModItems.GOLD_SCRAP.get(), ScrapDropConfig.goldScrapDropChance);
      case IRON ->
          new ScrapDropData(ModItems.IRON_SCRAP.get(), ScrapDropConfig.ironScrapDropChance);
      case LUMINOUS ->
          new ScrapDropData(ModItems.LUMINOUS_SCRAP.get(), ScrapDropConfig.luminousScrapDropChance);
      case METAL ->
          new ScrapDropData(ModItems.METAL_SCRAP.get(), ScrapDropConfig.metalScrapDropChance);
      case MINERAL ->
          new ScrapDropData(ModItems.MINERAL_SCRAP.get(), ScrapDropConfig.mineralScrapDropChance);
      case PLASTIC ->
          new ScrapDropData(ModItems.PLASTIC_SCRAP.get(), ScrapDropConfig.plasticScrapDropChance);
      case RUBBER ->
          new ScrapDropData(ModItems.RUBBER_SCRAP.get(), ScrapDropConfig.rubberScrapDropChance);
      case TECH ->
          new ScrapDropData(ModItems.TECH_SCRAP.get(), ScrapDropConfig.techScrapDropChance);
      case WOOD ->
          new ScrapDropData(ModItems.WOOD_SCRAP.get(), ScrapDropConfig.woodScrapDropChance);

      // Additional tech scrap items using existing tech drop chance
      case CIRCUIT ->
          new ScrapDropData(ModItems.CIRCUIT_SCRAP.get(), ScrapDropConfig.techScrapDropChance);
      case COIL ->
          new ScrapDropData(ModItems.COIL_SCRAP.get(), ScrapDropConfig.techScrapDropChance);
      case CAPACITOR ->
          new ScrapDropData(ModItems.CAPACITOR_SCRAP.get(), ScrapDropConfig.techScrapDropChance);
      case ENERGY_CELL ->
          new ScrapDropData(ModItems.ENERGY_CELL_SCRAP.get(), ScrapDropConfig.techScrapDropChance);

      // Additional material and synthetic scrap items
      case ALLOY ->
          new ScrapDropData(ModItems.ALLOY_SCRAP.get(), ScrapDropConfig.metalScrapDropChance);
      case INSULATION ->
          new ScrapDropData(
              ModItems.INSULATION_SCRAP.get(), ScrapDropConfig.plasticScrapDropChance);
    };
  }
}
