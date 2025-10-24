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

package de.markusbordihn.scraptechworkshop.registry.item;

import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.item.ScrapItem;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.Item;

public class ScrapItemRegistry {

  // Map to store all scrap items by their type
  private static final Map<ScrapType, ScrapItem> SCRAP_ITEMS = new EnumMap<>(ScrapType.class);

  static {
    // Create all scrap items in a loop - ensures no type is forgotten
    for (ScrapType type : ScrapType.values()) {
      ScrapItem item = new ScrapItem(new Item.Properties(), type);
      SCRAP_ITEMS.put(type, item);
    }
  }

  private ScrapItemRegistry() {}

  public static ScrapItem getScrapItem(ScrapType type) {
    return SCRAP_ITEMS.get(type);
  }

  public static Map<ScrapType, ScrapItem> getAllScrapItems() {
    return SCRAP_ITEMS;
  }
}
