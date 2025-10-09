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

import de.markusbordihn.scraptechworkshop.data.multitool.ScrapMultitoolData;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import net.minecraft.world.item.ItemStack;

public interface EnergyCellConsumer {

  int getMaxEnergy();

  default ItemStack getBattery(final ItemStack itemStack) {
    if (itemStack.getItem() instanceof EnergyCellConsumer) {
      ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
      return data.battery();
    }
    return ItemStack.EMPTY;
  }

  default void setBattery(final ItemStack consumer, final ItemStack battery) {
    if (consumer.getItem() instanceof EnergyCellConsumer) {
      ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(consumer);
      ScrapMultitoolData updatedData = data.withBattery(battery);
      updatedData.saveToItemStack(consumer);
    }
  }

  default boolean hasBattery(final ItemStack consumer) {
    ItemStack battery = getBattery(consumer);
    return !battery.isEmpty() && battery.getItem() instanceof EnergyCellItem;
  }

  default boolean hasEnergy(final ItemStack consumer, final int amount) {
    ItemStack battery = getBattery(consumer);
    if (!hasBattery(consumer)) {
      return false;
    }
    return EnergyManager.hasEnergyFromBattery(consumer, getMaxEnergy(), battery, amount);
  }

  default boolean consumeEnergy(final ItemStack itemStack, final int amount) {
    if (!hasBattery(itemStack)) {
      return false;
    }

    ItemStack battery = getBattery(itemStack);
    if (!(battery.getItem() instanceof EnergyCellItem batteryItem)) {
      return false;
    }

    // Check if battery has enough energy
    if (batteryItem.getEnergy(battery) < amount) {
      return false;
    }

    // Consume energy from battery (modifies battery ItemStack directly)
    batteryItem.consumeEnergy(battery, amount);

    // Save modified battery back to consumer
    setBattery(itemStack, battery);

    // Sync display energy
    syncEnergyDisplay(itemStack);

    return true;
  }

  default void syncEnergyDisplay(final ItemStack itemStack) {
    if (!hasBattery(itemStack)) {
      EnergyManager.setEnergy(itemStack, getMaxEnergy(), 0);
      return;
    }

    ItemStack battery = getBattery(itemStack);
    if (battery.getItem() instanceof EnergyCellItem batteryItem) {
      int batteryEnergy = batteryItem.getEnergy(battery);

      // If battery is empty/depleted, replace with empty battery
      if (batteryEnergy <= 1) {
        ItemStack emptyBattery = batteryItem.createEmptyBattery();
        setBattery(itemStack, emptyBattery);
        EnergyManager.setEnergy(itemStack, getMaxEnergy(), 0);
        return;
      }

      // Sync display energy based on battery level
      EnergyManager.syncEnergyWithBattery(itemStack, getMaxEnergy(), battery);
    } else {
      EnergyManager.setEnergy(itemStack, getMaxEnergy(), 0);
    }
  }

  default float getEnergyPercentage(final ItemStack itemStack) {
    if (!hasBattery(itemStack)) {
      return 0.0f;
    }

    ItemStack battery = getBattery(itemStack);
    if (battery.getItem() instanceof EnergyCellItem batteryItem) {
      int batteryEnergy = batteryItem.getEnergy(battery);
      return (float) batteryEnergy / EnergyCellItem.ENERGY_MAX;
    }

    return 0.0f;
  }
}
