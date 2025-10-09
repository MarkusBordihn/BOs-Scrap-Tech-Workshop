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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.client.screen.BaseContainerScreen;
import de.markusbordihn.scraptechworkshop.data.recycler.RecyclerStatus;
import de.markusbordihn.scraptechworkshop.menu.RecyclerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class RecyclerScreen extends BaseContainerScreen<RecyclerMenu> {

  private static final String TRANSLATION_KEY_PREFIX = Constants.GUI_PREFIX + "recycler.";
  private static final String TRANSLATION_PROGRESS = TRANSLATION_KEY_PREFIX + "progress";
  private static final String TRANSLATION_NO_RECIPE = TRANSLATION_KEY_PREFIX + "no_recipe";
  private static final String TRANSLATION_DONE = TRANSLATION_KEY_PREFIX + "done";
  private static final String TRANSLATION_IDLE = TRANSLATION_KEY_PREFIX + "idle";
  private static final String TRANSLATION_INPUT_SLOT = TRANSLATION_KEY_PREFIX + "input_slot";
  private static final String TRANSLATION_OUTPUT_SLOTS = TRANSLATION_KEY_PREFIX + "output_slots";
  private static final String TRANSLATION_UPGRADE_SLOTS = TRANSLATION_KEY_PREFIX + "upgrade_slots";

  private static final ResourceLocation CUSTOM_ELEMENTS_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/gui/recycler_elements.png");

  private static final int SCREEN_WIDTH = 176;
  private static final int SCREEN_HEIGHT = 205;
  private static final int INVENTORY_LABEL_OFFSET = 92;

  private static final int PROGRESS_ARROW_X = 80;
  private static final int PROGRESS_ARROW_Y = 35;
  private static final int PROGRESS_ARROW_HEIGHT = 16;
  private static final int PROGRESS_SCALE_FACTOR = 26;

  private static final int PROGRESS_AREA_X1 = 80;
  private static final int PROGRESS_AREA_X2 = 106;
  private static final int PROGRESS_AREA_Y1 = 35;
  private static final int PROGRESS_AREA_Y2 = 51;

  private static final int INPUT_SLOT_X = 26;
  private static final int INPUT_SLOT_Y = 35;

  private static final int OUTPUT_AREA_X = 116;
  private static final int OUTPUT_AREA_Y = 17;

  private static final int UPGRADE_SLOTS_X = 62;
  private static final int UPGRADE_SLOTS_Y = 71;

  private static final int BLOCK_RENDER_X = 53;
  private static final int BLOCK_RENDER_Y = 43;
  private static final float BLOCK_RENDER_SCALE = 12.0f;

  private BlockRenderDispatcher blockRenderer;
  private ItemRenderer itemRenderer;
  private float rotationAngle = 0;

  public RecyclerScreen(
      final RecyclerMenu menu, final Inventory playerInventory, final Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = SCREEN_WIDTH;
    this.imageHeight = SCREEN_HEIGHT;
    this.inventoryLabelY = this.imageHeight - INVENTORY_LABEL_OFFSET;
  }

  @Override
  protected void init() {
    super.init();
    this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    Minecraft minecraft = Minecraft.getInstance();
    this.blockRenderer = minecraft.getBlockRenderer();
    this.itemRenderer = minecraft.getItemRenderer();
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;

    renderDefaultBackground(guiGraphics, x, y, imageWidth, imageHeight);

    // Input slot
    renderSlot(guiGraphics, x + INPUT_SLOT_X, y + INPUT_SLOT_Y);

    // Output slots (3x3 grid)
    renderSlots(guiGraphics, x + OUTPUT_AREA_X, y + OUTPUT_AREA_Y, 3, 3);

    // Upgrade slots (2 horizontal)
    renderSlots(guiGraphics, x + UPGRADE_SLOTS_X, y + UPGRADE_SLOTS_Y, 2, 1);

    // Player inventory with custom positions
    renderPlayerInventoryAt(guiGraphics, x, y, 123, 181); // Moved down by 20 pixels

    // Render 3D block in center between input and progress arrow
    ItemStack inputItem = menu.getCurrentInput();
    if (!inputItem.isEmpty()) {
      render3DBlock(guiGraphics, x, y, inputItem, partialTick);
    }

    // Progress arrow frame (always visible)
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_FURNACE);
    guiGraphics.blit(
        Constants.TEXTURE_FURNACE,
        x + PROGRESS_ARROW_X,
        y + PROGRESS_ARROW_Y,
        176,
        14,
        26,
        PROGRESS_ARROW_HEIGHT);

    // Progress arrow fill (only when crafting)
    if (menu.isCrafting()) {
      guiGraphics.blit(
          Constants.TEXTURE_FURNACE,
          x + PROGRESS_ARROW_X,
          y + PROGRESS_ARROW_Y,
          176,
          14,
          menu.getScaledProgress(),
          PROGRESS_ARROW_HEIGHT);
    }
  }

  private void render3DBlock(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final ItemStack inputItem,
      final float partialTick) {
    PoseStack poseStack = guiGraphics.pose();
    poseStack.pushPose();

    // Position for 3D rendering - centered between input and arrow
    poseStack.translate(x + BLOCK_RENDER_X, y + BLOCK_RENDER_Y, 100);

    // Animate rotation
    rotationAngle += partialTick * 2;

    // Check if item is a block or regular item
    if (inputItem.getItem() instanceof BlockItem blockItem) {
      // Render as block
      BlockState blockState = blockItem.getBlock().defaultBlockState();

      poseStack.scale(BLOCK_RENDER_SCALE, -BLOCK_RENDER_SCALE, BLOCK_RENDER_SCALE);
      poseStack.mulPose(Axis.YP.rotationDegrees(rotationAngle));
      poseStack.mulPose(Axis.XP.rotationDegrees(30));

      MultiBufferSource.BufferSource bufferSource = guiGraphics.bufferSource();
      blockRenderer.renderSingleBlock(
          blockState,
          poseStack,
          bufferSource,
          15728880, // Light value
          OverlayTexture.NO_OVERLAY);
      bufferSource.endBatch();
    } else {
      // Render as item - scale up items to be more visible
      poseStack.scale(
          BLOCK_RENDER_SCALE * 1.5f, BLOCK_RENDER_SCALE * 1.5f, BLOCK_RENDER_SCALE * 1.5f);
      poseStack.mulPose(Axis.YP.rotationDegrees(rotationAngle));
      poseStack.mulPose(Axis.XP.rotationDegrees(30));

      itemRenderer.renderStatic(
          inputItem,
          ItemDisplayContext.GUI,
          15728880, // Light value
          OverlayTexture.NO_OVERLAY,
          poseStack,
          guiGraphics.bufferSource(),
          minecraft.level,
          0);
    }

    poseStack.popPose();
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

    // Progress arrow tooltip
    if (relativeX >= PROGRESS_AREA_X1
        && relativeX <= PROGRESS_AREA_X2
        && relativeY >= PROGRESS_AREA_Y1
        && relativeY <= PROGRESS_AREA_Y2) {
      RecyclerStatus status = menu.getRecyclerStatus();

      if (menu.isCrafting()) {
        guiGraphics.renderTooltip(
            this.font,
            Component.translatable(
                TRANSLATION_PROGRESS, menu.getScaledProgress() * 100 / PROGRESS_SCALE_FACTOR),
            x,
            y);
      } else if (status == RecyclerStatus.NO_RECIPE) {
        guiGraphics.renderTooltip(this.font, Component.translatable(TRANSLATION_NO_RECIPE), x, y);
      } else if (status == RecyclerStatus.DONE) {
        guiGraphics.renderTooltip(this.font, Component.translatable(TRANSLATION_DONE), x, y);
      } else {
        guiGraphics.renderTooltip(this.font, Component.translatable(TRANSLATION_IDLE), x, y);
      }
    }

    // Input slot tooltip
    if (!menu.isCrafting()
        && (relativeX >= INPUT_SLOT_X - 1
            && relativeX <= INPUT_SLOT_X + 17
            && relativeY >= INPUT_SLOT_Y - 1
            && relativeY <= INPUT_SLOT_Y + 17)) {
      guiGraphics.renderTooltip(this.font, Component.translatable(TRANSLATION_INPUT_SLOT), x, y);
    }

    // Output area tooltip
    if (relativeX >= OUTPUT_AREA_X
        && relativeX <= OUTPUT_AREA_X + 54
        && relativeY >= OUTPUT_AREA_Y
        && relativeY <= OUTPUT_AREA_Y + 54) {
      guiGraphics.renderTooltip(this.font, Component.translatable(TRANSLATION_OUTPUT_SLOTS), x, y);
    }

    // Upgrade slots tooltip
    if (relativeX >= UPGRADE_SLOTS_X
        && relativeX <= UPGRADE_SLOTS_X + 36
        && relativeY >= UPGRADE_SLOTS_Y
        && relativeY <= UPGRADE_SLOTS_Y + 18) {
      guiGraphics.renderTooltip(this.font, Component.translatable(TRANSLATION_UPGRADE_SLOTS), x, y);
    }
  }
}
