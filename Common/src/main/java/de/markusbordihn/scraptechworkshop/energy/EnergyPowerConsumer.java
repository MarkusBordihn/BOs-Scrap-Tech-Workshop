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

import de.markusbordihn.scraptechworkshop.data.energy.EnergyFlowStatus;
import de.markusbordihn.scraptechworkshop.data.energy.EnergyPowerData;
import de.markusbordihn.scraptechworkshop.data.energy.ExternalEnergyFlowStatus;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellBlockItem;
import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public interface EnergyPowerConsumer extends EnergyPowerBatteryHandler {

  String ENERGY_TAG = "Energy";
  String BATTERY_TAG = "Battery";
  String CHARGE_CYCLE_COUNT_TAG = "ChargeCycleCount";
  String LAST_ENERGY_CHANGE_TIME_TAG = "LastEnergyChangeTime";
  String LAST_ENERGY_LEVEL_TAG = "LastEnergyLevel";
  String ENERGY_FLOW_STATUS_TAG = "EnergyFlowStatus";

  void markDirty();

  EnergyPowerData getEnergyData();

  void setEnergyData(final EnergyPowerData data);

  int getLastEnergyReceiveAmount();

  void setLastEnergyReceiveAmount(int amount);

  int getLastEnergyDistributeAmount();

  void setLastEnergyDistributeAmount(int amount);

  default int getCurrentEnergy() {
    return getEnergyData().currentEnergy();
  }

  default void setCurrentEnergy(final int energy) {
    setEnergyData(getEnergyData().withCurrentEnergy(energy));
  }

  default void resetEnergyFlowAmounts() {
    if (getLastEnergyReceiveAmount() != 0 || getLastEnergyDistributeAmount() != 0) {
      setLastEnergyReceiveAmount(0);
      setLastEnergyDistributeAmount(0);
    }
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

  default ExternalEnergyFlowStatus getExternalEnergyFlowStatus() {
    return getEnergyData().externalEnergyFlowStatus();
  }

  default void setExternalEnergyFlowStatus(final ExternalEnergyFlowStatus status) {
    setEnergyData(getEnergyData().withExternalEnergyFlowStatus(status));
  }

  default ContainerData createEnergyContainerData() {
    return new ContainerData() {
      private int lastReceiveAmount = 0;
      private int lastDistributeAmount = 0;

      @Override
      public int get(int index) {
        return switch (index) {
          case 0 -> getCurrentEnergy();
          case 1 -> getEnergyCapacity();
          case 2 -> getEnergyFlowStatus().ordinal();
          case 3 -> getExternalEnergyFlowStatus().ordinal();
          case 4 -> lastReceiveAmount;
          case 5 -> lastDistributeAmount;
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
          case 3 -> {
            ExternalEnergyFlowStatus[] statuses = ExternalEnergyFlowStatus.values();
            if (value >= 0 && value < statuses.length) {
              setExternalEnergyFlowStatus(statuses[value]);
            }
          }
          case 4 -> lastReceiveAmount = value;
          case 5 -> lastDistributeAmount = value;
        }
      }

      @Override
      public int getCount() {
        return 6;
      }
    };
  }

  ContainerData getEnergyPowerData();

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
    if (battery.getItem() instanceof EmptyEnergyCellItem
        || battery.getItem() instanceof EmptyEnergyCellBlockItem) {
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
    if (battery.getItem() instanceof EmptyEnergyCellItem
        || battery.getItem() instanceof EmptyEnergyCellBlockItem) {
      return internalSpace + Math.min(10000, 150);
    }
    if (battery.getItem() instanceof EnergyCell cell) {
      return internalSpace
          + Math.min(cell.getCapacity() - cell.getEnergy(battery), cell.getChargeRate());
    }
    return internalSpace;
  }

  default int receiveEnergy(final int amount, final boolean simulate) {
    int spaceInInternal = getEnergyCapacity() - getCurrentEnergy();

    if (spaceInInternal >= amount) {
      if (!simulate) {
        setCurrentEnergy(getCurrentEnergy() + amount);
        setLastEnergyReceiveAmount(amount);
        if (amount > 0) {
          setExternalEnergyFlowStatus(ExternalEnergyFlowStatus.ENERGY_IN);
        }
      }
      return amount;
    }

    int overflow = amount - spaceInInternal;
    int toBattery = 0;

    ItemStack battery = getBattery();
    if (battery.getItem() instanceof EmptyEnergyCellItem
        || battery.getItem() instanceof EmptyEnergyCellBlockItem) {
      toBattery = Math.min(overflow, 150);
      if (!simulate) {
        ItemStack chargedBattery =
            battery.getItem() instanceof EmptyEnergyCellBlockItem
                ? new ItemStack(ModItems.ENERGY_CELL_BLOCK.get())
                : new ItemStack(ModItems.ENERGY_CELL.get());
        if (chargedBattery.getItem() instanceof EnergyCell energyCell) {
          energyCell.setEnergy(chargedBattery, toBattery);
          setBattery(chargedBattery);
        }
      }
    } else if (battery.getItem() instanceof EnergyCell cell) {
      toBattery =
          Math.min(
              overflow,
              Math.min(cell.getCapacity() - cell.getEnergy(battery), cell.getChargeRate()));
      if (!simulate && toBattery > 0) {
        cell.addEnergy(battery, toBattery);
        setBattery(battery);
      }
    }

    int totalReceived = spaceInInternal + toBattery;
    if (!simulate) {
      if (spaceInInternal > 0) {
        setCurrentEnergy(getCurrentEnergy() + spaceInInternal);
      }
      setLastEnergyReceiveAmount(totalReceived);
      if (totalReceived > 0) {
        setExternalEnergyFlowStatus(ExternalEnergyFlowStatus.ENERGY_IN);
      }
    }

    return totalReceived;
  }

  default boolean hasStableEnergy(final int requiredEnergy) {
    return hasStableEnergy(requiredEnergy, 10);
  }

  default boolean hasStableEnergy(final int requiredEnergy, final int threshold) {
    int currentEnergy = getCurrentEnergy();
    return currentEnergy >= requiredEnergy && currentEnergy >= requiredEnergy + threshold;
  }

  default void loadEnergyPowerConsumer(final CompoundTag compoundTag) {
    int energy = compoundTag.getInt(ENERGY_TAG);
    ItemStack battery =
        compoundTag.contains(BATTERY_TAG)
            ? ItemStack.of(compoundTag.getCompound(BATTERY_TAG))
            : ItemStack.EMPTY;
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
    ExternalEnergyFlowStatus externalFlowStatus = ExternalEnergyFlowStatus.IDLE;

    setEnergyData(
        new EnergyPowerData(
            energy,
            battery,
            chargeCycleCount,
            lastEnergyChangeTime,
            lastEnergyLevel,
            flowStatus,
            externalFlowStatus));
  }

  default void saveEnergyPowerConsumer(final CompoundTag compoundTag) {
    EnergyPowerData data = getEnergyData();
    compoundTag.putInt(ENERGY_TAG, data.currentEnergy());
    if (!data.battery().isEmpty()) {
      CompoundTag batteryTag = new CompoundTag();
      data.battery().save(batteryTag);
      compoundTag.put(BATTERY_TAG, batteryTag);
    }
    compoundTag.putInt(CHARGE_CYCLE_COUNT_TAG, data.chargeCycleCount());
    compoundTag.putLong(LAST_ENERGY_CHANGE_TIME_TAG, data.lastEnergyChangeTime());
    compoundTag.putInt(LAST_ENERGY_LEVEL_TAG, data.lastEnergyLevel());
    compoundTag.putInt(ENERGY_FLOW_STATUS_TAG, data.energyFlowStatus().ordinal());
  }
}
