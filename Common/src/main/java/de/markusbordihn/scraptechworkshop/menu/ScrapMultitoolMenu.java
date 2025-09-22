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

import de.markusbordihn.scraptechworkshop.data.ScrapMultitoolData;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.tool.ScrapMultitoolItem;
import de.markusbordihn.scraptechworkshop.menu.slots.MultitoolBatterySlot;
import de.markusbordihn.scraptechworkshop.menu.slots.MultitoolModuleSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ScrapMultitoolMenu extends AbstractContainerMenu {

  public static final int BATTERY_SLOT_X = 100;
  public static final int BATTERY_SLOT_Y = 20;
  public static final int SLOT_SPACING = 18;

  public static final int MODULE_SLOTS_START_X = 64;
  public static final int MODULE_SLOTS_Y = 50;
  public static final int MODULE_SLOTS_COUNT = ScrapMultitoolData.MODULE_SLOTS;

  public static final int PLAYER_INVENTORY_START_X = 8;
  public static final int PLAYER_INVENTORY_START_Y = 138;
  public static final int PLAYER_INVENTORY_ROWS = 3;
  public static final int PLAYER_INVENTORY_COLUMNS = 9;

  public static final int PLAYER_HOTBAR_START_X = 8;
  public static final int PLAYER_HOTBAR_Y = 196;
  public static final int PLAYER_HOTBAR_SLOTS = 9;

  public static final int TOTAL_TOOL_SLOTS = 1 + MODULE_SLOTS_COUNT; // Battery + Modules

  // Note: MenuType will be registered by platform-specific code
  public static MenuType<ScrapMultitoolMenu> TYPE;

  private final ItemStack multitoolStack;
  private final SimpleContainer toolContainer;
  private final int toolSlotIndex;
  private final InteractionHand hand;
  private boolean initialized = false;

  public ScrapMultitoolMenu(
      int windowId, Inventory playerInventory, FriendlyByteBuf additionalData) {
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
      int windowId,
      Inventory playerInventory,
      ItemStack multitoolStack,
      InteractionHand hand,
      int slotIndex) {
    super(TYPE, windowId);
    this.multitoolStack = multitoolStack;
    this.hand = hand;
    this.toolSlotIndex = slotIndex;

    // Validate multitool stack
    if (!(multitoolStack.getItem() instanceof ScrapMultitoolItem multitool)) {
      throw new IllegalArgumentException("Invalid multitool ItemStack");
    }

    this.toolContainer = createToolContainer(multitool);

    addToolSlots();
    addPlayerInventory(playerInventory);
    addPlayerHotbar(playerInventory);
  }

  private SimpleContainer createToolContainer(ScrapMultitoolItem multitool) {
    ScrapMultitoolData data = multitool.getData(multitoolStack);
    SimpleContainer container =
        new SimpleContainer(TOTAL_TOOL_SLOTS) {
          @Override
          public void setChanged() {
            super.setChanged();
            // Only save if fully initialized to prevent NPE during construction
            if (initialized) {
              saveToMultitool();
            }
          }
        };

    // Load existing data
    container.setItem(0, data.battery());
    for (int i = 0; i < data.modules().length; i++) {
      ItemStack module = data.modules()[i];
      if (module != null) {
        container.setItem(i + 1, module);
      }
    }

    // Mark as initialized after all data is loaded
    initialized = true;

    return container;
  }

  private void addToolSlots() {
    // Battery slot
    this.addSlot(new MultitoolBatterySlot(toolContainer, 0, BATTERY_SLOT_X, BATTERY_SLOT_Y));

    // Module slots
    for (int i = 0; i < MODULE_SLOTS_COUNT; i++) {
      int slotIndex = i + 1;
      int x = MODULE_SLOTS_START_X + i * SLOT_SPACING;
      this.addSlot(new MultitoolModuleSlot(toolContainer, slotIndex, x, MODULE_SLOTS_Y));
    }
  }

  private void addPlayerInventory(Inventory playerInventory) {
    for (int row = 0; row < PLAYER_INVENTORY_ROWS; ++row) {
      for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; ++col) {
        this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 138 + row * 18));
      }
    }
  }

  private void addPlayerHotbar(Inventory playerInventory) {
    for (int col = 0; col < PLAYER_HOTBAR_SLOTS; ++col) {
      int x = PLAYER_HOTBAR_START_X + col * SLOT_SPACING;
      final int index = col;

      this.addSlot(
          new Slot(playerInventory, index, x, PLAYER_HOTBAR_Y) {
            @Override
            public boolean mayPickup(Player player) {
              // Prevent picking up the multitool from hotbar while GUI is open
              return index != toolSlotIndex;
            }
          });
    }
  }

  private void saveToMultitool() {
    if (multitoolStack.getItem() instanceof ScrapMultitoolItem multitool && toolContainer != null) {
      ItemStack[] modules = new ItemStack[MODULE_SLOTS_COUNT];
      for (int i = 0; i < modules.length; i++) {
        modules[i] = toolContainer.getItem(i + 1);
      }

      ScrapMultitoolData currentData = multitool.getData(multitoolStack);
      ScrapMultitoolData newData =
          new ScrapMultitoolData(
              toolContainer.getItem(0),
              modules,
              currentData.hologramColor(),
              currentData.hudEnabled(),
              currentData.toolPriority());

      multitool.setData(multitoolStack, newData);

      // Check for battery and charge if present
      ItemStack battery = toolContainer.getItem(0);
      if (!battery.isEmpty() && battery.getItem() == ModItems.ENERGY_CELL_SCRAP.get()) {
        // Add energy from battery
        int energyToAdd = 1000; // Energy per battery
        multitool.addEnergy(multitoolStack, energyToAdd);
        battery.shrink(1);
      }
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
        if (slotStack.getItem() == ModItems.ENERGY_CELL_SCRAP.get()) {
          // Try to move battery to battery slot
          if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
            // If failed, try hotbar
            if (!this.moveItemStackTo(slotStack, playerInventoryEnd, playerHotbarEnd, false)) {
              return ItemStack.EMPTY;
            }
          }
        } else if (isValidModule(slotStack)) {
          // Try to move module to module slots
          if (!this.moveItemStackTo(slotStack, 1, TOTAL_TOOL_SLOTS, false)) {
            // If failed, try hotbar
            if (!this.moveItemStackTo(slotStack, playerInventoryEnd, playerHotbarEnd, false)) {
              return ItemStack.EMPTY;
            }
          }
        } else {
          // Regular item, try hotbar
          if (!this.moveItemStackTo(slotStack, playerInventoryEnd, playerHotbarEnd, false)) {
            return ItemStack.EMPTY;
          }
        }
      } else if (index < playerHotbarEnd) {
        // Moving from player hotbar
        if (slotStack.getItem() == ModItems.ENERGY_CELL_SCRAP.get()) {
          // Try to move battery to battery slot
          if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
            // If failed, try inventory
            if (!this.moveItemStackTo(slotStack, TOTAL_TOOL_SLOTS, playerInventoryEnd, false)) {
              return ItemStack.EMPTY;
            }
          }
        } else if (isValidModule(slotStack)) {
          // Try to move module to module slots
          if (!this.moveItemStackTo(slotStack, 1, TOTAL_TOOL_SLOTS, false)) {
            // If failed, try inventory
            if (!this.moveItemStackTo(slotStack, TOTAL_TOOL_SLOTS, playerInventoryEnd, false)) {
              return ItemStack.EMPTY;
            }
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

  private boolean isValidModule(ItemStack stack) {
    // Add module validation logic here
    return stack.getItem().toString().contains("module")
        || stack.getItem().toString().contains("upgrade");
  }

  @Override
  public boolean stillValid(Player player) {
    return !multitoolStack.isEmpty() && player.getInventory().contains(multitoolStack);
  }

  @Override
  public void removed(Player player) {
    super.removed(player);
    saveToMultitool();
  }

  public ItemStack getMultitoolStack() {
    return multitoolStack;
  }

  public InteractionHand getHand() {
    return hand;
  }
}
