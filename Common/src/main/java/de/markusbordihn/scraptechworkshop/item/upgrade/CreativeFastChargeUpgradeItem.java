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

package de.markusbordihn.scraptechworkshop.item.upgrade;

import de.markusbordihn.scraptechworkshop.Constants;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class CreativeFastChargeUpgradeItem extends ChargeUpgradeItem {

  public static final String ID = "creative_fast_charge_upgrade";
  private static final int CHARGE_MULTIPLIER = 10;

  public CreativeFastChargeUpgradeItem(final Properties properties) {
    super(properties.stacksTo(1).rarity(Rarity.EPIC), CHARGE_MULTIPLIER);
  }

  @Override
  public void appendHoverText(
      ItemStack itemStack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + ID + ".description")
            .withStyle(ChatFormatting.GOLD));
    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + ID + ".charge_rate", getChargeMultiplier())
            .withStyle(ChatFormatting.YELLOW));
    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + ID + ".creative_only")
            .withStyle(ChatFormatting.RED));
    super.appendHoverText(itemStack, level, tooltipComponents, isAdvanced);
  }

  @Override
  public boolean isFoil(ItemStack stack) {
    return true;
  }
}
