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

package de.markusbordihn.scraptechworkshop.block.entity.floatingscrapcollector;

import static de.markusbordihn.scraptechworkshop.block.entity.floatingscrapcollector.FloatingScrapCollectorSlots.*;

import de.markusbordihn.scraptechworkshop.item.ScrapFilterItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FloatingScrapCollectorContainer implements WorldlyContainer {

  private final NonNullList<ItemStack> items;
  private final FloatingScrapCollectorBlockEntity blockEntity;

  public FloatingScrapCollectorContainer(FloatingScrapCollectorBlockEntity blockEntity) {
    this.items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    this.blockEntity = blockEntity;
  }

  public NonNullList<ItemStack> getItems() {
    return this.items;
  }

  @Override
  public int getContainerSize() {
    return TOTAL_SLOTS;
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack itemStack : this.items) {
      if (!itemStack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getItem(int slot) {
    return slot >= 0 && slot < this.items.size() ? this.items.get(slot) : ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    ItemStack itemStack = ContainerHelper.removeItem(this.items, slot, amount);
    if (!itemStack.isEmpty()) {
      this.setChanged();
    }
    return itemStack;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return ContainerHelper.takeItem(this.items, slot);
  }

  @Override
  public void setItem(int slot, ItemStack itemStack) {
    this.items.set(slot, itemStack);
    if (!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
      itemStack.setCount(this.getMaxStackSize());
    }
    this.setChanged();
  }

  @Override
  public void setChanged() {
    if (blockEntity != null) {
      blockEntity.setChanged();
    }
  }

  @Override
  public boolean stillValid(Player player) {
    return blockEntity != null && blockEntity.stillValid(player);
  }

  @Override
  public void clearContent() {
    this.items.clear();
  }

  @Override
  public int[] getSlotsForFace(net.minecraft.core.Direction side) {
    return new int[0];
  }

  @Override
  public boolean canPlaceItemThroughFace(
      int slot, ItemStack stack, net.minecraft.core.Direction dir) {
    return canPlaceItem(slot, stack);
  }

  @Override
  public boolean canTakeItemThroughFace(
      int slot, ItemStack stack, net.minecraft.core.Direction dir) {
    return slot >= FIRST_OUTPUT_SLOT;
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    if (slot == NET_SLOT) {
      return stack.getItem() instanceof ScrapFilterItem;
    }
    return false;
  }
}
