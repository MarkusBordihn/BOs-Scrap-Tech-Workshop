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

import de.markusbordihn.scraptechworkshop.Constants;
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
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModItems {

  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
      DeferredRegister.create(
          net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

  // Metal Scrap Items
  public static final RegistryObject<Item> SCRAP_ALLOY =
      ITEMS.register(AlloyScrapItem.ALLOY_SCRAP_ID, () -> ScrapItemRegistry.ALLOY_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_METAL =
      ITEMS.register(MetalScrapItem.METAL_SCRAP_ID, () -> ScrapItemRegistry.METAL_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_GOLD =
      ITEMS.register(MetalScrapItem.GOLD_SCRAP_ID, () -> ScrapItemRegistry.GOLD_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_IRON =
      ITEMS.register(MetalScrapItem.IRON_SCRAP_ID, () -> ScrapItemRegistry.IRON_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_COPPER =
      ITEMS.register(MetalScrapItem.COPPER_SCRAP_ID, () -> ScrapItemRegistry.COPPER_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_CERAMIC =
      ITEMS.register(CeramicScrapItem.CERAMIC_SCRAP_ID, () -> ScrapItemRegistry.CERAMIC_SCRAP_ITEM);

  // Other Scrap Items
  public static final RegistryObject<Item> SCRAP_CRYSTAL =
      ITEMS.register(CrystalScrapItem.CRYSTAL_SCRAP_ID, () -> ScrapItemRegistry.CRYSTAL_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_FASTENER =
      ITEMS.register(
          FastenerScrapItem.FASTENER_SCRAP_ID, () -> ScrapItemRegistry.FASTENER_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_INSULATION =
      ITEMS.register(
          InsulationScrapItem.INSULATION_SCRAP_ID, () -> ScrapItemRegistry.INSULATION_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_LUMINOUS =
      ITEMS.register(
          LuminousScrapItem.LUMINOUS_SCRAP_ID, () -> ScrapItemRegistry.LUMINOUS_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_PLASTIC =
      ITEMS.register(PlasticScrapItem.PLASTIC_SCRAP_ID, () -> ScrapItemRegistry.PLASTIC_SCRAP_ITEM);

  // Additional Material Scrap Items
  public static final RegistryObject<Item> SCRAP_MINERAL =
      ITEMS.register(MineralScrapItem.MINERAL_SCRAP_ID, () -> ScrapItemRegistry.MINERAL_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_WOOD =
      ITEMS.register(WoodScrapItem.WOOD_SCRAP_ID, () -> ScrapItemRegistry.WOOD_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_RUBBER =
      ITEMS.register(RubberScrapItem.RUBBER_SCRAP_ID, () -> ScrapItemRegistry.RUBBER_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_GLASS =
      ITEMS.register(GlassScrapItem.GLASS_SCRAP_ID, () -> ScrapItemRegistry.GLASS_SCRAP_ITEM);

  // Organic and Textile Scrap Items
  public static final RegistryObject<Item> SCRAP_FIBER =
      ITEMS.register(FiberScrapItem.FIBER_SCRAP_ID, () -> ScrapItemRegistry.FIBER_SCRAP_ITEM);
  public static final RegistryObject<Item> SCRAP_BIO =
      ITEMS.register(BioScrapItem.BIO_SCRAP_ID, () -> ScrapItemRegistry.BIO_SCRAP_ITEM);

  // Tech Scrap Items
  public static final RegistryObject<Item> TECH_SCRAP =
      ITEMS.register(TechScrapItem.TECH_SCRAP_ID, () -> ScrapItemRegistry.TECH_SCRAP_ITEM);
  public static final RegistryObject<Item> CIRCUIT_SCRAP =
      ITEMS.register(TechScrapItem.CIRCUIT_SCRAP_ID, () -> ScrapItemRegistry.CIRCUIT_SCRAP_ITEM);
  public static final RegistryObject<Item> COIL_SCRAP =
      ITEMS.register(TechScrapItem.COIL_SCRAP_ID, () -> ScrapItemRegistry.COIL_SCRAP_ITEM);
  public static final RegistryObject<Item> CAPACITOR_SCRAP =
      ITEMS.register(
          TechScrapItem.CAPACITOR_SCRAP_ID, () -> ScrapItemRegistry.CAPACITOR_SCRAP_ITEM);
  public static final RegistryObject<Item> ENERGY_CELL_SCRAP =
      ITEMS.register(
          TechScrapItem.ENERGY_CELL_SCRAP_ID, () -> ScrapItemRegistry.ENERGY_CELL_SCRAP_ITEM);

  // Tool Items
  public static final RegistryObject<Item> SCRAP_MULTITOOL =
      ITEMS.register("scrap_multitool", () -> ToolItemRegistry.SCRAP_MULTITOOL_ITEM);

  // Fishing Rod Items
  public static final RegistryObject<Item> SCRAP_FISHING_ROD =
      ITEMS.register("scrap_fishing_rod", () -> ToolItemRegistry.SCRAP_FISHING_ROD_ITEM);
  public static final RegistryObject<Item> MAGNET_FISHING_ROD =
      ITEMS.register("magnet_fishing_rod", () -> ToolItemRegistry.MAGNET_FISHING_ROD_ITEM);

  // Component Items
  public static final RegistryObject<Item> ENERGY_CELL =
      ITEMS.register(EnergyCellItem.ID, () -> ToolItemRegistry.ENERGY_CELL_ITEM);
  public static final RegistryObject<Item> SLIGHTLY_DAMAGED_ENERGY_CELL =
      ITEMS.register(
          "slightly_damaged_energy_cell", () -> ToolItemRegistry.SLIGHTLY_DAMAGED_ENERGY_CELL_ITEM);
  public static final RegistryObject<Item> DAMAGED_ENERGY_CELL =
      ITEMS.register("damaged_energy_cell", () -> ToolItemRegistry.DAMAGED_ENERGY_CELL_ITEM);
  public static final RegistryObject<Item> EMPTY_ENERGY_CELL =
      ITEMS.register("empty_energy_cell", () -> ToolItemRegistry.EMPTY_ENERGY_CELL_ITEM);
  public static final RegistryObject<Item> ENERGY_CELL_BLOCK =
      ITEMS.register(EnergyCellBlockItem.ID, () -> ToolItemRegistry.ENERGY_CELL_BLOCK_ITEM);
  public static final RegistryObject<Item> CIRCUIT_BOARD =
      ITEMS.register(CircuitBoardItem.ID, () -> ToolItemRegistry.CIRCUIT_BOARD_ITEM);
  // Creative Mode Tabs
  public static final RegistryObject<CreativeModeTab> SCRAP_TECH_WORKSHOP_TAB =
      CREATIVE_MODE_TABS.register("scrap_tech_workshop", ModCreativeTabs.createMainTab()::build);
  public static final RegistryObject<CreativeModeTab> HOLO_CUBE_TAB =
      CREATIVE_MODE_TABS.register("holo_cubes", ModCreativeTabs.createHoloCubeTab()::build);
  public static final RegistryObject<CreativeModeTab> HOLO_PAD_TAB =
      CREATIVE_MODE_TABS.register("holo_pads", ModCreativeTabs.createHoloPadTab()::build);
  private static final Map<String, RegistryObject<Item>> HOLO_PAD_REGISTRY_OBJECTS =
      new LinkedHashMap<>();

  static {
    for (Map.Entry<String, ResourceLocation> entry :
        HoloLogRegistry.getHoloPadRegistry().entrySet()) {
      String itemId = entry.getKey();
      ResourceLocation holoLogId = entry.getValue();
      RegistryObject<Item> registryObject =
          ITEMS.register(
              HoloPadItem.ID_PREFIX + itemId,
              HoloLogItemRegistry.createHoloPadSupplier(itemId, holoLogId));
      HOLO_PAD_REGISTRY_OBJECTS.put(itemId, registryObject);
    }
  }

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
    CREATIVE_MODE_TABS.register(eventBus);

    // Basic Scrap Items
    ModItems.BIO_SCRAP = SCRAP_BIO;
    ModItems.CERAMIC_SCRAP = SCRAP_CERAMIC;
    ModItems.COPPER_SCRAP = SCRAP_COPPER;
    ModItems.CRYSTAL_SCRAP = SCRAP_CRYSTAL;
    ModItems.FASTENER_SCRAP = SCRAP_FASTENER;
    ModItems.FIBER_SCRAP = SCRAP_FIBER;
    ModItems.GLASS_SCRAP = SCRAP_GLASS;
    ModItems.GOLD_SCRAP = SCRAP_GOLD;
    ModItems.IRON_SCRAP = SCRAP_IRON;
    ModItems.LUMINOUS_SCRAP = SCRAP_LUMINOUS;
    ModItems.METAL_SCRAP = SCRAP_METAL;
    ModItems.MINERAL_SCRAP = SCRAP_MINERAL;
    ModItems.PLASTIC_SCRAP = SCRAP_PLASTIC;
    ModItems.RUBBER_SCRAP = SCRAP_RUBBER;
    ModItems.WOOD_SCRAP = SCRAP_WOOD;

    // Tech Scrap Items
    ModItems.CAPACITOR_SCRAP = CAPACITOR_SCRAP;
    ModItems.CIRCUIT_SCRAP = CIRCUIT_SCRAP;
    ModItems.COIL_SCRAP = COIL_SCRAP;
    ModItems.ENERGY_CELL_SCRAP = ENERGY_CELL_SCRAP;
    ModItems.TECH_SCRAP = TECH_SCRAP;

    // Additional Material and Synthetic Scrap Items
    ModItems.ALLOY_SCRAP = SCRAP_ALLOY;
    ModItems.INSULATION_SCRAP = SCRAP_INSULATION;

    // Tool Items
    ModItems.SCRAP_MULTITOOL = SCRAP_MULTITOOL;

    // Fishing Rod Items
    ModItems.SCRAP_FISHING_ROD = SCRAP_FISHING_ROD;
    ModItems.MAGNET_FISHING_ROD = MAGNET_FISHING_ROD;

    // Component Items
    ModItems.ENERGY_CELL = ENERGY_CELL;
    ModItems.SLIGHTLY_DAMAGED_ENERGY_CELL = SLIGHTLY_DAMAGED_ENERGY_CELL;
    ModItems.DAMAGED_ENERGY_CELL = DAMAGED_ENERGY_CELL;
    ModItems.EMPTY_ENERGY_CELL = EMPTY_ENERGY_CELL;
    ModItems.ENERGY_CELL_BLOCK = ENERGY_CELL_BLOCK;
    ModItems.CIRCUIT_BOARD = CIRCUIT_BOARD;

    if (!HOLO_PAD_REGISTRY_OBJECTS.isEmpty()) {
      ModItems.HOLO_PAD = HOLO_PAD_REGISTRY_OBJECTS.values().iterator().next();
    }
  }
}
