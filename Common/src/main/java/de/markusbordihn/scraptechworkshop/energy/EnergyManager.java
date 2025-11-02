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

import de.markusbordihn.scraptechworkshop.data.energy.BatteryStatus;
import de.markusbordihn.scraptechworkshop.data.energy.EnergyData;
import de.markusbordihn.scraptechworkshop.data.energy.EnergyStatus;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import net.minecraft.world.item.ItemStack;

public final class EnergyManager {

  private EnergyManager() {}

  public static EnergyData getEnergyData(final ItemStack itemStack, final int maxEnergy) {
    return new EnergyData(itemStack, maxEnergy);
  }

  public static BatteryStatus getBatteryStatus(final ItemStack itemStack) {
    return new BatteryStatus(itemStack);
  }

  public static void setEnergy(final ItemStack itemStack, final int maxEnergy, final int energy) {
    EnergyData energyData =
        new EnergyData(
            Math.max(1, Math.min(energy, maxEnergy)), maxEnergy, EnergyData.DEFAULT_TRANSFER_RATE);
    energyData.writeToItemStack(itemStack);
    updateBatteryStatus(itemStack, energyData);
  }

  private static void updateBatteryStatus(final ItemStack itemStack, final EnergyData energyData) {
    BatteryStatus currentStatus = getBatteryStatus(itemStack);
    EnergyStatus newStatus =
        EnergyStatus.fromEnergyLevel(energyData.current(), energyData.maximum());

    if (currentStatus.status() != newStatus) {
      BatteryStatus updatedStatus = currentStatus.withStatus(newStatus);
      updatedStatus.writeToItemStack(itemStack);
    }
  }

  public static boolean hasEnergyFromBattery(
      final ItemStack itemStack, final int maxEnergy, final ItemStack battery, final int amount) {
    if (!(battery.getItem() instanceof EnergyCellItem batteryItem)) {
      return false;
    }
    return batteryItem.getEnergy(battery) >= amount;
  }

  public static void syncEnergyWithBattery(
      final ItemStack itemStack, final int maxEnergy, final ItemStack battery) {
    if (battery.getItem() instanceof EnergyCell cell) {
      int batteryEnergy = cell.getEnergy(battery);
      if (batteryEnergy <= 1) {
        setEnergy(itemStack, maxEnergy, 0);
        return;
      }

      float energyRatio = (float) batteryEnergy / cell.getCapacity();
      int multitoolEnergy = Math.round(maxEnergy * energyRatio);
      setEnergy(itemStack, maxEnergy, multitoolEnergy);
    } else {
      setEnergy(itemStack, maxEnergy, 0);
    }
  }
}
