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

import static de.markusbordihn.scraptechworkshop.block.entity.collectorstation.CollectorStationBlockEntity.*;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.entity.collectorstation.CollectorStationBlockEntity;
import de.markusbordihn.scraptechworkshop.config.CollectorStationConfig;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import de.markusbordihn.scraptechworkshop.menu.slots.CollectorStationStorageSlot;
import de.markusbordihn.scraptechworkshop.menu.slots.CollectorStationUpgradeSlot;
import de.markusbordihn.scraptechworkshop.menu.slots.DummySlot;
import de.markusbordihn.scraptechworkshop.menu.slots.EnergyCellSlot;
import net.minecraft.core.BlockPos;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CollectorStationMenu extends AbstractContainerMenu {

  public static final int BATTERY_SLOT_X = 8;
  public static final int BATTERY_SLOT_Y = 18;
  public static final int ENERGY_BAR_X = 9;
  public static final int ENERGY_BAR_Y = 38;
  public static final int STORAGE_GRID_START_X = 30;
  public static final int STORAGE_GRID_START_Y = 18;
  public static final int STORAGE_GRID_COLUMNS = 6;
  public static final int STORAGE_GRID_ROWS = 4;
  public static final int PROGRESS_BAR_X = 39;
  public static final int PROGRESS_BAR_Y = 96;
  public static final int UPGRADE_SLOT_START_X = 150;
  public static final int UPGRADE_SLOT_Y = 18;
  public static final int UPGRADE_SLOTS_COUNT = 4;
  public static final int PLAYER_INVENTORY_START_Y = 139;
  public static final int PLAYER_HOTBAR_START_Y = 197;
  public static final int SLOT_SPACING = 18;
  public static final int CONTAINER_DATA_SIZE = 4;

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String LOG_PREFIX = "[CollectorStationMenu]";

  public static MenuType<CollectorStationMenu> TYPE;

  private final CollectorStationBlockEntity blockEntity;
  private final Level level;
  private final ContainerData data;
  private final SimpleContainer dummyContainer;
  private final BlockPos blockPos;

  public CollectorStationMenu(
      final int windowId, final Inventory playerInventory, final FriendlyByteBuf additionalData) {
    this(
        windowId,
        playerInventory,
        getBlockEntityFromData(playerInventory, additionalData),
        new SimpleContainerData(CONTAINER_DATA_SIZE));
  }

  public CollectorStationMenu(
      final int windowId,
      final Inventory playerInventory,
      final BlockEntity entity,
      final ContainerData data) {
    super(TYPE, windowId);
    this.level = playerInventory.player.level();
    this.data = data != null ? data : new SimpleContainerData(CONTAINER_DATA_SIZE);
    this.dummyContainer = new SimpleContainer(TOTAL_SLOTS);

    if (entity instanceof CollectorStationBlockEntity stationEntity) {
      this.blockEntity = stationEntity;
      this.blockPos = stationEntity.getBlockPos();
    } else {
      log.error(
          "{} Expected CollectorStationBlockEntity but got {} at {}",
          LOG_PREFIX,
          entity,
          entity != null ? entity.getBlockPos() : "NULL");
      this.blockEntity = null;
      this.blockPos = null;
    }

    checkContainerSize(playerInventory, TOTAL_SLOTS);
    addCollectorStationSlots();
    addPlayerInventory(playerInventory);
    addPlayerHotbar(playerInventory);
    addDataSlots(this.data);
  }

  private static BlockEntity getBlockEntityFromData(
      final Inventory playerInventory, final FriendlyByteBuf additionalData) {
    if (additionalData != null && additionalData.isReadable()) {
      BlockPos pos = additionalData.readBlockPos();
      Level level = playerInventory.player.level();
      if (level != null) {
        return level.getBlockEntity(pos);
      }
    }
    return null;
  }

  private void addCollectorStationSlots() {
    CollectorStationBlockEntity entityToUse = getValidBlockEntity();
    if (entityToUse != null) {
      // Battery slot
      this.addSlot(new EnergyCellSlot(entityToUse, BATTERY_SLOT, BATTERY_SLOT_X, BATTERY_SLOT_Y));

      // Storage slots
      int storageIndex = FIRST_STORAGE_SLOT;
      for (int row = 0; row < STORAGE_GRID_ROWS; row++) {
        for (int col = 0; col < STORAGE_GRID_COLUMNS; col++) {
          this.addSlot(
              new CollectorStationStorageSlot(
                  entityToUse,
                  storageIndex++,
                  STORAGE_GRID_START_X + col * SLOT_SPACING + 2,
                  STORAGE_GRID_START_Y + row * SLOT_SPACING));
        }
      }

      // Upgrade slots
      for (int i = 0; i < UPGRADE_SLOTS_COUNT; i++) {
        this.addSlot(
            new CollectorStationUpgradeSlot(
                entityToUse,
                FIRST_UPGRADE_SLOT + i,
                UPGRADE_SLOT_START_X + 2,
                UPGRADE_SLOT_Y + i * SLOT_SPACING));
      }
    } else {
      // Add dummy slots if no block entity
      for (int i = 0; i < TOTAL_SLOTS; i++) {
        this.addSlot(new DummySlot(dummyContainer, i, -1000, -1000));
      }
    }
  }

  private CollectorStationBlockEntity getValidBlockEntity() {
    if (blockEntity != null) {
      return blockEntity;
    }

    if (blockPos != null && level != null) {
      BlockEntity entity = level.getBlockEntity(blockPos);
      if (entity instanceof CollectorStationBlockEntity stationEntity) {
        log.debug("{} Resolved BlockEntity on delayed lookup", LOG_PREFIX);
        return stationEntity;
      }
    }

    return null;
  }

  private void addPlayerInventory(final Inventory playerInventory) {
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        this.addSlot(
            new Slot(
                playerInventory,
                col + row * 9 + 9,
                8 + col * SLOT_SPACING,
                PLAYER_INVENTORY_START_Y + row * SLOT_SPACING + 1));
      }
    }
  }

  private void addPlayerHotbar(final Inventory playerInventory) {
    for (int col = 0; col < 9; col++) {
      this.addSlot(
          new Slot(playerInventory, col, 8 + col * SLOT_SPACING, PLAYER_HOTBAR_START_Y + 1));
    }
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack returnStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);

    if (slot.hasItem()) {
      ItemStack slotStack = slot.getItem();
      returnStack = slotStack.copy();

      // From collector station to player inventory
      if (index < TOTAL_SLOTS) {
        if (!this.moveItemStackTo(slotStack, TOTAL_SLOTS, this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else {
        // From player inventory to collector station
        if (slotStack.getItem() instanceof EnergyCellItem) {
          // Try to move battery to battery slot
          if (!this.moveItemStackTo(slotStack, BATTERY_SLOT, BATTERY_SLOT + 1, false)) {
            return ItemStack.EMPTY;
          }
        } else {
          // Try to move to storage slots
          if (!this.moveItemStackTo(slotStack, FIRST_STORAGE_SLOT, LAST_STORAGE_SLOT + 1, false)) {
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

    return returnStack;
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

  public int getStatus() {
    return data.get(0);
  }

  public int getStateTimer() {
    return data.get(1);
  }

  public int getCollectionProgress() {
    return data.get(1);
  }

  public int getMaxCollectionTime() {
    return switch (getStatus()) {
      case 0 -> CollectorStationConfig.chargingTime; // CHARGING
      case 1 -> CollectorStationConfig.collectingTime; // COLLECTING
      case 2 -> CollectorStationConfig.returningTime; // RETURNING
      case 3 -> CollectorStationConfig.processingTime; // PROCESSING
      default -> CollectorStationConfig.collectingTime; // Fallback
    };
  }

  public int getCurrentEnergy() {
    return data.get(2);
  }

  public CollectorStationBlockEntity getBlockEntity() {
    return blockEntity;
  }
}
