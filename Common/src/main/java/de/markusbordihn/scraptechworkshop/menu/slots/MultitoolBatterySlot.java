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

package de.markusbordihn.scraptechworkshop.menu.slots;

import de.markusbordihn.scraptechworkshop.energy.EnergyCell;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MultitoolBatterySlot extends Slot {

  public MultitoolBatterySlot(final Container container, final int slot, final int x, final int y) {
    super(container, slot, x, y);
  }

  @Override
  public boolean mayPlace(ItemStack itemStack) {
    return itemStack.getItem() instanceof EnergyCell;
  }

  @Override
  public void setByPlayer(ItemStack itemStack) {
    super.setByPlayer(itemStack);
  }

  @Override
  public ItemStack remove(int amount) {
    ItemStack result = super.remove(amount);
    return result;
  }

  @Override
  public void setChanged() {
    super.setChanged();
  }

  @Override
  public int getMaxStackSize() {
    return 1;
  }
}
