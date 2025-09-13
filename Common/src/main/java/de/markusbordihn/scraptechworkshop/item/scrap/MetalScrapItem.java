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

package de.markusbordihn.scraptechworkshop.item.scrap;

import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.item.ScrapItem;
import net.minecraft.world.item.Rarity;

public class MetalScrapItem extends ScrapItem {

  public static final String METAL_SCRAP_ID = "metal_scrap";
  public static final String GOLD_SCRAP_ID = "gold_scrap";
  public static final String IRON_SCRAP_ID = "iron_scrap";
  public static final String COPPER_SCRAP_ID = "copper_scrap";

  public MetalScrapItem(Properties properties, ScrapType scrapType) {
    super(properties, scrapType);
  }

  @Override
  public int getScrapValue() {
    return switch (scrapType) {
      case GOLD -> 25;
      case IRON -> 15;
      case COPPER -> 12;
      case METAL -> 10;
      default -> 10;
    };
  }

  @Override
  public Rarity getScrapRarity() {
    return switch (scrapType) {
      case GOLD -> Rarity.UNCOMMON;
      default -> Rarity.COMMON;
    };
  }

  @Override
  public float getEfficiencyBonus() {
    return 1.0f; // Standard efficiency for metals
  }
}
