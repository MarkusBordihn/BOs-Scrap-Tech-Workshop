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

package de.markusbordihn.scraptechworkshop.menu;

import de.markusbordihn.scraptechworkshop.block.entity.recycler.RecyclerBlockEntity;
import de.markusbordihn.scraptechworkshop.menu.slots.DummySlot;
import de.markusbordihn.scraptechworkshop.menu.slots.RecyclerInputSlot;
import de.markusbordihn.scraptechworkshop.menu.slots.RecyclerOutputSlot;
import de.markusbordihn.scraptechworkshop.menu.slots.RecyclerUpgradeSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RecyclerMenu extends AbstractContainerMenu {

  // GUI Layout Constants
  public static final int INPUT_SLOT_X = 26;
  public static final int INPUT_SLOT_Y = 35;
  public static final int SLOT_SPACING = 18;

  public static final int OUTPUT_GRID_START_X = 116;
  public static final int OUTPUT_GRID_START_Y = 17;
  public static final int OUTPUT_GRID_ROWS = 3;
  public static final int OUTPUT_GRID_COLUMNS = 3;

  public static final int UPGRADE_SLOT_START_X = 62;
  public static final int UPGRADE_SLOT_Y = 71;

  public static final int PLAYER_INVENTORY_START_X = 8;
  public static final int PLAYER_INVENTORY_START_Y = 103;
  public static final int PLAYER_INVENTORY_ROWS = 3;
  public static final int PLAYER_INVENTORY_COLUMNS = 9;

  public static final int PLAYER_HOTBAR_START_X = 8;
  public static final int PLAYER_HOTBAR_Y = 161;
  public static final int PLAYER_HOTBAR_SLOTS = 9;

  // Progress Arrow Constants
  public static final int PROGRESS_ARROW_SIZE = 26;

  // Container Data Constants
  public static final int CONTAINER_DATA_SIZE = 2;
  public static final int PROGRESS_DATA_INDEX = 0;
  public static final int MAX_PROGRESS_DATA_INDEX = 1;

  // Note: MenuType will be registered by platform-specific code
  public static MenuType<RecyclerMenu> TYPE;

  private final RecyclerBlockEntity blockEntity;
  private final Level level;
  private final ContainerData data;
  private final SimpleContainer dummyContainer;

  public RecyclerMenu(int windowId, Inventory playerInventory, FriendlyByteBuf additionalData) {
    this(
        windowId,
        playerInventory,
        additionalData != null
            ? playerInventory.player.level().getBlockEntity(additionalData.readBlockPos())
            : null,
        new SimpleContainerData(CONTAINER_DATA_SIZE));
  }

  public RecyclerMenu(
      int windowId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
    super(TYPE, windowId);

    // Always set these fields first
    this.blockEntity = entity instanceof RecyclerBlockEntity recyclerEntity ? recyclerEntity : null;
    this.level = playerInventory.player.level();
    this.data = data != null ? data : new SimpleContainerData(CONTAINER_DATA_SIZE);
    this.dummyContainer = new SimpleContainer(RecyclerBlockEntity.TOTAL_SLOTS);

    // Always check container size with the total slot count
    checkContainerSize(playerInventory, RecyclerBlockEntity.TOTAL_SLOTS);

    // Add recycler slots first (always add them for consistent slot count)
    addRecyclerSlots();

    // Then add player inventory
    addPlayerInventory(playerInventory);
    addPlayerHotbar(playerInventory);

    addDataSlots(this.data);
  }

  private void addRecyclerSlots() {
    // Always add recycler slots, even if blockEntity is null
    // This ensures consistent slot count between server and client

    // Input slots (left side)
    for (int i = 0; i < RecyclerBlockEntity.INPUT_SLOTS; i++) {
      if (blockEntity != null) {
        this.addSlot(
            new RecyclerInputSlot(blockEntity, i, INPUT_SLOT_X, INPUT_SLOT_Y + i * SLOT_SPACING));
      } else {
        // Add dummy slot that doesn't accept items when blockEntity is null
        this.addSlot(
            new DummySlot(dummyContainer, i, INPUT_SLOT_X, INPUT_SLOT_Y + i * SLOT_SPACING));
      }
    }

    // Output slots (3x3 grid on the right)
    for (int row = 0; row < OUTPUT_GRID_ROWS; row++) {
      for (int col = 0; col < OUTPUT_GRID_COLUMNS; col++) {
        int slotIndex = RecyclerBlockEntity.INPUT_SLOTS + row * OUTPUT_GRID_COLUMNS + col;
        int x = OUTPUT_GRID_START_X + col * SLOT_SPACING;
        int y = OUTPUT_GRID_START_Y + row * SLOT_SPACING;

        if (blockEntity != null) {
          this.addSlot(new RecyclerOutputSlot(blockEntity, slotIndex, x, y));
        } else {
          // Add dummy slot when blockEntity is null
          this.addSlot(new DummySlot(dummyContainer, slotIndex, x, y));
        }
      }
    }

    // Upgrade slots (bottom)
    for (int i = 0; i < RecyclerBlockEntity.UPGRADE_SLOTS; i++) {
      int slotIndex = RecyclerBlockEntity.INPUT_SLOTS + RecyclerBlockEntity.OUTPUT_SLOTS + i;
      int x = UPGRADE_SLOT_START_X + i * SLOT_SPACING;

      if (blockEntity != null) {
        this.addSlot(new RecyclerUpgradeSlot(blockEntity, slotIndex, x, UPGRADE_SLOT_Y));
      } else {
        // Add dummy slot when blockEntity is null
        this.addSlot(new DummySlot(dummyContainer, slotIndex, x, UPGRADE_SLOT_Y));
      }
    }
  }

  private void addPlayerInventory(Inventory playerInventory) {
    for (int row = 0; row < PLAYER_INVENTORY_ROWS; ++row) {
      for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; ++col) {
        int x = PLAYER_INVENTORY_START_X + col * SLOT_SPACING;
        int y = PLAYER_INVENTORY_START_Y + row * SLOT_SPACING;
        int slotIndex = col + row * PLAYER_INVENTORY_COLUMNS + PLAYER_INVENTORY_COLUMNS;

        this.addSlot(new Slot(playerInventory, slotIndex, x, y));
      }
    }
  }

  private void addPlayerHotbar(Inventory playerInventory) {
    for (int col = 0; col < PLAYER_HOTBAR_SLOTS; ++col) {
      int x = PLAYER_HOTBAR_START_X + col * SLOT_SPACING;
      this.addSlot(new Slot(playerInventory, col, x, PLAYER_HOTBAR_Y));
    }
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack itemStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);

    if (slot.hasItem()) {
      ItemStack slotStack = slot.getItem();
      itemStack = slotStack.copy();

      // Now recycler slots are always present, so calculations are consistent
      int playerInventoryEnd =
          RecyclerBlockEntity.TOTAL_SLOTS + (PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS);
      int playerHotbarEnd = playerInventoryEnd + PLAYER_HOTBAR_SLOTS;

      if (blockEntity != null) {
        // Normal recycler behavior when blockEntity is available
        int inputEnd = RecyclerBlockEntity.INPUT_SLOTS;
        int outputEnd = inputEnd + RecyclerBlockEntity.OUTPUT_SLOTS;
        int upgradeEnd = outputEnd + RecyclerBlockEntity.UPGRADE_SLOTS;

        if (index < inputEnd) {
          // Moving from input slots
          if (!this.moveItemStackTo(
              slotStack, RecyclerBlockEntity.TOTAL_SLOTS, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < outputEnd) {
          // Moving from output slots
          if (!this.moveItemStackTo(
              slotStack, RecyclerBlockEntity.TOTAL_SLOTS, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < upgradeEnd) {
          // Moving from upgrade slots
          if (!this.moveItemStackTo(
              slotStack, RecyclerBlockEntity.TOTAL_SLOTS, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < playerInventoryEnd) {
          // Moving from player inventory
          if (!this.moveItemStackTo(slotStack, 0, inputEnd, false)
              && !this.moveItemStackTo(slotStack, outputEnd, upgradeEnd, false)
              && !this.moveItemStackTo(slotStack, playerInventoryEnd, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < playerHotbarEnd) {
          // Moving from player hotbar
          if (!this.moveItemStackTo(slotStack, 0, inputEnd, false)
              && !this.moveItemStackTo(slotStack, outputEnd, upgradeEnd, false)
              && !this.moveItemStackTo(
                  slotStack, RecyclerBlockEntity.TOTAL_SLOTS, playerInventoryEnd, false)) {
            return ItemStack.EMPTY;
          }
        }
      } else {
        // Simplified behavior when no valid blockEntity is available
        // Only allow movement between player inventory and hotbar
        if (index < playerInventoryEnd) {
          // Moving from player inventory to hotbar
          if (!this.moveItemStackTo(slotStack, playerInventoryEnd, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < playerHotbarEnd) {
          // Moving from player hotbar to inventory
          if (!this.moveItemStackTo(
              slotStack, RecyclerBlockEntity.TOTAL_SLOTS, playerInventoryEnd, false)) {
            return ItemStack.EMPTY;
          }
        }
      }

      if (slotStack.isEmpty()) {
        slot.setByPlayer(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
    }

    return itemStack;
  }

  @Override
  public boolean stillValid(Player player) {
    if (blockEntity == null) {
      return false;
    }
    return stillValid(
        ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
        player,
        blockEntity.getBlockState().getBlock());
  }

  public boolean isCrafting() {
    return data.get(PROGRESS_DATA_INDEX) > 0;
  }

  public int getScaledProgress() {
    int progress = this.data.get(PROGRESS_DATA_INDEX);
    int maxProgress = this.data.get(MAX_PROGRESS_DATA_INDEX);

    return maxProgress != 0 && progress != 0 ? progress * PROGRESS_ARROW_SIZE / maxProgress : 0;
  }

  public RecyclerBlockEntity getBlockEntity() {
    return blockEntity;
  }
}
