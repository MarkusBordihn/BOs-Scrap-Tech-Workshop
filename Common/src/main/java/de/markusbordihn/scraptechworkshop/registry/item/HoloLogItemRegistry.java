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

import de.markusbordihn.scraptechworkshop.item.hololog.HoloCubeItem;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloPadItem;
import de.markusbordihn.scraptechworkshop.registry.block.hololog.HoloLogBlockRegistry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class HoloLogItemRegistry {

  private static final Map<String, HoloPadItem> HOLO_PAD_ITEMS = new LinkedHashMap<>();
  private static final Map<String, HoloCubeItem> HOLO_CUBE_ITEMS = new LinkedHashMap<>();
  private static final Map<ResourceLocation, HoloCubeItem> HOLO_LOG_TO_CUBE_ITEM =
      new LinkedHashMap<>();

  private HoloLogItemRegistry() {}

  public static Supplier<HoloPadItem> createHoloPadSupplier(String id, ResourceLocation holoLogId) {
    return () -> {
      HoloPadItem item = new HoloPadItem(holoLogId, new Item.Properties());
      HOLO_PAD_ITEMS.put(id, item);
      return item;
    };
  }

  public static Supplier<HoloCubeItem> createHoloCubeSupplier(
      String id, ResourceLocation holoLogId) {
    return () -> {
      HoloCubeItem item =
          new HoloCubeItem(holoLogId, HoloLogBlockRegistry.HOLO_CUBE_BLOCK, new Item.Properties());
      HOLO_CUBE_ITEMS.put(id, item);
      HOLO_LOG_TO_CUBE_ITEM.put(holoLogId, item);
      return item;
    };
  }

  public static Map<String, HoloPadItem> getHoloPadItems() {
    return HOLO_PAD_ITEMS;
  }

  public static Map<String, HoloCubeItem> getHoloCubeItems() {
    return HOLO_CUBE_ITEMS;
  }

  public static HoloCubeItem getHoloCubeItemByHoloLog(ResourceLocation holoLogId) {
    return HOLO_LOG_TO_CUBE_ITEM.get(holoLogId);
  }
}
