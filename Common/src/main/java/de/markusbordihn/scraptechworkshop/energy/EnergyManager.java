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

import de.markusbordihn.scraptechworkshop.data.energy.EnergyData;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import net.minecraft.world.item.ItemStack;

public final class EnergyManager {

  private EnergyManager() {}

  public static EnergyData getEnergyData(final ItemStack itemStack, final int maxEnergy) {
    return new EnergyData(itemStack, maxEnergy);
  }

  public static EnergyData getEnergyData(final ItemStack itemStack) {
    return new EnergyData(itemStack);
  }

  public static BatteryStatus getBatteryStatus(final ItemStack itemStack) {
    return new BatteryStatus(itemStack);
  }

  public static EnergyStatus getEnergyStatus(final ItemStack itemStack) {
    return getBatteryStatus(itemStack).status();
  }

  public static boolean hasEnergy(
      final ItemStack itemStack, final int maxEnergy, final int amount) {
    return getEnergyData(itemStack, maxEnergy).hasEnergy(amount);
  }

  public static boolean hasEnergy(final ItemStack itemStack, final int amount) {
    return getEnergyData(itemStack).hasEnergy(amount);
  }

  public static void setEnergy(final ItemStack itemStack, final int maxEnergy, final int energy) {
    EnergyData energyData =
        new EnergyData(
            Math.max(1, Math.min(energy, maxEnergy)), maxEnergy, EnergyData.DEFAULT_TRANSFER_RATE);
    energyData.writeToItemStack(itemStack);
    updateBatteryStatus(itemStack, energyData);
  }

  public static void consumeEnergy(
      final ItemStack itemStack, final int maxEnergy, final int amount) {
    BatteryStatus batteryStatus = getBatteryStatus(itemStack);

    if (!batteryStatus.canDischarge()) {
      return;
    }

    EnergyData energyData = getEnergyData(itemStack, maxEnergy);
    EnergyData updatedData = energyData.consume(amount);
    updatedData.writeToItemStack(itemStack);
    updateBatteryStatus(itemStack, updatedData);
  }

  public static void addEnergy(final ItemStack itemStack, final int maxEnergy, final int amount) {
    BatteryStatus batteryStatus = getBatteryStatus(itemStack);

    if (!batteryStatus.canCharge()) {
      return;
    }

    EnergyData energyData = getEnergyData(itemStack, maxEnergy);
    EnergyData updatedData = energyData.add(amount);
    updatedData.writeToItemStack(itemStack);
    updateBatteryStatus(itemStack, updatedData);
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

  public static boolean canReceiveEnergy(final ItemStack itemStack) {
    return getBatteryStatus(itemStack).canCharge();
  }

  public static boolean canExtractEnergy(final ItemStack itemStack) {
    return getBatteryStatus(itemStack).canDischarge();
  }

  public static int receiveEnergy(
      final ItemStack itemStack,
      final int maxEnergy,
      final int maxReceive,
      final boolean simulate) {
    EnergyData energyData = getEnergyData(itemStack, maxEnergy);
    int canReceive = Math.min(maxReceive, energyData.maximum() - energyData.current());

    if (!simulate && canReceive > 0) {
      addEnergy(itemStack, maxEnergy, canReceive);
    }

    return canReceive;
  }

  public static int extractEnergy(
      final ItemStack itemStack,
      final int maxEnergy,
      final int maxExtract,
      final boolean simulate) {
    EnergyData energyData = getEnergyData(itemStack, maxEnergy);
    int canExtract = Math.min(maxExtract, energyData.current());

    if (!simulate && canExtract > 0) {
      consumeEnergy(itemStack, maxEnergy, canExtract);
    }

    return canExtract;
  }

  public static int getEnergyStored(final ItemStack itemStack, final int maxEnergy) {
    return getEnergyData(itemStack, maxEnergy).current();
  }

  public static int getEnergyStored(final ItemStack itemStack) {
    return getEnergyData(itemStack).current();
  }

  public static int getMaxEnergyStored(final ItemStack itemStack, final int maxEnergy) {
    return maxEnergy;
  }

  public static int getMaxEnergyStored(final ItemStack itemStack) {
    return getEnergyData(itemStack).maximum();
  }

  public static void syncFromBattery(
      final ItemStack itemStack, final int maxEnergy, final ItemStack battery) {
    if (battery.getItem() instanceof EnergyCellItem batteryItem) {
      int batteryEnergy = batteryItem.getEnergy(battery);
      EnergyData currentData = getEnergyData(itemStack, maxEnergy);

      int transferAmount = Math.min(batteryEnergy, maxEnergy - currentData.current());
      if (transferAmount > 0) {
        batteryItem.consumeEnergy(battery, transferAmount);
        addEnergy(itemStack, maxEnergy, transferAmount);
      }
    }
  }

  public static void consumeWithBatteryBackup(
      final ItemStack itemStack, final int maxEnergy, int amount, final ItemStack battery) {
    EnergyData energyData = getEnergyData(itemStack, maxEnergy);

    if (energyData.hasEnergy(amount)) {
      consumeEnergy(itemStack, maxEnergy, amount);
      return;
    }

    if (battery.getItem() instanceof EnergyCellItem batteryItem) {
      int batteryEnergy = batteryItem.getEnergy(battery);
      if (batteryEnergy >= amount) {
        batteryItem.consumeEnergy(battery, amount);
        syncFromBattery(itemStack, maxEnergy, battery);
        return;
      } else if (batteryEnergy > 0) {
        batteryItem.consumeEnergy(battery, batteryEnergy);
        amount -= batteryEnergy;
        syncFromBattery(itemStack, maxEnergy, battery);
      }
    }

    EnergyData updatedData = energyData.consume(Math.max(1, energyData.current() - amount));
    updatedData.writeToItemStack(itemStack);
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
    if (battery.getItem() instanceof EnergyCellItem batteryItem) {
      int batteryEnergy = batteryItem.getEnergy(battery);
      if (batteryEnergy <= 1) {
        setEnergy(itemStack, maxEnergy, 0);
        return;
      }

      float energyRatio = (float) batteryEnergy / EnergyCellItem.CAPACITY_MAH;
      int multitoolEnergy = Math.round(maxEnergy * energyRatio);
      setEnergy(itemStack, maxEnergy, multitoolEnergy);
    } else {
      setEnergy(itemStack, maxEnergy, 0);
    }
  }
}
