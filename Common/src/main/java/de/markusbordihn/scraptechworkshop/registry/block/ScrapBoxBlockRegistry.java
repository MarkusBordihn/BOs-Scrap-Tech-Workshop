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

package de.markusbordihn.scraptechworkshop.registry.block;

import de.markusbordihn.scraptechworkshop.block.scrapbox.ScrapBoxBlock;
import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;

public class ScrapBoxBlockRegistry {

  public static final Block ALLOY_SCRAP_BOX_BLOCK;
  public static final Block BIO_SCRAP_BOX_BLOCK;
  public static final Block CAPACITOR_SCRAP_BOX_BLOCK;
  public static final Block CERAMIC_SCRAP_BOX_BLOCK;
  public static final Block CIRCUIT_SCRAP_BOX_BLOCK;
  public static final Block COIL_SCRAP_BOX_BLOCK;
  public static final Block COPPER_SCRAP_BOX_BLOCK;
  public static final Block CRYSTAL_SCRAP_BOX_BLOCK;
  public static final Block ENERGY_CELL_SCRAP_BOX_BLOCK;
  public static final Block FASTENER_SCRAP_BOX_BLOCK;
  public static final Block FIBER_SCRAP_BOX_BLOCK;
  public static final Block GLASS_SCRAP_BOX_BLOCK;
  public static final Block GOLD_SCRAP_BOX_BLOCK;
  public static final Block INSULATION_SCRAP_BOX_BLOCK;
  public static final Block IRON_SCRAP_BOX_BLOCK;
  public static final Block LUMINOUS_SCRAP_BOX_BLOCK;
  public static final Block METAL_SCRAP_BOX_BLOCK;
  public static final Block MINERAL_SCRAP_BOX_BLOCK;
  public static final Block PLASTIC_SCRAP_BOX_BLOCK;
  public static final Block RUBBER_SCRAP_BOX_BLOCK;
  public static final Block TECH_SCRAP_BOX_BLOCK;
  public static final Block WOOD_SCRAP_BOX_BLOCK;

  private static final Map<ScrapType, Block> SCRAP_BOXES = new EnumMap<>(ScrapType.class);

  static {
    ALLOY_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.ALLOY);
    BIO_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.BIO);
    CAPACITOR_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.CAPACITOR);
    CERAMIC_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.CERAMIC);
    CIRCUIT_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.CIRCUIT);
    COIL_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.COIL);
    COPPER_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.COPPER);
    CRYSTAL_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.CRYSTAL);
    ENERGY_CELL_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.ENERGY_CELL);
    FASTENER_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.FASTENER);
    FIBER_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.FIBER);
    GLASS_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.GLASS);
    GOLD_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.GOLD);
    INSULATION_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.INSULATION);
    IRON_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.IRON);
    LUMINOUS_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.LUMINOUS);
    METAL_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.METAL);
    MINERAL_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.MINERAL);
    PLASTIC_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.PLASTIC);
    RUBBER_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.RUBBER);
    TECH_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.TECH);
    WOOD_SCRAP_BOX_BLOCK = createScrapBox(ScrapType.WOOD);
  }

  private ScrapBoxBlockRegistry() {}

  private static Block createScrapBox(ScrapType type) {
    Block block = new ScrapBoxBlock(type);
    SCRAP_BOXES.put(type, block);
    return block;
  }

  public static Block getScrapBox(ScrapType type) {
    return SCRAP_BOXES.get(type);
  }
}
