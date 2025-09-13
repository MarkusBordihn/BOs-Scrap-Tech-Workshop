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

package de.markusbordihn.scraptechworkshop.client.screen.recycler;

import com.mojang.blaze3d.systems.RenderSystem;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.menu.RecyclerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RecyclerScreen extends AbstractContainerScreen<RecyclerMenu> {

  private static final ResourceLocation TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/gui/recycler.png");

  public RecyclerScreen(RecyclerMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = 176;
    this.imageHeight = 185;
    this.inventoryLabelY = this.imageHeight - 94;
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
    RenderSystem.setShaderTexture(0, TEXTURE);

    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;

    // Draw main GUI background
    guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

    // Draw progress arrow if crafting
    if (menu.isCrafting()) {
      int progress = menu.getScaledProgress();
      guiGraphics.blit(TEXTURE, x + 80, y + 35, 176, 14, progress, 16);
    }
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

    // Add custom tooltips for progress area
    int relativeX = x - leftPos;
    int relativeY = y - topPos;

    // Progress arrow tooltip
    if (relativeX >= 80 && relativeX <= 106 && relativeY >= 35 && relativeY <= 51) {
      if (menu.isCrafting()) {
        Component tooltip =
            Component.translatable(
                "gui.scrap_tech_workshop.recycler.progress", menu.getScaledProgress() * 100 / 26);
        guiGraphics.renderTooltip(this.font, tooltip, x, y);
      } else {
        Component tooltip = Component.translatable("gui.scrap_tech_workshop.recycler.idle");
        guiGraphics.renderTooltip(this.font, tooltip, x, y);
      }
    }

    // Input slot tooltip
    if (!menu.isCrafting()
        & (relativeX >= 26 && relativeX <= 42 && relativeY >= 35 && relativeY <= 51)) {
      Component tooltip = Component.translatable("gui.scrap_tech_workshop.recycler.input_slot");
      guiGraphics.renderTooltip(this.font, tooltip, x, y);
    }

    // Output area tooltip
    if (relativeX >= 116 && relativeX <= 170 && relativeY >= 17 && relativeY <= 71) {
      Component tooltip = Component.translatable("gui.scrap_tech_workshop.recycler.output_slots");
      guiGraphics.renderTooltip(this.font, tooltip, x, y);
    }

    // Upgrade slots tooltip
    if (relativeX >= 62 && relativeX <= 98 && relativeY >= 71 && relativeY <= 87) {
      Component tooltip = Component.translatable("gui.scrap_tech_workshop.recycler.upgrade_slots");
      guiGraphics.renderTooltip(this.font, tooltip, x, y);
    }
  }
}
