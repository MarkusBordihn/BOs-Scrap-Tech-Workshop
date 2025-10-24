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
import de.markusbordihn.scraptechworkshop.registry.entity.MixedScrapRobotEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.HoloLogItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.HoloLogRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.ScrapBoxBlockItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.ScrapFilterItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.ScrapItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.ToolItemRegistry;
import de.markusbordihn.scraptechworkshop.tabs.ModCreativeTabs;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class FabricModItems {

  private FabricModItems() {}

  public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier) {
    T item =
        Registry.register(BuiltInRegistries.ITEM, ModItems.getItemId(name), itemSupplier.get());
    return () -> item;
  }

  public static void registerModItems() {
    for (ScrapType type : ScrapType.values()) {
      switch (type) {
        case ALLOY ->
            ModItems.ALLOY_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case METAL ->
            ModItems.METAL_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case GOLD ->
            ModItems.GOLD_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case IRON ->
            ModItems.IRON_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case COPPER ->
            ModItems.COPPER_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case CERAMIC ->
            ModItems.CERAMIC_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case CRYSTAL ->
            ModItems.CRYSTAL_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case FASTENER ->
            ModItems.FASTENER_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case INSULATION ->
            ModItems.INSULATION_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case LUMINOUS ->
            ModItems.LUMINOUS_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case PLASTIC ->
            ModItems.PLASTIC_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case MINERAL ->
            ModItems.MINERAL_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case WOOD ->
            ModItems.WOOD_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case RUBBER ->
            ModItems.RUBBER_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case GLASS ->
            ModItems.GLASS_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case FIBER ->
            ModItems.FIBER_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case BIO ->
            ModItems.BIO_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case TECH ->
            ModItems.TECH_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case CIRCUIT ->
            ModItems.CIRCUIT_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case COIL ->
            ModItems.COIL_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case CAPACITOR ->
            ModItems.CAPACITOR_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
        case ENERGY_CELL ->
            ModItems.ENERGY_CELL_SCRAP =
                registerItem(type.getItemId(), () -> ScrapItemRegistry.getScrapItem(type));
      }
    }

    for (ScrapType type : ScrapType.values()) {
      switch (type) {
        case ALLOY ->
            ModItems.ALLOY_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case BIO ->
            ModItems.BIO_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case CAPACITOR ->
            ModItems.CAPACITOR_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case CERAMIC ->
            ModItems.CERAMIC_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case CIRCUIT ->
            ModItems.CIRCUIT_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case COIL ->
            ModItems.COIL_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case COPPER ->
            ModItems.COPPER_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case CRYSTAL ->
            ModItems.CRYSTAL_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case ENERGY_CELL ->
            ModItems.ENERGY_CELL_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case FASTENER ->
            ModItems.FASTENER_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case FIBER ->
            ModItems.FIBER_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case GLASS ->
            ModItems.GLASS_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case GOLD ->
            ModItems.GOLD_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case INSULATION ->
            ModItems.INSULATION_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case IRON ->
            ModItems.IRON_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case LUMINOUS ->
            ModItems.LUMINOUS_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case METAL ->
            ModItems.METAL_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case MINERAL ->
            ModItems.MINERAL_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case PLASTIC ->
            ModItems.PLASTIC_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case RUBBER ->
            ModItems.RUBBER_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case TECH ->
            ModItems.TECH_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
        case WOOD ->
            ModItems.WOOD_SCRAP_BOX =
                registerItem(
                    type.getBlockId(), () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(type));
      }
    }

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
    ModItems.RECLAIMED_COPPER_WIRE =
        registerItem("reclaimed_copper_wire", () -> ToolItemRegistry.RECLAIMED_COPPER_WIRE_ITEM);
    ModItems.REFINED_COPPER_WIRE =
        registerItem("refined_copper_wire", () -> ToolItemRegistry.REFINED_COPPER_WIRE_ITEM);

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

    // Scrap Filter Items
    ModItems.BASIC_SCRAP_FILTER =
        registerItem("basic_scrap_filter", () -> ScrapFilterItemRegistry.BASIC_SCRAP_FILTER);
    ModItems.FINE_MESH_FILTER =
        registerItem("fine_mesh_filter", () -> ScrapFilterItemRegistry.FINE_MESH_FILTER);
    ModItems.MAGNETIC_COIL_FILTER =
        registerItem("magnetic_coil_filter", () -> ScrapFilterItemRegistry.MAGNETIC_COIL_FILTER);
    ModItems.ELECTRO_CONDENSATOR_FILTER =
        registerItem(
            "electro_condensator_filter", () -> ScrapFilterItemRegistry.ELECTRO_CONDENSATOR_FILTER);
    ModItems.JUNK_FILTER = registerItem("junk_filter", () -> ScrapFilterItemRegistry.JUNK_FILTER);

    ModItems.MIXED_SCRAP_ROBOT_SPAWN_EGG =
        registerItem(
            "mixed_scrap_robot_spawn_egg",
            () ->
                new SpawnEggItem(
                    MixedScrapRobotEntityRegistry.MIXED_SCRAP_ROBOT_ENTITY_TYPE,
                    0x808080,
                    0x404040,
                    new Item.Properties()));

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
        ModCreativeTabs.MAIN_TAB.location(),
        ModCreativeTabs.createMainTab().build());
  }
}
