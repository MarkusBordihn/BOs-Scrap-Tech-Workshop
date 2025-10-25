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

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public abstract class BaseMenu extends AbstractContainerMenu {

  public static final int SLOT_SPACING = 18;

  protected BaseMenu(MenuType<?> menuType, int containerId) {
    super(menuType, containerId);
  }

  protected void addPlayerInventory(
      final Inventory playerInventory, final int startX, final int startY) {
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        this.addSlot(
            new Slot(
                playerInventory,
                col + row * 9 + 9,
                startX + col * SLOT_SPACING,
                startY + row * SLOT_SPACING));
      }
    }
  }

  protected void addPlayerHotbar(
      final Inventory playerInventory, final int startX, final int startY) {
    for (int col = 0; col < 9; col++) {
      this.addSlot(new Slot(playerInventory, col, startX + col * SLOT_SPACING, startY));
    }
  }

  protected void addPlayerInventoryAndHotbar(
      final Inventory playerInventory, final int inventoryStartY) {
    addPlayerInventory(playerInventory, 8, inventoryStartY);
    addPlayerHotbar(playerInventory, 8, inventoryStartY + 58);
  }
}
