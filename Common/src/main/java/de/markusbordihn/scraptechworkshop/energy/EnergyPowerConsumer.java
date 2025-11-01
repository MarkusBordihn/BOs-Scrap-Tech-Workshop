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

import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellBlockItem;
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public interface EnergyPowerConsumer extends EnergyPowerBatteryHandler {

  String ENERGY_TAG = "Energy";
  String BATTERY_TAG = "Battery";
  String LAST_ENERGY_RECEIVE_TIME_TAG = "LastEnergyReceiveTime";
  String CHARGE_CYCLE_COUNT_TAG = "ChargeCycleCount";
  String LAST_ENERGY_CHANGE_TIME_TAG = "LastEnergyChangeTime";
  String LAST_ENERGY_LEVEL_TAG = "LastEnergyLevel";
  String ENERGY_FLOW_STATUS_TAG = "EnergyFlowStatus";

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

  default int getChargeCycleCount() {
    return getEnergyData().chargeCycleCount();
  }

  default void setChargeCycleCount(final int count) {
    setEnergyData(getEnergyData().withChargeCycleCount(count));
  }

  default long getLastEnergyChangeTime() {
    return getEnergyData().lastEnergyChangeTime();
  }

  default void setLastEnergyChangeTime(final long time) {
    setEnergyData(getEnergyData().withLastEnergyChangeTime(time));
  }

  default int getLastEnergyLevel() {
    return getEnergyData().lastEnergyLevel();
  }

  default void setLastEnergyLevel(final int energy) {
    setEnergyData(getEnergyData().withLastEnergyLevel(energy));
  }

  default EnergyFlowStatus getEnergyFlowStatus() {
    return getEnergyData().energyFlowStatus();
  }

  default void setEnergyFlowStatus(final EnergyFlowStatus status) {
    setEnergyData(getEnergyData().withEnergyFlowStatus(status));
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
          case 2 -> getEnergyFlowStatus().ordinal();
          default -> 0;
        };
      }

      @Override
      public void set(int index, int value) {
        switch (index) {
          case 0 -> setCurrentEnergy(value);
          case 2 -> {
            EnergyFlowStatus[] statuses = EnergyFlowStatus.values();
            if (value >= 0 && value < statuses.length) {
              setEnergyFlowStatus(statuses[value]);
            }
          }
        }
      }

      @Override
      public int getCount() {
        return 3;
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
    if (getCurrentEnergy() < getEnergyCapacity()) {
      return true;
    }
    ItemStack battery = getBattery();
    if (battery.getItem() instanceof EmptyEnergyCellItem || battery.getItem() instanceof EmptyEnergyCellBlockItem) {
      return true;
    }
    if (battery.getItem() instanceof EnergyCell cell) {
      return cell.getEnergy(battery) < cell.getCapacity();
    }
    return false;
  }

  default int getTotalSpaceAvailable() {
    int internalSpace = getEnergyCapacity() - getCurrentEnergy();
    ItemStack battery = getBattery();
    if (battery.getItem() instanceof EmptyEnergyCellItem || battery.getItem() instanceof EmptyEnergyCellBlockItem) {
      return internalSpace + Math.min(10000, 150);
    }
    if (battery.getItem() instanceof EnergyCell cell) {
      return internalSpace + Math.min(cell.getCapacity() - cell.getEnergy(battery), cell.getChargeRate());
    }
    return internalSpace;
  }

  default int receiveEnergy(final int amount, final boolean simulate) {
    int currentEnergy = getCurrentEnergy();
    int spaceInInternal = getEnergyCapacity() - currentEnergy;
    
    if (spaceInInternal >= amount) {
      if (!simulate) {
        setCurrentEnergy(currentEnergy + amount);
        updateEnergyReceiveTime();
      }
      return amount;
    }
    
    int toInternal = spaceInInternal;
    int overflow = amount - spaceInInternal;
    int toBattery = 0;
    
    ItemStack battery = getBattery();
    if (battery.getItem() instanceof EmptyEnergyCellItem || battery.getItem() instanceof EmptyEnergyCellBlockItem) {
      toBattery = Math.min(overflow, 150);
      if (!simulate) {
        ItemStack chargedBattery = battery.getItem() instanceof EmptyEnergyCellBlockItem
            ? new ItemStack(ModItems.ENERGY_CELL_BLOCK.get())
            : new ItemStack(ModItems.ENERGY_CELL.get());
        if (chargedBattery.getItem() instanceof EnergyCell energyCell) {
          energyCell.setEnergy(chargedBattery, toBattery);
          setBattery(chargedBattery);
        }
      }
    } else if (battery.getItem() instanceof EnergyCell cell) {
      toBattery = Math.min(overflow, Math.min(cell.getCapacity() - cell.getEnergy(battery), cell.getChargeRate()));
      if (!simulate && toBattery > 0) {
        cell.addEnergy(battery, toBattery);
        setBattery(battery);
      }
    }
    
    if (!simulate && toInternal > 0) {
      setCurrentEnergy(currentEnergy + toInternal);
      updateEnergyReceiveTime();
    }
    
    return toInternal + toBattery;
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
    int chargeCycleCount = compoundTag.getInt(CHARGE_CYCLE_COUNT_TAG);
    long lastEnergyChangeTime = compoundTag.getLong(LAST_ENERGY_CHANGE_TIME_TAG);
    int lastEnergyLevel = compoundTag.getInt(LAST_ENERGY_LEVEL_TAG);
    EnergyFlowStatus flowStatus =
        EnergyFlowStatus.values()[
            Math.max(
                0,
                Math.min(
                    compoundTag.getInt(ENERGY_FLOW_STATUS_TAG),
                    EnergyFlowStatus.values().length - 1))];

    setEnergyData(
        new EnergyPowerData(
            energy,
            battery,
            debounceData,
            chargeCycleCount,
            lastEnergyChangeTime,
            lastEnergyLevel,
            flowStatus));
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
    compoundTag.putInt(CHARGE_CYCLE_COUNT_TAG, data.chargeCycleCount());
    compoundTag.putLong(LAST_ENERGY_CHANGE_TIME_TAG, data.lastEnergyChangeTime());
    compoundTag.putInt(LAST_ENERGY_LEVEL_TAG, data.lastEnergyLevel());
    compoundTag.putInt(ENERGY_FLOW_STATUS_TAG, data.energyFlowStatus().ordinal());
  }
}
