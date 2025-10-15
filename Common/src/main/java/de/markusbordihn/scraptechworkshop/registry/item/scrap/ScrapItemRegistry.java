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

package de.markusbordihn.scraptechworkshop.registry.item.scrap;

import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.item.ScrapItem;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.Item;

public class ScrapItemRegistry {

  // Public fields for backwards compatibility
  public static final ScrapItem ALLOY_SCRAP_ITEM;
  public static final ScrapItem BIO_SCRAP_ITEM;
  public static final ScrapItem CAPACITOR_SCRAP_ITEM;
  public static final ScrapItem CERAMIC_SCRAP_ITEM;
  public static final ScrapItem CIRCUIT_SCRAP_ITEM;
  public static final ScrapItem COIL_SCRAP_ITEM;
  public static final ScrapItem COPPER_SCRAP_ITEM;
  public static final ScrapItem CRYSTAL_SCRAP_ITEM;
  public static final ScrapItem ENERGY_CELL_SCRAP_ITEM;
  public static final ScrapItem FASTENER_SCRAP_ITEM;
  public static final ScrapItem FIBER_SCRAP_ITEM;
  public static final ScrapItem GLASS_SCRAP_ITEM;
  public static final ScrapItem GOLD_SCRAP_ITEM;
  public static final ScrapItem INSULATION_SCRAP_ITEM;
  public static final ScrapItem IRON_SCRAP_ITEM;
  public static final ScrapItem LUMINOUS_SCRAP_ITEM;
  public static final ScrapItem METAL_SCRAP_ITEM;
  public static final ScrapItem MINERAL_SCRAP_ITEM;
  public static final ScrapItem PLASTIC_SCRAP_ITEM;
  public static final ScrapItem RUBBER_SCRAP_ITEM;
  public static final ScrapItem TECH_SCRAP_ITEM;
  public static final ScrapItem WOOD_SCRAP_ITEM;
  // Map to store all scrap items by their type
  private static final Map<ScrapType, ScrapItem> SCRAP_ITEMS = new EnumMap<>(ScrapType.class);

  static {
    // Create all scrap items in a loop - ensures no type is forgotten
    for (ScrapType type : ScrapType.values()) {
      ScrapItem item = new ScrapItem(new Item.Properties(), type);
      SCRAP_ITEMS.put(type, item);
    }

    // Assign to public fields for backwards compatibility
    ALLOY_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.ALLOY);
    BIO_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.BIO);
    CAPACITOR_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.CAPACITOR);
    CERAMIC_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.CERAMIC);
    CIRCUIT_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.CIRCUIT);
    COIL_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.COIL);
    COPPER_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.COPPER);
    CRYSTAL_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.CRYSTAL);
    ENERGY_CELL_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.ENERGY_CELL);
    FASTENER_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.FASTENER);
    FIBER_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.FIBER);
    GLASS_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.GLASS);
    GOLD_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.GOLD);
    INSULATION_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.INSULATION);
    IRON_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.IRON);
    LUMINOUS_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.LUMINOUS);
    METAL_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.METAL);
    MINERAL_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.MINERAL);
    PLASTIC_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.PLASTIC);
    RUBBER_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.RUBBER);
    TECH_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.TECH);
    WOOD_SCRAP_ITEM = SCRAP_ITEMS.get(ScrapType.WOOD);
  }

  private ScrapItemRegistry() {}

  public static ScrapItem getScrapItem(ScrapType type) {
    return SCRAP_ITEMS.get(type);
  }

  public static Map<ScrapType, ScrapItem> getAllScrapItems() {
    return Map.copyOf(SCRAP_ITEMS);
  }
}
