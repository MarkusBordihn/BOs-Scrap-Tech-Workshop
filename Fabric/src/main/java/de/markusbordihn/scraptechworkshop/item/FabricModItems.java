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

import de.markusbordihn.scraptechworkshop.item.scrap.CeramicScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.CrystalScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.FastenerScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.LuminousScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.MetalScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.PlasticScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.TechScrapItem;
import de.markusbordihn.scraptechworkshop.registry.item.scrap.ScrapItemRegistry;
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
    // Register metal scrap items
    ModItems.METAL_SCRAP =
        registerItem(MetalScrapItem.METAL_SCRAP_ID, () -> ScrapItemRegistry.METAL_SCRAP_ITEM);
    ModItems.GOLD_SCRAP =
        registerItem(MetalScrapItem.GOLD_SCRAP_ID, () -> ScrapItemRegistry.GOLD_SCRAP_ITEM);
    ModItems.IRON_SCRAP =
        registerItem(MetalScrapItem.IRON_SCRAP_ID, () -> ScrapItemRegistry.IRON_SCRAP_ITEM);
    ModItems.COPPER_SCRAP =
        registerItem(MetalScrapItem.COPPER_SCRAP_ID, () -> ScrapItemRegistry.COPPER_SCRAP_ITEM);

    // Register scrap items
    ModItems.CERAMIC_SCRAP =
        registerItem(CeramicScrapItem.CERAMIC_SCRAP_ID, () -> ScrapItemRegistry.CERAMIC_SCRAP_ITEM);
    ModItems.CRYSTAL_SCRAP =
        registerItem(CrystalScrapItem.CRYSTAL_SCRAP_ID, () -> ScrapItemRegistry.CRYSTAL_SCRAP_ITEM);
    ModItems.FASTENER_SCRAP =
        registerItem(
            FastenerScrapItem.FASTENER_SCRAP_ID, () -> ScrapItemRegistry.FASTENER_SCRAP_ITEM);
    ModItems.LUMINOUS_SCRAP =
        registerItem(
            LuminousScrapItem.LUMINOUS_SCRAP_ID, () -> ScrapItemRegistry.LUMINOUS_SCRAP_ITEM);
    ModItems.PLASTIC_SCRAP =
        registerItem(PlasticScrapItem.PLASTIC_SCRAP_ID, () -> ScrapItemRegistry.PLASTIC_SCRAP_ITEM);

    // Additional Tech Scrap Items
    ModItems.TECH_SCRAP =
        registerItem(TechScrapItem.TECH_SCRAP_ID, () -> ScrapItemRegistry.TECH_SCRAP_ITEM);
    ModItems.CIRCUIT_SCRAP =
        registerItem(TechScrapItem.CIRCUIT_SCRAP_ID, () -> ScrapItemRegistry.CIRCUIT_SCRAP_ITEM);
    ModItems.COIL_SCRAP =
        registerItem(TechScrapItem.COIL_SCRAP_ID, () -> ScrapItemRegistry.COIL_SCRAP_ITEM);
    ModItems.CAPACITOR_SCRAP =
        registerItem(
            TechScrapItem.CAPACITOR_SCRAP_ID, () -> ScrapItemRegistry.CAPACITOR_SCRAP_ITEM);
    ModItems.ENERGY_CELL_SCRAP =
        registerItem(
            TechScrapItem.ENERGY_CELL_SCRAP_ID, () -> ScrapItemRegistry.ENERGY_CELL_SCRAP_ITEM);

    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ModCreativeTabs.SCRAP_TECH_WORKSHOP_TAB.location(),
        ModCreativeTabs.createMainTab().build());
  }
}
