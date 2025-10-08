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

import de.markusbordihn.scraptechworkshop.item.component.CircuitBoardItem;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellBlockItem;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloPadItem;
import de.markusbordihn.scraptechworkshop.item.scrap.AlloyScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.BioScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.CeramicScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.CrystalScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.FastenerScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.FiberScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.GlassScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.InsulationScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.LuminousScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.MetalScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.MineralScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.PlasticScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.RubberScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.TechScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.WoodScrapItem;
import de.markusbordihn.scraptechworkshop.registry.item.hololog.HoloLogItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.hololog.HoloLogRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.scrap.ScrapItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.tools.ToolItemRegistry;
import de.markusbordihn.scraptechworkshop.tabs.ModCreativeTabs;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class FabricModItems {

  private FabricModItems() {}

  public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier) {
    T item =
        Registry.register(BuiltInRegistries.ITEM, ModItems.getItemId(name), itemSupplier.get());
    return () -> item;
  }

  public static void registerModItems() {
    // Register metal scrap items
    ModItems.ALLOY_SCRAP =
        registerItem(AlloyScrapItem.ALLOY_SCRAP_ID, () -> ScrapItemRegistry.ALLOY_SCRAP_ITEM);
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
    ModItems.INSULATION_SCRAP =
        registerItem(
            InsulationScrapItem.INSULATION_SCRAP_ID, () -> ScrapItemRegistry.INSULATION_SCRAP_ITEM);
    ModItems.LUMINOUS_SCRAP =
        registerItem(
            LuminousScrapItem.LUMINOUS_SCRAP_ID, () -> ScrapItemRegistry.LUMINOUS_SCRAP_ITEM);
    ModItems.PLASTIC_SCRAP =
        registerItem(PlasticScrapItem.PLASTIC_SCRAP_ID, () -> ScrapItemRegistry.PLASTIC_SCRAP_ITEM);

    // Additional Material Scrap Items
    ModItems.MINERAL_SCRAP =
        registerItem(MineralScrapItem.MINERAL_SCRAP_ID, () -> ScrapItemRegistry.MINERAL_SCRAP_ITEM);
    ModItems.WOOD_SCRAP =
        registerItem(WoodScrapItem.WOOD_SCRAP_ID, () -> ScrapItemRegistry.WOOD_SCRAP_ITEM);
    ModItems.RUBBER_SCRAP =
        registerItem(RubberScrapItem.RUBBER_SCRAP_ID, () -> ScrapItemRegistry.RUBBER_SCRAP_ITEM);
    ModItems.GLASS_SCRAP =
        registerItem(GlassScrapItem.GLASS_SCRAP_ID, () -> ScrapItemRegistry.GLASS_SCRAP_ITEM);

    // Organic and Textile Scrap Items
    ModItems.FIBER_SCRAP =
        registerItem(FiberScrapItem.FIBER_SCRAP_ID, () -> ScrapItemRegistry.FIBER_SCRAP_ITEM);
    ModItems.BIO_SCRAP =
        registerItem(BioScrapItem.BIO_SCRAP_ID, () -> ScrapItemRegistry.BIO_SCRAP_ITEM);

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

    // Tool Items
    ModItems.SCRAP_MULTITOOL =
        registerItem("scrap_multitool", () -> ToolItemRegistry.SCRAP_MULTITOOL_ITEM);

    // Fishing Rod Items
    ModItems.SCRAP_FISHING_ROD =
        registerItem("scrap_fishing_rod", () -> ToolItemRegistry.SCRAP_FISHING_ROD_ITEM);
    ModItems.MAGNET_FISHING_ROD =
        registerItem("magnet_fishing_rod", () -> ToolItemRegistry.MAGNET_FISHING_ROD_ITEM);

    // Component Items
    ModItems.ENERGY_CELL = registerItem(EnergyCellItem.ID, () -> ToolItemRegistry.ENERGY_CELL_ITEM);
    ModItems.SLIGHTLY_DAMAGED_ENERGY_CELL =
        registerItem(
            "slightly_damaged_energy_cell",
            () -> ToolItemRegistry.SLIGHTLY_DAMAGED_ENERGY_CELL_ITEM);
    ModItems.DAMAGED_ENERGY_CELL =
        registerItem("damaged_energy_cell", () -> ToolItemRegistry.DAMAGED_ENERGY_CELL_ITEM);
    ModItems.EMPTY_ENERGY_CELL =
        registerItem("empty_energy_cell", () -> ToolItemRegistry.EMPTY_ENERGY_CELL_ITEM);
    ModItems.ENERGY_CELL_BLOCK =
        registerItem(EnergyCellBlockItem.ID, () -> ToolItemRegistry.ENERGY_CELL_BLOCK_ITEM);
    ModItems.CIRCUIT_BOARD =
        registerItem(CircuitBoardItem.ID, () -> ToolItemRegistry.CIRCUIT_BOARD_ITEM);

    // Holo Pad Items
    for (Map.Entry<String, ResourceLocation> entry :
        HoloLogRegistry.getHoloPadRegistry().entrySet()) {
      if (ModItems.HOLO_PAD == null) {
        ModItems.HOLO_PAD =
            registerItem(
                HoloPadItem.ID_PREFIX + entry.getKey(),
                () ->
                    HoloLogItemRegistry.createHoloPadSupplier(entry.getKey(), entry.getValue())
                        .get());
      } else {
        registerItem(
            HoloPadItem.ID_PREFIX + entry.getKey(),
            () ->
                HoloLogItemRegistry.createHoloPadSupplier(entry.getKey(), entry.getValue()).get());
      }
    }
  }

  public static void registerCreativeModeTabs() {
    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ModCreativeTabs.SCRAP_TECH_WORKSHOP_TAB.location(),
        ModCreativeTabs.createMainTab().build());

    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ModCreativeTabs.HOLO_CUBE_TAB.location(),
        ModCreativeTabs.createHoloCubeTab().build());

    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ModCreativeTabs.HOLO_PAD_TAB.location(),
        ModCreativeTabs.createHoloPadTab().build());
  }
}
