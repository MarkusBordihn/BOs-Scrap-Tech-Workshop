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
import de.markusbordihn.scraptechworkshop.energy.EnergyCell;
import de.markusbordihn.scraptechworkshop.item.ModBlockItems;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloCubeItem;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloPadItem;
import de.markusbordihn.scraptechworkshop.registry.item.HoloLogItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.ScrapBoxBlockItemRegistry;
import de.markusbordihn.scraptechworkshop.registry.item.ScrapItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {

  public static final ResourceKey<CreativeModeTab> MAIN_TAB =
      ResourceKey.create(
          Registries.CREATIVE_MODE_TAB, new ResourceLocation(Constants.MOD_ID, "main"));

  public static CreativeModeTab.Builder createMainTab() {
    return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
        .title(Component.translatable("itemGroup.scrap_tech_workshop.main"))
        .icon(() -> new ItemStack(ModItems.MIXED_SCRAP_ROBOT_SPAWN_EGG.get()))
        .displayItems(
            (parameters, output) -> {
              ScrapItemRegistry.getAllScrapItems().values().forEach(output::accept);
              ScrapBoxBlockItemRegistry.getAllScrapBoxItems().values().forEach(output::accept);

              output.accept(ModBlockItems.MIXED_SCRAP_PILE.get());
              output.accept(ModBlockItems.METAL_SCRAP_PILE.get());
              output.accept(ModBlockItems.TECH_SCRAP_PILE.get());

              output.accept(ModItems.MAGNET_FISHING_ROD.get());
              output.accept(ModItems.SCRAP_FISHING_ROD.get());
              output.accept(ModItems.SCRAP_MULTITOOL.get());
              output.accept(createCreativeMultitoolWithBattery());

              output.accept(ModItems.CIRCUIT_BOARD.get());
              output.accept(ModItems.DAMAGED_ENERGY_CELL.get().getDefaultInstance());
              output.accept(ModItems.EMPTY_ENERGY_CELL.get());
              output.accept(ModItems.ENERGY_CELL.get().getDefaultInstance());
              output.accept(ModItems.SLIGHTLY_DAMAGED_ENERGY_CELL.get().getDefaultInstance());
              output.accept(ModItems.DAMAGED_ENERGY_CELL_BLOCK.get().getDefaultInstance());
              output.accept(ModItems.EMPTY_ENERGY_CELL_BLOCK.get());
              output.accept(ModItems.ENERGY_CELL_BLOCK.get().getDefaultInstance());
              output.accept(ModItems.SLIGHTLY_DAMAGED_ENERGY_CELL_BLOCK.get().getDefaultInstance());
              output.accept(ModItems.RECLAIMED_COPPER_WIRE.get());
              output.accept(ModItems.REFINED_COPPER_WIRE.get());

              output.accept(ModItems.BASIC_SCRAP_FILTER.get());
              output.accept(ModItems.FINE_MESH_FILTER.get());
              output.accept(ModItems.MAGNETIC_COIL_FILTER.get());
              output.accept(ModItems.ELECTRO_CONDENSATOR_FILTER.get());
              output.accept(ModItems.JUNK_FILTER.get());

              output.accept(ModItems.CREATIVE_FAST_CHARGE_UPGRADE.get());
              output.accept(ModItems.CREATIVE_SPEED_UPGRADE.get());
              output.accept(ModItems.FAST_CHARGE_UPGRADE.get());
              output.accept(ModItems.SPEED_UPGRADE.get());

              output.accept(ModBlockItems.BASIC_SCRAP_COLLECTOR_STATION.get());
              output.accept(ModBlockItems.FLOATING_SCRAP_COLLECTOR.get());
              output.accept(ModBlockItems.NEON_TUBE.get());
              output.accept(ModBlockItems.RECHARGE_STATION.get());
              output.accept(ModBlockItems.RECYCLER.get());
              output.accept(ModBlockItems.REPLICANT_TEST_LAMP.get());
              output.accept(ModBlockItems.SCRAP_WIND_TURBINE.get());

              for (HoloCubeItem item : HoloLogItemRegistry.getHoloCubeItems().values()) {
                output.accept(item);
              }

              for (HoloPadItem item : HoloLogItemRegistry.getHoloPadItems().values()) {
                output.accept(item);
              }

              output.accept(ModItems.MIXED_SCRAP_ROBOT_SPAWN_EGG.get());
            });
  }

  private static ItemStack createCreativeMultitoolWithBattery() {
    ItemStack multitool = new ItemStack(ModItems.CREATIVE_SCRAP_MULTITOOL.get());
    ItemStack battery = new ItemStack(ModItems.ENERGY_CELL.get());
    if (battery.getItem() instanceof EnergyCell energyCell) {
      energyCell.setEnergy(battery, energyCell.getCapacity());
    }
    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(multitool);
    data = data.withBattery(battery);
    data.saveToItemStack(multitool);
    DisplayMode displayMode = new DisplayMode(multitool);
    displayMode.updateModel(ToolMode.DEFAULT, data.getBatteryLevel());
    return multitool;
  }
}
