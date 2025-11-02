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
import com.mojang.math.Axis;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.energy.EnergyFlowStatus;
import de.markusbordihn.scraptechworkshop.data.energy.ExternalEnergyFlowStatus;
import de.markusbordihn.scraptechworkshop.menu.EnergyPowerGeneratorMenu;
import de.markusbordihn.scraptechworkshop.menu.EnergyPowerMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class EnergyPowerRenderer {

  public static final int TAB_WIDTH = 27;
  public static final int TAB_HEIGHT = 141;
  public static final int TAB_CORNER_HEIGHT = 10;
  public static final int SLOT_SIZE = 18;
  public static final int SLOT_BORDER = 1;
  public static final int ENERGY_BAR_X = -20;
  public static final int ENERGY_BAR_Y = 50;
  public static final int ENERGY_BAR_WIDTH = 16;
  public static final int ENERGY_BAR_HEIGHT = 60;
  public static final int ENERGY_BAR_BACKGROUND_COLOR = 0xFF2A2A2A;
  public static final int ENERGY_BAR_FILL_COLOR = 0xFF00FF00;
  public static final int ENERGY_BAR_FRAME_COLOR = 0xFF8B8B8B;
  public static final int ENERGY_BAR_HIGHLIGHT_COLOR = 0x80FFFFFF;
  public static final int ENERGY_BAR_SHADOW_COLOR = 0x40000000;
  public static final int BATTERY_CAP_WIDTH = 10;
  public static final int BATTERY_CAP_HEIGHT = 4;
  public static final int BATTERY_CAP_X_OFFSET = 3;
  public static final int BATTERY_CAP_Y_OFFSET = -5;
  public static final int STATUS_ICON_SIZE = 16;
  public static final int STATUS_ICON_X_OFFSET = -23;
  public static final int STATUS_ICON_Y_OFFSET = 25;
  public static final int TEXTURE_WIDTH = 64;
  public static final int TEXTURE_HEIGHT = 64;
  public static final int STATUS_ICON_U_OFFSET = 5;
  public static final float DEFAULT_VOLTAGE = 3.2f;
  private static final int[] receiveHistory = new int[20];
  private static final int[] distributeHistory = new int[20];
  private static final ResourceLocation ENERGY_POWER_GUI_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/gui/energy_power_gui.png");
  private static int historyIndex = 0;

  private EnergyPowerRenderer() {}

  public static void renderEnergyPowerUI(
      final GuiGraphics guiGraphics,
      final int screenX,
      final int screenY,
      final int currentEnergy,
      final int energyCapacity,
      final EnergyFlowStatus flowStatus,
      final int receiveAmount,
      final int distributeAmount,
      final int batterySlotY) {
    ExternalEnergyFlowStatus externalFlowStatus =
        calculateExternalFlowStatus(receiveAmount, distributeAmount);
    renderEnergyTab(guiGraphics, screenX - TAB_WIDTH, screenY);
    renderBatterySlot(
        guiGraphics, screenX + EnergyPowerMenu.ENERGY_TAB_BATTERY_SLOT_X, screenY + batterySlotY);
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
    renderExternalFlowIcon(
        guiGraphics,
        screenX + ENERGY_BAR_X,
        screenY + ENERGY_BAR_Y + ENERGY_BAR_HEIGHT + 9,
        externalFlowStatus);
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
    EnergyFlowStatus flowStatus = EnergyFlowStatus.IDLE;
    ExternalEnergyFlowStatus externalFlowStatus = ExternalEnergyFlowStatus.IDLE;
    int receiveAmount = 0;
    int distributeAmount = 0;

    if (menu instanceof EnergyPowerMenu energyMenu) {
      flowStatus = energyMenu.getEnergyFlowStatus();
      receiveAmount = energyMenu.getEnergyReceiveAmount();
      distributeAmount = energyMenu.getEnergyDistributeAmount();
      externalFlowStatus = calculateExternalFlowStatus(receiveAmount, distributeAmount);
    } else if (menu instanceof EnergyPowerGeneratorMenu generatorMenu) {
      flowStatus = generatorMenu.getEnergyFlowStatus();
      receiveAmount = generatorMenu.getEnergyReceiveAmount();
      distributeAmount = generatorMenu.getEnergyDistributeAmount();
      externalFlowStatus = calculateExternalFlowStatus(receiveAmount, distributeAmount);
    }

    int relativeX = mouseX - screenX;
    int relativeY = mouseY - screenY;

    if (isMouseOverArea(
        relativeX,
        relativeY,
        STATUS_ICON_X_OFFSET,
        STATUS_ICON_Y_OFFSET,
        STATUS_ICON_SIZE,
        STATUS_ICON_SIZE)) {
      Component statusTooltip = Component.literal("Status: " + flowStatus.name());
      guiGraphics.renderTooltip(font, statusTooltip, mouseX, mouseY);
    } else if (isMouseOverArea(
        relativeX,
        relativeY,
        ENERGY_BAR_X,
        ENERGY_BAR_Y + ENERGY_BAR_HEIGHT + 2,
        STATUS_ICON_SIZE,
        STATUS_ICON_SIZE)) {
      List<Component> tooltip = new ArrayList<>();
      tooltip.add(Component.literal("External: " + externalFlowStatus.name()));
      if (externalFlowStatus == ExternalEnergyFlowStatus.ENERGY_IN && receiveAmount > 0) {
        receiveHistory[historyIndex] = receiveAmount;
        int avgReceive = calculateAverage(receiveHistory);
        int perSecond = avgReceive * 20;
        tooltip.add(Component.literal("§aReceiving: " + perSecond + " mAh/s"));
        tooltip.add(Component.literal("§8(" + receiveAmount + " mAh/tick)"));
      } else if (externalFlowStatus == ExternalEnergyFlowStatus.ENERGY_OUT
          && distributeAmount > 0) {
        distributeHistory[historyIndex] = distributeAmount;
        int avgDistribute = calculateAverage(distributeHistory);
        int perSecond = avgDistribute * 20;
        tooltip.add(Component.literal("§cDistributing: " + perSecond + " mAh/s"));
        tooltip.add(Component.literal("§8(" + distributeAmount + " mAh/tick)"));
      }
      historyIndex = (historyIndex + 1) % 20;
      guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
    } else if (isMouseOverArea(
        relativeX, relativeY, ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT)) {
      renderEnergyTooltip(guiGraphics, font, mouseX, mouseY, currentEnergy, energyCapacity);
    } else if (isMouseOverArea(
        relativeX,
        relativeY,
        EnergyPowerMenu.ENERGY_TAB_BATTERY_SLOT_X - SLOT_BORDER,
        EnergyPowerMenu.ENERGY_TAB_BATTERY_SLOT_Y - SLOT_BORDER,
        SLOT_SIZE,
        SLOT_SIZE)) {
      Slot batterySlot = menu.getSlot(batterySlotIndex);
      if (batterySlot.getItem().isEmpty()) {
        Component batteryTooltip = Component.translatable(batteryTooltipKey);
        guiGraphics.renderTooltip(font, batteryTooltip, mouseX, mouseY);
      }
    }
  }

  private static void renderEnergyTab(final GuiGraphics guiGraphics, final int x, final int y) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_DEMO_BACKGROUND);
    RenderSystem.setShaderColor(0.98f, 0.98f, 0.98f, 1.0f);

    int cornerHeight = Math.min(TAB_HEIGHT / 2, TAB_CORNER_HEIGHT);
    guiGraphics.blit(Constants.TEXTURE_DEMO_BACKGROUND, x, y, 0, 0, TAB_WIDTH, cornerHeight);
    guiGraphics.blit(
        Constants.TEXTURE_DEMO_BACKGROUND,
        x,
        y + cornerHeight,
        0,
        5,
        TAB_WIDTH,
        TAB_HEIGHT - (cornerHeight * 2));
    guiGraphics.blit(
        Constants.TEXTURE_DEMO_BACKGROUND,
        x,
        y + TAB_HEIGHT - cornerHeight,
        0,
        166 - cornerHeight,
        TAB_WIDTH,
        cornerHeight);

    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
  }

  private static void renderBatterySlot(final GuiGraphics guiGraphics, final int x, final int y) {
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_INVENTORY);
    guiGraphics.blit(
        Constants.TEXTURE_INVENTORY, x - SLOT_BORDER, y - SLOT_BORDER, 7, 7, SLOT_SIZE, SLOT_SIZE);
  }

  private static void renderStatusIcon(
      final GuiGraphics guiGraphics, final int x, final int y, final EnergyFlowStatus flowStatus) {
    RenderSystem.setShaderTexture(0, ENERGY_POWER_GUI_TEXTURE);
    int spriteIndex = flowStatus.getSpriteIndex();
    int textureY = spriteIndex * STATUS_ICON_SIZE;
    guiGraphics.blit(
        ENERGY_POWER_GUI_TEXTURE,
        x,
        y,
        STATUS_ICON_U_OFFSET,
        textureY,
        STATUS_ICON_SIZE + 6,
        STATUS_ICON_SIZE,
        TEXTURE_WIDTH,
        TEXTURE_HEIGHT);
  }

  private static void renderExternalFlowIcon(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final ExternalEnergyFlowStatus flowStatus) {
    var poseStack = guiGraphics.pose();
    poseStack.pushPose();
    poseStack.translate(x + STATUS_ICON_SIZE / 2.0, y + STATUS_ICON_SIZE / 2.0, 0);
    poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
    poseStack.translate(-STATUS_ICON_SIZE / 2.0, -STATUS_ICON_SIZE / 2.0, 0);

    RenderSystem.setShaderTexture(0, ENERGY_POWER_GUI_TEXTURE);
    int textureY = flowStatus.getSpriteIndex() * STATUS_ICON_SIZE;
    guiGraphics.blit(
        ENERGY_POWER_GUI_TEXTURE,
        0,
        0,
        STATUS_ICON_U_OFFSET,
        textureY,
        STATUS_ICON_SIZE + 6,
        STATUS_ICON_SIZE,
        TEXTURE_WIDTH,
        TEXTURE_HEIGHT);

    poseStack.popPose();
  }

  public static void renderEnergyBarWithFrame(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final int barWidth,
      final int barHeight,
      final int currentEnergy,
      final int maxEnergy) {

    // Draw battery cap
    guiGraphics.fill(
        x + BATTERY_CAP_X_OFFSET - 1,
        y + BATTERY_CAP_Y_OFFSET - 1,
        x + BATTERY_CAP_X_OFFSET + BATTERY_CAP_WIDTH + 1,
        y + BATTERY_CAP_Y_OFFSET + BATTERY_CAP_HEIGHT + 1,
        ENERGY_BAR_FRAME_COLOR);
    guiGraphics.fill(
        x + BATTERY_CAP_X_OFFSET,
        y + BATTERY_CAP_Y_OFFSET,
        x + BATTERY_CAP_X_OFFSET + BATTERY_CAP_WIDTH,
        y + BATTERY_CAP_Y_OFFSET + BATTERY_CAP_HEIGHT,
        ENERGY_BAR_FRAME_COLOR);

    // Draw energy bar frame
    guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, ENERGY_BAR_FRAME_COLOR);
    guiGraphics.fill(x, y, x + barWidth, y + barHeight, ENERGY_BAR_BACKGROUND_COLOR);

    // Draw energy bar fill
    if (currentEnergy > 0 && maxEnergy > 0) {
      int fillY = y + barHeight - (int) ((float) currentEnergy / maxEnergy * barHeight);
      guiGraphics.fill(x, fillY, x + barWidth, y + barHeight, ENERGY_BAR_FILL_COLOR);
      guiGraphics.fill(x, fillY, x + barWidth, fillY + 1, ENERGY_BAR_SHADOW_COLOR);
      guiGraphics.fill(
          x + 2,
          fillY + 2,
          x + 6,
          fillY + 2 + Math.max(2, (y + barHeight - fillY) / 2),
          ENERGY_BAR_HIGHLIGHT_COLOR);
      guiGraphics.fill(
          x + barWidth - 2, fillY, x + barWidth, y + barHeight, ENERGY_BAR_SHADOW_COLOR);
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

  private static ExternalEnergyFlowStatus calculateExternalFlowStatus(
      final int receiveAmount, final int distributeAmount) {
    if (receiveAmount > 0) {
      return ExternalEnergyFlowStatus.ENERGY_IN;
    } else if (distributeAmount > 0) {
      return ExternalEnergyFlowStatus.ENERGY_OUT;
    }
    return ExternalEnergyFlowStatus.IDLE;
  }

  private static int calculateAverage(final int[] values) {
    int sum = 0;
    int count = 0;
    for (int value : values) {
      if (value > 0) {
        sum += value;
        count++;
      }
    }
    return count > 0 ? sum / count : 0;
  }
}
