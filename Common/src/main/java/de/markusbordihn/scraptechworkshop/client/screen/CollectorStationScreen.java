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
import de.markusbordihn.scraptechworkshop.menu.CollectorStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

  private static final ResourceLocation TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/gui/collector_station.png");

  private static final int SCREEN_WIDTH = 176;
  private static final int SCREEN_HEIGHT = 220;
  private static final int ENERGY_BAR_WIDTH = 14;
  private static final int ENERGY_BAR_HEIGHT = 50;
  private static final int ENERGY_BAR_TEXTURE_X = 176;
  private static final int ENERGY_BAR_TEXTURE_Y = 0;
  private static final int PROGRESS_BAR_WIDTH = 100;
  private static final int PROGRESS_BAR_HEIGHT = 14;
  private static final int PROGRESS_BAR_TEXTURE_X = 176;
  private static final int PROGRESS_BAR_TEXTURE_Y = 50;
  private static final int PROGRESS_TEXT_Y = 114;
  private static final int MAX_ENERGY = 5000;

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

    // Battery slot
    renderSlot(
        guiGraphics,
        x + CollectorStationMenu.BATTERY_SLOT_X,
        y + CollectorStationMenu.BATTERY_SLOT_Y);

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

    // Energy bar frame
    int energyBarX = x + CollectorStationMenu.ENERGY_BAR_X;
    int energyBarY = y + CollectorStationMenu.ENERGY_BAR_Y;
    guiGraphics.fill(
        energyBarX - 1,
        energyBarY - 1,
        energyBarX + ENERGY_BAR_WIDTH + 1,
        energyBarY + ENERGY_BAR_HEIGHT + 1,
        0xFF8B8B8B);

    // Progress bar frame
    int progressBarX = x + CollectorStationMenu.PROGRESS_BAR_X;
    int progressBarY = y + CollectorStationMenu.PROGRESS_BAR_Y;
    guiGraphics.fill(
        progressBarX - 1,
        progressBarY - 1,
        progressBarX + PROGRESS_BAR_WIDTH + 1,
        progressBarY + PROGRESS_BAR_HEIGHT + 1,
        0xFF8B8B8B);

    renderEnergyBar(guiGraphics, x, y);
    renderProgressBar(guiGraphics, x, y);
  }

  private void renderEnergyBar(final GuiGraphics guiGraphics, final int x, final int y) {
    int energy = menu.getCurrentEnergy();
    int maxEnergy = 5000;

    int energyBarX = x + CollectorStationMenu.ENERGY_BAR_X;
    int energyBarY = y + CollectorStationMenu.ENERGY_BAR_Y;

    // Dark background
    guiGraphics.fill(
        energyBarX,
        energyBarY,
        energyBarX + ENERGY_BAR_WIDTH,
        energyBarY + ENERGY_BAR_HEIGHT,
        0xFF555555);

    // Energy fill (bottom to top, green)
    if (energy > 0) {
      int energyBarHeight = (int) ((float) energy / maxEnergy * ENERGY_BAR_HEIGHT);
      guiGraphics.fill(
          energyBarX,
          energyBarY + (ENERGY_BAR_HEIGHT - energyBarHeight),
          energyBarX + ENERGY_BAR_WIDTH,
          energyBarY + ENERGY_BAR_HEIGHT,
          0xFF00FF00);
    }

    // Energy percentage text below energy bar (like multi-tool display)
    int percentage = energy * 100 / maxEnergy;
    String energyText = percentage + "%";
    int textX = energyBarX + (ENERGY_BAR_WIDTH / 2) - (this.font.width(energyText) / 2) + 1;
    int textY = energyBarY + ENERGY_BAR_HEIGHT + 3;
    guiGraphics.drawString(this.font, energyText, textX, textY, 0x55FF55, false);
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
    int status = menu.getStatus();
    int stateTimer = menu.getStateTimer();
    int maxTime = menu.getMaxCollectionTime();
    Component statusText;

    // Map status ordinal to appropriate message
    switch (status) {
      case 0: // CHARGING
        if (maxTime > 0 && stateTimer >= 0) {
          int percentage = (stateTimer * 100) / maxTime;
          statusText = Component.translatable(TRANSLATION_STATUS_CHARGING, percentage);
        } else {
          statusText = Component.translatable(TRANSLATION_STATUS_CHARGING, 0);
        }
        break;
      case 1: // COLLECTING
        if (maxTime > 0 && stateTimer >= 0) {
          int percentage = (stateTimer * 100) / maxTime;
          statusText = Component.translatable(TRANSLATION_STATUS_COLLECTING, percentage);
        } else {
          statusText = Component.translatable(TRANSLATION_STATUS_COLLECTING, 0);
        }
        break;
      case 2: // RETURNING
        if (maxTime > 0 && stateTimer >= 0) {
          int percentage = (stateTimer * 100) / maxTime;
          statusText = Component.translatable(TRANSLATION_STATUS_RETURNING, percentage);
        } else {
          statusText = Component.translatable(TRANSLATION_STATUS_RETURNING, 0);
        }
        break;
      case 3: // PROCESSING
        if (maxTime > 0 && stateTimer >= 0) {
          int percentage = (stateTimer * 100) / maxTime;
          statusText = Component.translatable(TRANSLATION_STATUS_PROCESSING, percentage);
        } else {
          statusText = Component.translatable(TRANSLATION_STATUS_PROCESSING, 0);
        }
        break;
      case 4: // NO_STORAGE
        statusText = Component.translatable(TRANSLATION_STATUS_NO_STORAGE);
        break;
      case 5: // NO_POWER
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
    int relativeX = x - leftPos;
    int relativeY = y - topPos;

    // Energy bar tooltip
    if (relativeX >= CollectorStationMenu.ENERGY_BAR_X
        && relativeX <= CollectorStationMenu.ENERGY_BAR_X + ENERGY_BAR_WIDTH
        && relativeY >= CollectorStationMenu.ENERGY_BAR_Y
        && relativeY <= CollectorStationMenu.ENERGY_BAR_Y + ENERGY_BAR_HEIGHT) {

      int currentEnergy = menu.getCurrentEnergy();
      int maxEnergy = 5000;
      int percentage = maxEnergy > 0 ? (currentEnergy * 100 / maxEnergy) : 0;

      Component tooltip =
          Component.literal(
              String.format("Energy: %d / %d (%d%%)", currentEnergy, maxEnergy, percentage));
      guiGraphics.renderTooltip(this.font, tooltip, x, y);
    }

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

  private String getStatusName(int status) {
    return switch (status) {
      case 0 -> "Charging";
      case 1 -> "Collecting";
      case 2 -> "Returning";
      case 3 -> "Processing";
      case 4 -> "No Storage";
      case 5 -> "No Power";
      default -> "Unknown";
    };
  }
}
