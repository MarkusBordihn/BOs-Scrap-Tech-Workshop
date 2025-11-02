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

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.entity.recycler.RecyclerBlockEntity;
import de.markusbordihn.scraptechworkshop.block.entity.recycler.RecyclerSlots;
import de.markusbordihn.scraptechworkshop.block.recycler.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.data.recycler.RecyclerStatus;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerConsumer;
import de.markusbordihn.scraptechworkshop.menu.slots.DummySlot;
import de.markusbordihn.scraptechworkshop.menu.slots.EnergyCellSlot;
import de.markusbordihn.scraptechworkshop.menu.slots.RecyclerInputSlot;
import de.markusbordihn.scraptechworkshop.menu.slots.RecyclerOutputSlot;
import de.markusbordihn.scraptechworkshop.menu.slots.RecyclerUpgradeSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecyclerMenu extends EnergyPowerMenu {

  // Main window positions
  public static final int INPUT_SLOT_X = 26;
  public static final int INPUT_SLOT_Y = 35;
  public static final int OUTPUT_GRID_START_X = 116;
  public static final int OUTPUT_GRID_START_Y = 17;
  public static final int OUTPUT_GRID_ROWS = 3;
  public static final int OUTPUT_GRID_COLUMNS = 3;
  public static final int UPGRADE_SLOT_START_X = 62;
  public static final int UPGRADE_SLOT_Y = 71;
  public static final int PLAYER_INVENTORY_START_Y = 124;
  public static final int PROGRESS_ARROW_SIZE = 26;

  public static final int ADDITIONAL_CONTAINER_DATA_SIZE = 2;
  public static final int PROGRESS_DATA_INDEX = 0;
  public static final int MAX_PROGRESS_DATA_INDEX = 1;
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String LOG_PREFIX = "[RECYCLER]";

  public static MenuType<RecyclerMenu> MENU_TYPE;

  private final RecyclerBlockEntity blockEntity;
  private final Level level;
  private final ContainerData additionalData;
  private final SimpleContainer dummyContainer;
  private final BlockPos blockPos;

  public RecyclerMenu(int windowId, Inventory playerInventory, FriendlyByteBuf additionalData) {
    this(
        windowId,
        playerInventory,
        getBlockEntityFromData(playerInventory, additionalData),
        new SimpleContainerData(ADDITIONAL_CONTAINER_DATA_SIZE));
  }

  public RecyclerMenu(
      int windowId, Inventory playerInventory, BlockEntity entity, ContainerData additionalData) {
    super(
        MENU_TYPE,
        windowId,
        entity instanceof EnergyPowerConsumer consumer
            ? consumer.getEnergyPowerData()
            : new SimpleContainerData(6));
    this.level = playerInventory.player.level();
    this.additionalData =
        additionalData != null
            ? additionalData
            : new SimpleContainerData(ADDITIONAL_CONTAINER_DATA_SIZE);
    this.dummyContainer = new SimpleContainer(RecyclerSlots.TOTAL_SLOTS);

    if (entity instanceof RecyclerBlockEntity recyclerEntity) {
      this.blockEntity = recyclerEntity;
      this.blockPos = recyclerEntity.getBlockPos();
    } else {
      log.error(
          "{} Expected RecyclerBlockEntity but got {} at {}",
          LOG_PREFIX,
          entity,
          entity != null ? entity.getBlockPos() : "NULL");
      this.blockEntity = null;
      this.blockPos = null;
    }

    checkContainerSize(playerInventory, RecyclerSlots.TOTAL_SLOTS);
    addRecyclerSlots();
    addPlayerInventoryAndHotbar(playerInventory, PLAYER_INVENTORY_START_Y);
    addDataSlots(this.additionalData);
  }

  private static BlockEntity getBlockEntityFromData(
      Inventory playerInventory, FriendlyByteBuf additionalData) {
    if (additionalData == null) {
      log.error("{} additionalData is NULL", LOG_PREFIX);
      return null;
    }

    try {
      BlockPos pos = additionalData.readBlockPos();
      Level level = playerInventory.player.level();
      BlockEntity blockEntity = level.getBlockEntity(pos);

      if (blockEntity == null) {
        log.error("{} BlockEntity is NULL at {}", LOG_PREFIX, pos);
        return null;
      }

      return blockEntity;

    } catch (Exception e) {
      log.error("{} Error reading BlockEntity from additionalData: {}", LOG_PREFIX, e.getMessage());
      return null;
    }
  }

  private void addRecyclerSlots() {
    RecyclerBlockEntity entityToUse = getValidBlockEntity();
    if (entityToUse != null) {
      // Input slot
      this.addSlot(new RecyclerInputSlot(entityToUse, 0, INPUT_SLOT_X, INPUT_SLOT_Y));

      // Output slots (3x3)
      int outputStartIndex = 1;
      for (int row = 0; row < OUTPUT_GRID_ROWS; row++) {
        for (int col = 0; col < OUTPUT_GRID_COLUMNS; col++) {
          this.addSlot(
              new RecyclerOutputSlot(
                  entityToUse,
                  outputStartIndex++,
                  OUTPUT_GRID_START_X + col * SLOT_SPACING,
                  OUTPUT_GRID_START_Y + row * SLOT_SPACING));
        }
      }

      // Upgrade slots
      this.addSlot(
          new RecyclerUpgradeSlot(
              entityToUse, RecyclerSlots.FIRST_UPGRADE_SLOT, UPGRADE_SLOT_START_X, UPGRADE_SLOT_Y));
      this.addSlot(
          new RecyclerUpgradeSlot(
              entityToUse,
              RecyclerSlots.LAST_UPGRADE_SLOT,
              UPGRADE_SLOT_START_X + SLOT_SPACING,
              UPGRADE_SLOT_Y));

      // Battery slot (using unified energy tab positions)
      this.addSlot(
          new EnergyCellSlot(
              entityToUse,
              RecyclerSlots.BATTERY_SLOT,
              ENERGY_TAB_BATTERY_SLOT_X,
              ENERGY_TAB_BATTERY_SLOT_Y));
    } else {
      // Add dummy slots if no block entity
      for (int i = 0; i < RecyclerSlots.TOTAL_SLOTS; i++) {
        this.addSlot(new DummySlot(dummyContainer, i, -1000, -1000));
      }
    }
  }

  private RecyclerBlockEntity getValidBlockEntity() {
    if (this.blockEntity != null) {
      return this.blockEntity;
    }

    if (this.blockPos != null && this.level != null) {
      BlockEntity entity = this.level.getBlockEntity(this.blockPos);
      if (entity instanceof RecyclerBlockEntity recyclerEntity) {
        log.debug("{} Resolved BlockEntity on delayed lookup", LOG_PREFIX);
        return recyclerEntity;
      }
    }

    return null;
  }

  public ItemStack getCurrentInput() {
    return this.blockEntity != null ? this.blockEntity.getItem(0) : ItemStack.EMPTY;
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack itemStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);

    if (slot.hasItem()) {
      ItemStack slotStack = slot.getItem();
      itemStack = slotStack.copy();
      int playerInventoryEnd = RecyclerSlots.TOTAL_SLOTS + 27; // 3 rows * 9 columns
      int playerHotbarEnd = playerInventoryEnd + 9; // 9 hotbar slots

      if (blockEntity != null) {
        int inputEnd = RecyclerSlots.INPUT_SLOTS;
        int outputEnd = inputEnd + RecyclerSlots.OUTPUT_SLOTS;
        int upgradeEnd = outputEnd + RecyclerSlots.UPGRADE_SLOTS;
        int batteryEnd = upgradeEnd + RecyclerSlots.BATTERY_SLOTS;

        if (index < inputEnd) {
          // Moving from input slots
          if (!this.moveItemStackTo(slotStack, RecyclerSlots.TOTAL_SLOTS, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < outputEnd) {
          // Moving from output slots
          if (!this.moveItemStackTo(slotStack, RecyclerSlots.TOTAL_SLOTS, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < upgradeEnd) {
          // Moving from upgrade slots
          if (!this.moveItemStackTo(slotStack, RecyclerSlots.TOTAL_SLOTS, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < batteryEnd) {
          // Moving from battery slot
          if (!this.moveItemStackTo(slotStack, RecyclerSlots.TOTAL_SLOTS, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < playerInventoryEnd || index < playerHotbarEnd) {
          // Moving from player inventory or hotbar
          if (slotStack.getItem()
              instanceof de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem) {
            // Try to move battery to battery slot
            if (!this.moveItemStackTo(
                slotStack, RecyclerSlots.BATTERY_SLOT, RecyclerSlots.BATTERY_SLOT + 1, false)) {
              return ItemStack.EMPTY;
            }
          } else if (!this.moveItemStackTo(slotStack, 0, inputEnd, false)
              && !this.moveItemStackTo(slotStack, outputEnd, upgradeEnd, false)) {
            // Move between inventory and hotbar
            if (index < playerInventoryEnd) {
              if (!this.moveItemStackTo(slotStack, playerInventoryEnd, playerHotbarEnd, false)) {
                return ItemStack.EMPTY;
              }
            } else {
              if (!this.moveItemStackTo(
                  slotStack, RecyclerSlots.TOTAL_SLOTS, playerInventoryEnd, false)) {
                return ItemStack.EMPTY;
              }
            }
          }
        }
      } else {
        // Only allow movement between player inventory and hotbar
        if (index < playerInventoryEnd) {
          // Moving from player inventory to hotbar
          if (!this.moveItemStackTo(slotStack, playerInventoryEnd, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < playerHotbarEnd) {
          // Moving from player hotbar to inventory
          if (!this.moveItemStackTo(
              slotStack, RecyclerSlots.TOTAL_SLOTS, playerInventoryEnd, false)) {
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
    return additionalData.get(PROGRESS_DATA_INDEX) > 0;
  }

  public int getScaledProgress() {
    int progress = this.additionalData.get(PROGRESS_DATA_INDEX);
    int maxProgress = this.additionalData.get(MAX_PROGRESS_DATA_INDEX);

    return maxProgress != 0 && progress != 0 ? progress * PROGRESS_ARROW_SIZE / maxProgress : 0;
  }

  public RecyclerBlockEntity getBlockEntity() {
    return blockEntity;
  }

  public RecyclerStatus getRecyclerStatus() {
    if (blockEntity == null || blockEntity.getLevel() == null) {
      return RecyclerStatus.IDLE;
    }
    return blockEntity.getBlockState().getValue(RecyclerBlock.STATUS);
  }

  @Override
  public int getBatterySlotIndex() {
    return RecyclerSlots.BATTERY_SLOT;
  }
}
