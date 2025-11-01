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
import de.markusbordihn.scraptechworkshop.client.screen.energy.EnergyPowerRenderer;
import de.markusbordihn.scraptechworkshop.energy.EnergyFlowStatus;
import de.markusbordihn.scraptechworkshop.menu.EnergyPowerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseContainerScreen<T extends AbstractContainerMenu>
    extends AbstractContainerScreen<T> {

  public static final int STANDARD_BACKGROUND_WIDTH = 248;
  public static final int STANDARD_BACKGROUND_HEIGHT = 166;
  public static final int PROGRESS_BAR_BACKGROUND_COLOR = 0xFF555555;
  public static final int PROGRESS_BAR_BORDER_COLOR = 0xFF000000;

  protected BaseContainerScreen(
      final T menu, final Inventory playerInventory, final Component title) {
    super(menu, playerInventory, title);
  }

  protected void renderDefaultBackground(
      final GuiGraphics guiGraphics, final int x, final int y, final int width, final int height) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_DEMO_BACKGROUND);

    int splitX = Math.min(width / 2, STANDARD_BACKGROUND_WIDTH / 2);
    int splitY = Math.min(height / 2, STANDARD_BACKGROUND_HEIGHT / 2);

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
          STANDARD_BACKGROUND_WIDTH - (width - splitX),
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
          STANDARD_BACKGROUND_HEIGHT - (height - splitY),
          Math.min(splitX, width),
          height - splitY);
    }

    // Bottom-right quadrant (includes right and bottom borders)
    if (width > splitX && height > splitY) {
      guiGraphics.blit(
          Constants.TEXTURE_DEMO_BACKGROUND,
          x + splitX,
          y + splitY,
          STANDARD_BACKGROUND_WIDTH - (width - splitX),
          STANDARD_BACKGROUND_HEIGHT - (height - splitY),
          width - splitX,
          height - splitY);
    }
  }

  protected void renderSlot(final GuiGraphics guiGraphics, final int x, final int y) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_INVENTORY);
    guiGraphics.blit(Constants.TEXTURE_INVENTORY, x - 1, y - 1, 7, 7, 18, 18);
  }

  protected void renderSlots(
      final GuiGraphics guiGraphics, final int x, final int y, final int columns, final int rows) {
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        renderSlot(guiGraphics, x + column * 18, y + row * 18);
      }
    }
  }

  protected void renderPlayerInventoryAt(
      final GuiGraphics guiGraphics, final int x, final int y, final int inventoryStartY) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_INVENTORY);
    guiGraphics.blit(Constants.TEXTURE_INVENTORY, x + 7, y + inventoryStartY - 1, 7, 83, 162, 54);
    guiGraphics.blit(Constants.TEXTURE_INVENTORY, x + 7, y + inventoryStartY + 57, 7, 141, 162, 18);
  }

  protected void renderProgressBar(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final int width,
      final int height,
      final int current,
      final int max,
      final int color) {
    guiGraphics.fill(x, y, x + width, y + height, PROGRESS_BAR_BACKGROUND_COLOR);

    if (max > 0 && current > 0) {
      int filledWidth = (current * width) / max;
      guiGraphics.fill(x, y, x + filledWidth, y + height, 0xFF000000 | color);
    }

    guiGraphics.fill(x, y, x + width, y + 1, PROGRESS_BAR_BORDER_COLOR);
    guiGraphics.fill(x, y + height - 1, x + width, y + height, PROGRESS_BAR_BORDER_COLOR);
    guiGraphics.fill(x, y, x + 1, y + height, PROGRESS_BAR_BORDER_COLOR);
    guiGraphics.fill(x + width - 1, y, x + width, y + height, PROGRESS_BAR_BORDER_COLOR);
  }

  protected void renderEnergyPowerUI(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final int currentEnergy,
      final int energyCapacity) {
    if (this.menu instanceof EnergyPowerMenu energyPowerMenu) {
      EnergyPowerRenderer.renderEnergyPowerUI(
          guiGraphics,
          x,
          y,
          currentEnergy,
          energyCapacity,
          energyPowerMenu.getEnergyFlowStatus());
    } else {
      EnergyPowerRenderer.renderEnergyPowerUI(
          guiGraphics, x, y, currentEnergy, energyCapacity, EnergyFlowStatus.IDLE);
    }
  }

  protected void renderEnergyPowerTooltips(
      final GuiGraphics guiGraphics,
      final int mouseX,
      final int mouseY,
      final int leftPos,
      final int topPos,
      final int currentEnergy,
      final int energyCapacity,
      final String batteryTooltipKey,
      final int batterySlotIndex) {
    EnergyPowerRenderer.renderEnergyPowerTooltips(
        guiGraphics,
        this.font,
        mouseX,
        mouseY,
        leftPos,
        topPos,
        currentEnergy,
        energyCapacity,
        batteryTooltipKey,
        this.menu,
        batterySlotIndex);
  }
}
