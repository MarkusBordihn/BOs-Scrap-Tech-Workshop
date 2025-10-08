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

import de.markusbordihn.scraptechworkshop.data.multitool.DisplayMode;
import de.markusbordihn.scraptechworkshop.data.multitool.ScrapMultitoolData;
import de.markusbordihn.scraptechworkshop.data.multitool.ToolMode;
import de.markusbordihn.scraptechworkshop.item.tool.ScrapMultitoolItem;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class MultitoolDataSaver {
  private MultitoolDataSaver() {}

  public static void saveToMultitool(
      final ItemStack multitoolStack, final SimpleContainer toolContainer) {
    if (!(multitoolStack.getItem() instanceof ScrapMultitoolItem) || toolContainer == null) {
      return;
    }

    ItemStack[] modules = new ItemStack[ScrapMultitoolData.MODULE_SLOTS];
    for (int i = 0; i < modules.length; i++) {
      modules[i] = toolContainer.getItem(i + 1);
    }

    ScrapMultitoolData currentData = ScrapMultitoolData.fromItemStack(multitoolStack);
    ScrapMultitoolData newData =
        new ScrapMultitoolData(
            toolContainer.getItem(0),
            modules,
            currentData.hologramColor(),
            currentData.hudEnabled(),
            currentData.toolPriority(),
            currentData.activeMode());

    newData.saveToItemStack(multitoolStack);

    if (multitoolStack.getItem() instanceof ScrapMultitoolItem multitoolItem) {
      multitoolItem.syncEnergyWithBattery(multitoolStack);
    }

    DisplayMode displayMode = new DisplayMode(multitoolStack);
    displayMode.updateModel(ToolMode.fromId(newData.activeMode()), newData.getBatteryLevel());
  }
}
