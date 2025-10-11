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

package de.markusbordihn.scraptechworkshop.block.entity.collectorstation;

import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CollectorStationContainer implements WorldlyContainer {

  private final NonNullList<ItemStack> items;
  private final CollectorStationBlockEntity blockEntity;

  public CollectorStationContainer(final CollectorStationBlockEntity blockEntity) {
    this.blockEntity = blockEntity;
    this.items = NonNullList.withSize(CollectorStationBlockEntity.TOTAL_SLOTS, ItemStack.EMPTY);
  }

  public NonNullList<ItemStack> getItems() {
    return this.items;
  }

  @Override
  public int getContainerSize() {
    return items.size();
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack itemstack : items) {
      if (!itemstack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getItem(int slot) {
    return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    ItemStack itemStack = ContainerHelper.removeItem(items, slot, amount);
    if (!itemStack.isEmpty()) {
      setChanged();
    }
    return itemStack;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return ContainerHelper.takeItem(items, slot);
  }

  @Override
  public void setItem(int slot, ItemStack itemStack) {
    if (slot >= 0 && slot < items.size()) {
      items.set(slot, itemStack);
      if (itemStack.getCount() > getMaxStackSize()) {
        itemStack.setCount(getMaxStackSize());
      }
      setChanged();
    }
  }

  @Override
  public void setChanged() {
    if (blockEntity != null) {
      blockEntity.setChanged();
    }
  }

  @Override
  public boolean stillValid(Player player) {
    if (blockEntity == null || blockEntity.getLevel() == null) {
      return false;
    }
    return Container.stillValidBlockEntity(blockEntity, player);
  }

  @Override
  public void clearContent() {
    items.clear();
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack itemStack) {
    if (slot == CollectorStationBlockEntity.BATTERY_SLOT) {
      return itemStack.getItem() instanceof EnergyCellItem;
    }
    if (slot >= CollectorStationBlockEntity.FIRST_STORAGE_SLOT
        && slot <= CollectorStationBlockEntity.LAST_STORAGE_SLOT) {
      return true;
    }
    if (slot >= CollectorStationBlockEntity.FIRST_UPGRADE_SLOT
        && slot <= CollectorStationBlockEntity.LAST_UPGRADE_SLOT) {
      return isValidUpgrade(itemStack);
    }
    return false;
  }

  private boolean isValidUpgrade(final ItemStack itemStack) {
    String itemName = itemStack.getItem().toString().toLowerCase();
    return itemName.contains("upgrade") || itemName.contains("module");
  }

  @Override
  public int[] getSlotsForFace(Direction direction) {
    if (direction == Direction.UP) {
      // Allow inserting into storage slots from above
      int[] storageSlots = new int[CollectorStationBlockEntity.STORAGE_SLOTS];
      for (int i = 0; i < CollectorStationBlockEntity.STORAGE_SLOTS; i++) {
        storageSlots[i] = CollectorStationBlockEntity.FIRST_STORAGE_SLOT + i;
      }
      return storageSlots;
    } else if (direction == Direction.DOWN) {
      // Allow extracting from storage slots from below
      int[] storageSlots = new int[CollectorStationBlockEntity.STORAGE_SLOTS];
      for (int i = 0; i < CollectorStationBlockEntity.STORAGE_SLOTS; i++) {
        storageSlots[i] = CollectorStationBlockEntity.FIRST_STORAGE_SLOT + i;
      }
      return storageSlots;
    }
    // Sides: only storage slots
    int[] storageSlots = new int[CollectorStationBlockEntity.STORAGE_SLOTS];
    for (int i = 0; i < CollectorStationBlockEntity.STORAGE_SLOTS; i++) {
      storageSlots[i] = CollectorStationBlockEntity.FIRST_STORAGE_SLOT + i;
    }
    return storageSlots;
  }

  @Override
  public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
    return slot >= CollectorStationBlockEntity.FIRST_STORAGE_SLOT
        && slot <= CollectorStationBlockEntity.LAST_STORAGE_SLOT
        && direction == Direction.UP;
  }

  @Override
  public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
    return slot >= CollectorStationBlockEntity.FIRST_STORAGE_SLOT
        && slot <= CollectorStationBlockEntity.LAST_STORAGE_SLOT;
  }
}
