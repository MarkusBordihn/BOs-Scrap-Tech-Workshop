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
import de.markusbordihn.scraptechworkshop.client.screen.energy.EnergyPowerRenderer;
import de.markusbordihn.scraptechworkshop.menu.ScrapMultitoolMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ScrapMultitoolScreen extends BaseContainerScreen<ScrapMultitoolMenu> {

  private static final String TRANSLATION_KEY_PREFIX = Constants.GUI_PREFIX + "scrap_multitool.";

  private static final String TRANSLATION_BATTERY_SLOT = TRANSLATION_KEY_PREFIX + "battery_slot";
  private static final String TRANSLATION_MODULE_SLOTS = TRANSLATION_KEY_PREFIX + "module_slots";

  private static final int SCREEN_WIDTH = 176;
  private static final int SCREEN_HEIGHT = 220;

  private static final int TOOL_RENDER_X = 25;
  private static final int TOOL_RENDER_Y = 75;
  private static final float TOOL_RENDER_SCALE = 4.0f;

  private static final int ENERGY_BAR_X = 150;
  private static final int ENERGY_BAR_Y = 25;
  private static final int ENERGY_BAR_WIDTH = 16;
  private static final int ENERGY_BAR_HEIGHT = 80;

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
    Minecraft minecraft = Minecraft.getInstance();
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
    renderSlot(
        guiGraphics, x + ScrapMultitoolMenu.BATTERY_SLOT_X, y + ScrapMultitoolMenu.BATTERY_SLOT_Y);

    // Module slots
    for (int i = 0; i < 4; i++) {
      renderSlot(
          guiGraphics,
          x + ScrapMultitoolMenu.MODULE_SLOTS_X + (i * 18),
          y + ScrapMultitoolMenu.MODULE_SLOTS_Y);
    }

    // Player inventory
    renderPlayerInventoryAt(guiGraphics, x, y, ScrapMultitoolMenu.PLAYER_INVENTORY_START_Y);

    render3DMultitool(guiGraphics, x, y);

    renderEnergyBar(guiGraphics, x, y);
  }

  private void render3DMultitool(GuiGraphics guiGraphics, int x, int y) {
    ItemStack multitoolStack = menu.getMultitoolStack();
    if (!multitoolStack.isEmpty()) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();

      poseStack.translate(x + TOOL_RENDER_X, y + TOOL_RENDER_Y, 100);
      poseStack.scale(TOOL_RENDER_SCALE * 16, TOOL_RENDER_SCALE * 16, TOOL_RENDER_SCALE * 16);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(60));
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-180));

      Minecraft minecraft = Minecraft.getInstance();
      itemRenderer.renderStatic(
          multitoolStack,
          ItemDisplayContext.NONE,
          LightTexture.FULL_BRIGHT,
          OverlayTexture.NO_OVERLAY,
          poseStack,
          guiGraphics.bufferSource(),
          minecraft.level,
          0);
      guiGraphics.flush();

      poseStack.popPose();
    }
  }

  private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
    int energy = menu.getCurrentEnergyFromBattery();
    int maxEnergy = menu.getMaxEnergyFromBattery();
    EnergyPowerRenderer.renderEnergyBarWithFrame(
        guiGraphics,
        x + ENERGY_BAR_X,
        y + ENERGY_BAR_Y,
        ENERGY_BAR_WIDTH,
        ENERGY_BAR_HEIGHT,
        energy,
        maxEnergy);
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
    if (relativeX >= ScrapMultitoolMenu.BATTERY_SLOT_X - 1
        && relativeX <= ScrapMultitoolMenu.BATTERY_SLOT_X + 17
        && relativeY >= ScrapMultitoolMenu.BATTERY_SLOT_Y - 1
        && relativeY <= ScrapMultitoolMenu.BATTERY_SLOT_Y + 17) {
      if (menu.getSlot(0).getItem().isEmpty()) {
        Component tooltip = Component.translatable(TRANSLATION_BATTERY_SLOT);
        guiGraphics.renderTooltip(this.font, tooltip, x, y);
      }
    }

    if (relativeX >= ScrapMultitoolMenu.MODULE_SLOTS_X
        && relativeX <= ScrapMultitoolMenu.MODULE_SLOTS_X + 73
        && relativeY >= ScrapMultitoolMenu.MODULE_SLOTS_Y - 1
        && relativeY <= ScrapMultitoolMenu.MODULE_SLOTS_Y + 17) {
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
