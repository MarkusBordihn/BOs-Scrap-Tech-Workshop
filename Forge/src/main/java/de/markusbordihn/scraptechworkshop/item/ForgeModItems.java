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
import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.item.component.CircuitBoardItem;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellBlockItem;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloPadItem;
import de.markusbordihn.scraptechworkshop.item.upgrade.CreativeFastChargeUpgradeItem;
import de.markusbordihn.scraptechworkshop.item.upgrade.CreativeSpeedUpgradeItem;
import de.markusbordihn.scraptechworkshop.item.upgrade.FastChargeUpgradeItem;
import de.markusbordihn.scraptechworkshop.item.upgrade.NormalSpeedUpgradeItem;
import de.markusbordihn.scraptechworkshop.registry.item.hololog.HoloLogItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.hololog.HoloLogRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.scrap.ScrapItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.scrapbox.ScrapBoxBlockItemRegistry;
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

  public static final RegistryObject<Item> SCRAP_ALLOY;
  public static final RegistryObject<Item> SCRAP_METAL;
  public static final RegistryObject<Item> SCRAP_GOLD;
  public static final RegistryObject<Item> SCRAP_IRON;
  public static final RegistryObject<Item> SCRAP_COPPER;
  public static final RegistryObject<Item> SCRAP_CERAMIC;
  public static final RegistryObject<Item> SCRAP_CRYSTAL;
  public static final RegistryObject<Item> SCRAP_FASTENER;
  public static final RegistryObject<Item> SCRAP_INSULATION;
  public static final RegistryObject<Item> SCRAP_LUMINOUS;
  public static final RegistryObject<Item> SCRAP_PLASTIC;
  public static final RegistryObject<Item> SCRAP_MINERAL;
  public static final RegistryObject<Item> SCRAP_WOOD;
  public static final RegistryObject<Item> SCRAP_RUBBER;
  public static final RegistryObject<Item> SCRAP_GLASS;
  public static final RegistryObject<Item> SCRAP_FIBER;
  public static final RegistryObject<Item> SCRAP_BIO;
  public static final RegistryObject<Item> TECH_SCRAP;
  public static final RegistryObject<Item> CIRCUIT_SCRAP;
  public static final RegistryObject<Item> COIL_SCRAP;
  public static final RegistryObject<Item> CAPACITOR_SCRAP;
  public static final RegistryObject<Item> ENERGY_CELL_SCRAP;

  public static final RegistryObject<Item> ALLOY_SCRAP_BOX;
  public static final RegistryObject<Item> BIO_SCRAP_BOX;
  public static final RegistryObject<Item> CAPACITOR_SCRAP_BOX;
  public static final RegistryObject<Item> CERAMIC_SCRAP_BOX;
  public static final RegistryObject<Item> CIRCUIT_SCRAP_BOX;
  public static final RegistryObject<Item> COIL_SCRAP_BOX;
  public static final RegistryObject<Item> COPPER_SCRAP_BOX;
  public static final RegistryObject<Item> CRYSTAL_SCRAP_BOX;
  public static final RegistryObject<Item> ENERGY_CELL_SCRAP_BOX;
  public static final RegistryObject<Item> FASTENER_SCRAP_BOX;
  public static final RegistryObject<Item> FIBER_SCRAP_BOX;
  public static final RegistryObject<Item> GLASS_SCRAP_BOX;
  public static final RegistryObject<Item> GOLD_SCRAP_BOX;
  public static final RegistryObject<Item> INSULATION_SCRAP_BOX;
  public static final RegistryObject<Item> IRON_SCRAP_BOX;
  public static final RegistryObject<Item> LUMINOUS_SCRAP_BOX;
  public static final RegistryObject<Item> METAL_SCRAP_BOX;
  public static final RegistryObject<Item> MINERAL_SCRAP_BOX;
  public static final RegistryObject<Item> PLASTIC_SCRAP_BOX;
  public static final RegistryObject<Item> RUBBER_SCRAP_BOX;
  public static final RegistryObject<Item> TECH_SCRAP_BOX;
  public static final RegistryObject<Item> WOOD_SCRAP_BOX;
  // Tool Items
  public static final RegistryObject<Item> SCRAP_MULTITOOL =
      ITEMS.register("scrap_multitool", () -> ToolItemRegistry.SCRAP_MULTITOOL_ITEM);
  public static final RegistryObject<Item> CREATIVE_SCRAP_MULTITOOL =
      ITEMS.register(
          "creative_scrap_multitool", () -> ToolItemRegistry.CREATIVE_SCRAP_MULTITOOL_ITEM);
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
  // Upgrade Items
  public static final RegistryObject<Item> SPEED_UPGRADE =
      ITEMS.register(NormalSpeedUpgradeItem.ID, () -> ToolItemRegistry.SPEED_UPGRADE_ITEM);
  public static final RegistryObject<Item> CREATIVE_SPEED_UPGRADE =
      ITEMS.register(
          CreativeSpeedUpgradeItem.ID, () -> ToolItemRegistry.CREATIVE_SPEED_UPGRADE_ITEM);
  public static final RegistryObject<Item> FAST_CHARGE_UPGRADE =
      ITEMS.register(FastChargeUpgradeItem.ID, () -> ToolItemRegistry.FAST_CHARGE_UPGRADE_ITEM);
  public static final RegistryObject<Item> CREATIVE_FAST_CHARGE_UPGRADE =
      ITEMS.register(
          CreativeFastChargeUpgradeItem.ID,
          () -> ToolItemRegistry.CREATIVE_FAST_CHARGE_UPGRADE_ITEM);
  // Creative Mode Tabs
  public static final RegistryObject<CreativeModeTab> SCRAP_TAB =
      CREATIVE_MODE_TABS.register("scrap", ModCreativeTabs.createScrapTab()::build);
  public static final RegistryObject<CreativeModeTab> UPGRADES_AND_TOOLS_TAB =
      CREATIVE_MODE_TABS.register(
          "upgrades_and_tools", ModCreativeTabs.createUpgradesAndToolsTab()::build);
  public static final RegistryObject<CreativeModeTab> HOLO_CUBE_TAB =
      CREATIVE_MODE_TABS.register("holo_cubes", ModCreativeTabs.createHoloCubeTab()::build);
  public static final RegistryObject<CreativeModeTab> HOLO_PAD_TAB =
      CREATIVE_MODE_TABS.register("holo_pads", ModCreativeTabs.createHoloPadTab()::build);
  private static final Map<String, RegistryObject<Item>> HOLO_PAD_REGISTRY_OBJECTS =
      new LinkedHashMap<>();

  static {
    SCRAP_ALLOY =
        ITEMS.register(
            ScrapType.ALLOY.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.ALLOY));
    SCRAP_METAL =
        ITEMS.register(
            ScrapType.METAL.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.METAL));
    SCRAP_GOLD =
        ITEMS.register(
            ScrapType.GOLD.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.GOLD));
    SCRAP_IRON =
        ITEMS.register(
            ScrapType.IRON.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.IRON));
    SCRAP_COPPER =
        ITEMS.register(
            ScrapType.COPPER.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.COPPER));
    SCRAP_CERAMIC =
        ITEMS.register(
            ScrapType.CERAMIC.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.CERAMIC));
    SCRAP_CRYSTAL =
        ITEMS.register(
            ScrapType.CRYSTAL.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.CRYSTAL));
    SCRAP_FASTENER =
        ITEMS.register(
            ScrapType.FASTENER.getItemId(),
            () -> ScrapItemRegistry.getScrapItem(ScrapType.FASTENER));
    SCRAP_INSULATION =
        ITEMS.register(
            ScrapType.INSULATION.getItemId(),
            () -> ScrapItemRegistry.getScrapItem(ScrapType.INSULATION));
    SCRAP_LUMINOUS =
        ITEMS.register(
            ScrapType.LUMINOUS.getItemId(),
            () -> ScrapItemRegistry.getScrapItem(ScrapType.LUMINOUS));
    SCRAP_PLASTIC =
        ITEMS.register(
            ScrapType.PLASTIC.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.PLASTIC));
    SCRAP_MINERAL =
        ITEMS.register(
            ScrapType.MINERAL.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.MINERAL));
    SCRAP_WOOD =
        ITEMS.register(
            ScrapType.WOOD.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.WOOD));
    SCRAP_RUBBER =
        ITEMS.register(
            ScrapType.RUBBER.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.RUBBER));
    SCRAP_GLASS =
        ITEMS.register(
            ScrapType.GLASS.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.GLASS));
    SCRAP_FIBER =
        ITEMS.register(
            ScrapType.FIBER.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.FIBER));
    SCRAP_BIO =
        ITEMS.register(
            ScrapType.BIO.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.BIO));
    TECH_SCRAP =
        ITEMS.register(
            ScrapType.TECH.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.TECH));
    CIRCUIT_SCRAP =
        ITEMS.register(
            ScrapType.CIRCUIT.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.CIRCUIT));
    COIL_SCRAP =
        ITEMS.register(
            ScrapType.COIL.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.COIL));
    CAPACITOR_SCRAP =
        ITEMS.register(
            ScrapType.CAPACITOR.getItemId(),
            () -> ScrapItemRegistry.getScrapItem(ScrapType.CAPACITOR));
    ENERGY_CELL_SCRAP =
        ITEMS.register(
            ScrapType.ENERGY_CELL.getItemId(),
            () -> ScrapItemRegistry.getScrapItem(ScrapType.ENERGY_CELL));

    ALLOY_SCRAP_BOX =
        ITEMS.register(
            ScrapType.ALLOY.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.ALLOY));
    BIO_SCRAP_BOX =
        ITEMS.register(
            ScrapType.BIO.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.BIO));
    CAPACITOR_SCRAP_BOX =
        ITEMS.register(
            ScrapType.CAPACITOR.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.CAPACITOR));
    CERAMIC_SCRAP_BOX =
        ITEMS.register(
            ScrapType.CERAMIC.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.CERAMIC));
    CIRCUIT_SCRAP_BOX =
        ITEMS.register(
            ScrapType.CIRCUIT.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.CIRCUIT));
    COIL_SCRAP_BOX =
        ITEMS.register(
            ScrapType.COIL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.COIL));
    COPPER_SCRAP_BOX =
        ITEMS.register(
            ScrapType.COPPER.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.COPPER));
    CRYSTAL_SCRAP_BOX =
        ITEMS.register(
            ScrapType.CRYSTAL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.CRYSTAL));
    ENERGY_CELL_SCRAP_BOX =
        ITEMS.register(
            ScrapType.ENERGY_CELL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.ENERGY_CELL));
    FASTENER_SCRAP_BOX =
        ITEMS.register(
            ScrapType.FASTENER.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.FASTENER));
    FIBER_SCRAP_BOX =
        ITEMS.register(
            ScrapType.FIBER.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.FIBER));
    GLASS_SCRAP_BOX =
        ITEMS.register(
            ScrapType.GLASS.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.GLASS));
    GOLD_SCRAP_BOX =
        ITEMS.register(
            ScrapType.GOLD.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.GOLD));
    INSULATION_SCRAP_BOX =
        ITEMS.register(
            ScrapType.INSULATION.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.INSULATION));
    IRON_SCRAP_BOX =
        ITEMS.register(
            ScrapType.IRON.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.IRON));
    LUMINOUS_SCRAP_BOX =
        ITEMS.register(
            ScrapType.LUMINOUS.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.LUMINOUS));
    METAL_SCRAP_BOX =
        ITEMS.register(
            ScrapType.METAL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.METAL));
    MINERAL_SCRAP_BOX =
        ITEMS.register(
            ScrapType.MINERAL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.MINERAL));
    PLASTIC_SCRAP_BOX =
        ITEMS.register(
            ScrapType.PLASTIC.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.PLASTIC));
    RUBBER_SCRAP_BOX =
        ITEMS.register(
            ScrapType.RUBBER.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.RUBBER));
    TECH_SCRAP_BOX =
        ITEMS.register(
            ScrapType.TECH.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.TECH));
    WOOD_SCRAP_BOX =
        ITEMS.register(
            ScrapType.WOOD.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.WOOD));
  }

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

    // Scrap Box Items
    ModItems.ALLOY_SCRAP_BOX = ALLOY_SCRAP_BOX;
    ModItems.BIO_SCRAP_BOX = BIO_SCRAP_BOX;
    ModItems.CAPACITOR_SCRAP_BOX = CAPACITOR_SCRAP_BOX;
    ModItems.CERAMIC_SCRAP_BOX = CERAMIC_SCRAP_BOX;
    ModItems.CIRCUIT_SCRAP_BOX = CIRCUIT_SCRAP_BOX;
    ModItems.COIL_SCRAP_BOX = COIL_SCRAP_BOX;
    ModItems.COPPER_SCRAP_BOX = COPPER_SCRAP_BOX;
    ModItems.CRYSTAL_SCRAP_BOX = CRYSTAL_SCRAP_BOX;
    ModItems.ENERGY_CELL_SCRAP_BOX = ENERGY_CELL_SCRAP_BOX;
    ModItems.FASTENER_SCRAP_BOX = FASTENER_SCRAP_BOX;
    ModItems.FIBER_SCRAP_BOX = FIBER_SCRAP_BOX;
    ModItems.GLASS_SCRAP_BOX = GLASS_SCRAP_BOX;
    ModItems.GOLD_SCRAP_BOX = GOLD_SCRAP_BOX;
    ModItems.INSULATION_SCRAP_BOX = INSULATION_SCRAP_BOX;
    ModItems.IRON_SCRAP_BOX = IRON_SCRAP_BOX;
    ModItems.LUMINOUS_SCRAP_BOX = LUMINOUS_SCRAP_BOX;
    ModItems.METAL_SCRAP_BOX = METAL_SCRAP_BOX;
    ModItems.MINERAL_SCRAP_BOX = MINERAL_SCRAP_BOX;
    ModItems.PLASTIC_SCRAP_BOX = PLASTIC_SCRAP_BOX;
    ModItems.RUBBER_SCRAP_BOX = RUBBER_SCRAP_BOX;
    ModItems.TECH_SCRAP_BOX = TECH_SCRAP_BOX;
    ModItems.WOOD_SCRAP_BOX = WOOD_SCRAP_BOX;

    // Tool Items
    ModItems.SCRAP_MULTITOOL = SCRAP_MULTITOOL;
    ModItems.CREATIVE_SCRAP_MULTITOOL = CREATIVE_SCRAP_MULTITOOL;

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

    // Upgrade Items
    ModItems.SPEED_UPGRADE = SPEED_UPGRADE;
    ModItems.CREATIVE_SPEED_UPGRADE = CREATIVE_SPEED_UPGRADE;
    ModItems.FAST_CHARGE_UPGRADE = FAST_CHARGE_UPGRADE;
    ModItems.CREATIVE_FAST_CHARGE_UPGRADE = CREATIVE_FAST_CHARGE_UPGRADE;

    if (!HOLO_PAD_REGISTRY_OBJECTS.isEmpty()) {
      ModItems.HOLO_PAD = HOLO_PAD_REGISTRY_OBJECTS.values().iterator().next();
    }
  }
}
