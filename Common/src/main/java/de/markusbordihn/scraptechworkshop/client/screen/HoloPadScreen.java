/*
 * Copyright 2024 Markus Bordihn
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
import com.mojang.math.Axis;
import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.client.renderer.hololog.HoloLogScreenPlayer;
import de.markusbordihn.scraptechworkshop.data.hololog.DisplayType;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogData;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogDisplayEntity;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogDisplayRecipe;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogLine;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogManager;
import de.markusbordihn.scraptechworkshop.data.hololog.UIContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloPadScreen extends Screen {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final ResourceLocation HOLOPAD_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/gui/holo_pad.png");

  private static final int TEXTURE_WIDTH = 512;
  private static final int TEXTURE_HEIGHT = 256;
  private static final int SCREEN_WIDTH = 370;
  private static final int SCREEN_HEIGHT = 250;

  private static final int HOLOGRAM_AREA_X = 16;
  private static final int HOLOGRAM_AREA_Y = 16;
  private static final int HOLOGRAM_AREA_WIDTH = 338;
  private static final int HOLOGRAM_AREA_HEIGHT = 100;
  private static final int HOLOGRAM_AREA_CENTER_Y_OFFSET = 0;

  private static final int HOLOLOG_TITLE_AREA_X = 24;
  private static final int HOLOLOG_TITLE_AREA_Y = 94;

  private static final int TEXT_AREA_X = 12;
  private static final int TEXT_AREA_Y = 118;
  private static final int TEXT_AREA_WIDTH = 338;
  private static final int TEXT_AREA_HEIGHT = 120;
  private static final int TEXT_PADDING = 4;
  private static final int TITLE_SPACING = 2;
  private static final int SUBTITLE_SPACING = 4;
  private static final int LINE_SPACING = 2;
  private static final int SCROLL_BUTTON_WIDTH = 12;
  private static final int SCROLL_BUTTON_HEIGHT = 17;
  private static final int SCROLL_BUTTON_X_OFFSET = 338;

  private static final float HOLOGRAM_SCALE_FACTOR = 35.0f;
  private static final float HOLOGRAM_ITEM_SCALE_MULTIPLIER = 2.5f;
  private static final float HOLOGRAM_BLOCK_SCALE_MULTIPLIER = 2.0f;
  private static final float HOLOGRAM_ENTITY_SCALE_MULTIPLIER = 1.95f;
  private static final int HOLOGRAM_ENTITY_Y_OFFSET = 20;
  private static final float HOLOGRAM_Z_OFFSET = 100.0f;
  private static final int ROTATION_X = 180;
  private static final int MAX_LIGHT_LEVEL = 15728880;

  private static final int COLOR_TITLE_TEXT = 0x00FFFF;
  private static final int COLOR_SUBTITLE_TEXT = 0xAAAAAA;
  private static final int COLOR_LINE_TEXT = 0xFFFFFF;

  private final ResourceLocation holoLogId;
  private final List<String> wrappedTextLines = new ArrayList<>();
  private HoloLogData holoLogData;
  private HoloLogScreenPlayer player;
  private int scrollOffset;
  private int leftPos;
  private int topPos;
  private Button replayButton;
  private Button closeButton;
  private boolean playbackCompleted = false;

  public HoloPadScreen(final ResourceLocation holoLogId) {
    super(Component.translatable(Constants.ITEM_PREFIX + "holo_pad"));
    this.holoLogId = holoLogId;
  }

  @Override
  protected void init() {
    super.init();
    this.leftPos = (this.width - SCREEN_WIDTH) / 2;
    this.topPos = (this.height - SCREEN_HEIGHT) / 2;

    addScrollButtons();
    addReplayButton();
    addCloseButton();

    if (player != null) {
      return;
    }

    if (holoLogId == null) {
      log.warn("{} No holo log ID provided", Constants.LOG_NAME);
      return;
    }

    log.info("{} Opening holo pad screen: {}", Constants.LOG_NAME, holoLogId);
    HoloLogManager.loadHoloLog(holoLogId)
        .ifPresentOrElse(
            data -> {
              this.holoLogData = data;
              prepareTextLines();
              startHoloLogPlayback(data);
            },
            () -> log.error("{} Failed to load hololog: {}", Constants.LOG_NAME, holoLogId));
  }

  private void addScrollButtons() {
    int buttonX = leftPos + TEXT_AREA_X + SCROLL_BUTTON_X_OFFSET;
    int scrollUpY = topPos + TEXT_AREA_Y + 2;
    int scrollDownY = topPos + TEXT_AREA_Y + TEXT_AREA_HEIGHT - SCROLL_BUTTON_HEIGHT - 2;

    this.addRenderableWidget(
        Button.builder(Component.literal("▲"), button -> scrollUp())
            .bounds(buttonX, scrollUpY, SCROLL_BUTTON_WIDTH, SCROLL_BUTTON_HEIGHT)
            .build());

    this.addRenderableWidget(
        Button.builder(Component.literal("▼"), button -> scrollDown())
            .bounds(buttonX, scrollDownY, SCROLL_BUTTON_WIDTH, SCROLL_BUTTON_HEIGHT)
            .build());
  }

  private void addReplayButton() {
    int buttonWidth = 80;
    int buttonHeight = 20;
    int buttonX = leftPos + HOLOGRAM_AREA_X + (HOLOGRAM_AREA_WIDTH - buttonWidth) / 2;
    int buttonY = topPos + HOLOGRAM_AREA_Y + (HOLOGRAM_AREA_HEIGHT - buttonHeight) / 2 - 15;
    replayButton =
        Button.builder(
                Component.translatable("gui.scrap_tech_workshop.holo_pad.replay"),
                button -> replayHololog())
            .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
            .build();
    replayButton.visible = false;
    this.addRenderableWidget(replayButton);
  }

  private void addCloseButton() {
    int buttonWidth = 80;
    int buttonHeight = 20;
    int buttonX = leftPos + HOLOGRAM_AREA_X + (HOLOGRAM_AREA_WIDTH - buttonWidth) / 2;
    int buttonY = topPos + HOLOGRAM_AREA_Y + (HOLOGRAM_AREA_HEIGHT - buttonHeight) / 2 + 10;
    closeButton =
        Button.builder(
                Component.translatable("gui.scrap_tech_workshop.holo_pad.close"),
                button -> this.onClose())
            .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
            .build();
    closeButton.visible = false;
    this.addRenderableWidget(closeButton);
  }

  private void scrollUp() {
    scrollOffset = Math.max(0, scrollOffset - 10);
  }

  private void scrollDown() {
    int maxScroll = Math.max(0, calculateTotalTextHeight() - (TEXT_AREA_HEIGHT - TEXT_PADDING * 2));
    scrollOffset = Math.min(scrollOffset + 10, maxScroll);
  }

  private void startHoloLogPlayback(final HoloLogData data) {
    if (minecraft == null || minecraft.level == null) {
      log.error("{} Cannot start playback: minecraft or level is null", Constants.LOG_NAME);
      return;
    }

    // Create player instance with completion callback
    player =
        new HoloLogScreenPlayer(
            data,
            new UIContext(minecraft.level, Vec3.ZERO),
            UUID.randomUUID(),
            this::onPlaybackComplete);
    player.start();
    playbackCompleted = false;

    // Hide replay and close buttons if visible
    if (replayButton != null) {
      replayButton.visible = false;
    }
    if (closeButton != null) {
      closeButton.visible = false;
    }

    log.info("{} Started hololog playback for: {}", Constants.LOG_NAME, holoLogId);
  }

  private void onPlaybackComplete() {
    playbackCompleted = true;
    log.info("{} Hololog playback completed: {}", Constants.LOG_NAME, holoLogId);

    // Show replay and close buttons
    if (replayButton != null) {
      replayButton.visible = true;
    }
    if (closeButton != null) {
      closeButton.visible = true;
    }
  }

  private void replayHololog() {
    log.info("{} Replaying hololog: {}", Constants.LOG_NAME, holoLogId);
    scrollOffset = 0; // Reset scroll
    if (holoLogData != null) {
      startHoloLogPlayback(holoLogData);
    }
  }

  private void prepareTextLines() {
    wrappedTextLines.clear();
    if (holoLogData == null || holoLogData.lines().isEmpty()) {
      return;
    }
    holoLogData.lines().stream()
        .map(HoloLogLine::text)
        .filter(text -> text != null && !text.isEmpty())
        .forEach(wrappedTextLines::add);
  }

  @Override
  public void tick() {
    super.tick();

    if (player != null && player.isPlaying()) {
      player.tick();
      autoScrollText();
    }
  }

  private void autoScrollText() {
    if (player == null) {
      return;
    }

    int currentContentHeight = 0;

    if (holoLogData != null) {
      if (holoLogData.title() != null) {
        currentContentHeight += font.lineHeight + TITLE_SPACING;
      }
      if (holoLogData.subtitle() != null && !holoLogData.subtitle().isEmpty()) {
        currentContentHeight += font.lineHeight + SUBTITLE_SPACING;
      }
    }

    int displayedLines = player.getDisplayedLines().size();
    currentContentHeight += displayedLines * (font.lineHeight + LINE_SPACING);

    String partialLine = player.getCurrentPartialLine();
    if (partialLine != null && !partialLine.isEmpty()) {
      currentContentHeight += font.lineHeight + LINE_SPACING;
    }

    int visibleHeight = TEXT_AREA_HEIGHT - TEXT_PADDING * 2;
    if (currentContentHeight > visibleHeight) {
      int maxScroll = Math.max(0, calculateTotalTextHeight() - visibleHeight);
      int neededScroll = currentContentHeight - visibleHeight;
      int targetScroll = Math.min(neededScroll, maxScroll);
      if (scrollOffset < targetScroll) {
        scrollOffset += 1;
        scrollOffset = Math.min(scrollOffset, targetScroll);
      }
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    renderBackground(guiGraphics);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    renderDefaultBackground(guiGraphics, leftPos, topPos);

    // Only render hologram if playback is active
    if (!playbackCompleted) {
      renderHologram(guiGraphics, leftPos, topPos, partialTick);
    }

    renderHolologTitleArea(guiGraphics, leftPos, topPos);
    renderText(guiGraphics, leftPos, topPos);

    super.render(guiGraphics, mouseX, mouseY, partialTick);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (delta > 0) {
      scrollUp();
    } else if (delta < 0) {
      scrollDown();
    }
    return true;
  }

  @Override
  public void onClose() {
    if (player != null) {
      player.stop();
      player = null;
    }
    super.onClose();
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }

  private void renderDefaultBackground(final GuiGraphics guiGraphics, final int x, final int y) {
    guiGraphics.blit(
        HOLOPAD_TEXTURE, x, y, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
  }

  private void renderHologram(
      final GuiGraphics guiGraphics, final int x, final int y, final float partialTick) {
    if (player == null) {
      return;
    }

    // Check if we should render a recipe instead
    HoloLogDisplayRecipe displayRecipe = player.getCurrentDisplayRecipe();
    if (displayRecipe != null) {
      renderRecipe(guiGraphics, x, y, displayRecipe);
      return;
    }

    HoloLogDisplayEntity displayEntity = player.getCurrentDisplayEntity();
    if (displayEntity == null) {
      return;
    }

    PoseStack poseStack = guiGraphics.pose();
    poseStack.pushPose();

    int centerX = x + HOLOGRAM_AREA_X + HOLOGRAM_AREA_WIDTH / 2;
    int centerY = y + HOLOGRAM_AREA_Y + HOLOGRAM_AREA_HEIGHT / 2 + HOLOGRAM_AREA_CENTER_Y_OFFSET;

    if (displayEntity.type() == DisplayType.ITEM) {
      centerY -= 10; // Move items up a bit more
    } else if (displayEntity.type() == DisplayType.ENTITY
        || displayEntity.type() == DisplayType.HOLO_ENTITY) {
      centerY += HOLOGRAM_ENTITY_Y_OFFSET;
    }

    poseStack.translate(centerX, centerY, HOLOGRAM_Z_OFFSET);

    float scale = displayEntity.scale() * HOLOGRAM_SCALE_FACTOR;
    if (displayEntity.type() == DisplayType.ITEM) {
      scale *= HOLOGRAM_ITEM_SCALE_MULTIPLIER;
    } else if (displayEntity.type() == DisplayType.BLOCK) {
      scale *= HOLOGRAM_BLOCK_SCALE_MULTIPLIER;
    } else if (displayEntity.type() == DisplayType.ENTITY
        || displayEntity.type() == DisplayType.HOLO_ENTITY) {
      scale *= HOLOGRAM_ENTITY_SCALE_MULTIPLIER;
    }

    poseStack.scale(scale, scale, scale);

    if (displayEntity.type() == DisplayType.ENTITY
        || displayEntity.type() == DisplayType.HOLO_ENTITY) {
      poseStack.mulPose(Axis.XP.rotationDegrees(ROTATION_X));
      poseStack.mulPose(Axis.YP.rotationDegrees(180));

      if (displayEntity.rotationX() != 0.0f) {
        poseStack.mulPose(Axis.XP.rotationDegrees(displayEntity.rotationX()));
      }
      if (displayEntity.rotationY() != 0.0f) {
        poseStack.mulPose(Axis.YP.rotationDegrees(displayEntity.rotationY()));
      }
      if (displayEntity.rotationZ() != 0.0f) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(displayEntity.rotationZ()));
      }
    } else if (displayEntity.type() == DisplayType.ITEM) {
      poseStack.mulPose(Axis.XP.rotationDegrees(ROTATION_X));
      poseStack.mulPose(Axis.YP.rotationDegrees(0));

      if (displayEntity.rotationSpeed() > 0 && minecraft != null && minecraft.level != null) {
        float time = minecraft.level.getGameTime();
        float rotationSpeed = displayEntity.rotationSpeed();
        poseStack.mulPose(Axis.YP.rotationDegrees(time * rotationSpeed));
      }

      if (displayEntity.rotationX() != 0.0f) {
        poseStack.mulPose(Axis.XP.rotationDegrees(displayEntity.rotationX()));
      }
      if (displayEntity.rotationY() != 0.0f) {
        poseStack.mulPose(Axis.YP.rotationDegrees(displayEntity.rotationY()));
      }
      if (displayEntity.rotationZ() != 0.0f) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(displayEntity.rotationZ()));
      }
    } else {
      poseStack.mulPose(Axis.XP.rotationDegrees(ROTATION_X));

      if (displayEntity.rotationSpeed() > 0 && minecraft != null && minecraft.level != null) {
        float time = minecraft.level.getGameTime();
        float rotationSpeed = displayEntity.rotationSpeed();
        poseStack.mulPose(Axis.YP.rotationDegrees(time * rotationSpeed));
      }

      if (displayEntity.rotationX() != 0.0f) {
        poseStack.mulPose(Axis.XP.rotationDegrees(displayEntity.rotationX()));
      }
      if (displayEntity.rotationY() != 0.0f) {
        poseStack.mulPose(Axis.YP.rotationDegrees(displayEntity.rotationY()));
      }
      if (displayEntity.rotationZ() != 0.0f) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(displayEntity.rotationZ()));
      }

      if (displayEntity.type() == DisplayType.BLOCK) {
        poseStack.translate(-0.5, -0.5, -0.5);
      }
    }

    player.renderDisplayEntity(poseStack, partialTick, MAX_LIGHT_LEVEL);

    poseStack.popPose();
  }

  private void renderHolologTitleArea(final GuiGraphics guiGraphics, final int x, final int y) {
    if (player == null && holoLogData == null) {
      return;
    }

    int titleX = x + HOLOLOG_TITLE_AREA_X + TEXT_PADDING;
    int titleY = y + HOLOLOG_TITLE_AREA_Y + 2;

    if (player != null) {
      String title = player.getTitle();
      if (title != null && !title.isEmpty()) {
        guiGraphics.drawString(font, title, titleX, titleY, COLOR_TITLE_TEXT, false);
        titleY += font.lineHeight + TITLE_SPACING;
      }

      String subtitle = player.getSubtitle();
      if (subtitle != null && !subtitle.isEmpty()) {
        guiGraphics.drawString(font, subtitle, titleX, titleY, COLOR_SUBTITLE_TEXT, false);
      }
    } else if (holoLogData != null) {
      if (holoLogData.title() != null) {
        guiGraphics.drawString(font, holoLogData.title(), titleX, titleY, COLOR_TITLE_TEXT, false);
        titleY += font.lineHeight + TITLE_SPACING;
      }
      if (holoLogData.subtitle() != null && !holoLogData.subtitle().isEmpty()) {
        guiGraphics.drawString(
            font, holoLogData.subtitle(), titleX, titleY, COLOR_SUBTITLE_TEXT, false);
      }
    }
  }

  private void renderText(final GuiGraphics guiGraphics, final int x, final int y) {
    int textX = x + TEXT_AREA_X + TEXT_PADDING;
    int textY = y + TEXT_AREA_Y + TEXT_PADDING;

    guiGraphics.enableScissor(
        x + TEXT_AREA_X,
        y + TEXT_AREA_Y,
        x + TEXT_AREA_X + TEXT_AREA_WIDTH,
        y + TEXT_AREA_Y + TEXT_AREA_HEIGHT);

    int yOffset = textY - scrollOffset;

    if (player != null) {
      yOffset = renderPlayerContent(guiGraphics, textX, yOffset, y);
    } else if (holoLogData != null) {
      yOffset = renderFallbackContent(guiGraphics, textX, yOffset);
    } else {
      renderNoContent(guiGraphics, textX, textY);
    }

    guiGraphics.disableScissor();
  }

  private int renderPlayerContent(
      final GuiGraphics guiGraphics, final int textX, int yOffset, final int areaY) {
    int maxY = areaY + TEXT_AREA_Y + TEXT_AREA_HEIGHT - TEXT_PADDING * 2;
    for (String line : player.getDisplayedLines()) {
      if (yOffset >= areaY + TEXT_AREA_Y && yOffset < maxY) {
        guiGraphics.drawString(font, line, textX, yOffset, COLOR_LINE_TEXT, false);
      }
      yOffset += font.lineHeight + LINE_SPACING;
    }

    String partialLine = player.getCurrentPartialLine();
    if (partialLine != null
        && !partialLine.isEmpty()
        && yOffset >= areaY + TEXT_AREA_Y
        && yOffset < maxY) {
      guiGraphics.drawString(font, partialLine, textX, yOffset, COLOR_LINE_TEXT, false);
    }

    return yOffset;
  }

  private int renderFallbackContent(
      final GuiGraphics guiGraphics, final int textX, final int yOffset) {
    return yOffset;
  }

  private void renderNoContent(final GuiGraphics guiGraphics, final int textX, final int textY) {
    String message = "No hololog content";
    guiGraphics.drawString(
        font,
        message,
        textX + (TEXT_AREA_WIDTH - TEXT_PADDING * 2 - font.width(message)) / 2,
        textY + (TEXT_AREA_HEIGHT - TEXT_PADDING * 2 - font.lineHeight) / 2,
        COLOR_SUBTITLE_TEXT,
        false);
  }

  private void renderRecipe(
      final GuiGraphics guiGraphics,
      final int x,
      final int y,
      final HoloLogDisplayRecipe displayRecipe) {
    if (minecraft == null || minecraft.level == null) {
      return;
    }

    // Get the recipe from the recipe manager
    Optional<? extends Recipe<?>> recipeOpt =
        minecraft.level.getRecipeManager().byKey(displayRecipe.recipeId());
    if (recipeOpt.isEmpty()) {
      log.warn("Recipe not found: {}", displayRecipe.recipeId());
      return;
    }
    Recipe<?> recipe = recipeOpt.get();

    // Calculate positions (moved up by 15 pixels)
    int centerX = x + HOLOGRAM_AREA_X + HOLOGRAM_AREA_WIDTH / 2;
    int centerY = y + HOLOGRAM_AREA_Y + HOLOGRAM_AREA_HEIGHT / 2 - 15;

    // Get recipe inputs and outputs
    List<ItemStack> inputs = new ArrayList<>();
    try {
      // For most recipes, get the ingredients (show all ingredients, not grouped)
      if (!recipe.getIngredients().isEmpty()) {
        recipe
            .getIngredients()
            .forEach(
                ingredient -> {
                  ItemStack[] items = ingredient.getItems();
                  if (items.length > 0) {
                    inputs.add(items[0]);
                  }
                });
      }
    } catch (Exception e) {
      log.error("Failed to get recipe inputs: {}", e.getMessage());
    }

    ItemStack output = recipe.getResultItem(minecraft.level.registryAccess());

    int itemSize = 24;
    int spacing = 12;
    float inputScale = 1.5f * displayRecipe.scale();
    int gridSize = (int) Math.ceil(Math.sqrt(inputs.size()));
    int totalGridWidth = gridSize * itemSize + (gridSize - 1) * 4;
    int totalGridHeight = gridSize * itemSize + (gridSize - 1) * 4;
    int inputStartX = centerX - totalGridWidth - spacing - 10;
    int inputStartY = centerY - totalGridHeight / 2;

    PoseStack poseStack = guiGraphics.pose();

    for (int i = 0; i < inputs.size(); i++) {
      ItemStack input = inputs.get(i);
      if (!input.isEmpty()) {
        int gridX = i % gridSize;
        int gridY = i / gridSize;
        int xPos = inputStartX + gridX * (itemSize + 4);
        int yPos = inputStartY + gridY * (itemSize + 4);

        poseStack.pushPose();
        poseStack.translate(xPos, yPos, 0);
        poseStack.scale(inputScale, inputScale, 1.0f);
        guiGraphics.renderItem(input, 0, 0);
        guiGraphics.renderItemDecorations(font, input, 0, 0);
        poseStack.popPose();
      }
    }

    // Render arrow - larger and more prominent
    String arrow = "→";
    float arrowScale = 2.5f; // Make arrow much larger
    poseStack.pushPose();

    // Center the scaled arrow
    int arrowWidth = (int) (font.width(arrow) * arrowScale);
    int arrowHeight = (int) (font.lineHeight * arrowScale);
    int arrowX = centerX - arrowWidth / 2;
    int arrowY = centerY - arrowHeight / 2;

    poseStack.translate(arrowX, arrowY, 0);
    poseStack.scale(arrowScale, arrowScale, 1.0f);
    guiGraphics.drawString(font, arrow, 0, 0, COLOR_TITLE_TEXT, false);
    poseStack.popPose();

    // Render output
    if (!output.isEmpty()) {
      int outputX = centerX + spacing + 10;
      int outputY = centerY - itemSize / 2;
      // Scale the output item
      float outputScale = 1.8f * displayRecipe.scale();
      poseStack.pushPose();
      poseStack.translate(outputX, outputY, 0);
      poseStack.scale(outputScale, outputScale, 1.0f);
      guiGraphics.renderItem(output, 0, 0);
      guiGraphics.renderItemDecorations(font, output, 0, 0);
      poseStack.popPose();
    }
  }

  private int calculateTotalTextHeight() {
    return wrappedTextLines.size() * (font.lineHeight + LINE_SPACING);
  }
}
