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
import de.markusbordihn.scraptechworkshop.data.recycler.RecyclerStatus;
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

  private static final int SCREEN_WIDTH = 176;
  private static final int SCREEN_HEIGHT = 185;
  private static final int INVENTORY_LABEL_OFFSET = 94;

  private static final int PROGRESS_ARROW_X = 80;
  private static final int PROGRESS_ARROW_Y = 35;
  private static final int PROGRESS_TEXTURE_X = 176;
  private static final int PROGRESS_TEXTURE_Y = 14;
  private static final int PROGRESS_ARROW_HEIGHT = 16;
  private static final int PROGRESS_SCALE_FACTOR = 26;

  private static final int PROGRESS_AREA_X1 = 80;
  private static final int PROGRESS_AREA_X2 = 106;
  private static final int PROGRESS_AREA_Y1 = 35;
  private static final int PROGRESS_AREA_Y2 = 51;

  private static final int INPUT_SLOT_X1 = 26;
  private static final int INPUT_SLOT_X2 = 42;
  private static final int INPUT_SLOT_Y1 = 35;
  private static final int INPUT_SLOT_Y2 = 51;

  private static final int OUTPUT_AREA_X1 = 116;
  private static final int OUTPUT_AREA_X2 = 170;
  private static final int OUTPUT_AREA_Y1 = 17;
  private static final int OUTPUT_AREA_Y2 = 71;

  private static final int UPGRADE_SLOTS_X1 = 62;
  private static final int UPGRADE_SLOTS_X2 = 98;
  private static final int UPGRADE_SLOTS_Y1 = 71;
  private static final int UPGRADE_SLOTS_Y2 = 87;

  public RecyclerScreen(RecyclerMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = SCREEN_WIDTH;
    this.imageHeight = SCREEN_HEIGHT;
    this.inventoryLabelY = this.imageHeight - INVENTORY_LABEL_OFFSET;
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

    guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

    if (menu.isCrafting()) {
      int progress = menu.getScaledProgress();
      guiGraphics.blit(
          TEXTURE,
          x + PROGRESS_ARROW_X,
          y + PROGRESS_ARROW_Y,
          PROGRESS_TEXTURE_X,
          PROGRESS_TEXTURE_Y,
          progress,
          PROGRESS_ARROW_HEIGHT);
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

    int relativeX = x - leftPos;
    int relativeY = y - topPos;

    if (relativeX >= PROGRESS_AREA_X1
        && relativeX <= PROGRESS_AREA_X2
        && relativeY >= PROGRESS_AREA_Y1
        && relativeY <= PROGRESS_AREA_Y2) {
      RecyclerStatus status = menu.getRecyclerStatus();

      if (menu.isCrafting()) {
        Component tooltip =
            Component.translatable(
                "gui.scrap_tech_workshop.recycler.progress",
                menu.getScaledProgress() * 100 / PROGRESS_SCALE_FACTOR);
        guiGraphics.renderTooltip(this.font, tooltip, x, y);
      } else if (status == RecyclerStatus.NO_RECIPE) {
        Component tooltip = Component.translatable("gui.scrap_tech_workshop.recycler.no_recipe");
        guiGraphics.renderTooltip(this.font, tooltip, x, y);
      } else if (status == RecyclerStatus.DONE) {
        Component tooltip = Component.translatable("gui.scrap_tech_workshop.recycler.done");
        guiGraphics.renderTooltip(this.font, tooltip, x, y);
      } else {
        Component tooltip = Component.translatable("gui.scrap_tech_workshop.recycler.idle");
        guiGraphics.renderTooltip(this.font, tooltip, x, y);
      }
    }

    if (!menu.isCrafting()
        & (relativeX >= INPUT_SLOT_X1
            && relativeX <= INPUT_SLOT_X2
            && relativeY >= INPUT_SLOT_Y1
            && relativeY <= INPUT_SLOT_Y2)) {
      Component tooltip = Component.translatable("gui.scrap_tech_workshop.recycler.input_slot");
      guiGraphics.renderTooltip(this.font, tooltip, x, y);
    }

    if (relativeX >= OUTPUT_AREA_X1
        && relativeX <= OUTPUT_AREA_X2
        && relativeY >= OUTPUT_AREA_Y1
        && relativeY <= OUTPUT_AREA_Y2) {
      Component tooltip = Component.translatable("gui.scrap_tech_workshop.recycler.output_slots");
      guiGraphics.renderTooltip(this.font, tooltip, x, y);
    }

    if (relativeX >= UPGRADE_SLOTS_X1
        && relativeX <= UPGRADE_SLOTS_X2
        && relativeY >= UPGRADE_SLOTS_Y1
        && relativeY <= UPGRADE_SLOTS_Y2) {
      Component tooltip = Component.translatable("gui.scrap_tech_workshop.recycler.upgrade_slots");
      guiGraphics.renderTooltip(this.font, tooltip, x, y);
    }
  }
}
