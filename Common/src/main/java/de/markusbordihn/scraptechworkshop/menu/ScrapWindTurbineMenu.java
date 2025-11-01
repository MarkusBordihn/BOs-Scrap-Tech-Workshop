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
import de.markusbordihn.scraptechworkshop.block.entity.windturbine.ScrapWindTurbineBlockEntity;
import de.markusbordihn.scraptechworkshop.block.entity.windturbine.ScrapWindTurbineSlots;
import de.markusbordihn.scraptechworkshop.block.windturbine.ScrapWindTurbineBlock;
import de.markusbordihn.scraptechworkshop.data.windturbine.ScrapWindTurbineStatus;
import de.markusbordihn.scraptechworkshop.energy.EnergyPowerGenerator;
import de.markusbordihn.scraptechworkshop.menu.slots.DummySlot;
import de.markusbordihn.scraptechworkshop.menu.slots.EnergyCellSlot;
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

public class ScrapWindTurbineMenu extends EnergyPowerGeneratorMenu {

  // Slot positions
  public static final int CHARGING_BATTERY_SLOT_X = 135;
  public static final int CHARGING_BATTERY_SLOT_Y = 43;
  public static final int PLAYER_INVENTORY_START_Y = 119;
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String LOG_PREFIX = "[SCRAP_WIND_TURBINE]";
  private static final int PLAYER_INVENTORY_START_INDEX = ScrapWindTurbineSlots.TOTAL_SLOTS;
  private static final int PLAYER_INVENTORY_END_INDEX = PLAYER_INVENTORY_START_INDEX + 27;
  private static final int PLAYER_HOTBAR_END_INDEX = PLAYER_INVENTORY_END_INDEX + 9;
  private static final int ADDITIONAL_CONTAINER_DATA_SIZE = 2;
  public static MenuType<ScrapWindTurbineMenu> TYPE;
  private final ScrapWindTurbineBlockEntity blockEntity;
  private final Level level;
  private final ContainerData additionalData;
  private final SimpleContainer dummyContainer;
  private final BlockPos blockPos;

  public ScrapWindTurbineMenu(
      final int windowId, final Inventory playerInventory, final FriendlyByteBuf additionalData) {
    this(
        windowId,
        playerInventory,
        getBlockEntityFromData(playerInventory, additionalData),
        new SimpleContainerData(ADDITIONAL_CONTAINER_DATA_SIZE));
  }

  public ScrapWindTurbineMenu(
      final int windowId,
      final Inventory playerInventory,
      final BlockEntity entity,
      final ContainerData additionalData) {
    super(
        TYPE,
        windowId,
        entity instanceof EnergyPowerGenerator generator
            ? generator.getEnergyPowerData()
            : new SimpleContainerData(3));
    this.level = playerInventory.player.level();
    this.additionalData =
        additionalData != null
            ? additionalData
            : new SimpleContainerData(ADDITIONAL_CONTAINER_DATA_SIZE);
    this.dummyContainer = new SimpleContainer(ScrapWindTurbineSlots.TOTAL_SLOTS);

    if (entity instanceof ScrapWindTurbineBlockEntity windTurbineEntity) {
      this.blockEntity = windTurbineEntity;
      this.blockPos = windTurbineEntity.getBlockPos();
    } else {
      log.error(
          "{} Expected ScrapWindTurbineBlockEntity but got {} at {}",
          LOG_PREFIX,
          entity,
          entity != null ? entity.getBlockPos() : "NULL");
      this.blockEntity = null;
      this.blockPos = null;
    }

    checkContainerSize(playerInventory, ScrapWindTurbineSlots.TOTAL_SLOTS);
    addWindTurbineSlots();
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

  private void addWindTurbineSlots() {
    ScrapWindTurbineBlockEntity entityToUse = getValidBlockEntity();
    if (entityToUse != null) {
      this.addSlot(
          new EnergyCellSlot(
              entityToUse,
              ScrapWindTurbineSlots.ENERGY_TAB_BATTERY_SLOT,
              ENERGY_TAB_BATTERY_SLOT_X,
              ENERGY_TAB_BATTERY_SLOT_Y));

      this.addSlot(
          new EnergyCellSlot(
              entityToUse,
              ScrapWindTurbineSlots.CHARGING_BATTERY_SLOT,
              CHARGING_BATTERY_SLOT_X,
              CHARGING_BATTERY_SLOT_Y));
    } else {
      for (int i = 0; i < ScrapWindTurbineSlots.TOTAL_SLOTS; i++) {
        this.addSlot(new DummySlot(dummyContainer, i, -1000, -1000));
      }
      log.warn("{} Using dummy slots because BlockEntity is not available", LOG_PREFIX);
    }
  }

  private ScrapWindTurbineBlockEntity getValidBlockEntity() {
    if (this.blockEntity != null) {
      return this.blockEntity;
    }

    if (this.blockPos != null && this.level != null) {
      BlockEntity entity = this.level.getBlockEntity(this.blockPos);
      if (entity instanceof ScrapWindTurbineBlockEntity windTurbineEntity) {
        log.debug("{} Resolved BlockEntity on delayed lookup", LOG_PREFIX);
        return windTurbineEntity;
      }
    }

    return null;
  }

  public int getWindSpeed() {
    return additionalData.get(0);
  }

  public int getPowerGeneration() {
    return additionalData.get(1);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack itemStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);

    if (slot != null && slot.hasItem()) {
      ItemStack slotItem = slot.getItem();
      itemStack = slotItem.copy();

      if (index < PLAYER_INVENTORY_START_INDEX) {
        if (!this.moveItemStackTo(
            slotItem, PLAYER_INVENTORY_START_INDEX, PLAYER_HOTBAR_END_INDEX, true)) {
          return ItemStack.EMPTY;
        }
      } else if (index < PLAYER_HOTBAR_END_INDEX) {
        if (!this.moveItemStackTo(slotItem, 0, PLAYER_INVENTORY_START_INDEX, false)) {
          return ItemStack.EMPTY;
        }
      }

      if (slotItem.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
    }

    return itemStack;
  }

  @Override
  public boolean stillValid(Player player) {
    if (blockEntity != null && blockEntity.getLevel() != null) {
      return stillValid(
          ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
          player,
          blockEntity.getBlockState().getBlock());
    }
    return false;
  }

  public ScrapWindTurbineBlockEntity getBlockEntity() {
    return blockEntity;
  }

  public ScrapWindTurbineStatus getWindTurbineStatus() {
    if (blockEntity == null || blockEntity.getLevel() == null) {
      return ScrapWindTurbineStatus.IDLE;
    }
    return blockEntity.getBlockState().getValue(ScrapWindTurbineBlock.STATUS);
  }

  public int getEnergyTabBatterySlotIndex() {
    return ScrapWindTurbineSlots.ENERGY_TAB_BATTERY_SLOT;
  }

  public int getChargingBatterySlotIndex() {
    return ScrapWindTurbineSlots.CHARGING_BATTERY_SLOT;
  }
}
