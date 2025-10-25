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
import de.markusbordihn.scraptechworkshop.block.entity.rechargestation.RechargeStationBlockEntity;
import de.markusbordihn.scraptechworkshop.block.entity.rechargestation.RechargeStationSlots;
import de.markusbordihn.scraptechworkshop.block.rechargestation.RechargeStationBlock;
import de.markusbordihn.scraptechworkshop.data.rechargestation.RechargeStationStatus;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerConsumer;
import de.markusbordihn.scraptechworkshop.menu.slots.DummySlot;
import de.markusbordihn.scraptechworkshop.menu.slots.EnergyCellSlot;
import de.markusbordihn.scraptechworkshop.menu.slots.RechargeStationInputSlot;
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

public class RechargeStationMenu extends EnergyPowerMenu {

  public static final int INPUT_SLOT_X = 80;
  public static final int INPUT_SLOT_Y = 35;
  public static final int PLAYER_INVENTORY_START_Y = 99;
  public static final int PROGRESS_BAR_X = 68;
  public static final int PROGRESS_BAR_Y = 56;
  public static final int PROGRESS_BAR_WIDTH = 40;
  public static final int PROGRESS_BAR_HEIGHT = 5;

  public static final int ADDITIONAL_CONTAINER_DATA_SIZE = 2;
  public static final int PROGRESS_DATA_INDEX = 0;
  public static final int MAX_PROGRESS_DATA_INDEX = 1;
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String LOG_PREFIX = "[RECHARGE_STATION]";

  public static MenuType<RechargeStationMenu> TYPE;

  private final RechargeStationBlockEntity blockEntity;
  private final Level level;
  private final ContainerData additionalData;
  private final SimpleContainer dummyContainer;
  private final BlockPos blockPos;

  public RechargeStationMenu(
      final int windowId, final Inventory playerInventory, final FriendlyByteBuf additionalData) {
    this(
        windowId,
        playerInventory,
        getBlockEntityFromData(playerInventory, additionalData),
        new SimpleContainerData(ADDITIONAL_CONTAINER_DATA_SIZE));
  }

  public RechargeStationMenu(
      final int windowId,
      final Inventory playerInventory,
      final BlockEntity entity,
      final ContainerData additionalData) {
    super(
        TYPE,
        windowId,
        entity instanceof EnergyPowerConsumer consumer
            ? consumer.getEnergyPowerData()
            : new SimpleContainerData(2));
    this.level = playerInventory.player.level();
    this.additionalData =
        additionalData != null
            ? additionalData
            : new SimpleContainerData(ADDITIONAL_CONTAINER_DATA_SIZE);
    this.dummyContainer = new SimpleContainer(RechargeStationSlots.TOTAL_SLOTS);

    if (entity instanceof RechargeStationBlockEntity rechargeStationBlockEntity) {
      this.blockEntity = rechargeStationBlockEntity;
      this.blockPos = rechargeStationBlockEntity.getBlockPos();
    } else {
      log.error(
          "{} Expected RechargeStationBlockEntity but got {} at {}",
          LOG_PREFIX,
          entity,
          entity != null ? entity.getBlockPos() : "NULL");
      this.blockEntity = null;
      this.blockPos = null;
    }

    checkContainerSize(playerInventory, RechargeStationSlots.TOTAL_SLOTS);
    addRechargeStationSlots();
    addPlayerInventoryAndHotbar(playerInventory, PLAYER_INVENTORY_START_Y);
    addDataSlots(this.additionalData);
  }

  private static BlockEntity getBlockEntityFromData(
      final Inventory playerInventory, final FriendlyByteBuf additionalData) {
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

  private void addRechargeStationSlots() {
    RechargeStationBlockEntity entityToUse = getValidBlockEntity();
    if (entityToUse != null) {
      this.addSlot(
          new RechargeStationInputSlot(
              entityToUse, RechargeStationSlots.INPUT_SLOT, INPUT_SLOT_X, INPUT_SLOT_Y));

      this.addSlot(
          new EnergyCellSlot(
              entityToUse,
              RechargeStationSlots.BATTERY_SLOT,
              ENERGY_TAB_BATTERY_SLOT_X,
              ENERGY_TAB_BATTERY_SLOT_Y));
    } else {
      for (int i = 0; i < RechargeStationSlots.TOTAL_SLOTS; i++) {
        this.addSlot(new DummySlot(dummyContainer, i, -1000, -1000));
      }
      log.warn("{} Using dummy slots because BlockEntity is not available", LOG_PREFIX);
    }
  }

  private RechargeStationBlockEntity getValidBlockEntity() {
    if (this.blockEntity != null) {
      return this.blockEntity;
    }

    if (this.blockPos != null && this.level != null) {
      BlockEntity entity = this.level.getBlockEntity(this.blockPos);
      if (entity instanceof RechargeStationBlockEntity rechargeStationEntity) {
        log.debug("{} Resolved BlockEntity on delayed lookup", LOG_PREFIX);
        return rechargeStationEntity;
      }
    }

    return null;
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack returnStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);

    if (slot.hasItem()) {
      ItemStack slotStack = slot.getItem();
      returnStack = slotStack.copy();

      if (index < RechargeStationSlots.TOTAL_SLOTS) {
        if (!this.moveItemStackTo(
            slotStack, RechargeStationSlots.TOTAL_SLOTS, this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else {
        if (!this.moveItemStackTo(slotStack, 0, RechargeStationSlots.TOTAL_SLOTS, false)) {
          return ItemStack.EMPTY;
        }
      }

      if (slotStack.isEmpty()) {
        slot.set(ItemStack.EMPTY);
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

  public boolean isCharging() {
    int progress = additionalData.get(PROGRESS_DATA_INDEX);
    int maxProgress = additionalData.get(MAX_PROGRESS_DATA_INDEX);
    return progress > 0 && progress < maxProgress;
  }

  public int getScaledProgress() {
    int progress = additionalData.get(PROGRESS_DATA_INDEX);
    int maxProgress = additionalData.get(MAX_PROGRESS_DATA_INDEX);
    return maxProgress != 0 && progress != 0 ? progress * PROGRESS_BAR_WIDTH / maxProgress : 0;
  }

  public RechargeStationBlockEntity getBlockEntity() {
    return blockEntity;
  }

  public RechargeStationStatus getRechargeStationStatus() {
    if (blockEntity == null || blockEntity.getLevel() == null) {
      return RechargeStationStatus.IDLE;
    }
    return blockEntity.getBlockState().getValue(RechargeStationBlock.STATUS);
  }
}
