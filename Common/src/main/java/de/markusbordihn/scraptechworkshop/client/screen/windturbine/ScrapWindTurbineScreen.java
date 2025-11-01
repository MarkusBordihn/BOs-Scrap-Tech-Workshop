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

package de.markusbordihn.scraptechworkshop.client.screen.windturbine;

import com.mojang.blaze3d.systems.RenderSystem;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.client.screen.BaseContainerScreen;
import de.markusbordihn.scraptechworkshop.client.screen.energy.EnergyPowerRenderer;
import de.markusbordihn.scraptechworkshop.data.windturbine.ScrapWindTurbineStatus;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import de.markusbordihn.scraptechworkshop.menu.ScrapWindTurbineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScrapWindTurbineScreen extends BaseContainerScreen<ScrapWindTurbineMenu> {

  private static final String TRANSLATION_KEY_PREFIX = Constants.GUI_PREFIX + "scrap_wind_turbine.";
  private static final String TRANSLATION_WIND_SPEED = TRANSLATION_KEY_PREFIX + "wind_speed";
  private static final String TRANSLATION_POWER_GEN = TRANSLATION_KEY_PREFIX + "power_generation";
  private static final String TRANSLATION_BATTERY_SLOT = TRANSLATION_KEY_PREFIX + "battery_slot";
  private static final String TRANSLATION_CHARGING_SLOT = TRANSLATION_KEY_PREFIX + "charging_slot";
  private static final String TRANSLATION_STATUS_IDLE = TRANSLATION_KEY_PREFIX + "status.idle";
  private static final String TRANSLATION_STATUS_NO_WIND =
      TRANSLATION_KEY_PREFIX + "status.no_wind";
  private static final String TRANSLATION_STATUS_LOW_WIND =
      TRANSLATION_KEY_PREFIX + "status.low_wind";
  private static final String TRANSLATION_STATUS_GENERATING =
      TRANSLATION_KEY_PREFIX + "status.generating";
  private static final String TRANSLATION_STATUS_ERROR = TRANSLATION_KEY_PREFIX + "status.error";
  private static final String TRANSLATION_STATUS_ERROR_HINT =
      TRANSLATION_KEY_PREFIX + "status.error.hint";
  private static final String TRANSLATION_STATUS_LOW_WIND_HINT =
      TRANSLATION_KEY_PREFIX + "status.low_wind.hint";

  private static final int SCREEN_WIDTH = 176;
  private static final int SCREEN_HEIGHT = 201;

  private static final int UI_LEFT_MARGIN = 8;
  private static final int WIND_SPEED_BAR_Y = 50;
  private static final int POWER_GEN_BAR_Y = 70;
  private static final int WIND_SPEED_TEXT_Y = 38;
  private static final int POWER_GEN_TEXT_Y = 58;
  private static final int STATUS_TEXT_Y = 78;
  private static final int HINT_TEXT_Y = 88;
  private static final int PROGRESS_BAR_WIDTH = 70;
  private static final int CHARGING_SLOT_ENERGY_BAR_OFFSET_Y = 20;

  private float displayWindSpeed = 0.0f;
  private float displayPowerGen = 0.0f;
  private long lastFluctuationTime = 0;

  public ScrapWindTurbineScreen(
      final ScrapWindTurbineMenu menu, final Inventory playerInventory, final Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = SCREEN_WIDTH;
    this.imageHeight = SCREEN_HEIGHT;
    this.inventoryLabelY = 107;
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

    EnergyPowerRenderer.renderEnergyPowerUI(
        guiGraphics, x, y, menu.getEnergy(), menu.getEnergyCapacity(), menu.getEnergyFlowStatus());

    renderSlot(
        guiGraphics,
        x + ScrapWindTurbineMenu.CHARGING_BATTERY_SLOT_X,
        y + ScrapWindTurbineMenu.CHARGING_BATTERY_SLOT_Y);

    int chargingSlotEnergy = getChargingBatteryEnergy();
    renderProgressBar(
        guiGraphics,
        x + ScrapWindTurbineMenu.CHARGING_BATTERY_SLOT_X - 1,
        y + ScrapWindTurbineMenu.CHARGING_BATTERY_SLOT_Y + CHARGING_SLOT_ENERGY_BAR_OFFSET_Y,
        18,
        2,
        chargingSlotEnergy,
        5000,
        0xFFD700);

    updateFluctuatingValues();

    renderProgressBar(
        guiGraphics,
        x + UI_LEFT_MARGIN,
        y + WIND_SPEED_BAR_Y,
        PROGRESS_BAR_WIDTH,
        4,
        (int) (displayWindSpeed * 10),
        250,
        0x4A9FD8);

    renderProgressBar(
        guiGraphics,
        x + UI_LEFT_MARGIN,
        y + POWER_GEN_BAR_Y,
        PROGRESS_BAR_WIDTH,
        4,
        (int) displayPowerGen,
        50,
        0x4ADB4A);

    renderPlayerInventoryAt(guiGraphics, x, y, ScrapWindTurbineMenu.PLAYER_INVENTORY_START_Y);

    renderInfoText(guiGraphics, x, y);
  }

  private void updateFluctuatingValues() {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastFluctuationTime > 1000) {
      int baseWindSpeed = menu.getWindSpeed();
      int basePowerGen = menu.getPowerGeneration();
      if (baseWindSpeed > 0) {
        float fluctuation = (float) ((Math.random() * 4.0 - 2.0) / 100.0);
        displayWindSpeed = (baseWindSpeed / 10.0f) * (1.0f + fluctuation);
        displayPowerGen = basePowerGen * (1.0f + fluctuation);
      } else {
        displayWindSpeed = 0.0f;
        displayPowerGen = 0.0f;
      }
      lastFluctuationTime = currentTime;
    }

    // Initialize if first time
    if (displayWindSpeed == 0.0f && menu.getWindSpeed() > 0) {
      displayWindSpeed = menu.getWindSpeed() / 10.0f;
      displayPowerGen = menu.getPowerGeneration();
    }
  }

  private void renderInfoText(GuiGraphics guiGraphics, int x, int y) {
    Component windSpeedText =
        Component.translatable(TRANSLATION_WIND_SPEED, String.format("%.1f", displayWindSpeed));
    Component powerGenText =
        Component.translatable(TRANSLATION_POWER_GEN, String.format("%.1f", displayPowerGen));

    // Text above the progress bars
    guiGraphics.drawString(
        this.font, windSpeedText, x + UI_LEFT_MARGIN, y + WIND_SPEED_TEXT_Y, 4210752, false);
    guiGraphics.drawString(
        this.font, powerGenText, x + UI_LEFT_MARGIN, y + POWER_GEN_TEXT_Y, 4210752, false);

    // Render status below power generation bar
    renderStatusInfo(guiGraphics, x, y);
  }

  private void renderStatusInfo(GuiGraphics guiGraphics, int x, int y) {
    var status = menu.getWindTurbineStatus();

    // Draw status text below power bar
    Component statusText;
    int color = 4210752;
    switch (status) {
      case IDLE -> {
        statusText = Component.translatable(TRANSLATION_STATUS_IDLE);
        color = 0xAAAAAA;
      }
      case NO_WIND -> {
        statusText = Component.translatable(TRANSLATION_STATUS_NO_WIND);
        color = 0xFFAA00;
      }
      case LOW_WIND -> {
        statusText = Component.translatable(TRANSLATION_STATUS_LOW_WIND);
        color = 0xFFCC00;
      }
      case GENERATING -> {
        statusText = Component.translatable(TRANSLATION_STATUS_GENERATING);
        color = 0x00FF00;
      }
      case ERROR -> {
        statusText = Component.translatable(TRANSLATION_STATUS_ERROR);
        color = 0xFF0000;
      }
      default -> statusText = Component.literal("Unknown");
    }
    guiGraphics.drawString(
        this.font, statusText, x + UI_LEFT_MARGIN, y + STATUS_TEXT_Y, color, false);

    // Show hints for error and low wind states
    if (status == ScrapWindTurbineStatus.ERROR) {
      Component hintText = Component.translatable(TRANSLATION_STATUS_ERROR_HINT);
      guiGraphics.drawString(
          this.font, hintText, x + UI_LEFT_MARGIN, y + HINT_TEXT_Y, 0xFF5555, false);
    } else if (status == ScrapWindTurbineStatus.LOW_WIND) {
      Component hintText = Component.translatable(TRANSLATION_STATUS_LOW_WIND_HINT);
      guiGraphics.drawString(
          this.font, hintText, x + UI_LEFT_MARGIN, y + HINT_TEXT_Y, 0xFFAA55, false);
    }
  }

  private int getChargingBatteryEnergy() {
    // Get energy of the battery in the charging slot
    var slot = menu.getSlot(menu.getChargingBatterySlotIndex());
    if (slot != null && !slot.getItem().isEmpty()) {
      var item = slot.getItem().getItem();
      if (item instanceof EnergyCellItem energyCell) {
        return energyCell.getEnergy(slot.getItem());
      }
    }
    return 0;
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
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;

    renderEnergyPowerTooltips(
        guiGraphics,
        mouseX,
        mouseY,
        x,
        y,
        menu.getEnergy(),
        menu.getEnergyCapacity(),
        TRANSLATION_BATTERY_SLOT,
        menu.getEnergyTabBatterySlotIndex());

    int relativeX = mouseX - x;
    int relativeY = mouseY - y;

    if (relativeX >= ScrapWindTurbineMenu.CHARGING_BATTERY_SLOT_X
        && relativeX <= ScrapWindTurbineMenu.CHARGING_BATTERY_SLOT_X + 16
        && relativeY >= ScrapWindTurbineMenu.CHARGING_BATTERY_SLOT_Y
        && relativeY <= ScrapWindTurbineMenu.CHARGING_BATTERY_SLOT_Y + 16) {
      guiGraphics.renderTooltip(
          this.font, Component.translatable(TRANSLATION_CHARGING_SLOT), mouseX, mouseY);
    }

    super.renderTooltip(guiGraphics, mouseX, mouseY);
  }
}
