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

package de.markusbordihn.scraptechworkshop.loot;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Generates scrap items based on biome, location, and surrounding environment. Used by Collector
 * Station to generate realistic scrap collections.
 */
public class ScrapLootGenerator {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String LOG_PREFIX = "[ScrapLootGenerator]";
  private static final Random RANDOM = new Random();

  /**
   * Generate scrap items based on biome and location.
   *
   * @param level The level/world
   * @param pos Position of the collector station
   * @param biomeKey Cached biome key (e.g., "minecraft:plains")
   * @return List of ItemStacks containing generated scrap (1-3 items)
   */
  public static List<ItemStack> generateScrapForBiome(Level level, BlockPos pos, String biomeKey) {
    List<ItemStack> loot = new ArrayList<>();

    // Generate 1-3 scrap items per collection
    int itemCount = 1 + RANDOM.nextInt(3);

    for (int i = 0; i < itemCount; i++) {
      // 20% chance to get fishing junk instead of regular scrap
      ItemStack scrap =
          RANDOM.nextInt(100) < 20
              ? generateFishingJunk()
              : generateSingleScrap(level, pos, biomeKey);

      if (!scrap.isEmpty()) {
        loot.add(scrap);
      }
    }

    log.debug(
        "{} Generated {} scrap items for biome {} at {}", LOG_PREFIX, loot.size(), biomeKey, pos);

    return loot;
  }

  /** Generate fishing junk items (like you get when fishing). */
  private static ItemStack generateFishingJunk() {
    return switch (RANDOM.nextInt(15)) {
      case 0 -> new ItemStack(Items.LEATHER, 1);
      case 1 -> new ItemStack(Items.LEATHER_BOOTS, 1);
      case 2 -> new ItemStack(Items.ROTTEN_FLESH, 1 + RANDOM.nextInt(2));
      case 3 -> new ItemStack(Items.STICK, 1 + RANDOM.nextInt(3));
      case 4 -> new ItemStack(Items.STRING, 1 + RANDOM.nextInt(3));
      case 5 -> new ItemStack(Items.BOWL, 1);
      case 6 -> new ItemStack(Items.GLASS_BOTTLE, 1 + RANDOM.nextInt(2));
      case 7 -> new ItemStack(Items.BONE, 1 + RANDOM.nextInt(2));
      case 8 -> new ItemStack(Items.TRIPWIRE_HOOK, 1);
      case 9 -> new ItemStack(Items.INK_SAC, 1 + RANDOM.nextInt(2));
      case 10 -> new ItemStack(Items.BAMBOO, 1 + RANDOM.nextInt(4));
      case 11 -> new ItemStack(Items.COCOA_BEANS, 1 + RANDOM.nextInt(2));
      case 12 -> new ItemStack(Items.LILY_PAD, 1);
      case 13 -> new ItemStack(Items.FEATHER, 1 + RANDOM.nextInt(2));
      default -> new ItemStack(Items.STICK, 1);
    };
  }

  /** Generate a single scrap item based on biome type. */
  private static ItemStack generateSingleScrap(Level level, BlockPos pos, String biomeKey) {
    Biome biome = level.getBiome(pos).value();

    // Temperature-based loot
    if (biome.coldEnoughToSnow(pos)) {
      return generateColdBiomeScrap();
    } else if (biome.getBaseTemperature() > 1.0f) {
      return generateHotBiomeScrap();
    }

    // Biome-specific loot
    if (biomeKey.contains("ocean") || biomeKey.contains("river") || biomeKey.contains("beach")) {
      return generateWaterBiomeScrap();
    } else if (biomeKey.contains("desert")) {
      return generateDesertScrap();
    } else if (biomeKey.contains("jungle")) {
      return generateJungleScrap();
    } else if (biomeKey.contains("forest") || biomeKey.contains("taiga")) {
      return generateForestScrap();
    } else if (biomeKey.contains("mountain") || biomeKey.contains("hill")) {
      return generateMountainScrap();
    } else if (biomeKey.contains("swamp")) {
      return generateSwampScrap();
    } else if (biomeKey.contains("nether")) {
      return generateNetherScrap();
    } else if (biomeKey.contains("end")) {
      return generateEndScrap();
    }

    // Default: Plains/generic biomes
    return generateGenericScrap();
  }

  // Cold biomes: More metal scrap (preserved in cold)
  private static ItemStack generateColdBiomeScrap() {
    return switch (RANDOM.nextInt(5)) {
      case 0 -> new ItemStack(ModItems.METAL_SCRAP.get(), 1 + RANDOM.nextInt(3));
      case 1 -> new ItemStack(ModItems.TECH_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 2 -> new ItemStack(ModItems.IRON_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 3 -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1 + RANDOM.nextInt(2));
      default -> new ItemStack(ModItems.METAL_SCRAP.get(), 1);
    };
  }

  // Hot biomes: More degraded/mixed scrap
  private static ItemStack generateHotBiomeScrap() {
    return switch (RANDOM.nextInt(5)) {
      case 0 -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1 + RANDOM.nextInt(3));
      case 1 -> new ItemStack(ModItems.METAL_SCRAP.get(), 1);
      case 2 -> new ItemStack(ModItems.COPPER_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 3 -> new ItemStack(ModItems.TECH_SCRAP.get(), 1);
      default -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1 + RANDOM.nextInt(2));
    };
  }

