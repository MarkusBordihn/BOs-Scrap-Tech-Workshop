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

import de.markusbordihn.scraptechworkshop.block.scrapbox.*;
import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ScrapBoxBlockRegistry {

  public static final Block ALLOY_SCRAP_BOX_BLOCK =
      new AlloyScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block BIO_SCRAP_BOX_BLOCK =
      new BioScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.PLANT)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block CERAMIC_SCRAP_BOX_BLOCK =
      new CeramicScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.TERRACOTTA_WHITE)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block CRYSTAL_SCRAP_BOX_BLOCK =
      new CrystalScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.DIAMOND)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block FASTENER_SCRAP_BOX_BLOCK =
      new FastenerScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block FIBER_SCRAP_BOX_BLOCK =
      new FiberScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.WOOL)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block GLASS_SCRAP_BOX_BLOCK =
      new GlassScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.NONE)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block INSULATION_SCRAP_BOX_BLOCK =
      new InsulationScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.CLAY)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block LUMINOUS_SCRAP_BOX_BLOCK =
      new LuminousScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.COLOR_LIGHT_BLUE)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .lightLevel((state) -> 7)
              .noOcclusion());

  public static final Block MINERAL_SCRAP_BOX_BLOCK =
      new MineralScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.STONE)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block PLASTIC_SCRAP_BOX_BLOCK =
      new PlasticScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.COLOR_CYAN)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block RUBBER_SCRAP_BOX_BLOCK =
      new RubberScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.COLOR_BLACK)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  public static final Block WOOD_SCRAP_BOX_BLOCK =
      new WoodScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.WOOD)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion());

  // Metal Scrap Boxes (using MetalScrapBoxBlock)
  public static final Block METAL_SCRAP_BOX_BLOCK =
      new MetalScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion(),
          ScrapType.METAL);

  public static final Block GOLD_SCRAP_BOX_BLOCK =
      new MetalScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.GOLD)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion(),
          ScrapType.GOLD);

  public static final Block IRON_SCRAP_BOX_BLOCK =
      new MetalScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion(),
          ScrapType.IRON);

  public static final Block COPPER_SCRAP_BOX_BLOCK =
      new MetalScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.COLOR_ORANGE)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion(),
          ScrapType.COPPER);

  // Tech Scrap Boxes (using TechScrapBoxBlock)
  public static final Block TECH_SCRAP_BOX_BLOCK =
      new TechScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion(),
          ScrapType.TECH);

  public static final Block CIRCUIT_SCRAP_BOX_BLOCK =
      new TechScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion(),
          ScrapType.CIRCUIT);

  public static final Block COIL_SCRAP_BOX_BLOCK =
      new TechScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion(),
          ScrapType.COIL);

  public static final Block CAPACITOR_SCRAP_BOX_BLOCK =
      new TechScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion(),
          ScrapType.CAPACITOR);

  public static final Block ENERGY_CELL_SCRAP_BOX_BLOCK =
      new TechScrapBoxBlock(
          BlockBehaviour.Properties.of()
              .mapColor(MapColor.METAL)
              .strength(1.5F, 3.0F)
              .sound(SoundType.WOOD)
              .noOcclusion(),
          ScrapType.ENERGY_CELL);

  private ScrapBoxBlockRegistry() {}
}
