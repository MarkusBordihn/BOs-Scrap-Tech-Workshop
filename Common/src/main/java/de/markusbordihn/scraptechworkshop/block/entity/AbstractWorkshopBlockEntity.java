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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractWorkshopBlockEntity extends BlockEntity
    implements MenuProvider, WorldlyContainer {

  protected AbstractWorkshopBlockEntity(
      final BlockEntityType<?> type, final BlockPos blockPos, final BlockState blockState) {
    super(type, blockPos, blockState);
  }

  protected abstract NonNullList<ItemStack> getItems();

  protected abstract WorldlyContainer getContainerDelegate();

  protected void loadItems(final CompoundTag compoundTag) {
    ContainerHelper.loadAllItems(compoundTag, getItems());
  }

  protected void saveItems(final CompoundTag compoundTag) {
    ContainerHelper.saveAllItems(compoundTag, getItems());
  }

  @Override
  public void load(CompoundTag compoundTag) {
    super.load(compoundTag);
    loadItems(compoundTag);
  }

  @Override
  protected void saveAdditional(CompoundTag compoundTag) {
    super.saveAdditional(compoundTag);
    saveItems(compoundTag);
  }

  @Override
  public int getContainerSize() {
    return getContainerDelegate().getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return getContainerDelegate().isEmpty();
  }

  @Override
  public ItemStack getItem(int slot) {
    return getContainerDelegate().getItem(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    return getContainerDelegate().removeItem(slot, amount);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return getContainerDelegate().removeItemNoUpdate(slot);
  }

  @Override
  public void setItem(int slot, ItemStack itemStack) {
    getContainerDelegate().setItem(slot, itemStack);
  }

  @Override
  public boolean stillValid(Player player) {
    return Container.stillValidBlockEntity(this, player);
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack itemStack) {
    return getContainerDelegate().canPlaceItem(slot, itemStack);
  }

  @Override
  public void clearContent() {
    getContainerDelegate().clearContent();
  }

  @Override
  public int[] getSlotsForFace(Direction direction) {
    return getContainerDelegate().getSlotsForFace(direction);
  }

  @Override
  public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
    return getContainerDelegate().canPlaceItemThroughFace(slot, itemStack, direction);
  }

  @Override
  public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
    return getContainerDelegate().canTakeItemThroughFace(slot, itemStack, direction);
  }
}
