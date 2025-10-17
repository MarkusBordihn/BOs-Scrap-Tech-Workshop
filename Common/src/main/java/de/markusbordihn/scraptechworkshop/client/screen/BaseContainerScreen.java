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

  protected BaseContainerScreen(
      final T menu, final Inventory playerInventory, final Component title) {
    super(menu, playerInventory, title);
  }

  protected void renderDefaultBackground(
      final GuiGraphics guiGraphics, final int x, final int y, final int width, final int height) {
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
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final int inventoryY,
      final int hotbarY) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_INVENTORY);
    guiGraphics.blit(Constants.TEXTURE_INVENTORY, x + 7, y + inventoryY, 7, 83, 162, 54);
    guiGraphics.blit(Constants.TEXTURE_INVENTORY, x + 7, y + hotbarY, 7, 141, 162, 18);
  }

  protected void renderEnergyBar(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final int barWidth,
      final int barHeight,
      final int currentEnergy,
      final int maxEnergy) {
    guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF555555);

    if (currentEnergy > 0 && maxEnergy > 0) {
      int energyBarHeight = (int) ((float) currentEnergy / maxEnergy * barHeight);
      guiGraphics.fill(
          x, y + (barHeight - energyBarHeight), x + barWidth, y + barHeight, 0xFF00FF00);
    }
  }

  protected void renderEnergyBarWithFrame(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final int barWidth,
      final int barHeight,
      final int currentEnergy,
      final int maxEnergy) {
    guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF8B8B8B);
    renderEnergyBar(guiGraphics, x, y, barWidth, barHeight, currentEnergy, maxEnergy);
  }

  protected void renderEnergyPercentage(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final int barWidth,
      final int percentage) {
    String energyText = percentage + "%";
    int textX = x + (barWidth / 2) - (this.font.width(energyText) / 2);
    guiGraphics.drawString(this.font, energyText, textX, y, 0x55FF55, false);
  }

  protected void renderEnergyTooltip(
      final GuiGraphics guiGraphics,
      final int mouseX,
      final int mouseY,
      final int currentEnergy,
      final int maxEnergy) {
    renderEnergyTooltip(guiGraphics, mouseX, mouseY, currentEnergy, maxEnergy, 3.7f);
  }

  protected void renderEnergyTooltip(
      final GuiGraphics guiGraphics,
      final int mouseX,
      final int mouseY,
      final int currentEnergy,
      final int maxEnergy,
      final float voltage) {
    int percentage = maxEnergy > 0 ? (currentEnergy * 100 / maxEnergy) : 0;
    Component tooltip =
        Component.literal(
            String.format(
                "%d / %d mAh (%.1fV) - %d%%", currentEnergy, maxEnergy, voltage, percentage));
    guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
  }

  protected boolean isMouseOverEnergyBar(
      int mouseX, int mouseY, int barX, int barY, int barWidth, int barHeight) {
    return mouseX >= barX
        && mouseX <= barX + barWidth
        && mouseY >= barY
        && mouseY <= barY + barHeight;
  }

  protected void renderEnergyTab(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final int tabWidth,
      final int tabHeight) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_DEMO_BACKGROUND);

    int cornerHeight = Math.min(tabHeight / 2, 10);
    guiGraphics.blit(Constants.TEXTURE_DEMO_BACKGROUND, x, y, 0, 0, tabWidth, cornerHeight);

    int middleHeight = tabHeight - (cornerHeight * 2);
    if (middleHeight > 0) {
      guiGraphics.blit(
          Constants.TEXTURE_DEMO_BACKGROUND, x, y + cornerHeight, 0, 5, tabWidth, middleHeight);
    }

    guiGraphics.blit(
        Constants.TEXTURE_DEMO_BACKGROUND,
        x,
        y + tabHeight - cornerHeight,
        0,
        166 - cornerHeight,
        tabWidth,
        cornerHeight);
  }

  protected void renderEnergyPowerUI(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final int batterySlotX,
      final int batterySlotY,
      final int energyBarX,
      final int energyBarY,
      final int energyBarWidth,
      final int energyBarHeight,
      final int currentEnergy,
      final int energyCapacity) {

    int tabWidth = 26;
    int tabHeight = 93;
    int tabX = x - tabWidth;
    int tabY = y;

    renderEnergyTab(guiGraphics, tabX, tabY, tabWidth, tabHeight);
    renderSlot(guiGraphics, x + batterySlotX, y + batterySlotY);
    renderEnergyBarWithFrame(
        guiGraphics,
        x + energyBarX,
        y + energyBarY,
        energyBarWidth,
        energyBarHeight,
        currentEnergy,
        energyCapacity);
  }

  protected void renderEnergyPowerTooltips(
      final GuiGraphics guiGraphics,
      final int mouseX,
      final int mouseY,
      final int leftPos,
      final int topPos,
      final int batterySlotX,
      final int batterySlotY,
      final int energyBarX,
      final int energyBarY,
      final int energyBarWidth,
      final int energyBarHeight,
      final int currentEnergy,
      final int energyCapacity,
      final String batteryTooltipKey) {

    int relativeX = mouseX - leftPos;
    int relativeY = mouseY - topPos;

    if (relativeX >= batterySlotX - 1
        && relativeX <= batterySlotX + 17
        && relativeY >= batterySlotY - 1
        && relativeY <= batterySlotY + 17) {
      guiGraphics.renderTooltip(
          this.font, Component.translatable(batteryTooltipKey), mouseX, mouseY);
    }

    if (isMouseOverEnergyBar(
        relativeX, relativeY, energyBarX, energyBarY, energyBarWidth, energyBarHeight)) {
      renderEnergyTooltip(guiGraphics, mouseX, mouseY, currentEnergy, energyCapacity);
    }
  }
}
