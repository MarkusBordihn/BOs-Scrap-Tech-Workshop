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

package de.markusbordihn.scraptechworkshop.menu.multitool;

import de.markusbordihn.scraptechworkshop.config.MultitoolConfig;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class MultitoolBatteryInfo {
  private MultitoolBatteryInfo() {}

  public static int getCurrentEnergy(final SimpleContainer toolContainer) {
    ItemStack battery = toolContainer.getItem(0);
    if (battery.getItem() instanceof EnergyCellItem batteryItem) {
      int batteryEnergy = batteryItem.getEnergy(battery);
      return (batteryEnergy <= 1) ? 0 : batteryEnergy;
    }
    return 0;
  }

  public static int getMaxEnergy(final SimpleContainer toolContainer) {
    ItemStack battery = toolContainer.getItem(0);
    if (battery.getItem() instanceof EnergyCellItem) {
      return EnergyCellItem.ENERGY_MAX;
    }
    return MultitoolConfig.energyMax;
  }

  public static int getPercentage(final SimpleContainer toolContainer) {
    ItemStack battery = toolContainer.getItem(0);
    if (battery.getItem() instanceof EnergyCellItem batteryItem) {
      int batteryEnergy = batteryItem.getEnergy(battery);
      if (batteryEnergy <= 1) {
        return 0;
      }
      return Math.round(batteryItem.getEnergyPercentage(battery) * 100);
    }
    return 0;
  }
}
