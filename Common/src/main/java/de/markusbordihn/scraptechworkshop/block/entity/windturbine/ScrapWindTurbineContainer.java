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

package de.markusbordihn.scraptechworkshop.block.entity.windturbine;

import de.markusbordihn.scraptechworkshop.item.component.EmptyEnergyCellItem;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ScrapWindTurbineContainer implements WorldlyContainer {

  private static final int[] BATTERY_SLOTS =
      new int[] {
        ScrapWindTurbineSlots.ENERGY_TAB_BATTERY_SLOT, ScrapWindTurbineSlots.CHARGING_BATTERY_SLOT
      };

  private final NonNullList<ItemStack> items;
  private final Runnable onContentsChanged;

  public ScrapWindTurbineContainer(int size, Runnable onContentsChanged) {
    this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    this.onContentsChanged = onContentsChanged;
  }

  @Override
  public int[] getSlotsForFace(Direction side) {
    return BATTERY_SLOTS;
  }

  @Override
  public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
    if (index == ScrapWindTurbineSlots.ENERGY_TAB_BATTERY_SLOT
        || index == ScrapWindTurbineSlots.CHARGING_BATTERY_SLOT) {
      return itemStack.getItem() instanceof EmptyEnergyCellItem;
    }
    return false;
  }

  @Override
  public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
    if (index == ScrapWindTurbineSlots.ENERGY_TAB_BATTERY_SLOT
        || index == ScrapWindTurbineSlots.CHARGING_BATTERY_SLOT) {
      return stack.getItem() instanceof EnergyCellItem;
    }
    return false;
  }

  @Override
  public int getContainerSize() {
    return items.size();
  }

  @Override
  public boolean isEmpty() {
    return items.stream().allMatch(ItemStack::isEmpty);
  }

  @Override
  public ItemStack getItem(int slot) {
    return items.get(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    ItemStack result = items.get(slot).split(amount);
    if (!result.isEmpty()) {
      setChanged();
    }
    return result;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return items.set(slot, ItemStack.EMPTY);
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    items.set(slot, stack);
    if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
      stack.setCount(getMaxStackSize());
    }
    setChanged();
  }

  @Override
  public void setChanged() {
    if (onContentsChanged != null) {
      onContentsChanged.run();
    }
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

  @Override
  public void clearContent() {
    items.clear();
  }

  public NonNullList<ItemStack> getItems() {
    return items;
  }
}
