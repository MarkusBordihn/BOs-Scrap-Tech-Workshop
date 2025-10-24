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

package de.markusbordihn.scraptechworkshop.item;

import de.markusbordihn.scraptechworkshop.Constants;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class DescriptiveBlockItem extends BlockItem {

  private final String itemKey;

  public DescriptiveBlockItem(final Block block, final Properties properties) {
    super(block, properties);
    this.itemKey =
        Constants.ITEM_PREFIX
            + block.getDescriptionId().replace("block." + Constants.MOD_ID + ".", "");
  }

  @Override
  public void appendHoverText(
      ItemStack itemStack, Level level, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(itemStack, level, tooltip, flag);

    MutableComponent description = Component.translatable(itemKey + ".description");
    if (!description.getString().equals(itemKey + ".description")) {
      tooltip.add(description.withStyle(ChatFormatting.GRAY));
    }

    MutableComponent usage = Component.translatable(itemKey + ".usage");
    if (!usage.getString().equals(itemKey + ".usage")) {
      tooltip.add(usage.withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    MutableComponent tooltipText = Component.translatable(itemKey + ".tooltip");
    if (!tooltipText.getString().equals(itemKey + ".tooltip")) {
      tooltip.add(tooltipText.withStyle(ChatFormatting.DARK_GRAY));
    }
  }
}
