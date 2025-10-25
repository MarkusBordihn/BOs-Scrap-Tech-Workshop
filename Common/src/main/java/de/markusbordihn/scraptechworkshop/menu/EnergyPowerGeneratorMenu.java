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

import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;

public abstract class EnergyPowerGeneratorMenu extends BaseMenu {

  public static final int ENERGY_TAB_BATTERY_SLOT_X = -20;
  public static final int ENERGY_TAB_BATTERY_SLOT_Y = 6;
  protected static final int ENERGY_DATA_INDEX = 0;
  protected static final int ENERGY_CAPACITY_DATA_INDEX = 1;
  protected final ContainerData energyData;

  protected EnergyPowerGeneratorMenu(MenuType<?> menuType, int windowId, ContainerData energyData) {
    super(menuType, windowId);
    this.energyData = energyData != null ? energyData : new SimpleContainerData(2);
    addDataSlots(this.energyData);
  }

  public int getEnergy() {
    return energyData.get(ENERGY_DATA_INDEX);
  }

  public int getEnergyCapacity() {
    return energyData.get(ENERGY_CAPACITY_DATA_INDEX);
  }
}
