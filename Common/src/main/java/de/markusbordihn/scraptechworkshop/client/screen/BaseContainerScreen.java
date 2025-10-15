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

package de.markusbordihn.scraptechworkshop.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.markusbordihn.scraptechworkshop.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseContainerScreen<T extends AbstractContainerMenu>
    extends AbstractContainerScreen<T> {

  protected BaseContainerScreen(T menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
  }

  protected void renderDefaultBackground(
      GuiGraphics guiGraphics, int x, int y, int width, int height) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_DEMO_BACKGROUND);

    int standardWidth = 176;
    int standardHeight = 166;

    // We split at roughly half of standard size to balance the quadrants
    int splitX = Math.min(width / 2, standardWidth / 2);
    int splitY = Math.min(height / 2, standardHeight / 2);

    // Top-left quadrant (includes left and top borders)
    guiGraphics.blit(
        Constants.TEXTURE_DEMO_BACKGROUND,
        x,
        y,
        0,
        0,
        Math.min(splitX, width),
        Math.min(splitY, height));

    // Top-right quadrant (includes right and top borders)
    if (width > splitX) {
      guiGraphics.blit(
          Constants.TEXTURE_DEMO_BACKGROUND,
          x + splitX,
          y,
          standardWidth - (width - splitX),
          0,
          width - splitX,
          Math.min(splitY, height));
    }

    // Bottom-left quadrant (includes left and bottom borders)
    if (height > splitY) {
      guiGraphics.blit(
          Constants.TEXTURE_DEMO_BACKGROUND,
          x,
          y + splitY,
          0,
          standardHeight - (height - splitY),
          Math.min(splitX, width),
          height - splitY);
    }

    // Bottom-right quadrant (includes right and bottom borders)
    if (width > splitX && height > splitY) {
      guiGraphics.blit(
          Constants.TEXTURE_DEMO_BACKGROUND,
          x + splitX,
          y + splitY,
          standardWidth - (width - splitX),
          standardHeight - (height - splitY),
          width - splitX,
          height - splitY);
    }
  }

  protected void renderSlot(GuiGraphics guiGraphics, int x, int y) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_INVENTORY);
    guiGraphics.blit(Constants.TEXTURE_INVENTORY, x - 1, y - 1, 7, 7, 18, 18);
  }

  protected void renderSlots(GuiGraphics guiGraphics, int x, int y, int columns, int rows) {
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        renderSlot(guiGraphics, x + column * 18, y + row * 18);
      }
    }
  }

  protected void renderPlayerInventoryAt(
      GuiGraphics guiGraphics, int x, int y, int inventoryY, int hotbarY) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_INVENTORY);
    guiGraphics.blit(Constants.TEXTURE_INVENTORY, x + 7, y + inventoryY, 7, 83, 162, 54);
    guiGraphics.blit(Constants.TEXTURE_INVENTORY, x + 7, y + hotbarY, 7, 141, 162, 18);
  }
}
