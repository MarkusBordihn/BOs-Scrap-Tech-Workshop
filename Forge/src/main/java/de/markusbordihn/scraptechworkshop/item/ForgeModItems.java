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
import de.markusbordihn.scraptechworkshop.registry.entity.MixedScrapRobotEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.HoloLogItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.HoloLogRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.ScrapBoxBlockItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.ScrapFilterItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.ScrapItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.ToolItemRegistry;
import de.markusbordihn.scraptechworkshop.tabs.ModCreativeTabs;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
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

  public static final RegistryObject<CreativeModeTab> MAIN_TAB =
      CREATIVE_MODE_TABS.register("main", ModCreativeTabs.createMainTab()::build);
  private static final Map<String, RegistryObject<Item>> HOLO_PAD_REGISTRY_OBJECTS =
      new LinkedHashMap<>();

  static {
    // Scrap Items
    ModItems.ALLOY_SCRAP =
        ITEMS.register(
            ScrapType.ALLOY.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.ALLOY));
    ModItems.METAL_SCRAP =
        ITEMS.register(
            ScrapType.METAL.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.METAL));
    ModItems.GOLD_SCRAP =
        ITEMS.register(
            ScrapType.GOLD.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.GOLD));
    ModItems.IRON_SCRAP =
        ITEMS.register(
            ScrapType.IRON.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.IRON));
    ModItems.COPPER_SCRAP =
        ITEMS.register(
            ScrapType.COPPER.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.COPPER));
    ModItems.CERAMIC_SCRAP =
        ITEMS.register(
            ScrapType.CERAMIC.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.CERAMIC));
    ModItems.CRYSTAL_SCRAP =
        ITEMS.register(
            ScrapType.CRYSTAL.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.CRYSTAL));
    ModItems.FASTENER_SCRAP =
        ITEMS.register(
            ScrapType.FASTENER.getItemId(),
            () -> ScrapItemRegistry.getScrapItem(ScrapType.FASTENER));
    ModItems.INSULATION_SCRAP =
        ITEMS.register(
            ScrapType.INSULATION.getItemId(),
            () -> ScrapItemRegistry.getScrapItem(ScrapType.INSULATION));
    ModItems.LUMINOUS_SCRAP =
        ITEMS.register(
            ScrapType.LUMINOUS.getItemId(),
            () -> ScrapItemRegistry.getScrapItem(ScrapType.LUMINOUS));
    ModItems.PLASTIC_SCRAP =
        ITEMS.register(
            ScrapType.PLASTIC.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.PLASTIC));
    ModItems.MINERAL_SCRAP =
        ITEMS.register(
            ScrapType.MINERAL.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.MINERAL));
    ModItems.WOOD_SCRAP =
        ITEMS.register(
            ScrapType.WOOD.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.WOOD));
    ModItems.RUBBER_SCRAP =
        ITEMS.register(
            ScrapType.RUBBER.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.RUBBER));
    ModItems.GLASS_SCRAP =
        ITEMS.register(
            ScrapType.GLASS.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.GLASS));
    ModItems.FIBER_SCRAP =
        ITEMS.register(
            ScrapType.FIBER.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.FIBER));
    ModItems.BIO_SCRAP =
        ITEMS.register(
            ScrapType.BIO.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.BIO));
    ModItems.TECH_SCRAP =
        ITEMS.register(
            ScrapType.TECH.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.TECH));
    ModItems.CIRCUIT_SCRAP =
        ITEMS.register(
            ScrapType.CIRCUIT.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.CIRCUIT));
    ModItems.COIL_SCRAP =
        ITEMS.register(
            ScrapType.COIL.getItemId(), () -> ScrapItemRegistry.getScrapItem(ScrapType.COIL));
    ModItems.CAPACITOR_SCRAP =
        ITEMS.register(
            ScrapType.CAPACITOR.getItemId(),
            () -> ScrapItemRegistry.getScrapItem(ScrapType.CAPACITOR));
    ModItems.ENERGY_CELL_SCRAP =
        ITEMS.register(
            ScrapType.ENERGY_CELL.getItemId(),
            () -> ScrapItemRegistry.getScrapItem(ScrapType.ENERGY_CELL));

    // Scrap Box Items
    ModItems.ALLOY_SCRAP_BOX =
        ITEMS.register(
            ScrapType.ALLOY.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.ALLOY));
    ModItems.BIO_SCRAP_BOX =
        ITEMS.register(
            ScrapType.BIO.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.BIO));
    ModItems.CAPACITOR_SCRAP_BOX =
        ITEMS.register(
            ScrapType.CAPACITOR.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.CAPACITOR));
    ModItems.CERAMIC_SCRAP_BOX =
        ITEMS.register(
            ScrapType.CERAMIC.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.CERAMIC));
    ModItems.CIRCUIT_SCRAP_BOX =
        ITEMS.register(
            ScrapType.CIRCUIT.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.CIRCUIT));
    ModItems.COIL_SCRAP_BOX =
        ITEMS.register(
            ScrapType.COIL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.COIL));
    ModItems.COPPER_SCRAP_BOX =
        ITEMS.register(
            ScrapType.COPPER.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.COPPER));
    ModItems.CRYSTAL_SCRAP_BOX =
        ITEMS.register(
            ScrapType.CRYSTAL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.CRYSTAL));
    ModItems.ENERGY_CELL_SCRAP_BOX =
        ITEMS.register(
            ScrapType.ENERGY_CELL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.ENERGY_CELL));
    ModItems.FASTENER_SCRAP_BOX =
        ITEMS.register(
            ScrapType.FASTENER.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.FASTENER));
    ModItems.FIBER_SCRAP_BOX =
        ITEMS.register(
            ScrapType.FIBER.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.FIBER));
    ModItems.GLASS_SCRAP_BOX =
        ITEMS.register(
            ScrapType.GLASS.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.GLASS));
    ModItems.GOLD_SCRAP_BOX =
        ITEMS.register(
            ScrapType.GOLD.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.GOLD));
    ModItems.INSULATION_SCRAP_BOX =
        ITEMS.register(
            ScrapType.INSULATION.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.INSULATION));
    ModItems.IRON_SCRAP_BOX =
        ITEMS.register(
            ScrapType.IRON.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.IRON));
    ModItems.LUMINOUS_SCRAP_BOX =
        ITEMS.register(
            ScrapType.LUMINOUS.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.LUMINOUS));
    ModItems.METAL_SCRAP_BOX =
        ITEMS.register(
            ScrapType.METAL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.METAL));
    ModItems.MINERAL_SCRAP_BOX =
        ITEMS.register(
            ScrapType.MINERAL.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.MINERAL));
    ModItems.PLASTIC_SCRAP_BOX =
        ITEMS.register(
            ScrapType.PLASTIC.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.PLASTIC));
    ModItems.RUBBER_SCRAP_BOX =
        ITEMS.register(
            ScrapType.RUBBER.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.RUBBER));
    ModItems.TECH_SCRAP_BOX =
        ITEMS.register(
            ScrapType.TECH.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.TECH));
    ModItems.WOOD_SCRAP_BOX =
        ITEMS.register(
            ScrapType.WOOD.getBlockId(),
            () -> ScrapBoxBlockItemRegistry.getScrapBoxItem(ScrapType.WOOD));

    // Tool Items
    ModItems.SCRAP_MULTITOOL =
        ITEMS.register("scrap_multitool", () -> ToolItemRegistry.SCRAP_MULTITOOL_ITEM);
    ModItems.CREATIVE_SCRAP_MULTITOOL =
        ITEMS.register(
            "creative_scrap_multitool", () -> ToolItemRegistry.CREATIVE_SCRAP_MULTITOOL_ITEM);

    // Fishing Rod Items
    ModItems.SCRAP_FISHING_ROD =
        ITEMS.register("scrap_fishing_rod", () -> ToolItemRegistry.SCRAP_FISHING_ROD_ITEM);
    ModItems.MAGNET_FISHING_ROD =
        ITEMS.register("magnet_fishing_rod", () -> ToolItemRegistry.MAGNET_FISHING_ROD_ITEM);

    // Component Items
    ModItems.ENERGY_CELL =
        ITEMS.register(EnergyCellItem.ID, () -> ToolItemRegistry.ENERGY_CELL_ITEM);
    ModItems.SLIGHTLY_DAMAGED_ENERGY_CELL =
        ITEMS.register(
            "slightly_damaged_energy_cell",
            () -> ToolItemRegistry.SLIGHTLY_DAMAGED_ENERGY_CELL_ITEM);
    ModItems.DAMAGED_ENERGY_CELL =
        ITEMS.register("damaged_energy_cell", () -> ToolItemRegistry.DAMAGED_ENERGY_CELL_ITEM);
    ModItems.EMPTY_ENERGY_CELL =
        ITEMS.register("empty_energy_cell", () -> ToolItemRegistry.EMPTY_ENERGY_CELL_ITEM);
    ModItems.ENERGY_CELL_BLOCK =
        ITEMS.register(EnergyCellBlockItem.ID, () -> ToolItemRegistry.ENERGY_CELL_BLOCK_ITEM);
    ModItems.CIRCUIT_BOARD =
        ITEMS.register(CircuitBoardItem.ID, () -> ToolItemRegistry.CIRCUIT_BOARD_ITEM);
    ModItems.RECLAIMED_COPPER_WIRE =
        ITEMS.register("reclaimed_copper_wire", () -> ToolItemRegistry.RECLAIMED_COPPER_WIRE_ITEM);
    ModItems.REFINED_COPPER_WIRE =
        ITEMS.register("refined_copper_wire", () -> ToolItemRegistry.REFINED_COPPER_WIRE_ITEM);

    // Dummy Items for Rendering (not in creative tab)
    ModItems.SCRAP_WIND_TURBINE_BLADES =
        ITEMS.register("scrap_wind_turbine_blades", () -> new Item(new Item.Properties()));

    // Upgrade Items
    ModItems.SPEED_UPGRADE =
        ITEMS.register(NormalSpeedUpgradeItem.ID, () -> ToolItemRegistry.SPEED_UPGRADE_ITEM);
    ModItems.CREATIVE_SPEED_UPGRADE =
        ITEMS.register(
            CreativeSpeedUpgradeItem.ID, () -> ToolItemRegistry.CREATIVE_SPEED_UPGRADE_ITEM);
    ModItems.FAST_CHARGE_UPGRADE =
        ITEMS.register(FastChargeUpgradeItem.ID, () -> ToolItemRegistry.FAST_CHARGE_UPGRADE_ITEM);
    ModItems.CREATIVE_FAST_CHARGE_UPGRADE =
        ITEMS.register(
            CreativeFastChargeUpgradeItem.ID,
            () -> ToolItemRegistry.CREATIVE_FAST_CHARGE_UPGRADE_ITEM);

    // Scrap Filter Items
    ModItems.BASIC_SCRAP_FILTER =
        ITEMS.register("basic_scrap_filter", () -> ScrapFilterItemRegistry.BASIC_SCRAP_FILTER);
    ModItems.FINE_MESH_FILTER =
        ITEMS.register("fine_mesh_filter", () -> ScrapFilterItemRegistry.FINE_MESH_FILTER);
    ModItems.MAGNETIC_COIL_FILTER =
        ITEMS.register("magnetic_coil_filter", () -> ScrapFilterItemRegistry.MAGNETIC_COIL_FILTER);
    ModItems.ELECTRO_CONDENSATOR_FILTER =
        ITEMS.register(
            "electro_condensator_filter", () -> ScrapFilterItemRegistry.ELECTRO_CONDENSATOR_FILTER);
    ModItems.JUNK_FILTER = ITEMS.register("junk_filter", () -> ScrapFilterItemRegistry.JUNK_FILTER);

    // Spawn Eggs
    ModItems.MIXED_SCRAP_ROBOT_SPAWN_EGG =
        ITEMS.register(
            "mixed_scrap_robot_spawn_egg",
            () ->
                new ForgeSpawnEggItem(
                    () -> MixedScrapRobotEntityRegistry.MIXED_SCRAP_ROBOT_ENTITY_TYPE,
                    0x808080,
                    0x404040,
                    new Item.Properties()));
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

    if (!HOLO_PAD_REGISTRY_OBJECTS.isEmpty()) {
      ModItems.HOLO_PAD = HOLO_PAD_REGISTRY_OBJECTS.values().iterator().next();
    }
  }
}
