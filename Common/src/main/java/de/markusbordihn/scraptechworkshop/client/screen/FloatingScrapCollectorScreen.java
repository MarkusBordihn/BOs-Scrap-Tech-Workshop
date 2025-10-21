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
import de.markusbordihn.scraptechworkshop.menu.FloatingScrapCollectorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FloatingScrapCollectorScreen extends BaseContainerScreen<FloatingScrapCollectorMenu> {

  private static final String TRANSLATION_KEY_PREFIX =
      Constants.GUI_PREFIX + "floating_scrap_collector.";
  private static final String TRANSLATION_FILTER_SLOT = TRANSLATION_KEY_PREFIX + "filter_slot";
  private static final String TRANSLATION_DURABILITY = TRANSLATION_KEY_PREFIX + "durability";

  private static final int SCREEN_WIDTH = 176;
  private static final int SCREEN_HEIGHT = 166;

  public FloatingScrapCollectorScreen(
      FloatingScrapCollectorMenu menu, Inventory playerInventory, Component title) {
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

    renderSlot(
        guiGraphics,
        x + FloatingScrapCollectorMenu.NET_SLOT_X,
        y + FloatingScrapCollectorMenu.NET_SLOT_Y);

    renderSlots(
        guiGraphics,
        x + FloatingScrapCollectorMenu.OUTPUT_GRID_START_X,
        y + FloatingScrapCollectorMenu.OUTPUT_GRID_START_Y,
        FloatingScrapCollectorMenu.OUTPUT_GRID_COLUMNS,
        FloatingScrapCollectorMenu.OUTPUT_GRID_ROWS);

    renderPlayerInventoryAt(
        guiGraphics,
        x,
        y,
        FloatingScrapCollectorMenu.PLAYER_INVENTORY_START_Y,
        FloatingScrapCollectorMenu.PLAYER_INVENTORY_START_Y + 58);

    renderProgressBar(guiGraphics, x, y);
    renderDurabilityBar(guiGraphics, x, y);
  }

  private void renderProgressBar(GuiGraphics guiGraphics, int x, int y) {
    int progressHeight = menu.getScaledProgress();
    int barX = x + FloatingScrapCollectorMenu.PROGRESS_BAR_X;
    int barY = y + FloatingScrapCollectorMenu.PROGRESS_BAR_Y;
    int barWidth = FloatingScrapCollectorMenu.PROGRESS_BAR_WIDTH;
    int barHeight = FloatingScrapCollectorMenu.PROGRESS_BAR_HEIGHT;

    guiGraphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF222222);
    guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF444444);
    if (progressHeight > 0) {
      int fillY = barY + barHeight - progressHeight;
      guiGraphics.fill(barX, fillY, barX + barWidth, barY + barHeight, 0xFF0066CC);
      if (progressHeight > 2) {
        guiGraphics.fill(barX + 1, fillY, barX + barWidth - 1, fillY + 2, 0xFF3399FF);
      }
    }
  }

  private void renderDurabilityBar(GuiGraphics guiGraphics, int x, int y) {
    float durabilityPercent = menu.getNetDurabilityPercent();
    if (durabilityPercent > 0) {
      int barWidth = (int) (FloatingScrapCollectorMenu.DURABILITY_BAR_WIDTH * durabilityPercent);
      renderEnergyBar(
          guiGraphics,
          x + FloatingScrapCollectorMenu.DURABILITY_BAR_X,
          y + FloatingScrapCollectorMenu.DURABILITY_BAR_Y,
          FloatingScrapCollectorMenu.DURABILITY_BAR_WIDTH,
          FloatingScrapCollectorMenu.DURABILITY_BAR_HEIGHT,
          barWidth,
          FloatingScrapCollectorMenu.DURABILITY_BAR_WIDTH);
    }
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    super.renderTooltip(guiGraphics, mouseX, mouseY);
    if (isHovering(
            FloatingScrapCollectorMenu.NET_SLOT_X,
            FloatingScrapCollectorMenu.NET_SLOT_Y,
            16,
            16,
            mouseX,
            mouseY)
        && this.menu.getSlot(0).getItem().isEmpty()) {
      guiGraphics.renderTooltip(
          this.font, Component.translatable(TRANSLATION_FILTER_SLOT), mouseX, mouseY);
    }

    if (isHovering(
        FloatingScrapCollectorMenu.DURABILITY_BAR_X,
        FloatingScrapCollectorMenu.DURABILITY_BAR_Y,
        FloatingScrapCollectorMenu.DURABILITY_BAR_WIDTH,
        FloatingScrapCollectorMenu.DURABILITY_BAR_HEIGHT,
        mouseX,
        mouseY)) {
      float percent = menu.getNetDurabilityPercent();
      int percentInt = (int) (percent * 100);
      guiGraphics.renderTooltip(
          this.font,
          Component.translatable(TRANSLATION_DURABILITY, percentInt + "%"),
          mouseX,
          mouseY);
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    renderTooltip(guiGraphics, mouseX, mouseY);
  }
}
