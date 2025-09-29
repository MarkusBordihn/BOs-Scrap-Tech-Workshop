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
import com.mojang.blaze3d.vertex.PoseStack;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.menu.ScrapMultitoolMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ScrapMultitoolScreen extends BaseContainerScreen<ScrapMultitoolMenu> {

  @SuppressWarnings("deprecation")
  private static final ResourceLocation CUSTOM_ELEMENTS_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/gui/scrap_multitool_elements.png");

  private static final String TRANSLATION_KEY_PREFIX = Constants.GUI_PREFIX + "scrap_multitool.";

  private static final String TRANSLATION_BATTERY_SLOT = TRANSLATION_KEY_PREFIX + "battery_slot";
  private static final String TRANSLATION_MODULE_SLOTS = TRANSLATION_KEY_PREFIX + "module_slots";

  private static final int SCREEN_WIDTH = 176;
  private static final int SCREEN_HEIGHT = 220;

  private static final int TOOL_RENDER_X = 10;
  private static final int TOOL_RENDER_Y = 30;
  private static final float TOOL_RENDER_SCALE = 4.0f;

  private static final int ENERGY_BAR_X = 152;
  private static final int ENERGY_BAR_Y = 20;
  private static final int ENERGY_BAR_WIDTH = 16;
  private static final int ENERGY_BAR_HEIGHT = 68;

  private static final int BATTERY_SLOT_X = 100;
  private static final int BATTERY_SLOT_Y = 20;

  private static final int MODULE_SLOTS_X1 = 64;
  private static final int MODULE_SLOTS_X2 = 136;
  private static final int MODULE_SLOTS_Y = 50;

  private ItemRenderer itemRenderer;

  public ScrapMultitoolScreen(ScrapMultitoolMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = SCREEN_WIDTH;
    this.imageHeight = SCREEN_HEIGHT;
  }

  @Override
  protected void init() {
    super.init();
    this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    this.titleLabelY = 6;
    this.inventoryLabelY = this.imageHeight - 94;
    this.itemRenderer = minecraft.getItemRenderer();
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    int x = this.leftPos;
    int y = this.topPos;

    renderDefaultBackground(guiGraphics, x, y, imageWidth, imageHeight);

    // Battery slot
    renderSlot(guiGraphics, x + BATTERY_SLOT_X, y + BATTERY_SLOT_Y);

    // Module slots (2 left, 2 right)
    for (int i = 0; i < 2; i++) {
      renderSlot(guiGraphics, x + MODULE_SLOTS_X1 + (i * 18), y + MODULE_SLOTS_Y);
      renderSlot(guiGraphics, x + MODULE_SLOTS_X2 - 36 + (i * 18), y + MODULE_SLOTS_Y);
    }

    // Player inventory moved down appropriately
    renderPlayerInventoryAt(guiGraphics, x, y, 137, 195);

    render3DMultitool(guiGraphics, x, y);

    renderEnergyBar(guiGraphics, x, y);
  }

  private void render3DMultitool(GuiGraphics guiGraphics, int x, int y) {
    ItemStack multitoolStack = menu.getMultitoolStack();
    if (!multitoolStack.isEmpty()) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();

      // Position and scale the 3D item
      poseStack.translate(x + TOOL_RENDER_X + 24, y + TOOL_RENDER_Y + 34, 100);
      poseStack.scale(TOOL_RENDER_SCALE * 16, TOOL_RENDER_SCALE * 16, TOOL_RENDER_SCALE * 16);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-45));
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-180));

      // Render the item with proper lighting - using combined light values for better visibility
      itemRenderer.renderStatic(
          multitoolStack,
          ItemDisplayContext.FIXED,
          LightTexture.FULL_BRIGHT,
          OverlayTexture.NO_OVERLAY,
          poseStack,
          guiGraphics.bufferSource(),
          minecraft.level,
          0);

      poseStack.popPose();
    }
  }

  private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
    RenderSystem.setShaderTexture(0, CUSTOM_ELEMENTS_TEXTURE);

    int energy = menu.getCurrentEnergyFromBattery();
    int maxEnergy = menu.getMaxEnergyFromBattery();

    if (maxEnergy > 0) {
      guiGraphics.blit(
          CUSTOM_ELEMENTS_TEXTURE,
          x + ENERGY_BAR_X,
          y + ENERGY_BAR_Y,
          0,
          0,
          ENERGY_BAR_WIDTH,
          ENERGY_BAR_HEIGHT);

      int energyBarHeight = (int) ((float) energy / maxEnergy * ENERGY_BAR_HEIGHT);
      guiGraphics.blit(
          CUSTOM_ELEMENTS_TEXTURE,
          x + ENERGY_BAR_X,
          y + ENERGY_BAR_Y + (ENERGY_BAR_HEIGHT - energyBarHeight),
          16,
          ENERGY_BAR_HEIGHT - energyBarHeight,
          ENERGY_BAR_WIDTH,
          energyBarHeight);
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    this.renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    super.renderTooltip(guiGraphics, x, y);

    int relativeX = x - leftPos;
    int relativeY = y - topPos;

    if (relativeX >= ENERGY_BAR_X
        && relativeX <= ENERGY_BAR_X + ENERGY_BAR_WIDTH
        && relativeY >= ENERGY_BAR_Y
        && relativeY <= ENERGY_BAR_Y + ENERGY_BAR_HEIGHT) {

      int currentEnergy = menu.getCurrentEnergyFromBattery();
      int maxEnergy = menu.getMaxEnergyFromBattery();
      int percentage = menu.getBatteryPercentageFromSlot();

      Component tooltip =
          Component.translatable(
              Constants.GUI_PREFIX + "scrap_multitool.energy_tooltip",
              currentEnergy,
              maxEnergy,
              percentage);
      guiGraphics.renderTooltip(this.font, tooltip, x, y);
    }
    if (relativeX >= BATTERY_SLOT_X - 1
        && relativeX <= BATTERY_SLOT_X + 17
        && relativeY >= BATTERY_SLOT_Y - 1
        && relativeY <= BATTERY_SLOT_Y + 17) {
      if (menu.getSlot(0).getItem().isEmpty()) {
        Component tooltip = Component.translatable(TRANSLATION_BATTERY_SLOT);
        guiGraphics.renderTooltip(this.font, tooltip, x, y);
      }
    }

    if (relativeX >= MODULE_SLOTS_X1
        && relativeX <= MODULE_SLOTS_X2
        && relativeY >= MODULE_SLOTS_Y - 1
        && relativeY <= MODULE_SLOTS_Y + 17) {
      boolean allModuleSlotsEmpty = true;
      for (int i = 1; i <= 4; i++) {
        if (!menu.getSlot(i).getItem().isEmpty()) {
          allModuleSlotsEmpty = false;
          break;
        }
      }

      if (allModuleSlotsEmpty) {
        Component tooltip = Component.translatable(TRANSLATION_MODULE_SLOTS);
        guiGraphics.renderTooltip(this.font, tooltip, x, y);
      }
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

    // Energy percentage text below the energy bar
    String energyText = menu.getBatteryPercentageFromSlot() + "%";
    int textWidth = this.font.width(energyText);
    guiGraphics.drawString(
        this.font,
        energyText,
        ENERGY_BAR_X + (ENERGY_BAR_WIDTH - textWidth) / 2,
        ENERGY_BAR_Y + ENERGY_BAR_HEIGHT + 2,
        0x404040,
        false);
  }
}
