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

package de.markusbordihn.scraptechworkshop.data.scrap;

import java.util.Locale;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MapColor;

public enum ScrapType {
  ALLOY(ScrapCategory.MATERIAL, 20, Rarity.UNCOMMON, 1.2f, MapColor.METAL),

  COIL(ScrapCategory.MATERIAL, 12, Rarity.COMMON, 1.0f, MapColor.METAL),
  COPPER(ScrapCategory.MATERIAL, 12, Rarity.COMMON, 1.0f, MapColor.COLOR_ORANGE),
  FASTENER(ScrapCategory.MATERIAL, 8, Rarity.COMMON, 1.0f, MapColor.METAL),
  GOLD(ScrapCategory.MATERIAL, 25, Rarity.UNCOMMON, 1.0f, MapColor.GOLD),
  IRON(ScrapCategory.MATERIAL, 15, Rarity.COMMON, 1.0f, MapColor.METAL),
  METAL(ScrapCategory.MATERIAL, 10, Rarity.COMMON, 1.0f, MapColor.METAL),

  CRYSTAL(ScrapCategory.MINERAL, 20, Rarity.UNCOMMON, 1.3f, MapColor.DIAMOND),
  MINERAL(ScrapCategory.MINERAL, 3, Rarity.COMMON, 0.5f, MapColor.STONE),

  BIO(ScrapCategory.ORGANIC, 3, Rarity.COMMON, 0.6f, MapColor.PLANT),
  FIBER(ScrapCategory.ORGANIC, 4, Rarity.COMMON, 0.7f, MapColor.WOOL),
  RUBBER(ScrapCategory.ORGANIC, 6, Rarity.COMMON, 0.8f, MapColor.COLOR_BLACK),
  WOOD(ScrapCategory.ORGANIC, 4, Rarity.COMMON, 0.7f, MapColor.WOOD),

  CERAMIC(ScrapCategory.SYNTHETIC, 8, Rarity.COMMON, 0.8f, MapColor.TERRACOTTA_WHITE),
  GLASS(ScrapCategory.SYNTHETIC, 7, Rarity.COMMON, 0.9f, MapColor.NONE),
  INSULATION(ScrapCategory.SYNTHETIC, 8, Rarity.COMMON, 0.8f, MapColor.CLAY),
  LUMINOUS(ScrapCategory.SYNTHETIC, 30, Rarity.RARE, 1.5f, MapColor.COLOR_LIGHT_BLUE, true),
  PLASTIC(ScrapCategory.SYNTHETIC, 5, Rarity.COMMON, 0.6f, MapColor.COLOR_CYAN),

  CAPACITOR(ScrapCategory.TECHNOLOGY, 15, Rarity.UNCOMMON, 1.2f, MapColor.METAL),
  CIRCUIT(ScrapCategory.TECHNOLOGY, 18, Rarity.UNCOMMON, 1.2f, MapColor.METAL),
  ENERGY_CELL(ScrapCategory.TECHNOLOGY, 25, Rarity.RARE, 1.5f, MapColor.METAL),
  TECH(ScrapCategory.TECHNOLOGY, 18, Rarity.UNCOMMON, 1.2f, MapColor.METAL);

  private final boolean luminous;
  private final float efficiencyBonus;
  private final int value;
  private final MapColor mapColor;
  private final Rarity rarity;
  private final ScrapCategory category;
  private final String blockName;
  private final String itemName;

  ScrapType(
      ScrapCategory category, int value, Rarity rarity, float efficiencyBonus, MapColor mapColor) {
    this(category, value, rarity, efficiencyBonus, mapColor, false);
  }

  ScrapType(
      ScrapCategory category,
      int value,
      Rarity rarity,
      float efficiencyBonus,
      MapColor mapColor,
      boolean luminous) {
    this.category = category;
    this.value = value;
    this.rarity = rarity;
    this.efficiencyBonus = efficiencyBonus;
    this.mapColor = mapColor;
    this.luminous = luminous;
    this.blockName = name().toLowerCase(Locale.ROOT) + "_scrap_box";
    this.itemName = name().toLowerCase(Locale.ROOT) + "_scrap";
  }

  public ScrapCategory getCategory() {
    return category;
  }

  public int getValue() {
    return value;
  }

  public Rarity getRarity() {
    return rarity;
  }

  public float getEfficiencyBonus() {
    return efficiencyBonus;
  }

  public MapColor getMapColor() {
    return mapColor;
  }

  public boolean isLuminous() {
    return luminous;
  }

  public String getItemId() {
    return itemName;
  }

  public String getBlockId() {
    return blockName;
  }
}
