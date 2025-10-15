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

package de.markusbordihn.scraptechworkshop.registry.item.scrapbox;

import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.item.scrapbox.ScrapBoxBlockItem;
import de.markusbordihn.scraptechworkshop.registry.block.ScrapBoxBlockRegistry;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.Item;

public class ScrapBoxBlockItemRegistry {

  public static final ScrapBoxBlockItem ALLOY_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem BIO_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem CAPACITOR_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem CERAMIC_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem CIRCUIT_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem COIL_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem COPPER_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem CRYSTAL_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem ENERGY_CELL_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem FASTENER_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem FIBER_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem GLASS_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem GOLD_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem INSULATION_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem IRON_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem LUMINOUS_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem METAL_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem MINERAL_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem PLASTIC_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem RUBBER_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem TECH_SCRAP_BOX_ITEM;
  public static final ScrapBoxBlockItem WOOD_SCRAP_BOX_ITEM;
  private static final Map<ScrapType, ScrapBoxBlockItem> SCRAP_BOX_ITEMS =
      new EnumMap<>(ScrapType.class);

  static {
    for (ScrapType type : ScrapType.values()) {
      ScrapBoxBlockItem item =
          new ScrapBoxBlockItem(
              ScrapBoxBlockRegistry.getScrapBox(type), new Item.Properties(), type);
      SCRAP_BOX_ITEMS.put(type, item);
    }

    ALLOY_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.ALLOY);
    BIO_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.BIO);
    CAPACITOR_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.CAPACITOR);
    CERAMIC_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.CERAMIC);
    CIRCUIT_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.CIRCUIT);
    COIL_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.COIL);
    COPPER_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.COPPER);
    CRYSTAL_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.CRYSTAL);
    ENERGY_CELL_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.ENERGY_CELL);
    FASTENER_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.FASTENER);
    FIBER_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.FIBER);
    GLASS_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.GLASS);
    GOLD_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.GOLD);
    INSULATION_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.INSULATION);
    IRON_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.IRON);
    LUMINOUS_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.LUMINOUS);
    METAL_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.METAL);
    MINERAL_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.MINERAL);
    PLASTIC_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.PLASTIC);
    RUBBER_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.RUBBER);
    TECH_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.TECH);
    WOOD_SCRAP_BOX_ITEM = SCRAP_BOX_ITEMS.get(ScrapType.WOOD);
  }

  private ScrapBoxBlockItemRegistry() {}

  public static ScrapBoxBlockItem getScrapBoxItem(final ScrapType scrapType) {
    return SCRAP_BOX_ITEMS.get(scrapType);
  }
}
