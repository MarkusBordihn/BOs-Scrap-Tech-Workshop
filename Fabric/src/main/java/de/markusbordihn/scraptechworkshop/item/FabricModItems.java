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

package de.markusbordihn.scraptechworkshop.item;

import de.markusbordihn.scraptechworkshop.item.scrap.MetalScrapItem;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class FabricModItems {

  @SuppressWarnings("unchecked")
  public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier) {
    T item =
        Registry.register(BuiltInRegistries.ITEM, ModItems.getItemId(name), itemSupplier.get());
    return () -> item;
  }

  public static void registerModItems() {
    ModItems.SCRAP_METAL =
        registerItem(
            MetalScrapItem.METAL_SCRAP_ID, () -> new MetalScrapItem(new Item.Properties()));

    ModItems.SCRAP_GOLD =
        registerItem(
            MetalScrapItem.GOLD_SCRAP_ID,
            () -> new MetalScrapItem(new Item.Properties(), MetalScrapItem.GOLD_SCRAP_ID));

    ModItems.SCRAP_IRON =
        registerItem(
            MetalScrapItem.IRON_SCRAP_ID,
            () -> new MetalScrapItem(new Item.Properties(), MetalScrapItem.IRON_SCRAP_ID));

    ModItems.SCRAP_COPPER =
        registerItem(
            MetalScrapItem.COPPER_SCRAP_ID,
            () -> new MetalScrapItem(new Item.Properties(), MetalScrapItem.COPPER_SCRAP_ID));

    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ModCreativeTabs.SCRAP_TECH_WORKSHOP_TAB.location(),
        ModCreativeTabs.createMainTab().build());
  }
}
