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
import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.menu.CollectorStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CollectorStationScreen extends BaseContainerScreen<CollectorStationMenu> {

  private static final String TRANSLATION_KEY_PREFIX = Constants.GUI_PREFIX + "collector_station.";
  private static final String TRANSLATION_STATUS_CHARGING =
      TRANSLATION_KEY_PREFIX + "status.charging";
  private static final String TRANSLATION_STATUS_COLLECTING =
      TRANSLATION_KEY_PREFIX + "status.collecting";
  private static final String TRANSLATION_STATUS_RETURNING =
      TRANSLATION_KEY_PREFIX + "status.returning";
  private static final String TRANSLATION_STATUS_PROCESSING =
      TRANSLATION_KEY_PREFIX + "status.processing";
  private static final String TRANSLATION_STATUS_NO_STORAGE =
      TRANSLATION_KEY_PREFIX + "status.no_storage";
  private static final String TRANSLATION_STATUS_NO_POWER =
      TRANSLATION_KEY_PREFIX + "status.no_power";
  private static final String TRANSLATION_BATTERY_SLOT = TRANSLATION_KEY_PREFIX + "battery_slot";

  private static final int SCREEN_WIDTH = 176;
  private static final int SCREEN_HEIGHT = 220;
  private static final int PROGRESS_BAR_WIDTH = 100;
  private static final int PROGRESS_BAR_HEIGHT = 14;
  private static final int PROGRESS_TEXT_Y = 114;

  public CollectorStationScreen(
      final CollectorStationMenu menu, final Inventory playerInventory, final Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = SCREEN_WIDTH;
    this.imageHeight = SCREEN_HEIGHT;
  }

  @Override
  protected void init() {
    super.init();
    this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    this.inventoryLabelY = this.imageHeight - 94;
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    int x = this.leftPos;
    int y = this.topPos;

    renderDefaultBackground(guiGraphics, x, y, imageWidth, imageHeight);

    renderEnergyPowerUI(
        guiGraphics,
        x,
        y,
        CollectorStationMenu.ENERGY_TAB_BATTERY_SLOT_X,
        CollectorStationMenu.ENERGY_TAB_BATTERY_SLOT_Y,
        CollectorStationMenu.ENERGY_TAB_ENERGY_BAR_X,
        CollectorStationMenu.ENERGY_TAB_ENERGY_BAR_Y,
        CollectorStationMenu.ENERGY_TAB_ENERGY_BAR_WIDTH,
        CollectorStationMenu.ENERGY_TAB_ENERGY_BAR_HEIGHT,
        menu.getCurrentEnergy(),
        menu.getEnergyCapacity());

    // Storage slots
    renderSlots(
        guiGraphics,
        x + CollectorStationMenu.STORAGE_GRID_START_X + 2,
        y + CollectorStationMenu.STORAGE_GRID_START_Y,
        CollectorStationMenu.STORAGE_GRID_COLUMNS,
        CollectorStationMenu.STORAGE_GRID_ROWS);

    // Upgrade slots
    for (int i = 0; i < CollectorStationMenu.UPGRADE_SLOTS_COUNT; i++) {
      renderSlot(
          guiGraphics,
          x + CollectorStationMenu.UPGRADE_SLOT_START_X + 2,
          y + CollectorStationMenu.UPGRADE_SLOT_Y + i * CollectorStationMenu.SLOT_SPACING);
    }

    // Player inventory
    renderPlayerInventoryAt(
        guiGraphics,
        x,
        y,
        CollectorStationMenu.PLAYER_INVENTORY_START_Y,
        CollectorStationMenu.PLAYER_HOTBAR_START_Y);

    // Progress bar frame
    int progressBarX = x + CollectorStationMenu.PROGRESS_BAR_X;
    int progressBarY = y + CollectorStationMenu.PROGRESS_BAR_Y;
    guiGraphics.fill(
        progressBarX - 1,
        progressBarY - 1,
        progressBarX + PROGRESS_BAR_WIDTH + 1,
        progressBarY + PROGRESS_BAR_HEIGHT + 1,
        0xFF8B8B8B);

    renderProgressBar(guiGraphics, x, y);
  }

  private void renderProgressBar(final GuiGraphics guiGraphics, final int x, final int y) {
    int progress = menu.getCollectionProgress();
    int maxProgress = menu.getMaxCollectionTime();

    int progressBarX = x + CollectorStationMenu.PROGRESS_BAR_X;
    int progressBarY = y + CollectorStationMenu.PROGRESS_BAR_Y;

    // Dark background
    guiGraphics.fill(
        progressBarX,
        progressBarY,
        progressBarX + PROGRESS_BAR_WIDTH,
        progressBarY + PROGRESS_BAR_HEIGHT,
        0xFF555555);

    // Progress fill (left to right, orange)
    if (maxProgress > 0 && progress > 0) {
      int progressBarWidth = (int) ((float) progress / maxProgress * PROGRESS_BAR_WIDTH);
      guiGraphics.fill(
          progressBarX,
          progressBarY,
          progressBarX + progressBarWidth,
          progressBarY + PROGRESS_BAR_HEIGHT,
          0xFFFFAA00);
    }
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawString(
        this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    guiGraphics.drawString(
        this.font,
        this.playerInventoryTitle,
        this.inventoryLabelX,
        this.inventoryLabelY,
        4210752,
        false);

    // Progress status text below progress bar
    int statusOrdinal = menu.getStatus();
    CollectorStationStatus status = CollectorStationStatus.values()[statusOrdinal];
    int stateTimer = menu.getStateTimer();
    int maxTime = menu.getMaxCollectionTime();
    Component statusText;

    // Use enum-based switch for status handling
    switch (status) {
      case CHARGING:
        if (maxTime > 0 && stateTimer >= 0) {
          int percentage = (stateTimer * 100) / maxTime;
          statusText = Component.translatable(TRANSLATION_STATUS_CHARGING, percentage);
        } else {
          statusText = Component.translatable(TRANSLATION_STATUS_CHARGING, 0);
        }
        break;
      case COLLECTING:
        if (maxTime > 0 && stateTimer >= 0) {
          int percentage = (stateTimer * 100) / maxTime;
          statusText = Component.translatable(TRANSLATION_STATUS_COLLECTING, percentage);
        } else {
          statusText = Component.translatable(TRANSLATION_STATUS_COLLECTING, 0);
        }
        break;
      case RETURNING:
        if (maxTime > 0 && stateTimer >= 0) {
          int percentage = (stateTimer * 100) / maxTime;
          statusText = Component.translatable(TRANSLATION_STATUS_RETURNING, percentage);
        } else {
          statusText = Component.translatable(TRANSLATION_STATUS_RETURNING, 0);
        }
        break;
      case PROCESSING:
        if (maxTime > 0 && stateTimer >= 0) {
          int percentage = (stateTimer * 100) / maxTime;
          statusText = Component.translatable(TRANSLATION_STATUS_PROCESSING, percentage);
        } else {
          statusText = Component.translatable(TRANSLATION_STATUS_PROCESSING, 0);
        }
        break;
      case NO_STORAGE:
        statusText = Component.translatable(TRANSLATION_STATUS_NO_STORAGE);
        break;
      case NO_POWER:
        statusText = Component.translatable(TRANSLATION_STATUS_NO_POWER);
        break;
      default:
        statusText = Component.translatable(TRANSLATION_STATUS_NO_POWER);
        break;
    }

    int textX =
        CollectorStationMenu.PROGRESS_BAR_X
            - (this.font.width(statusText) / 2)
            + (PROGRESS_BAR_WIDTH / 2);
    guiGraphics.drawString(this.font, statusText, textX, PROGRESS_TEXT_Y, 4210752, false);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    renderTooltip(guiGraphics, mouseX, mouseY);
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
        CollectorStationMenu.ENERGY_TAB_BATTERY_SLOT_X,
        CollectorStationMenu.ENERGY_TAB_BATTERY_SLOT_Y,
        CollectorStationMenu.ENERGY_TAB_ENERGY_BAR_X,
        CollectorStationMenu.ENERGY_TAB_ENERGY_BAR_Y,
        CollectorStationMenu.ENERGY_TAB_ENERGY_BAR_WIDTH,
        CollectorStationMenu.ENERGY_TAB_ENERGY_BAR_HEIGHT,
        menu.getCurrentEnergy(),
        menu.getEnergyCapacity(),
        TRANSLATION_BATTERY_SLOT,
        0);

    int relativeX = x - leftPos;
    int relativeY = y - topPos;

    // Progress bar tooltip
    if (relativeX >= CollectorStationMenu.PROGRESS_BAR_X
        && relativeX <= CollectorStationMenu.PROGRESS_BAR_X + PROGRESS_BAR_WIDTH
        && relativeY >= CollectorStationMenu.PROGRESS_BAR_Y
        && relativeY <= CollectorStationMenu.PROGRESS_BAR_Y + PROGRESS_BAR_HEIGHT) {

      int progress = menu.getCollectionProgress();
      int maxProgress = menu.getMaxCollectionTime();
      int percentage = maxProgress > 0 ? (progress * 100 / maxProgress) : 0;

      String statusName = getStatusName(menu.getStatus());
      Component tooltip = Component.literal(String.format("%s: %d%%", statusName, percentage));
      guiGraphics.renderTooltip(this.font, tooltip, x, y);
    }
  }

  private String getStatusName(final int statusOrdinal) {
    if (statusOrdinal < 0 || statusOrdinal >= CollectorStationStatus.values().length) {
      return "Unknown";
    }
    CollectorStationStatus status = CollectorStationStatus.values()[statusOrdinal];
    return switch (status) {
      case CHARGING -> "Charging";
      case COLLECTING -> "Collecting";
      case RETURNING -> "Returning";
      case PROCESSING -> "Processing";
      case NO_STORAGE -> "No Storage";
      case NO_POWER -> "No Power";
    };
  }
}
