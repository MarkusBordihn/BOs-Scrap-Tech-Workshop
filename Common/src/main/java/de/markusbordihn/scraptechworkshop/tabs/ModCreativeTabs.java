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

package de.markusbordihn.scraptechworkshop.tabs;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.multitool.DisplayMode;
import de.markusbordihn.scraptechworkshop.data.multitool.ScrapMultitoolData;
import de.markusbordihn.scraptechworkshop.data.multitool.ToolMode;
import de.markusbordihn.scraptechworkshop.item.ModBlockItems;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloCubeItem;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloPadItem;
import de.markusbordihn.scraptechworkshop.registry.item.hololog.HoloLogItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.scrap.ScrapItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.scrapbox.ScrapBoxBlockItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {

  public static final ResourceKey<CreativeModeTab> SCRAP_TAB =
      ResourceKey.create(
          Registries.CREATIVE_MODE_TAB, new ResourceLocation(Constants.MOD_ID, "scrap"));

  public static final ResourceKey<CreativeModeTab> UPGRADES_AND_TOOLS_TAB =
      ResourceKey.create(
          Registries.CREATIVE_MODE_TAB,
          new ResourceLocation(Constants.MOD_ID, "upgrades_and_tools"));

  public static final ResourceKey<CreativeModeTab> HOLO_CUBE_TAB =
      ResourceKey.create(
          Registries.CREATIVE_MODE_TAB, new ResourceLocation(Constants.MOD_ID, "holo_cubes"));

  public static final ResourceKey<CreativeModeTab> HOLO_PAD_TAB =
      ResourceKey.create(
          Registries.CREATIVE_MODE_TAB, new ResourceLocation(Constants.MOD_ID, "holo_pads"));

  public static CreativeModeTab.Builder createScrapTab() {
    return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
        .title(Component.translatable("itemGroup.scrap_tech_workshop.scrap"))
        .icon(() -> new ItemStack(ModItems.METAL_SCRAP.get()))
        .displayItems(
            (parameters, output) -> {
              // Add all scrap items
              ScrapItemRegistry.getAllScrapItems().values().forEach(output::accept);

              // Add all scrap box items
              ScrapBoxBlockItemRegistry.getAllScrapBoxItems().values().forEach(output::accept);

              // Scrap Pile Block Items
              output.accept(ModBlockItems.MIXED_SCRAP_PILE.get());
              output.accept(ModBlockItems.METAL_SCRAP_PILE.get());
              output.accept(ModBlockItems.TECH_SCRAP_PILE.get());
            });
  }

  public static CreativeModeTab.Builder createUpgradesAndToolsTab() {
    return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
        .title(Component.translatable("itemGroup.scrap_tech_workshop.upgrades_and_tools"))
        .icon(() -> new ItemStack(ModItems.CREATIVE_SCRAP_MULTITOOL.get()))
        .displayItems(
            (parameters, output) -> {
              // Tool Items
              output.accept(ModItems.MAGNET_FISHING_ROD.get());
              output.accept(ModItems.SCRAP_FISHING_ROD.get());
              output.accept(ModItems.SCRAP_MULTITOOL.get());
              output.accept(createCreativeMultitoolWithBattery());

              // Component Items
              output.accept(ModItems.CIRCUIT_BOARD.get());
              output.accept(ModItems.DAMAGED_ENERGY_CELL.get().getDefaultInstance());
              output.accept(ModItems.EMPTY_ENERGY_CELL.get());
              output.accept(ModItems.ENERGY_CELL_BLOCK.get());
              output.accept(ModItems.ENERGY_CELL.get().getDefaultInstance());
              output.accept(ModItems.SLIGHTLY_DAMAGED_ENERGY_CELL.get().getDefaultInstance());
              output.accept(ModItems.RECLAIMED_COPPER_WIRE.get());
              output.accept(ModItems.REFINED_COPPER_WIRE.get());

              // Filter Items
              output.accept(ModItems.BASIC_SCRAP_FILTER.get());
              output.accept(ModItems.FINE_MESH_FILTER.get());
              output.accept(ModItems.MAGNETIC_COIL_FILTER.get());
              output.accept(ModItems.ELECTRO_CONDENSATOR_FILTER.get());
              output.accept(ModItems.JUNK_FILTER.get());

              // Upgrade Items
              output.accept(ModItems.CREATIVE_FAST_CHARGE_UPGRADE.get());
              output.accept(ModItems.CREATIVE_SPEED_UPGRADE.get());
              output.accept(ModItems.FAST_CHARGE_UPGRADE.get());
              output.accept(ModItems.SPEED_UPGRADE.get());

              // Block Items
              output.accept(ModBlockItems.BASIC_SCRAP_COLLECTOR_STATION.get());
              output.accept(ModBlockItems.FLOATING_SCRAP_COLLECTOR.get());
              output.accept(ModBlockItems.NEON_TUBE.get());
              output.accept(ModBlockItems.RECHARGE_STATION.get());
              output.accept(ModBlockItems.RECYCLER.get());
            });
  }

  public static CreativeModeTab.Builder createHoloCubeTab() {
    return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 2)
        .title(Component.translatable("itemGroup.scrap_tech_workshop.holo_cubes"))
        .icon(() -> new ItemStack(ModBlockItems.HOLO_CUBE.get()))
        .displayItems(
            (parameters, output) -> {
              for (HoloCubeItem item : HoloLogItemRegistry.getHoloCubeItems().values()) {
                output.accept(item);
              }
            });
  }

  public static CreativeModeTab.Builder createHoloPadTab() {
    return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 3)
        .title(Component.translatable("itemGroup.scrap_tech_workshop.holo_pads"))
        .icon(() -> new ItemStack(ModItems.HOLO_PAD.get()))
        .displayItems(
            (parameters, output) -> {
              for (HoloPadItem item : HoloLogItemRegistry.getHoloPadItems().values()) {
                output.accept(item);
              }
            });
  }

  private static ItemStack createCreativeMultitoolWithBattery() {
    ItemStack multitool = new ItemStack(ModItems.CREATIVE_SCRAP_MULTITOOL.get());
    ItemStack battery = new ItemStack(ModItems.ENERGY_CELL.get());
    if (battery.getItem() instanceof EnergyCellItem energyCell) {
      energyCell.setEnergy(battery, EnergyCellItem.CAPACITY_MAH);
    }
    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(multitool);
    data = data.withBattery(battery);
    data.saveToItemStack(multitool);
    DisplayMode displayMode = new DisplayMode(multitool);
    displayMode.updateModel(ToolMode.DEFAULT, data.getBatteryLevel());
    return multitool;
  }
}
