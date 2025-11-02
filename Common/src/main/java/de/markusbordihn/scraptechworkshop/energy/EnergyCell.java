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

public interface EnergyCell {

  int getCapacity();

  int getChargeRate();

  int getDischargeRate();

  ItemStack createEmptyBattery();

  default int getEnergy(final ItemStack itemStack) {
    if (itemStack.getDamageValue() > getCapacity() - 1) {
      itemStack.setDamageValue(getCapacity() - 1);
    }
    return Math.max(1, getCapacity() - itemStack.getDamageValue());
  }

  default void setEnergy(final ItemStack itemStack, final int energy) {
    itemStack.setDamageValue(
        Math.min(getCapacity() - Math.max(1, Math.min(energy, getCapacity())), getCapacity() - 1));
  }

  default void consumeEnergy(final ItemStack itemStack, final int amount) {
    setEnergy(itemStack, Math.max(1, getEnergy(itemStack) - amount));
  }

  default boolean hasEnergy(final ItemStack itemStack, final int amount) {
    return getEnergy(itemStack) >= amount;
  }

  default int addEnergy(final ItemStack itemStack, final int amount) {
    int currentEnergy = getEnergy(itemStack);
    int transferAmount = Math.min(amount, getChargeRate());
    int newEnergy = Math.min(getCapacity(), currentEnergy + transferAmount);
    setEnergy(itemStack, newEnergy);
    return newEnergy - currentEnergy;
  }

  default int addEnergy(final ItemStack itemStack) {
    return addEnergy(itemStack, getChargeRate());
  }

  default int extractEnergy(final ItemStack itemStack, final int maxAmount) {
    int currentEnergy = getEnergy(itemStack);
    int amount = Math.min(maxAmount, Math.min(currentEnergy - 1, getDischargeRate()));
    if (amount > 0) {
      consumeEnergy(itemStack, amount);
    }
    return amount;
  }

  default int extractEnergy(final ItemStack itemStack) {
    return extractEnergy(itemStack, getDischargeRate());
  }

  default float getEnergyPercentage(final ItemStack itemStack) {
    return (float) getEnergy(itemStack) / getCapacity();
  }

  default int getBarColor(final ItemStack itemStack) {
    float energyRatio = getEnergyPercentage(itemStack);
    return energyRatio > 0.6f ? 0x00FF00 : energyRatio > 0.3f ? 0xFFFF00 : 0xFF0000;
  }
}
