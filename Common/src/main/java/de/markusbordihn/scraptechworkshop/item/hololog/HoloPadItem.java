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
import de.markusbordihn.scraptechworkshop.item.ModItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

  public static final String ID = "holo_pad";
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String HOLOLOG_ID_TAG = "HoloLogId";
  private static final String TRANSLATION_KEY_PREFIX = "item.scrap_tech_workshop.holo_pad";

  public HoloPadItem(final Properties properties) {
    super(properties);
  }

  public static ItemStack create(final ResourceLocation holoLogId) {
    ItemStack stack = new ItemStack(ModItems.HOLO_PAD.get());
    setHoloLogId(stack, holoLogId);
    return stack;
  }

  public static ResourceLocation getHoloLogId(final ItemStack stack) {
    CompoundTag tag = stack.getTag();
    if (tag == null || !tag.contains(HOLOLOG_ID_TAG)) {
      return null;
    }
    return new ResourceLocation(tag.getString(HOLOLOG_ID_TAG));
  }

  public static void setHoloLogId(final ItemStack stack, final ResourceLocation holoLogId) {
    stack.getOrCreateTag().putString(HOLOLOG_ID_TAG, holoLogId.toString());
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    ResourceLocation holoLogId = getHoloLogId(stack);

    if (holoLogId == null) {
      if (!level.isClientSide) {
        player.displayClientMessage(
            Component.translatable(TRANSLATION_KEY_PREFIX + ".no_hololog")
                .withStyle(ChatFormatting.RED),
            true);
      }
      return InteractionResultHolder.fail(stack);
    }

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
    } else {
      ClientHelper.openHoloPadScreen(holoLogId);
    }

    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag flag) {
    super.appendHoverText(stack, level, tooltipComponents, flag);

    ResourceLocation holoLogId = getHoloLogId(stack);
    if (holoLogId != null) {
      tooltipComponents.add(
          Component.translatable(TRANSLATION_KEY_PREFIX + ".holo_log")
              .append(Component.literal(": " + holoLogId))
              .withStyle(ChatFormatting.GRAY));
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
    } else {
      tooltipComponents.add(
          Component.translatable(TRANSLATION_KEY_PREFIX + ".empty")
              .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
    tooltipComponents.add(
        Component.translatable(TRANSLATION_KEY_PREFIX + ".description")
            .withStyle(ChatFormatting.GRAY));
  }
}
