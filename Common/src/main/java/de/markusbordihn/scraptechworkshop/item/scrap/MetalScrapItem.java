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

package de.markusbordihn.scraptechworkshop.item.scrap;

import de.markusbordihn.scraptechworkshop.item.ScrapItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class MetalScrapItem extends Item implements ScrapItem {

  public static final String METAL_SCRAP_ID = "metal_scrap";
  public static final String GOLD_SCRAP_ID = "gold_scrap";
  public static final String IRON_SCRAP_ID = "iron_scrap";
  public static final String COPPER_SCRAP_ID = "copper_scrap";

  // Keep legacy ID for backward compatibility
  public static final String ID = METAL_SCRAP_ID;

  private final String scrapType;

  public MetalScrapItem(Properties properties) {
    this(properties, METAL_SCRAP_ID);
  }

  public MetalScrapItem(Properties properties, String scrapType) {
    super(properties);
    this.scrapType = scrapType;
  }

  @Override
  public int getScrapValue() {
    return switch (scrapType) {
      case GOLD_SCRAP_ID -> 25;
      case IRON_SCRAP_ID -> 15;
      case COPPER_SCRAP_ID -> 12;
      default -> 10; // metal_scrap default
    };
  }

  @Override
  public Rarity getScrapRarity() {
    return switch (scrapType) {
      case GOLD_SCRAP_ID -> Rarity.UNCOMMON;
      case IRON_SCRAP_ID -> Rarity.COMMON;
      case COPPER_SCRAP_ID -> Rarity.COMMON;
      default -> Rarity.COMMON; // metal_scrap default
    };
  }

  @Override
  public Rarity getRarity(ItemStack stack) {
    return getScrapRarity();
  }

  @Override
  public boolean isRecyclable() {
    return true;
  }

  @Override
  public float getEfficiencyBonus() {
    return 1.0f;
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, level, tooltip, flag);

    String translationKey = "item.scrap_tech_workshop." + scrapType + ".description";
    tooltip.add(
        Component.translatable(translationKey).withStyle(net.minecraft.ChatFormatting.DARK_AQUA));

    addScrapTooltip(stack, level, tooltip, flag);
  }
}
