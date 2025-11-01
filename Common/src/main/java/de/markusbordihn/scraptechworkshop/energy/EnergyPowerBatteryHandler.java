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

import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellItem;
import net.minecraft.world.item.ItemStack;

public interface EnergyPowerBatteryHandler extends EnergyBatteryHandler {

  int getChargeCycleCount();

  void setChargeCycleCount(final int count);

  long getLastEnergyChangeTime();

  void setLastEnergyChangeTime(final long time);

  int getLastEnergyLevel();

  void setLastEnergyLevel(final int energy);

  EnergyFlowStatus getEnergyFlowStatus();

  void setEnergyFlowStatus(final EnergyFlowStatus status);

  default ChargingMode getChargingMode() {
    int cycles = getChargeCycleCount();
    for (ChargingMode mode : ChargingMode.values()) {
      if (cycles < mode.getCycleLimit()) {
        return mode;
      }
    }
    return ChargingMode.CONSERVATION;
  }

  default boolean shouldCharge(final long currentTime) {
    return currentTime - getLastEnergyChangeTime() > 40;
  }

  default void updateEnergyFlow(final long currentTime) {
    ItemStack battery = getBattery();
    EnergyFlowStatus currentStatus = getEnergyFlowStatus();
    
    if (battery.isEmpty()) {
      if (currentStatus != EnergyFlowStatus.NO_BATTERY) {
        setEnergyFlowStatus(EnergyFlowStatus.NO_BATTERY);
        setLastEnergyLevel(0);
        markDirty();
      }
      return;
    }

    if (battery.getItem() instanceof EmptyEnergyCellItem) {
      if (currentStatus != EnergyFlowStatus.IDLE) {
        setEnergyFlowStatus(EnergyFlowStatus.IDLE);
        setLastEnergyLevel(0);
        markDirty();
      }
      return;
    }

    if (!(battery.getItem() instanceof EnergyCell cell)) {
      if (currentStatus != EnergyFlowStatus.NO_BATTERY) {
        setEnergyFlowStatus(EnergyFlowStatus.NO_BATTERY);
        setLastEnergyLevel(0);
        markDirty();
      }
      return;
    }

    int currentEnergy = getCurrentEnergy();
    int capacity = getEnergyCapacity();
    int batteryEnergy = cell.getEnergy(battery);
    int lastBatteryEnergy = getLastEnergyLevel();
    int targetEnergy = (capacity * getChargingMode().getTargetPercentage()) / 100;
    int hysteresisEnergy = (capacity * getChargingMode().getHysteresisPercentage()) / 100;
    boolean canStatusChange = shouldCharge(currentTime);
    EnergyFlowStatus newStatus = currentStatus;

    if (batteryEnergy > lastBatteryEnergy) {
      newStatus = EnergyFlowStatus.INTERNAL_TO_BATTERY;
      if (canStatusChange) {
        setLastEnergyLevel(batteryEnergy);
        setLastEnergyChangeTime(currentTime);
      }
    }
    else if (batteryEnergy < lastBatteryEnergy) {
      newStatus = EnergyFlowStatus.BATTERY_TO_INTERNAL;
      if (canStatusChange) {
        setLastEnergyLevel(batteryEnergy);
        setLastEnergyChangeTime(currentTime);
      }
    }
    else if (currentEnergy < hysteresisEnergy && batteryEnergy > 1 && canStatusChange) {
      int extracted = cell.extractEnergy(battery);
      if (extracted > 0) {
        setCurrentEnergy(currentEnergy + extracted);
        newStatus = EnergyFlowStatus.BATTERY_TO_INTERNAL;

        if (currentEnergy < hysteresisEnergy && getCurrentEnergy() >= targetEnergy) {
          setChargeCycleCount(getChargeCycleCount() + 1);
        }

        if (cell.getEnergy(battery) <= 1) {
          setBattery(cell.createEmptyBattery());
        } else {
          setBattery(battery);
        }
        setLastEnergyLevel(cell.getEnergy(battery));
        setLastEnergyChangeTime(currentTime);
        markDirty();
      }
    }
    else if (currentEnergy >= hysteresisEnergy && batteryEnergy < cell.getCapacity()) {
      if (currentEnergy >= capacity || (currentStatus == EnergyFlowStatus.INTERNAL_TO_BATTERY && currentEnergy >= hysteresisEnergy)) {
        int transferred = cell.addEnergy(battery, currentEnergy - hysteresisEnergy + 1);
        if (transferred > 0) {
          setCurrentEnergy(currentEnergy - transferred);
          setBattery(battery);
          newStatus = EnergyFlowStatus.INTERNAL_TO_BATTERY;
          if (canStatusChange) {
            setLastEnergyLevel(cell.getEnergy(battery));
            setLastEnergyChangeTime(currentTime);
          }
          markDirty();
        }
      }
    }
    else if (canStatusChange && batteryEnergy == lastBatteryEnergy && currentStatus != EnergyFlowStatus.IDLE) {
      newStatus = EnergyFlowStatus.IDLE;
      setLastEnergyChangeTime(currentTime);
    }

    if (newStatus != currentStatus) {
      setEnergyFlowStatus(newStatus);
      markDirty();
    }
  }
}
