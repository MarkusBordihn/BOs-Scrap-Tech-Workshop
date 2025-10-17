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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public interface EnergyPowerConsumer {

  String ENERGY_TAG = "Energy";
  String BATTERY_TAG = "Battery";

  int getEnergyCapacity();

  int getBatterySlot();

  void markDirty();

  EnergyPowerData getEnergyData();

  void setEnergyData(final EnergyPowerData data);

  default int getCurrentEnergy() {
    return getEnergyData().currentEnergy();
  }

  default void setCurrentEnergy(final int energy) {
    setEnergyData(getEnergyData().withCurrentEnergy(energy));
  }

  default ItemStack getBattery() {
    return getEnergyData().battery();
  }

  default void setBattery(final ItemStack battery) {
    setEnergyData(getEnergyData().withBattery(battery));
  }

  default int getEnergyTransferRate() {
    return 50;
  }

  default ContainerData getEnergyPowerData() {
    return new ContainerData() {
      @Override
      public int get(int index) {
        return switch (index) {
          case 0 -> getCurrentEnergy();
          case 1 -> getEnergyCapacity();
          default -> 0;
        };
      }

      @Override
      public void set(int index, int value) {
        if (index == 0) {
          setCurrentEnergy(value);
        }
      }

      @Override
      public int getCount() {
        return 2;
      }
    };
  }

  default boolean hasEnergy(final int amount) {
    return getCurrentEnergy() >= amount;
  }

  default boolean consumeEnergy(final int amount) {
    if (getCurrentEnergy() >= amount) {
      setCurrentEnergy(getCurrentEnergy() - amount);
      return true;
    }
    return false;
  }

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

  default int getEnergyPercentage() {
    int capacity = getEnergyCapacity();
    return capacity > 0 ? (getCurrentEnergy() * 100) / capacity : 0;
  }

  default boolean hasBattery() {
    ItemStack battery = getBattery();
    return !battery.isEmpty() && battery.getItem() instanceof EnergyCellItem;
  }

  default boolean canAcceptExternalEnergy() {
    return getCurrentEnergy() < getEnergyCapacity();
  }

  default int receiveEnergy(final int amount, final boolean simulate) {
    int capacity = getEnergyCapacity();
    int currentEnergy = getCurrentEnergy();
    int energyReceived = Math.min(amount, capacity - currentEnergy);

    if (!simulate && energyReceived > 0) {
      setCurrentEnergy(currentEnergy + energyReceived);
    }

    return energyReceived;
  }

  default void loadEnergyPowerConsumer(final CompoundTag compoundTag) {
    int energy = compoundTag.getInt(ENERGY_TAG);
    ItemStack battery = ItemStack.EMPTY;
    if (compoundTag.contains(BATTERY_TAG)) {
      battery = ItemStack.of(compoundTag.getCompound(BATTERY_TAG));
    }
    setEnergyData(new EnergyPowerData(energy, battery));
  }

  default void saveEnergyPowerConsumer(final CompoundTag compoundTag) {
    EnergyPowerData data = getEnergyData();
    compoundTag.putInt(ENERGY_TAG, data.currentEnergy());
    if (!data.battery().isEmpty()) {
      CompoundTag batteryTag = new CompoundTag();
      data.battery().save(batteryTag);
      compoundTag.put(BATTERY_TAG, batteryTag);
    }
  }
}
