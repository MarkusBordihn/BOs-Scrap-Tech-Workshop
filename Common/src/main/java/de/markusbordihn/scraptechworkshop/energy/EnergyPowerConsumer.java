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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public interface EnergyPowerConsumer extends EnergyBatteryHandler {

  String ENERGY_TAG = "Energy";
  String BATTERY_TAG = "Battery";
  String LAST_ENERGY_RECEIVE_TIME_TAG = "LastEnergyReceiveTime";

  int DEFAULT_ENERGY_DEBOUNCE_THRESHOLD = 10;
  int DEFAULT_ENERGY_DEBOUNCE_TICKS = 40;

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

  default boolean consumeEnergy(final int amount) {
    if (getCurrentEnergy() >= amount) {
      setCurrentEnergy(getCurrentEnergy() - amount);
      return true;
    }
    return false;
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
      updateEnergyReceiveTime();
    }

    return energyReceived;
  }

  default void updateEnergyReceiveTime() {
    EnergyPowerData data = getEnergyData();
    setEnergyData(
        data.withDebounceData(
            data.debounceData()
                .withReceiveTime(System.currentTimeMillis() / 50, getCurrentEnergy())));
  }

  default boolean hasStableEnergy(final int requiredEnergy) {
    return hasStableEnergy(requiredEnergy, DEFAULT_ENERGY_DEBOUNCE_THRESHOLD);
  }

  default boolean hasStableEnergy(final int requiredEnergy, final int threshold) {
    int currentEnergy = getCurrentEnergy();
    if (currentEnergy < requiredEnergy) {
      return false;
    }
    return currentEnergy >= requiredEnergy + threshold
        || isReceivingEnergy(DEFAULT_ENERGY_DEBOUNCE_TICKS);
  }

  default boolean isReceivingEnergy(final int debounceTicks) {
    long currentTime = System.currentTimeMillis() / 50;
    return getEnergyData().debounceData().isReceivingEnergy(currentTime, debounceTicks);
  }

  default void loadEnergyPowerConsumer(final CompoundTag compoundTag) {
    int energy = compoundTag.getInt(ENERGY_TAG);
    ItemStack battery = ItemStack.EMPTY;
    if (compoundTag.contains(BATTERY_TAG)) {
      battery = ItemStack.of(compoundTag.getCompound(BATTERY_TAG));
    }
    long lastReceiveTime = compoundTag.getLong(LAST_ENERGY_RECEIVE_TIME_TAG);
    EnergyDebounceData debounceData =
        lastReceiveTime > 0
            ? new EnergyDebounceData(lastReceiveTime, energy)
            : EnergyDebounceData.empty();
    setEnergyData(new EnergyPowerData(energy, battery, debounceData));
  }

  default void saveEnergyPowerConsumer(final CompoundTag compoundTag) {
    EnergyPowerData data = getEnergyData();
    compoundTag.putInt(ENERGY_TAG, data.currentEnergy());
    if (!data.battery().isEmpty()) {
      CompoundTag batteryTag = new CompoundTag();
      data.battery().save(batteryTag);
      compoundTag.put(BATTERY_TAG, batteryTag);
    }
    if (data.debounceData() != null) {
      compoundTag.putLong(
          LAST_ENERGY_RECEIVE_TIME_TAG, data.debounceData().lastEnergyReceiveTime());
    }
  }
}
