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

import static de.markusbordihn.scraptechworkshop.block.entity.floatingscrapcollector.FloatingScrapCollectorSlots.*;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.entity.floatingscrapcollector.FloatingScrapCollectorBlockEntity;
import de.markusbordihn.scraptechworkshop.item.ScrapFilterItem;
import de.markusbordihn.scraptechworkshop.menu.slots.DummySlot;
import de.markusbordihn.scraptechworkshop.menu.slots.FloatingCollectorOutputSlot;
import de.markusbordihn.scraptechworkshop.menu.slots.ScrapFilterSlot;
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

public class FloatingScrapCollectorMenu extends BaseMenu {

  public static final int NET_SLOT_X = 8;
  public static final int NET_SLOT_Y = 17;
  public static final int OUTPUT_GRID_START_X = 80;
  public static final int OUTPUT_GRID_START_Y = 17;
  public static final int OUTPUT_GRID_COLUMNS = 3;
  public static final int OUTPUT_GRID_ROWS = 3;
  public static final int PLAYER_INVENTORY_START_Y = 84;
  public static final int PROGRESS_BAR_X = 35;
  public static final int PROGRESS_BAR_Y = 17;
  public static final int PROGRESS_BAR_WIDTH = 18;
  public static final int PROGRESS_BAR_HEIGHT = 54;
  public static final int DURABILITY_BAR_X = 8;
  public static final int DURABILITY_BAR_Y = 38;
  public static final int DURABILITY_BAR_WIDTH = 16;
  public static final int DURABILITY_BAR_HEIGHT = 4;
  public static final int ADDITIONAL_CONTAINER_DATA_SIZE = 2;

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String LOG_PREFIX = "[FloatingScrapCollectorMenu]";

  public static MenuType<FloatingScrapCollectorMenu> TYPE;

  private final FloatingScrapCollectorBlockEntity blockEntity;
  private final Level level;
  private final ContainerData additionalData;
  private final SimpleContainer dummyContainer;
  private final BlockPos blockPos;

  public FloatingScrapCollectorMenu(
      int windowId, Inventory playerInventory, FriendlyByteBuf additionalData) {
    this(
        windowId,
        playerInventory,
        getBlockEntityFromData(playerInventory, additionalData),
        new SimpleContainerData(ADDITIONAL_CONTAINER_DATA_SIZE));
  }

  public FloatingScrapCollectorMenu(
      int windowId, Inventory playerInventory, BlockEntity entity, ContainerData additionalData) {
    super(TYPE, windowId);
    this.level = playerInventory.player.level();
    this.additionalData =
        additionalData != null
            ? additionalData
            : new SimpleContainerData(ADDITIONAL_CONTAINER_DATA_SIZE);
    this.dummyContainer = new SimpleContainer(TOTAL_SLOTS);

    if (entity instanceof FloatingScrapCollectorBlockEntity collectorEntity) {
      this.blockEntity = collectorEntity;
      this.blockPos = collectorEntity.getBlockPos();
    } else {
      log.error(
          "{} Expected FloatingScrapCollectorBlockEntity but got {} at {}",
          LOG_PREFIX,
          entity,
          entity != null ? entity.getBlockPos() : "NULL");
      this.blockEntity = null;
      this.blockPos = null;
    }

    checkContainerSize(playerInventory, TOTAL_SLOTS);
    addCollectorSlots();
    addPlayerInventoryAndHotbar(playerInventory, PLAYER_INVENTORY_START_Y);
    addDataSlots(this.additionalData);
  }

  private static BlockEntity getBlockEntityFromData(
      Inventory playerInventory, FriendlyByteBuf additionalData) {
    BlockPos pos = additionalData.readBlockPos();
    return playerInventory.player.level().getBlockEntity(pos);
  }

  private void addCollectorSlots() {
    FloatingScrapCollectorBlockEntity entityToUse = getValidBlockEntity();
    if (entityToUse != null) {
      this.addSlot(new ScrapFilterSlot(entityToUse, NET_SLOT, NET_SLOT_X, NET_SLOT_Y));

      int outputIndex = FIRST_OUTPUT_SLOT;
      for (int row = 0; row < OUTPUT_GRID_ROWS; row++) {
        for (int col = 0; col < OUTPUT_GRID_COLUMNS; col++) {
          this.addSlot(
              new FloatingCollectorOutputSlot(
                  entityToUse,
                  outputIndex++,
                  OUTPUT_GRID_START_X + col * SLOT_SPACING,
                  OUTPUT_GRID_START_Y + row * SLOT_SPACING));
        }
      }
    } else {
      for (int i = 0; i < TOTAL_SLOTS; i++) {
        this.addSlot(new DummySlot(dummyContainer, i, -1000, -1000));
      }
    }
  }

  private FloatingScrapCollectorBlockEntity getValidBlockEntity() {
    if (blockEntity != null) {
      return blockEntity;
    }

    if (blockPos != null && level != null) {
      BlockEntity entity = level.getBlockEntity(blockPos);
      if (entity instanceof FloatingScrapCollectorBlockEntity collectorEntity) {
        log.debug("{} Resolved BlockEntity on delayed lookup", LOG_PREFIX);
        return collectorEntity;
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

      if (index < TOTAL_SLOTS) {
        if (!this.moveItemStackTo(slotStack, TOTAL_SLOTS, this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else {
        if (slotStack.getItem() instanceof ScrapFilterItem) {
          if (!this.moveItemStackTo(slotStack, NET_SLOT, NET_SLOT + 1, false)) {
            return ItemStack.EMPTY;
          }
        } else {
          return ItemStack.EMPTY;
        }
      }

      if (slotStack.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }

      if (slotStack.getCount() == returnStack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTake(player, slotStack);
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

  public int getProgress() {
    return additionalData.get(0);
  }

  public int getMaxProgress() {
    return additionalData.get(1);
  }

  public int getScaledProgress() {
    int progress = getProgress();
    int maxProgress = getMaxProgress();
    return maxProgress != 0 && progress != 0 ? progress * PROGRESS_BAR_HEIGHT / maxProgress : 0;
  }

  public float getNetDurabilityPercent() {
    if (blockEntity == null) {
      return 0;
    }
    ItemStack netStack = blockEntity.getContainer().getItem(NET_SLOT);
    if (netStack.isEmpty() || !(netStack.getItem() instanceof ScrapFilterItem)) {
      return 0;
    }
    return ScrapFilterItem.getDurabilityPercent(netStack);
  }

  public FloatingScrapCollectorBlockEntity getBlockEntity() {
    return blockEntity;
  }
}