  // Water biomes: Rusty metal, degraded tech
  private static ItemStack generateWaterBiomeScrap() {
    return switch (RANDOM.nextInt(6)) {
      case 0 -> new ItemStack(ModItems.METAL_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 1 -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1 + RANDOM.nextInt(3));
      case 2 -> new ItemStack(ModItems.COPPER_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 3 -> new ItemStack(ModItems.TECH_SCRAP.get(), 1);
      case 4 -> new ItemStack(ModItems.FASTENER_SCRAP.get(), 2 + RANDOM.nextInt(4));
      default -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1);
    };
  }

  // Desert: Tech scrap (abandoned tech, preserved by dryness)
  private static ItemStack generateDesertScrap() {
    return switch (RANDOM.nextInt(5)) {
      case 0 -> new ItemStack(ModItems.TECH_SCRAP.get(), 1 + RANDOM.nextInt(3));
      case 1 -> new ItemStack(ModItems.CIRCUIT_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 2 -> new ItemStack(ModItems.GOLD_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 3 -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1);
      default -> new ItemStack(ModItems.TECH_SCRAP.get(), 1 + RANDOM.nextInt(2));
    };
  }

  // Jungle: Overgrown mixed scrap
  private static ItemStack generateJungleScrap() {
    return switch (RANDOM.nextInt(5)) {
      case 0 -> new ItemStack(ModItems.BIO_SCRAP.get(), 2 + RANDOM.nextInt(3));
      case 1 -> new ItemStack(ModItems.WOOD_SCRAP.get(), 1 + RANDOM.nextInt(3));
      case 2 -> new ItemStack(ModItems.FIBER_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 3 -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1);
      default -> new ItemStack(ModItems.BIO_SCRAP.get(), 1 + RANDOM.nextInt(2));
    };
  }

  // Forest: Balanced mix
  private static ItemStack generateForestScrap() {
    return switch (RANDOM.nextInt(6)) {
      case 0 -> new ItemStack(ModItems.METAL_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 1 -> new ItemStack(ModItems.TECH_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 2 -> new ItemStack(ModItems.WOOD_SCRAP.get(), 1 + RANDOM.nextInt(3));
      case 3 -> new ItemStack(ModItems.IRON_SCRAP.get(), 1);
      case 4 -> new ItemStack(ModItems.COPPER_SCRAP.get(), 1);
      default -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1);
    };
  }

  // Mountain: More metal ores and metal scrap
  private static ItemStack generateMountainScrap() {
    return switch (RANDOM.nextInt(6)) {
      case 0 -> new ItemStack(ModItems.METAL_SCRAP.get(), 2 + RANDOM.nextInt(3));
      case 1 -> new ItemStack(ModItems.IRON_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 2 -> new ItemStack(ModItems.COPPER_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 3 -> new ItemStack(ModItems.MINERAL_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 4 -> new ItemStack(ModItems.GOLD_SCRAP.get(), 1);
      default -> new ItemStack(ModItems.METAL_SCRAP.get(), 1 + RANDOM.nextInt(2));
    };
  }

  // Swamp: Very degraded, mostly mixed scrap
  private static ItemStack generateSwampScrap() {
    return switch (RANDOM.nextInt(5)) {
      case 0 -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 2 + RANDOM.nextInt(4));
      case 1 -> new ItemStack(ModItems.BIO_SCRAP.get(), 1 + RANDOM.nextInt(3));
      case 2 -> new ItemStack(ModItems.FASTENER_SCRAP.get(), 1 + RANDOM.nextInt(4));
      case 3 -> new ItemStack(ModItems.RUBBER_SCRAP.get(), 1 + RANDOM.nextInt(2));
      default -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1 + RANDOM.nextInt(3));
    };
  }

  // Nether: Special tech scrap (heat-resistant tech)
  private static ItemStack generateNetherScrap() {
    return switch (RANDOM.nextInt(5)) {
      case 0 -> new ItemStack(ModItems.TECH_SCRAP.get(), 2 + RANDOM.nextInt(3));
      case 1 -> new ItemStack(ModItems.GOLD_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 2 -> new ItemStack(ModItems.COIL_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 3 -> new ItemStack(Items.NETHERITE_SCRAP, 1);
      default -> new ItemStack(ModItems.TECH_SCRAP.get(), 1 + RANDOM.nextInt(2));
    };
  }

  // End: Rare tech scrap
  private static ItemStack generateEndScrap() {
    return switch (RANDOM.nextInt(5)) {
      case 0 -> new ItemStack(ModItems.TECH_SCRAP.get(), 2 + RANDOM.nextInt(4));
      case 1 -> new ItemStack(ModItems.CRYSTAL_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 2 -> new ItemStack(ModItems.LUMINOUS_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 3 -> new ItemStack(ModItems.ENERGY_CELL_SCRAP.get(), 1);
      default -> new ItemStack(ModItems.TECH_SCRAP.get(), 1 + RANDOM.nextInt(3));
    };
  }

  // Generic/Plains: Balanced common scrap
  private static ItemStack generateGenericScrap() {
    return switch (RANDOM.nextInt(6)) {
      case 0 -> new ItemStack(ModItems.METAL_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 1 -> new ItemStack(ModItems.TECH_SCRAP.get(), 1 + RANDOM.nextInt(2));
      case 2 -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1 + RANDOM.nextInt(3));
      case 3 -> new ItemStack(ModItems.FASTENER_SCRAP.get(), 1 + RANDOM.nextInt(3));
      case 4 -> new ItemStack(ModItems.WOOD_SCRAP.get(), 1 + RANDOM.nextInt(2));
      default -> new ItemStack(ModItems.ALLOY_SCRAP.get(), 1 + RANDOM.nextInt(2));
    };
  }
}
