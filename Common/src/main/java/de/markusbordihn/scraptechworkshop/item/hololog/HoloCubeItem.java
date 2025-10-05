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
import de.markusbordihn.scraptechworkshop.item.ModBlockItems;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class HoloCubeItem extends BlockItem {
  public static final String ID = "holocube";
  private static final String HOLOLOG_ID_TAG = "HolologId";

  public HoloCubeItem(Block block, Properties properties) {
    super(block, properties);
  }

  public static ItemStack create(ResourceLocation holologId) {
    ItemStack stack = new ItemStack(ModBlockItems.HOLOCUBE_BLOCK_ITEM.get());
    setHolologId(stack, holologId);
    return stack;
  }

  public static void setHolologId(ItemStack stack, ResourceLocation holologId) {
    CompoundTag tag = stack.getOrCreateTag();
    tag.putString(HOLOLOG_ID_TAG, holologId.toString());
  }

  public static ResourceLocation getHolologId(ItemStack stack) {
    CompoundTag tag = stack.getTag();
    if (tag == null || !tag.contains(HOLOLOG_ID_TAG)) {
      return null;
    }
    return new ResourceLocation(tag.getString(HOLOLOG_ID_TAG));
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
    super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + "holocube.description")
            .withStyle(style -> style.withColor(0x999999)));

    ResourceLocation holologId = getHolologId(stack);
    if (holologId != null) {
      tooltipComponents.add(
          Component.translatable(
              Constants.TOOLTIP_PREFIX + "holocube.hololog", holologId.toString()));
    } else {
      tooltipComponents.add(Component.translatable(Constants.TOOLTIP_PREFIX + "holocube.empty"));
    }

    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + "holocube.usage")
            .withStyle(style -> style.withColor(0x5555FF).withItalic(true)));
  }
}
