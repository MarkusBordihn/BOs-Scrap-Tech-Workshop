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

package de.markusbordihn.scraptechworkshop.menu;

import de.markusbordihn.scraptechworkshop.energy.EnergyPowerConsumer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;

public abstract class EnergyPowerMenu extends AbstractContainerMenu {

  public static final int ENERGY_TAB_BATTERY_SLOT_X = -20;
  public static final int ENERGY_TAB_BATTERY_SLOT_Y = 6;
  public static final int ENERGY_TAB_ENERGY_BAR_X = -20;
  public static final int ENERGY_TAB_ENERGY_BAR_Y = 26;
  public static final int ENERGY_TAB_ENERGY_BAR_WIDTH = 16;
  public static final int ENERGY_TAB_ENERGY_BAR_HEIGHT = 60;
  protected static final int ENERGY_DATA_INDEX = 0;
  protected static final int ENERGY_CAPACITY_DATA_INDEX = 1;
  protected final ContainerData energyData;

  protected EnergyPowerMenu(MenuType<?> menuType, int windowId, ContainerData energyData) {
    super(menuType, windowId);
    this.energyData = energyData != null ? energyData : new SimpleContainerData(2);
    addDataSlots(this.energyData);
  }

  public int getCurrentEnergy() {
    return energyData.get(ENERGY_DATA_INDEX);
  }

  public int getEnergyCapacity() {
    return energyData.get(ENERGY_CAPACITY_DATA_INDEX);
  }

  public int getEnergyPercentage() {
    int capacity = getEnergyCapacity();
    return capacity > 0 ? (getCurrentEnergy() * 100) / capacity : 0;
  }

  public abstract EnergyPowerConsumer getEnergyConsumer();
}
