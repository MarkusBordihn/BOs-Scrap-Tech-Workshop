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

import net.minecraft.world.item.ItemStack;

public record EnergyPowerData(
    int currentEnergy,
    ItemStack battery,
    EnergyDebounceData debounceData,
    int chargeCycleCount,
    long lastEnergyChangeTime,
    int lastEnergyLevel,
    EnergyFlowStatus energyFlowStatus) {

  public static EnergyPowerData empty() {
    return new EnergyPowerData(
        0,
        ItemStack.EMPTY,
        EnergyDebounceData.empty(),
        0,
        0,
        0,
        EnergyFlowStatus.NO_BATTERY);
  }

  public static EnergyPowerData withEnergy(int energy) {
    return new EnergyPowerData(
        energy,
        ItemStack.EMPTY,
        EnergyDebounceData.empty(),
        0,
        0,
        energy,
        EnergyFlowStatus.IDLE);
  }

  public EnergyPowerData withCurrentEnergy(int newEnergy) {
    return new EnergyPowerData(
        newEnergy,
        battery,
        debounceData,
        chargeCycleCount,
        lastEnergyChangeTime,
        lastEnergyLevel,
        energyFlowStatus);
  }

  public EnergyPowerData withBattery(ItemStack newBattery) {
    return new EnergyPowerData(
        currentEnergy,
        newBattery,
        debounceData,
        chargeCycleCount,
        lastEnergyChangeTime,
        lastEnergyLevel,
        energyFlowStatus);
  }

  public EnergyPowerData withDebounceData(EnergyDebounceData newDebounceData) {
    return new EnergyPowerData(
        currentEnergy,
        battery,
        newDebounceData,
        chargeCycleCount,
        lastEnergyChangeTime,
        lastEnergyLevel,
        energyFlowStatus);
  }

  public EnergyPowerData withChargeCycleCount(int newCycleCount) {
    return new EnergyPowerData(
        currentEnergy,
        battery,
        debounceData,
        newCycleCount,
        lastEnergyChangeTime,
        lastEnergyLevel,
        energyFlowStatus);
  }

  public EnergyPowerData withLastEnergyChangeTime(long newTime) {
    return new EnergyPowerData(
        currentEnergy,
        battery,
        debounceData,
        chargeCycleCount,
        newTime,
        lastEnergyLevel,
        energyFlowStatus);
  }

  public EnergyPowerData withLastEnergyLevel(int newLevel) {
    return new EnergyPowerData(
        currentEnergy,
        battery,
        debounceData,
        chargeCycleCount,
        lastEnergyChangeTime,
        newLevel,
        energyFlowStatus);
  }

  public EnergyPowerData withEnergyFlowStatus(EnergyFlowStatus newStatus) {
    return new EnergyPowerData(
        currentEnergy,
        battery,
        debounceData,
        chargeCycleCount,
        lastEnergyChangeTime,
        lastEnergyLevel,
        newStatus);
  }
}
