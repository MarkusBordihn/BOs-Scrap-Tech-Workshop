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

import de.markusbordihn.scraptechworkshop.config.ScrapPileConfig;
import de.markusbordihn.scraptechworkshop.data.ScrapPileVariant;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ScrapLootTables {

  private static final Object METAL_LOCK = new Object();
  private static final Object TECH_LOCK = new Object();
  private static final Object MIXED_LOCK = new Object();
  private static volatile WeightedLootTable<Item> metalTable;
  private static volatile WeightedLootTable<Item> techTable;
  private static volatile WeightedLootTable<Item> mixedTable;

  private ScrapLootTables() {}

  public static ItemStack generateRandomScrap(ScrapPileVariant variant, RandomSource random) {
    WeightedLootTable<Item> table =
        switch (variant) {
          case METAL -> getMetalTable();
          case TECH -> getTechTable();
          default -> getMixedTable();
        };

    return new ItemStack(table.generate(random));
  }

  public static WeightedLootTable<Item> getMetalTable() {
    WeightedLootTable<Item> table = metalTable;
    if (table == null) {
      synchronized (METAL_LOCK) {
        table = metalTable;
        if (table == null) {
          metalTable = table = createMetalTable();
        }
      }
    }
    return table;
  }

  public static WeightedLootTable<Item> getTechTable() {
    WeightedLootTable<Item> table = techTable;
    if (table == null) {
      synchronized (TECH_LOCK) {
        table = techTable;
        if (table == null) {
          techTable = table = createTechTable();
        }
      }
    }
    return table;
  }

  public static WeightedLootTable<Item> getMixedTable() {
    WeightedLootTable<Item> table = mixedTable;
    if (table == null) {
      synchronized (MIXED_LOCK) {
        table = mixedTable;
        if (table == null) {
          mixedTable = table = createMixedTable();
        }
      }
    }
    return table;
  }

  public static void resetTables() {
    synchronized (METAL_LOCK) {
      metalTable = null;
    }
    synchronized (TECH_LOCK) {
      techTable = null;
    }
    synchronized (MIXED_LOCK) {
      mixedTable = null;
    }
  }

  private static WeightedLootTable<Item> createMetalTable() {
    return new WeightedLootTable<>(
        new WeightedLootTable.Entry<>(ModItems.METAL_SCRAP.get(), ScrapPileConfig.metalScrapWeight),
        new WeightedLootTable.Entry<>(ModItems.IRON_SCRAP.get(), ScrapPileConfig.ironScrapWeight),
        new WeightedLootTable.Entry<>(
            ModItems.COPPER_SCRAP.get(), ScrapPileConfig.copperScrapWeight),
        new WeightedLootTable.Entry<>(ModItems.GOLD_SCRAP.get(), ScrapPileConfig.goldScrapWeight));
  }

  private static WeightedLootTable<Item> createTechTable() {
    return new WeightedLootTable<>(
        new WeightedLootTable.Entry<>(ModItems.TECH_SCRAP.get(), ScrapPileConfig.techScrapWeight),
        new WeightedLootTable.Entry<>(
            ModItems.CIRCUIT_SCRAP.get(), ScrapPileConfig.circuitScrapWeight),
        new WeightedLootTable.Entry<>(ModItems.COIL_SCRAP.get(), ScrapPileConfig.coilScrapWeight),
        new WeightedLootTable.Entry<>(
            ModItems.CAPACITOR_SCRAP.get(), ScrapPileConfig.capacitorScrapWeight),
        new WeightedLootTable.Entry<>(
            ModItems.ENERGY_CELL_SCRAP.get(), ScrapPileConfig.energyCellScrapWeight));
  }

  private static WeightedLootTable<Item> createMixedTable() {
    return new WeightedLootTable<>(
        new WeightedLootTable.Entry<>(ModItems.METAL_SCRAP.get(), ScrapPileConfig.metalScrapWeight),
        new WeightedLootTable.Entry<>(ModItems.IRON_SCRAP.get(), ScrapPileConfig.ironScrapWeight),
        new WeightedLootTable.Entry<>(
            ModItems.COPPER_SCRAP.get(), ScrapPileConfig.copperScrapWeight),
        new WeightedLootTable.Entry<>(ModItems.TECH_SCRAP.get(), ScrapPileConfig.techScrapWeight),
        new WeightedLootTable.Entry<>(
            ModItems.PLASTIC_SCRAP.get(), ScrapPileConfig.plasticScrapWeight),
        new WeightedLootTable.Entry<>(ModItems.WOOD_SCRAP.get(), ScrapPileConfig.woodScrapWeight),
        new WeightedLootTable.Entry<>(
            ModItems.CRYSTAL_SCRAP.get(), ScrapPileConfig.crystalScrapWeight),
        new WeightedLootTable.Entry<>(
            ModItems.LUMINOUS_SCRAP.get(), ScrapPileConfig.luminousScrapWeight));
  }

  public static String getDebugInfo() {
    StringBuilder info = new StringBuilder("ScrapLootTables Debug Info:\n");

    if (metalTable != null) {
      info.append("Metal Table: ")
          .append(metalTable.size())
          .append(" entries, total weight: ")
          .append(metalTable.getTotalWeight())
          .append("\n");
    } else {
      info.append("Metal Table: Not initialized\n");
    }

    if (techTable != null) {
      info.append("Tech Table: ")
          .append(techTable.size())
          .append(" entries, total weight: ")
          .append(techTable.getTotalWeight())
          .append("\n");
    } else {
      info.append("Tech Table: Not initialized\n");
    }

    if (mixedTable != null) {
      info.append("Mixed Table: ")
          .append(mixedTable.size())
          .append(" entries, total weight: ")
          .append(mixedTable.getTotalWeight())
          .append("\n");
    } else {
      info.append("Mixed Table: Not initialized\n");
    }

    return info.toString();
  }
}
