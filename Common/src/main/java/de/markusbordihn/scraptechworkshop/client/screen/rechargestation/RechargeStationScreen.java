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

package de.markusbordihn.scraptechworkshop.client.screen.rechargestation;

import com.mojang.blaze3d.systems.RenderSystem;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.client.screen.BaseContainerScreen;
import de.markusbordihn.scraptechworkshop.data.rechargestation.RechargeStationStatus;
import de.markusbordihn.scraptechworkshop.energy.EnergyCell;
import de.markusbordihn.scraptechworkshop.menu.RechargeStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class RechargeStationScreen extends BaseContainerScreen<RechargeStationMenu> {

  private static final String TRANSLATION_KEY_PREFIX = Constants.GUI_PREFIX + "recharge_station.";
  private static final String TRANSLATION_NO_POWER = TRANSLATION_KEY_PREFIX + "no_power";
  private static final String TRANSLATION_CHARGING = TRANSLATION_KEY_PREFIX + "charging";
  private static final String TRANSLATION_DONE = TRANSLATION_KEY_PREFIX + "done";
  private static final String TRANSLATION_IDLE = TRANSLATION_KEY_PREFIX + "idle";
  private static final String TRANSLATION_INPUT_SLOT = TRANSLATION_KEY_PREFIX + "input_slot";
  private static final String TRANSLATION_BATTERY_SLOT = TRANSLATION_KEY_PREFIX + "battery_slot";

  private static final int SCREEN_WIDTH = 176;
  private static final int SCREEN_HEIGHT = 181;

  private static final int PROGRESS_BAR_AREA_X1 = 68;
  private static final int PROGRESS_BAR_AREA_X2 = 108;
  private static final int PROGRESS_BAR_AREA_Y1 = 56;
  private static final int PROGRESS_BAR_AREA_Y2 = 61;

  private static final int INPUT_SLOT_X = 80;
  private static final int INPUT_SLOT_Y = 35;

  public RechargeStationScreen(
      final RechargeStationMenu menu, final Inventory playerInventory, final Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = SCREEN_WIDTH;
    this.imageHeight = SCREEN_HEIGHT;
    this.inventoryLabelY = RechargeStationMenu.PLAYER_INVENTORY_START_Y - 11;
  }

  @Override
  protected void init() {
    super.init();
    this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;

    renderDefaultBackground(guiGraphics, x, y, imageWidth, imageHeight);

    renderEnergyPowerUI(guiGraphics, x, y, menu.getCurrentEnergy(), menu.getEnergyCapacity());

    renderSlot(guiGraphics, x + INPUT_SLOT_X, y + INPUT_SLOT_Y);

    renderPlayerInventoryAt(guiGraphics, x, y, RechargeStationMenu.PLAYER_INVENTORY_START_Y);

    int progressBarX = x + RechargeStationMenu.PROGRESS_BAR_X;
    int progressBarY = y + RechargeStationMenu.PROGRESS_BAR_Y;
    int progressBarWidth = RechargeStationMenu.PROGRESS_BAR_WIDTH;
    int progressBarHeight = RechargeStationMenu.PROGRESS_BAR_HEIGHT;

    guiGraphics.fill(
        progressBarX,
        progressBarY,
        progressBarX + progressBarWidth,
        progressBarY + progressBarHeight,
        0xFF404040);

    if (menu.isCharging()) {
      int scaledProgress = menu.getScaledProgress();
      guiGraphics.fill(
          progressBarX,
          progressBarY,
          progressBarX + scaledProgress,
          progressBarY + progressBarHeight,
          0xFF00FF00);
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    super.renderLabels(guiGraphics, mouseX, mouseY);

    // Display battery charge status under the progress bar
    ItemStack inputStack = menu.getSlot(0).getItem();
    if (!inputStack.isEmpty() && inputStack.getItem() instanceof EnergyCell energyCell) {
      int currentEnergy = energyCell.getEnergy(inputStack);
      int maxEnergy = energyCell.getCapacity();
      String batteryText = currentEnergy + " / " + maxEnergy + " mAh";

      int textWidth = this.font.width(batteryText);
      int batteryTextX =
          RechargeStationMenu.PROGRESS_BAR_X
              + (RechargeStationMenu.PROGRESS_BAR_WIDTH - textWidth) / 2;
      int batteryTextY =
          RechargeStationMenu.PROGRESS_BAR_Y + RechargeStationMenu.PROGRESS_BAR_HEIGHT + 3;

      guiGraphics.drawString(this.font, batteryText, batteryTextX, batteryTextY, 0x404040, false);
    }
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    super.renderTooltip(guiGraphics, x, y);

    renderEnergyPowerTooltips(
        guiGraphics,
        x,
        y,
        leftPos,
        topPos,
        menu.getCurrentEnergy(),
        menu.getEnergyCapacity(),
        TRANSLATION_BATTERY_SLOT,
        1);

    int relativeX = x - leftPos;
    int relativeY = y - topPos;

    if (relativeX >= PROGRESS_BAR_AREA_X1
        && relativeX <= PROGRESS_BAR_AREA_X2
        && relativeY >= PROGRESS_BAR_AREA_Y1
        && relativeY <= PROGRESS_BAR_AREA_Y2) {
      RechargeStationStatus status = menu.getRechargeStationStatus();
      Component statusText =
          switch (status) {
            case NO_POWER -> Component.translatable(TRANSLATION_NO_POWER);
            case CHARGING -> Component.translatable(TRANSLATION_CHARGING);
            case DONE -> Component.translatable(TRANSLATION_DONE);
            case IDLE -> Component.translatable(TRANSLATION_IDLE);
          };
      guiGraphics.renderTooltip(this.font, statusText, x, y);
    }

    if (!menu.isCharging()
        && relativeX >= INPUT_SLOT_X - 1
        && relativeX <= INPUT_SLOT_X + 17
        && relativeY >= INPUT_SLOT_Y - 1
        && relativeY <= INPUT_SLOT_Y + 17
        && this.menu.getSlot(0).getItem().isEmpty()) {
      guiGraphics.renderTooltip(this.font, Component.translatable(TRANSLATION_INPUT_SLOT), x, y);
    }
  }
}
