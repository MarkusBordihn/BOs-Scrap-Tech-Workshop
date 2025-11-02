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

package de.markusbordihn.scraptechworkshop.data.energy;

import net.minecraft.world.item.ItemStack;

public record EnergyPowerData(
    int currentEnergy,
    ItemStack battery,
    int chargeCycleCount,
    long lastEnergyChangeTime,
    int lastEnergyLevel,
    EnergyFlowStatus energyFlowStatus,
    ExternalEnergyFlowStatus externalEnergyFlowStatus) {

  public static EnergyPowerData empty() {
    return new EnergyPowerData(
        0, ItemStack.EMPTY, 0, 0, 0, EnergyFlowStatus.NO_BATTERY, ExternalEnergyFlowStatus.IDLE);
  }

  public static EnergyPowerData withEnergy(final int energy) {
    return new EnergyPowerData(
        energy,
        ItemStack.EMPTY,
        0,
        0,
        energy,
        EnergyFlowStatus.IDLE,
        ExternalEnergyFlowStatus.IDLE);
  }

  public EnergyPowerData withCurrentEnergy(final int newEnergy) {
    return new EnergyPowerData(
        newEnergy,
        battery,
        chargeCycleCount,
        lastEnergyChangeTime,
        lastEnergyLevel,
        energyFlowStatus,
        externalEnergyFlowStatus);
  }

  public EnergyPowerData withBattery(final ItemStack newBattery) {
    return new EnergyPowerData(
        currentEnergy,
        newBattery,
        chargeCycleCount,
        lastEnergyChangeTime,
        lastEnergyLevel,
        energyFlowStatus,
        externalEnergyFlowStatus);
  }

  public EnergyPowerData withChargeCycleCount(final int newCycleCount) {
    return new EnergyPowerData(
        currentEnergy,
        battery,
        newCycleCount,
        lastEnergyChangeTime,
        lastEnergyLevel,
        energyFlowStatus,
        externalEnergyFlowStatus);
  }

  public EnergyPowerData withLastEnergyChangeTime(final long newTime) {
    return new EnergyPowerData(
        currentEnergy,
        battery,
        chargeCycleCount,
        newTime,
        lastEnergyLevel,
        energyFlowStatus,
        externalEnergyFlowStatus);
  }

  public EnergyPowerData withLastEnergyLevel(final int newLevel) {
    return new EnergyPowerData(
        currentEnergy,
        battery,
        chargeCycleCount,
        lastEnergyChangeTime,
        newLevel,
        energyFlowStatus,
        externalEnergyFlowStatus);
  }

  public EnergyPowerData withEnergyFlowStatus(final EnergyFlowStatus newStatus) {
    return new EnergyPowerData(
        currentEnergy,
        battery,
        chargeCycleCount,
        lastEnergyChangeTime,
        lastEnergyLevel,
        newStatus,
        externalEnergyFlowStatus);
  }

  public EnergyPowerData withExternalEnergyFlowStatus(final ExternalEnergyFlowStatus newStatus) {
    return new EnergyPowerData(
        currentEnergy,
        battery,
        chargeCycleCount,
        lastEnergyChangeTime,
        lastEnergyLevel,
        energyFlowStatus,
        newStatus);
  }
}
