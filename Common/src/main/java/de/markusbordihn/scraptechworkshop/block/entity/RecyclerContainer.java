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

package de.markusbordihn.scraptechworkshop.block.entity;

import java.util.Arrays;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class RecyclerContainer implements WorldlyContainer {

  private final ItemStack[] items;
  private final Runnable onChanged;

  public RecyclerContainer(final int totalSlots, final Runnable onChanged) {
    this.items = new ItemStack[totalSlots];
    Arrays.fill(items, ItemStack.EMPTY);
    this.onChanged = onChanged;
  }

  public ItemStack[] getItems() {
    return items;
  }

  @Override
  public int getContainerSize() {
    return items.length;
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack item : items) {
      if (!item.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getItem(final int slot) {
    if (slot < 0 || slot >= items.length) {
      return ItemStack.EMPTY;
    }
    return items[slot];
  }

  @Override
  public ItemStack removeItem(final int slot, final int amount) {
    if (slot < 0 || slot >= items.length || items[slot].isEmpty()) {
      return ItemStack.EMPTY;
    }

    ItemStack result = items[slot].split(amount);
    if (items[slot].isEmpty()) {
      items[slot] = ItemStack.EMPTY;
    }
    onChanged.run();
    return result;
  }

  @Override
  public ItemStack removeItemNoUpdate(final int slot) {
    if (slot < 0 || slot >= items.length) {
      return ItemStack.EMPTY;
    }

    ItemStack result = items[slot];
    items[slot] = ItemStack.EMPTY;
    return result;
  }

  @Override
  public void setItem(final int slot, final ItemStack itemStack) {
    if (slot >= 0 && slot < items.length) {
      items[slot] = itemStack;
      if (!itemStack.isEmpty() && itemStack.getCount() > getMaxStackSize()) {
        itemStack.setCount(getMaxStackSize());
      }
      onChanged.run();
    }
  }

  @Override
  public boolean stillValid(final Player player) {
    return true;
  }

  @Override
  public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
    return slot == RecyclerSlots.INPUT_SLOT;
  }

  @Override
  public void clearContent() {
    Arrays.fill(items, ItemStack.EMPTY);
    onChanged.run();
  }

  @Override
  public void setChanged() {
    onChanged.run();
  }

  @Override
  public int[] getSlotsForFace(final Direction direction) {
    if (direction == Direction.UP) {
      return new int[] {RecyclerSlots.INPUT_SLOT};
    } else if (direction == Direction.DOWN) {
      return new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
    }
    return new int[] {};
  }

  @Override
  public boolean canPlaceItemThroughFace(
      final int slot, final ItemStack itemStack, final Direction direction) {
    return slot == RecyclerSlots.INPUT_SLOT && direction == Direction.UP;
  }

  @Override
  public boolean canTakeItemThroughFace(
      final int slot, final ItemStack itemStack, final Direction direction) {
    return slot >= RecyclerSlots.FIRST_OUTPUT_SLOT && slot <= RecyclerSlots.LAST_OUTPUT_SLOT;
  }
}
