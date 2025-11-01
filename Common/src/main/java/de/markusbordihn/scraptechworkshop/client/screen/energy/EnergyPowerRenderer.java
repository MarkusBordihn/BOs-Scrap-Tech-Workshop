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

package de.markusbordihn.scraptechworkshop.client.screen.energy;

import com.mojang.blaze3d.systems.RenderSystem;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.energy.EnergyFlowStatus;
import de.markusbordihn.scraptechworkshop.menu.EnergyPowerMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class EnergyPowerRenderer {

  public static final int TAB_WIDTH = 25;
  public static final int TAB_HEIGHT = 125;
  public static final int TAB_CORNER_HEIGHT = 10;
  public static final int SLOT_SIZE = 18;
  public static final int SLOT_BORDER = 1;

  public static final int ENERGY_BAR_X = -19;
  public static final int ENERGY_BAR_Y = 40;
  public static final int ENERGY_BAR_WIDTH = 16;
  public static final int ENERGY_BAR_HEIGHT = 60;
  public static final int ENERGY_BAR_BACKGROUND_COLOR = 0xFF555555;
  public static final int ENERGY_BAR_FILL_COLOR = 0xFF00FF00;
  public static final int ENERGY_BAR_FRAME_COLOR = 0xFF8B8B8B;

  public static final int STATUS_ICON_SIZE = 16;
  public static final int STATUS_ICON_X_OFFSET = -19;
  public static final int STATUS_ICON_Y_OFFSET = 22;
  public static final int TEXTURE_WIDTH = 64;
  public static final int TEXTURE_HEIGHT = 64;

  public static final float DEFAULT_VOLTAGE = 3.7f;

  private static final ResourceLocation ENERGY_POWER_GUI_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/gui/energy_power_gui.png");

  private EnergyPowerRenderer() {}

  public static void renderEnergyPowerUI(
      final GuiGraphics guiGraphics,
      final int screenX,
      final int screenY,
      final int currentEnergy,
      final int energyCapacity,
      final EnergyFlowStatus flowStatus) {
    int tabX = screenX - TAB_WIDTH;

    renderEnergyTab(guiGraphics, tabX, screenY);
    renderBatterySlot(
        guiGraphics,
        screenX + EnergyPowerMenu.ENERGY_TAB_BATTERY_SLOT_X,
        screenY + EnergyPowerMenu.ENERGY_TAB_BATTERY_SLOT_Y);
    renderStatusIcon(
        guiGraphics, screenX + STATUS_ICON_X_OFFSET, screenY + STATUS_ICON_Y_OFFSET, flowStatus);
    renderEnergyBarWithFrame(
        guiGraphics,
        screenX + ENERGY_BAR_X,
        screenY + ENERGY_BAR_Y,
        ENERGY_BAR_WIDTH,
        ENERGY_BAR_HEIGHT,
        currentEnergy,
        energyCapacity);
  }

  public static void renderEnergyPowerTooltips(
      final GuiGraphics guiGraphics,
      final Font font,
      final int mouseX,
      final int mouseY,
      final int screenX,
      final int screenY,
      final int currentEnergy,
      final int energyCapacity,
      final String batteryTooltipKey,
      final AbstractContainerMenu menu,
      final int batterySlotIndex) {
    int relativeX = mouseX - screenX;
    int relativeY = mouseY - screenY;

    Slot batterySlot = menu.getSlot(batterySlotIndex);
    if (isMouseOverArea(
            relativeX,
            relativeY,
            EnergyPowerMenu.ENERGY_TAB_BATTERY_SLOT_X - SLOT_BORDER,
            EnergyPowerMenu.ENERGY_TAB_BATTERY_SLOT_Y - SLOT_BORDER,
            SLOT_SIZE,
            SLOT_SIZE)
        && batterySlot.getItem().isEmpty()) {
      guiGraphics.renderTooltip(font, Component.translatable(batteryTooltipKey), mouseX, mouseY);
    }

    if (isMouseOverArea(
        relativeX, relativeY, ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT)) {
      renderEnergyTooltip(guiGraphics, font, mouseX, mouseY, currentEnergy, energyCapacity);
    }
  }

  private static void renderEnergyTab(final GuiGraphics guiGraphics, final int x, final int y) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_DEMO_BACKGROUND);

    int cornerHeight = Math.min(TAB_HEIGHT / 2, TAB_CORNER_HEIGHT);
    guiGraphics.blit(Constants.TEXTURE_DEMO_BACKGROUND, x, y, 0, 0, TAB_WIDTH, cornerHeight);

    int middleHeight = TAB_HEIGHT - (cornerHeight * 2);
    if (middleHeight > 0) {
      guiGraphics.blit(
          Constants.TEXTURE_DEMO_BACKGROUND, x, y + cornerHeight, 0, 5, TAB_WIDTH, middleHeight);
    }

    guiGraphics.blit(
        Constants.TEXTURE_DEMO_BACKGROUND,
        x,
        y + TAB_HEIGHT - cornerHeight,
        0,
        166 - cornerHeight,
        TAB_WIDTH,
        cornerHeight);
  }

  private static void renderBatterySlot(final GuiGraphics guiGraphics, final int x, final int y) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_INVENTORY);
    guiGraphics.blit(
        Constants.TEXTURE_INVENTORY, x - SLOT_BORDER, y - SLOT_BORDER, 7, 7, SLOT_SIZE, SLOT_SIZE);
  }

  private static void renderStatusIcon(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final EnergyFlowStatus flowStatus) {
    RenderSystem.setShaderTexture(0, ENERGY_POWER_GUI_TEXTURE);
    int spriteIndex = flowStatus.getSpriteIndex();
    int textureY = spriteIndex * STATUS_ICON_SIZE;
    guiGraphics.blit(
        ENERGY_POWER_GUI_TEXTURE,
        x,
        y,
        0,
        textureY,
        STATUS_ICON_SIZE,
        STATUS_ICON_SIZE,
        TEXTURE_WIDTH,
        TEXTURE_HEIGHT);
  }

  private static void renderEnergyBarWithFrame(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final int barWidth,
      final int barHeight,
      final int currentEnergy,
      final int maxEnergy) {
    guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, ENERGY_BAR_FRAME_COLOR);
    guiGraphics.fill(x, y, x + barWidth, y + barHeight, ENERGY_BAR_BACKGROUND_COLOR);

    if (currentEnergy > 0 && maxEnergy > 0) {
      int energyBarHeight = (int) ((float) currentEnergy / maxEnergy * barHeight);
      guiGraphics.fill(
          x, y + (barHeight - energyBarHeight), x + barWidth, y + barHeight, ENERGY_BAR_FILL_COLOR);
    }
  }

  private static void renderEnergyTooltip(
      final GuiGraphics guiGraphics,
      final Font font,
      final int mouseX,
      final int mouseY,
      final int currentEnergy,
      final int maxEnergy) {
    int percentage = maxEnergy > 0 ? (currentEnergy * 100 / maxEnergy) : 0;
    Component tooltip =
        Component.literal(
            String.format(
                "%d / %d mAh (%.1fV) - %d%%",
                currentEnergy, maxEnergy, DEFAULT_VOLTAGE, percentage));
    guiGraphics.renderTooltip(font, tooltip, mouseX, mouseY);
  }

  private static boolean isMouseOverArea(
      final int mouseX,
      final int mouseY,
      final int areaX,
      final int areaY,
      final int areaWidth,
      final int areaHeight) {
    return mouseX >= areaX
        && mouseX <= areaX + areaWidth
        && mouseY >= areaY
        && mouseY <= areaY + areaHeight;
  }
}
