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

package de.markusbordihn.scraptechworkshop.energy;

import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import net.minecraft.world.item.ItemStack;

public interface EnergyBatteryHandler {

  int getEnergyCapacity();

  int getCurrentEnergy();

  void setCurrentEnergy(final int energy);

  ItemStack getBattery();

  void setBattery(final ItemStack battery);

  void markDirty();

  default boolean chargeFromBattery(final int amount) {
    ItemStack battery = getBattery();
    if (battery.isEmpty() || !(battery.getItem() instanceof EnergyCellItem batteryItem)) {
      return false;
    }

    int batteryEnergy = batteryItem.getEnergy(battery);
    if (batteryEnergy <= 1) {
      setBattery(batteryItem.createEmptyBattery());
      markDirty();
      return false;
    }

    int currentEnergy = getCurrentEnergy();
    int capacity = getEnergyCapacity();
    int neededEnergy = Math.min(amount, capacity - currentEnergy);

    if (neededEnergy <= 0) {
      return false;
    }

    int energyToTransfer = Math.min(neededEnergy, batteryEnergy);
    batteryItem.consumeEnergy(battery, energyToTransfer);
    setCurrentEnergy(currentEnergy + energyToTransfer);

    if (batteryItem.getEnergy(battery) <= 1) {
      setBattery(batteryItem.createEmptyBattery());
    } else {
      setBattery(battery);
    }

    markDirty();
    return true;
  }
}
