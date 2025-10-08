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

import de.markusbordihn.scraptechworkshop.config.ScrapFishingConfig;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

public final class ScrapFishingLootTables {

  private static WeightedLootTable<Item> vanillaRodTable;
  private static WeightedLootTable<Item> scrapRodTable;
  private static WeightedLootTable<Item> magnetRodTable;

  private ScrapFishingLootTables() {}

  public static Item getVanillaRodLoot(RandomSource random) {
    if (vanillaRodTable == null) {
      initialize();
    }
    return vanillaRodTable.generate(random);
  }

  public static Item getScrapRodLoot(RandomSource random) {
    if (scrapRodTable == null) {
      initialize();
    }
    return scrapRodTable.generate(random);
  }

  public static Item getMagnetRodLoot(RandomSource random) {
    if (magnetRodTable == null) {
      initialize();
    }
    return magnetRodTable.generate(random);
  }

  public static void reset() {
    vanillaRodTable = null;
    scrapRodTable = null;
    magnetRodTable = null;
  }

  private static void initialize() {
    vanillaRodTable = createVanillaRodTable();
    scrapRodTable = createScrapRodTable();
    magnetRodTable = createMagnetRodTable();
  }

  private static WeightedLootTable<Item> createVanillaRodTable() {
    return new WeightedLootTable<>(
        new WeightedLootTable.Entry<>(
            ModItems.PLASTIC_SCRAP.get(), ScrapFishingConfig.vanillaPlasticWeight),
        new WeightedLootTable.Entry<>(
            ModItems.RUBBER_SCRAP.get(), ScrapFishingConfig.vanillaRubberWeight),
        new WeightedLootTable.Entry<>(
            ModItems.FASTENER_SCRAP.get(), ScrapFishingConfig.vanillaFastenerWeight),
        new WeightedLootTable.Entry<>(
            ModItems.METAL_SCRAP.get(), ScrapFishingConfig.vanillaMetalWeight),
        new WeightedLootTable.Entry<>(
            ModItems.IRON_SCRAP.get(), ScrapFishingConfig.vanillaIronWeight),
        new WeightedLootTable.Entry<>(
            ModItems.CIRCUIT_SCRAP.get(), ScrapFishingConfig.vanillaCircuitWeight),
        new WeightedLootTable.Entry<>(
            ModItems.GLASS_SCRAP.get(), ScrapFishingConfig.vanillaGlassWeight));
  }

  private static WeightedLootTable<Item> createScrapRodTable() {
    return new WeightedLootTable<>(
        new WeightedLootTable.Entry<>(
            ModItems.METAL_SCRAP.get(), ScrapFishingConfig.scrapRodMetalWeight),
        new WeightedLootTable.Entry<>(
            ModItems.IRON_SCRAP.get(), ScrapFishingConfig.scrapRodIronWeight),
        new WeightedLootTable.Entry<>(
            ModItems.COPPER_SCRAP.get(), ScrapFishingConfig.scrapRodCopperWeight),
        new WeightedLootTable.Entry<>(
            ModItems.CIRCUIT_SCRAP.get(), ScrapFishingConfig.scrapRodCircuitWeight),
        new WeightedLootTable.Entry<>(
            ModItems.CAPACITOR_SCRAP.get(), ScrapFishingConfig.scrapRodCapacitorWeight),
        new WeightedLootTable.Entry<>(
            ModItems.PLASTIC_SCRAP.get(), ScrapFishingConfig.scrapRodPlasticWeight),
        new WeightedLootTable.Entry<>(
            ModItems.RUBBER_SCRAP.get(), ScrapFishingConfig.scrapRodRubberWeight),
        new WeightedLootTable.Entry<>(
            ModItems.GLASS_SCRAP.get(), ScrapFishingConfig.scrapRodGlassWeight));
  }

  private static WeightedLootTable<Item> createMagnetRodTable() {
    return new WeightedLootTable<>(
        new WeightedLootTable.Entry<>(
            ModItems.CIRCUIT_SCRAP.get(), ScrapFishingConfig.magnetRodCircuitWeight),
        new WeightedLootTable.Entry<>(
            ModItems.CAPACITOR_SCRAP.get(), ScrapFishingConfig.magnetRodCapacitorWeight),
        new WeightedLootTable.Entry<>(
            ModItems.COIL_SCRAP.get(), ScrapFishingConfig.magnetRodCoilWeight),
        new WeightedLootTable.Entry<>(
            ModItems.METAL_SCRAP.get(), ScrapFishingConfig.magnetRodMetalWeight),
        new WeightedLootTable.Entry<>(
            ModItems.IRON_SCRAP.get(), ScrapFishingConfig.magnetRodIronWeight),
        new WeightedLootTable.Entry<>(
            ModItems.COPPER_SCRAP.get(), ScrapFishingConfig.magnetRodCopperWeight),
        new WeightedLootTable.Entry<>(
            ModItems.ALLOY_SCRAP.get(), ScrapFishingConfig.magnetRodAlloyWeight),
        new WeightedLootTable.Entry<>(
            ModItems.PLASTIC_SCRAP.get(), ScrapFishingConfig.magnetRodPlasticWeight));
  }
}
