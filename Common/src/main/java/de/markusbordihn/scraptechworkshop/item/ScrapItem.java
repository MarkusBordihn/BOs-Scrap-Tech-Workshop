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

import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class ScrapItem extends Item {

  protected final ScrapType scrapType;

  protected ScrapItem(Properties properties, ScrapType scrapType) {
    super(properties);
    this.scrapType = scrapType;
  }

  public ScrapType getScrapType() {
    return scrapType;
  }

  public abstract int getScrapValue();

  public abstract Rarity getScrapRarity();

  public boolean isRecyclable() {
    return true;
  }

  public abstract float getEfficiencyBonus();

  @Override
  public Rarity getRarity(ItemStack stack) {
    return getScrapRarity();
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, level, tooltip, flag);
    addScrapTooltip(stack, level, tooltip, flag);
  }

  protected void addScrapTooltip(
      ItemStack itemStack, Level level, List<Component> tooltip, TooltipFlag flag) {
    tooltip.add(
        Component.translatable("tooltip.scrap_tech_workshop.scrap_value", getScrapValue())
            .withStyle(net.minecraft.ChatFormatting.GRAY));

    Rarity rarity = getScrapRarity();
    tooltip.add(
        Component.translatable(
                "tooltip.scrap_tech_workshop.rarity",
                Component.translatable("rarity." + rarity.name().toLowerCase()))
            .withStyle(rarity.color));

    if (getEfficiencyBonus() != 1.0f) {
      String efficiencyKey =
          getEfficiencyBonus() > 1.0f
              ? "tooltip.scrap_tech_workshop.efficiency.bonus"
              : "tooltip.scrap_tech_workshop.efficiency.penalty";
      int percentage = Math.round((getEfficiencyBonus() - 1.0f) * 100);
      tooltip.add(
          Component.translatable(efficiencyKey, Math.abs(percentage))
              .withStyle(
                  getEfficiencyBonus() > 1.0f
                      ? net.minecraft.ChatFormatting.GREEN
                      : net.minecraft.ChatFormatting.RED));
    }

    if (!isRecyclable()) {
      tooltip.add(
          Component.translatable("tooltip.scrap_tech_workshop.not_recyclable")
              .withStyle(net.minecraft.ChatFormatting.DARK_RED));
    }
  }
}
