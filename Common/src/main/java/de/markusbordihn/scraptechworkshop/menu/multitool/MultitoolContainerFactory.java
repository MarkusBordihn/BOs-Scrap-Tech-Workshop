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

import de.markusbordihn.scraptechworkshop.data.multitool.ScrapMultitoolData;
import de.markusbordihn.scraptechworkshop.item.tool.ScrapMultitoolItem;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class MultitoolContainerFactory {

  private static final int TOTAL_TOOL_SLOTS = 1 + ScrapMultitoolData.MODULE_SLOTS;

  private MultitoolContainerFactory() {}

  public static SimpleContainer createToolContainer(
      final ItemStack multitoolStack, final Runnable onChanged) {
    if (!(multitoolStack.getItem() instanceof ScrapMultitoolItem)) {
      throw new IllegalArgumentException("Invalid multitool ItemStack");
    }

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(multitoolStack);
    SimpleContainer container =
        new SimpleContainer(TOTAL_TOOL_SLOTS) {
          @Override
          public void setChanged() {
            super.setChanged();
            onChanged.run();
          }
        };

    container.setItem(0, data.battery());
    for (int i = 0; i < data.modules().length; i++) {
      ItemStack module = data.modules()[i];
      if (module != null) {
        container.setItem(i + 1, module);
      }
    }

    return container;
  }
}
