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

package de.markusbordihn.scraptechworkshop.registry.item.scrapbox;

import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.item.scrapbox.ScrapBoxBlockItem;
import de.markusbordihn.scraptechworkshop.registry.block.ScrapBoxBlockRegistry;
import net.minecraft.world.item.Item;

public class ScrapBoxBlockItemRegistry {

  public static final ScrapBoxBlockItem ALLOY_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.ALLOY_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.ALLOY);

  public static final ScrapBoxBlockItem BIO_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.BIO_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.BIO);

  public static final ScrapBoxBlockItem CAPACITOR_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.CAPACITOR_SCRAP_BOX_BLOCK,
          new Item.Properties(),
          ScrapType.CAPACITOR);

  public static final ScrapBoxBlockItem CERAMIC_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.CERAMIC_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.CERAMIC);

  public static final ScrapBoxBlockItem CIRCUIT_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.CIRCUIT_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.CIRCUIT);

  public static final ScrapBoxBlockItem COIL_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.COIL_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.COIL);

  public static final ScrapBoxBlockItem COPPER_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.COPPER_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.COPPER);

  public static final ScrapBoxBlockItem CRYSTAL_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.CRYSTAL_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.CRYSTAL);

  public static final ScrapBoxBlockItem ENERGY_CELL_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.ENERGY_CELL_SCRAP_BOX_BLOCK,
          new Item.Properties(),
          ScrapType.ENERGY_CELL);

  public static final ScrapBoxBlockItem FASTENER_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.FASTENER_SCRAP_BOX_BLOCK,
          new Item.Properties(),
          ScrapType.FASTENER);

  public static final ScrapBoxBlockItem FIBER_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.FIBER_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.FIBER);

  public static final ScrapBoxBlockItem GLASS_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.GLASS_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.GLASS);

  public static final ScrapBoxBlockItem GOLD_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.GOLD_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.GOLD);

  public static final ScrapBoxBlockItem INSULATION_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.INSULATION_SCRAP_BOX_BLOCK,
          new Item.Properties(),
          ScrapType.INSULATION);

  public static final ScrapBoxBlockItem IRON_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.IRON_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.IRON);

  public static final ScrapBoxBlockItem LUMINOUS_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.LUMINOUS_SCRAP_BOX_BLOCK,
          new Item.Properties(),
          ScrapType.LUMINOUS);

  public static final ScrapBoxBlockItem METAL_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.METAL_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.METAL);

  public static final ScrapBoxBlockItem MINERAL_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.MINERAL_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.MINERAL);

  public static final ScrapBoxBlockItem PLASTIC_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.PLASTIC_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.PLASTIC);

  public static final ScrapBoxBlockItem RUBBER_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.RUBBER_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.RUBBER);

  public static final ScrapBoxBlockItem TECH_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.TECH_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.TECH);

  public static final ScrapBoxBlockItem WOOD_SCRAP_BOX_ITEM =
      new ScrapBoxBlockItem(
          ScrapBoxBlockRegistry.WOOD_SCRAP_BOX_BLOCK, new Item.Properties(), ScrapType.WOOD);

  private ScrapBoxBlockItemRegistry() {}
}
