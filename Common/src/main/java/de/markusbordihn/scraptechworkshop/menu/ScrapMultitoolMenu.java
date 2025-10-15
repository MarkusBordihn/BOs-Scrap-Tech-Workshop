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

import de.markusbordihn.scraptechworkshop.data.multitool.DisplayMode;
import de.markusbordihn.scraptechworkshop.data.multitool.ScrapMultitoolData;
import de.markusbordihn.scraptechworkshop.data.multitool.ToolMode;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import de.markusbordihn.scraptechworkshop.item.tool.ScrapMultitoolItem;
import de.markusbordihn.scraptechworkshop.menu.multitool.MultitoolBatteryInfo;
import de.markusbordihn.scraptechworkshop.menu.multitool.MultitoolContainerFactory;
import de.markusbordihn.scraptechworkshop.menu.multitool.MultitoolDataSaver;
import de.markusbordihn.scraptechworkshop.menu.slots.EnergyCellSlot;
import de.markusbordihn.scraptechworkshop.menu.slots.MultitoolModuleSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ScrapMultitoolMenu extends AbstractContainerMenu {

  // Layout constants
  private static final int BATTERY_SLOT_X = 100;
  private static final int BATTERY_SLOT_Y = 20;
  private static final int MODULE_SLOTS_START_X = 64;
  private static final int MODULE_SLOTS_Y = 50;
  private static final int MODULE_SLOTS_COUNT = ScrapMultitoolData.MODULE_SLOTS;
  private static final int PLAYER_INVENTORY_START_X = 8;
  private static final int PLAYER_INVENTORY_START_Y = 138;
  private static final int PLAYER_INVENTORY_ROWS = 3;
  private static final int PLAYER_INVENTORY_COLUMNS = 9;
  private static final int PLAYER_HOTBAR_START_X = 8;
  private static final int PLAYER_HOTBAR_Y = 196;
  private static final int PLAYER_HOTBAR_SLOTS = 9;
  private static final int SLOT_SPACING = 18;
  private static final int TOTAL_TOOL_SLOTS = 1 + MODULE_SLOTS_COUNT;

  // Note: MenuType will be registered by platform-specific code
  public static MenuType<ScrapMultitoolMenu> TYPE;

  private final ItemStack multitoolStack;
  private final SimpleContainer toolContainer;
  private final int toolSlotIndex;
  private final InteractionHand hand;
  private boolean initialized = false;

  public ScrapMultitoolMenu(
      final int windowId, final Inventory playerInventory, final FriendlyByteBuf additionalData) {
    this(
        windowId,
        playerInventory,
        additionalData != null
            ? additionalData.readItem()
            : playerInventory.player.getMainHandItem(),
        additionalData != null
            ? additionalData.readEnum(InteractionHand.class)
            : InteractionHand.MAIN_HAND,
        additionalData != null ? additionalData.readInt() : playerInventory.selected);
  }

  public ScrapMultitoolMenu(
      final int windowId,
      final Inventory playerInventory,
      final ItemStack multitoolStack,
      final InteractionHand hand,
      final int slotIndex) {
    super(TYPE, windowId);
    this.multitoolStack = multitoolStack;
    this.hand = hand;
    this.toolSlotIndex = slotIndex;

    // Validate multitool stack
    if (!(multitoolStack.getItem() instanceof ScrapMultitoolItem multitool)) {
      throw new IllegalArgumentException("Invalid multitool ItemStack");
    }

    // Create container with lambda that references the final toolContainer
    SimpleContainer tempContainer =
        MultitoolContainerFactory.createToolContainer(multitoolStack, () -> {});
    this.toolContainer = tempContainer;

    // Now set up the change listener that uses the initialized toolContainer
    tempContainer.addListener(
        container -> {
          if (initialized) {
            MultitoolDataSaver.saveToMultitool(multitoolStack, toolContainer);
          }
        });

    addToolSlots();
    addPlayerInventory(playerInventory);
    addPlayerHotbar(playerInventory);

    initialized = true;
  }

  private void addToolSlots() {
    // Battery slot
    this.addSlot(new EnergyCellSlot(toolContainer, 0, BATTERY_SLOT_X, BATTERY_SLOT_Y));

    // Module slots
    for (int i = 0; i < MODULE_SLOTS_COUNT; i++) {
      int slotIndex = i + 1;
      int x = MODULE_SLOTS_START_X + i * SLOT_SPACING;
      this.addSlot(new MultitoolModuleSlot(toolContainer, slotIndex, x, MODULE_SLOTS_Y));
    }
  }

  private void addPlayerInventory(final Inventory playerInventory) {
    for (int row = 0; row < PLAYER_INVENTORY_ROWS; ++row) {
      for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; ++col) {
        this.addSlot(
            new Slot(
                playerInventory,
                col + row * 9 + 9,
                PLAYER_INVENTORY_START_X + col * 18,
                PLAYER_INVENTORY_START_Y + row * 18));
      }
    }
  }

  private void addPlayerHotbar(final Inventory playerInventory) {
    for (int index = 0; index < PLAYER_HOTBAR_SLOTS; ++index) {
      int x = PLAYER_HOTBAR_START_X + index * SLOT_SPACING;
      this.addSlot(
          new Slot(playerInventory, index, x, PLAYER_HOTBAR_Y) {
            @Override
            public boolean mayPickup(Player player) {
              return index != toolSlotIndex;
            }
          });
    }
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack itemStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);

    if (slot.hasItem()) {
      ItemStack slotStack = slot.getItem();
      itemStack = slotStack.copy();
      int playerInventoryEnd =
          TOTAL_TOOL_SLOTS + (PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS);
      int playerHotbarEnd = playerInventoryEnd + PLAYER_HOTBAR_SLOTS;

      if (index < TOTAL_TOOL_SLOTS) {
        // Moving from tool slots to player inventory
        if (!this.moveItemStackTo(slotStack, TOTAL_TOOL_SLOTS, playerHotbarEnd, true)) {
          return ItemStack.EMPTY;
        }
      } else if (index < playerInventoryEnd) {
        // Moving from player inventory
        if (slotStack.getItem() instanceof EnergyCellItem) {
          // Try to move battery to battery slot
          if (!this.moveItemStackTo(slotStack, 0, 1, false)
              && !this.moveItemStackTo(slotStack, playerInventoryEnd, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (isValidModule(slotStack)) {
          // Try to move module to module slots
          if (!this.moveItemStackTo(slotStack, 1, TOTAL_TOOL_SLOTS, false)
              && !this.moveItemStackTo(slotStack, playerInventoryEnd, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else {
          // Regular item, try hotbar
          if (!this.moveItemStackTo(slotStack, playerInventoryEnd, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        }
      } else if (index < playerHotbarEnd) {
        // Moving from player hotbar
        if (slotStack.getItem() instanceof EnergyCellItem) {
          // Try to move battery to battery slot
          if (!this.moveItemStackTo(slotStack, 0, 1, false)
              && !this.moveItemStackTo(slotStack, TOTAL_TOOL_SLOTS, playerInventoryEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else if (isValidModule(slotStack)) {
          // Try to move module to module slots
          if (!this.moveItemStackTo(slotStack, 1, TOTAL_TOOL_SLOTS, false)
              && !this.moveItemStackTo(slotStack, TOTAL_TOOL_SLOTS, playerInventoryEnd, false)) {
            return ItemStack.EMPTY;
          }
        } else {
          // Regular item, try inventory
          if (!this.moveItemStackTo(slotStack, TOTAL_TOOL_SLOTS, playerInventoryEnd, false)) {
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

  private boolean isValidModule(final ItemStack itemStack) {
    return itemStack.getItem().toString().contains("module")
        || itemStack.getItem().toString().contains("upgrade");
  }

  @Override
  public boolean stillValid(Player player) {
    return !multitoolStack.isEmpty() && player.getInventory().contains(multitoolStack);
  }

  @Override
  public void slotsChanged(Container container) {
    super.slotsChanged(container);

    if (container == toolContainer && initialized) {
      updateMultitoolInPlayerInventory();
    }
  }

  private void updateMultitoolInPlayerInventory() {
    Player player = null;
    for (Slot slot : slots) {
      if (slot.container instanceof Inventory inventory && inventory.player != null) {
        player = inventory.player;
        break;
      }
    }

    if (player == null) {
      return;
    }

    updateMultitoolInPlayerInventoryForPlayer(player);
  }

  @Override
  public void removed(Player player) {
    super.removed(player);
    updateMultitoolInPlayerInventoryForPlayer(player);
  }

  private void updateMultitoolInPlayerInventoryForPlayer(final Player player) {
    ItemStack actualMultitoolStack = player.getItemInHand(hand);
    if (!(actualMultitoolStack.getItem() instanceof ScrapMultitoolItem)) {
      return;
    }

    ItemStack[] modules = new ItemStack[MODULE_SLOTS_COUNT];
    for (int i = 0; i < modules.length; i++) {
      modules[i] = toolContainer.getItem(i + 1);
    }

    ScrapMultitoolData currentData = ScrapMultitoolData.fromItemStack(actualMultitoolStack);
    ScrapMultitoolData newData =
        new ScrapMultitoolData(
            toolContainer.getItem(0),
            modules,
            currentData.hologramColor(),
            currentData.hudEnabled(),
            currentData.toolPriority(),
            currentData.activeMode());
    newData.saveToItemStack(actualMultitoolStack);

    // Always sync energy: either from battery or reset to 0 if no battery
    if (actualMultitoolStack.getItem() instanceof ScrapMultitoolItem multitoolItem) {
      multitoolItem.syncEnergyDisplay(actualMultitoolStack);
    }

    DisplayMode displayMode = new DisplayMode(actualMultitoolStack);
    displayMode.updateModel(ToolMode.fromId(newData.activeMode()), newData.getBatteryLevel());
  }

  public ItemStack getMultitoolStack() {
    return multitoolStack;
  }

  public int getCurrentEnergyFromBattery() {
    return MultitoolBatteryInfo.getCurrentEnergy(toolContainer);
  }

  public int getMaxEnergyFromBattery() {
    return MultitoolBatteryInfo.getMaxEnergy(toolContainer);
  }

  public int getBatteryPercentageFromSlot() {
    return MultitoolBatteryInfo.getPercentage(toolContainer);
  }
}
