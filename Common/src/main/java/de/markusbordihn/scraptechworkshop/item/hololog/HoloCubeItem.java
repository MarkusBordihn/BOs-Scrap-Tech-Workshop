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
  public static final String ID = "holo_cube";
  private static final String HOLO_LOG_ID_TAG = "HoloLogId";

  public HoloCubeItem(final Block block, final Properties properties) {
    super(block, properties);
  }

  public static ItemStack create(final ResourceLocation holoLogId) {
    ItemStack stack = new ItemStack(ModBlockItems.HOLO_CUBE.get());
    setHoloLogId(stack, holoLogId);
    return stack;
  }

  public static void setHoloLogId(final ItemStack itemStack, final ResourceLocation holoLogId) {
    CompoundTag tag = itemStack.getOrCreateTag();
    tag.putString(HOLO_LOG_ID_TAG, holoLogId.toString());
  }

  public static ResourceLocation getHoloLogId(final ItemStack itemStack) {
    CompoundTag tag = itemStack.getTag();
    if (tag == null || !tag.contains(HOLO_LOG_ID_TAG)) {
      return null;
    }
    return new ResourceLocation(tag.getString(HOLO_LOG_ID_TAG));
  }

  @Override
  public void appendHoverText(
      ItemStack itemStack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
    super.appendHoverText(itemStack, level, tooltipComponents, isAdvanced);

    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + "holo_cube.description")
            .withStyle(style -> style.withColor(0x999999)));

    ResourceLocation holoLogId = getHoloLogId(itemStack);
    if (holoLogId != null) {
      tooltipComponents.add(
          Component.translatable(
              Constants.TOOLTIP_PREFIX + "holo_cube.holo_log", holoLogId.toString()));
    } else {
      tooltipComponents.add(Component.translatable(Constants.TOOLTIP_PREFIX + "holo_cube.empty"));
    }

    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + "holo_cube.usage")
            .withStyle(style -> style.withColor(0x5555FF).withItalic(true)));
  }
}
