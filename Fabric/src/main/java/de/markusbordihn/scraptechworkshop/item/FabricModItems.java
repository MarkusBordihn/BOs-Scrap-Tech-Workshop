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
        registerItem(ScrapType.ALLOY.getItemId(), () -> ScrapItemRegistry.ALLOY_SCRAP_ITEM);
    ModItems.METAL_SCRAP =
        registerItem(ScrapType.METAL.getItemId(), () -> ScrapItemRegistry.METAL_SCRAP_ITEM);
    ModItems.GOLD_SCRAP =
        registerItem(ScrapType.GOLD.getItemId(), () -> ScrapItemRegistry.GOLD_SCRAP_ITEM);
    ModItems.IRON_SCRAP =
        registerItem(ScrapType.IRON.getItemId(), () -> ScrapItemRegistry.IRON_SCRAP_ITEM);
    ModItems.COPPER_SCRAP =
        registerItem(ScrapType.COPPER.getItemId(), () -> ScrapItemRegistry.COPPER_SCRAP_ITEM);

    // Register scrap items
    ModItems.CERAMIC_SCRAP =
        registerItem(ScrapType.CERAMIC.getItemId(), () -> ScrapItemRegistry.CERAMIC_SCRAP_ITEM);
    ModItems.CRYSTAL_SCRAP =
        registerItem(ScrapType.CRYSTAL.getItemId(), () -> ScrapItemRegistry.CRYSTAL_SCRAP_ITEM);
    ModItems.FASTENER_SCRAP =
        registerItem(ScrapType.FASTENER.getItemId(), () -> ScrapItemRegistry.FASTENER_SCRAP_ITEM);
    ModItems.INSULATION_SCRAP =
        registerItem(
            ScrapType.INSULATION.getItemId(), () -> ScrapItemRegistry.INSULATION_SCRAP_ITEM);
    ModItems.LUMINOUS_SCRAP =
        registerItem(ScrapType.LUMINOUS.getItemId(), () -> ScrapItemRegistry.LUMINOUS_SCRAP_ITEM);
    ModItems.PLASTIC_SCRAP =
        registerItem(ScrapType.PLASTIC.getItemId(), () -> ScrapItemRegistry.PLASTIC_SCRAP_ITEM);

    // Additional Material Scrap Items
    ModItems.MINERAL_SCRAP =
        registerItem(ScrapType.MINERAL.getItemId(), () -> ScrapItemRegistry.MINERAL_SCRAP_ITEM);
    ModItems.WOOD_SCRAP =
        registerItem(ScrapType.WOOD.getItemId(), () -> ScrapItemRegistry.WOOD_SCRAP_ITEM);
    ModItems.RUBBER_SCRAP =
        registerItem(ScrapType.RUBBER.getItemId(), () -> ScrapItemRegistry.RUBBER_SCRAP_ITEM);
    ModItems.GLASS_SCRAP =
        registerItem(ScrapType.GLASS.getItemId(), () -> ScrapItemRegistry.GLASS_SCRAP_ITEM);

    // Organic and Textile Scrap Items
    ModItems.FIBER_SCRAP =
        registerItem(ScrapType.FIBER.getItemId(), () -> ScrapItemRegistry.FIBER_SCRAP_ITEM);
    ModItems.BIO_SCRAP =
        registerItem(ScrapType.BIO.getItemId(), () -> ScrapItemRegistry.BIO_SCRAP_ITEM);

    // Additional Tech Scrap Items
    ModItems.TECH_SCRAP =
        registerItem(ScrapType.TECH.getItemId(), () -> ScrapItemRegistry.TECH_SCRAP_ITEM);
    ModItems.CIRCUIT_SCRAP =
        registerItem(ScrapType.CIRCUIT.getItemId(), () -> ScrapItemRegistry.CIRCUIT_SCRAP_ITEM);
    ModItems.COIL_SCRAP =
        registerItem(ScrapType.COIL.getItemId(), () -> ScrapItemRegistry.COIL_SCRAP_ITEM);
    ModItems.CAPACITOR_SCRAP =
        registerItem(ScrapType.CAPACITOR.getItemId(), () -> ScrapItemRegistry.CAPACITOR_SCRAP_ITEM);
    ModItems.ENERGY_CELL_SCRAP =
        registerItem(
            ScrapType.ENERGY_CELL.getItemId(), () -> ScrapItemRegistry.ENERGY_CELL_SCRAP_ITEM);

    // Register scrap box items
    ModItems.ALLOY_SCRAP_BOX =
        registerItem(
            ScrapType.ALLOY.getBlockId(), () -> ScrapBoxBlockItemRegistry.ALLOY_SCRAP_BOX_ITEM);
    ModItems.BIO_SCRAP_BOX =
        registerItem(
            ScrapType.BIO.getBlockId(), () -> ScrapBoxBlockItemRegistry.BIO_SCRAP_BOX_ITEM);
    ModItems.CAPACITOR_SCRAP_BOX =
        registerItem(
            ScrapType.CAPACITOR.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.CAPACITOR_SCRAP_BOX_ITEM);
    ModItems.CERAMIC_SCRAP_BOX =
        registerItem(
            ScrapType.CERAMIC.getBlockId(), () -> ScrapBoxBlockItemRegistry.CERAMIC_SCRAP_BOX_ITEM);
    ModItems.CIRCUIT_SCRAP_BOX =
        registerItem(
            ScrapType.CIRCUIT.getBlockId(), () -> ScrapBoxBlockItemRegistry.CIRCUIT_SCRAP_BOX_ITEM);
    ModItems.COIL_SCRAP_BOX =
        registerItem(
            ScrapType.COIL.getBlockId(), () -> ScrapBoxBlockItemRegistry.COIL_SCRAP_BOX_ITEM);
    ModItems.COPPER_SCRAP_BOX =
        registerItem(
            ScrapType.COPPER.getBlockId(), () -> ScrapBoxBlockItemRegistry.COPPER_SCRAP_BOX_ITEM);
    ModItems.CRYSTAL_SCRAP_BOX =
        registerItem(
            ScrapType.CRYSTAL.getBlockId(), () -> ScrapBoxBlockItemRegistry.CRYSTAL_SCRAP_BOX_ITEM);
    ModItems.ENERGY_CELL_SCRAP_BOX =
        registerItem(
            ScrapType.ENERGY_CELL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.ENERGY_CELL_SCRAP_BOX_ITEM);
    ModItems.FASTENER_SCRAP_BOX =
        registerItem(
            ScrapType.FASTENER.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.FASTENER_SCRAP_BOX_ITEM);
    ModItems.FIBER_SCRAP_BOX =
        registerItem(
            ScrapType.FIBER.getBlockId(), () -> ScrapBoxBlockItemRegistry.FIBER_SCRAP_BOX_ITEM);
    ModItems.GLASS_SCRAP_BOX =
        registerItem(
            ScrapType.GLASS.getBlockId(), () -> ScrapBoxBlockItemRegistry.GLASS_SCRAP_BOX_ITEM);
    ModItems.GOLD_SCRAP_BOX =
        registerItem(
            ScrapType.GOLD.getBlockId(), () -> ScrapBoxBlockItemRegistry.GOLD_SCRAP_BOX_ITEM);
    ModItems.INSULATION_SCRAP_BOX =
        registerItem(
            ScrapType.INSULATION.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.INSULATION_SCRAP_BOX_ITEM);
    ModItems.IRON_SCRAP_BOX =
        registerItem(
            ScrapType.IRON.getBlockId(), () -> ScrapBoxBlockItemRegistry.IRON_SCRAP_BOX_ITEM);
    ModItems.LUMINOUS_SCRAP_BOX =
        registerItem(
            ScrapType.LUMINOUS.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.LUMINOUS_SCRAP_BOX_ITEM);
    ModItems.METAL_SCRAP_BOX =
        registerItem(
            ScrapType.METAL.getBlockId(), () -> ScrapBoxBlockItemRegistry.METAL_SCRAP_BOX_ITEM);
    ModItems.MINERAL_SCRAP_BOX =
        registerItem(
            ScrapType.MINERAL.getBlockId(), () -> ScrapBoxBlockItemRegistry.MINERAL_SCRAP_BOX_ITEM);
    ModItems.PLASTIC_SCRAP_BOX =
        registerItem(
            ScrapType.PLASTIC.getBlockId(), () -> ScrapBoxBlockItemRegistry.PLASTIC_SCRAP_BOX_ITEM);
    ModItems.RUBBER_SCRAP_BOX =
        registerItem(
            ScrapType.RUBBER.getBlockId(), () -> ScrapBoxBlockItemRegistry.RUBBER_SCRAP_BOX_ITEM);
    ModItems.TECH_SCRAP_BOX =
        registerItem(
            ScrapType.TECH.getBlockId(), () -> ScrapBoxBlockItemRegistry.TECH_SCRAP_BOX_ITEM);
    ModItems.WOOD_SCRAP_BOX =
        registerItem(
            ScrapType.WOOD.getBlockId(), () -> ScrapBoxBlockItemRegistry.WOOD_SCRAP_BOX_ITEM);

    // Tool Items
    ModItems.SCRAP_MULTITOOL =
        registerItem("scrap_multitool", () -> ToolItemRegistry.SCRAP_MULTITOOL_ITEM);
    ModItems.CREATIVE_SCRAP_MULTITOOL =
        registerItem(
            "creative_scrap_multitool", () -> ToolItemRegistry.CREATIVE_SCRAP_MULTITOOL_ITEM);

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

    // Upgrade Items
    ModItems.SPEED_UPGRADE =
        registerItem(NormalSpeedUpgradeItem.ID, () -> ToolItemRegistry.SPEED_UPGRADE_ITEM);
    ModItems.CREATIVE_SPEED_UPGRADE =
        registerItem(
            CreativeSpeedUpgradeItem.ID, () -> ToolItemRegistry.CREATIVE_SPEED_UPGRADE_ITEM);
    ModItems.FAST_CHARGE_UPGRADE =
        registerItem(FastChargeUpgradeItem.ID, () -> ToolItemRegistry.FAST_CHARGE_UPGRADE_ITEM);
    ModItems.CREATIVE_FAST_CHARGE_UPGRADE =
        registerItem(
            CreativeFastChargeUpgradeItem.ID,
            () -> ToolItemRegistry.CREATIVE_FAST_CHARGE_UPGRADE_ITEM);

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
        ModCreativeTabs.SCRAP_TAB.location(),
        ModCreativeTabs.createScrapTab().build());

    Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        ModCreativeTabs.UPGRADES_AND_TOOLS_TAB.location(),
        ModCreativeTabs.createUpgradesAndToolsTab().build());

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
