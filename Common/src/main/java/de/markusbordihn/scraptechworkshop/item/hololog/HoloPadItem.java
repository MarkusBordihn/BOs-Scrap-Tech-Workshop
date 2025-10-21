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

package de.markusbordihn.scraptechworkshop.item.hololog;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.client.ClientHelper;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogManager;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloPadItem extends Item {

  public static final String ID_PREFIX = "holo_pad_";
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String TRANSLATION_KEY_PREFIX = Constants.ITEM_PREFIX + "holo_pad";

  private final ResourceLocation holoLogId;

  public HoloPadItem(final ResourceLocation holoLogId, final Properties properties) {
    super(properties);
    this.holoLogId = holoLogId;
  }

  public ResourceLocation getHoloLogId() {
    return holoLogId;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);

    if (!level.isClientSide) {
      if (!HoloLogManager.isValidHoloLog(holoLogId)) {
        player.displayClientMessage(
            Component.translatable(TRANSLATION_KEY_PREFIX + ".invalid_hololog")
                .withStyle(ChatFormatting.RED),
            true);
        return InteractionResultHolder.fail(stack);
      }
      log.info(
          "{} Player {} used HoloPad with hololog: {}",
          Constants.LOG_NAME,
          player.getName().getString(),
          holoLogId);

      // Grant advancement for scavenging basics HoloPad
      if (player instanceof ServerPlayer serverPlayer
          && holoLogId.equals(
              new ResourceLocation(
                  Constants.MOD_ID, "holologs/tutorial/scavenging_field_briefing_01"))) {
        MinecraftServer server = serverPlayer.getServer();
        if (server != null) {
          Advancement advancement =
              server
                  .getAdvancements()
                  .getAdvancement(
                      new ResourceLocation(Constants.MOD_ID, "hololog/read_scav_pad_01"));
          if (advancement != null) {
            AdvancementProgress progress =
                serverPlayer.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
              for (String criterion : progress.getRemainingCriteria()) {
                serverPlayer.getAdvancements().award(advancement, criterion);
              }
            }
          }
        }
      }

    } else {
      ClientHelper.openHoloPadScreen(holoLogId);
    }

    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
  }

  @Override
  public Component getName(ItemStack stack) {
    return HoloLogManager.loadHoloLog(holoLogId)
        .map(data -> (Component) Component.literal("HoloPad: " + data.title()))
        .orElse(super.getName(stack));
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag flag) {
    super.appendHoverText(stack, level, tooltipComponents, flag);

    HoloLogManager.loadHoloLog(holoLogId)
        .ifPresent(
            data -> {
              tooltipComponents.add(
                  Component.literal("\"" + data.title() + "\"").withStyle(ChatFormatting.AQUA));
              if (data.subtitle() != null && !data.subtitle().isEmpty()) {
                tooltipComponents.add(
                    Component.literal(data.subtitle()).withStyle(ChatFormatting.DARK_GRAY));
              }
            });
    tooltipComponents.add(
        Component.translatable(TRANSLATION_KEY_PREFIX + ".description")
            .withStyle(ChatFormatting.GRAY));
    tooltipComponents.add(
        Component.translatable(TRANSLATION_KEY_PREFIX + ".holo_log")
            .append(Component.literal(": " + holoLogId))
            .withStyle(ChatFormatting.GRAY));
  }
}
