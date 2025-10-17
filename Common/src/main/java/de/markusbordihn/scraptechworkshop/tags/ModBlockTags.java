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

package de.markusbordihn.scraptechworkshop.tags;

import de.markusbordihn.scraptechworkshop.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {

  // Scrap drop tags for each ScrapType
  public static final TagKey<Block> SCRAP_DROP_ALLOY =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/alloy"));

  public static final TagKey<Block> SCRAP_DROP_BIO =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/bio"));

  public static final TagKey<Block> SCRAP_DROP_CAPACITOR =
      TagKey.create(
          Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/capacitor"));

  public static final TagKey<Block> SCRAP_DROP_CERAMIC =
      TagKey.create(
          Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/ceramic"));

  public static final TagKey<Block> SCRAP_DROP_CIRCUIT =
      TagKey.create(
          Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/circuit"));

  public static final TagKey<Block> SCRAP_DROP_COIL =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/coil"));

  public static final TagKey<Block> SCRAP_DROP_COPPER =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/copper"));

  public static final TagKey<Block> SCRAP_DROP_CRYSTAL =
      TagKey.create(
          Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/crystal"));

  public static final TagKey<Block> SCRAP_DROP_ENERGY_CELL =
      TagKey.create(
          Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/energy_cell"));

  public static final TagKey<Block> SCRAP_DROP_FASTENER =
      TagKey.create(
          Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/fastener"));

  public static final TagKey<Block> SCRAP_DROP_FIBER =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/fiber"));

  public static final TagKey<Block> SCRAP_DROP_GLASS =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/glass"));

  public static final TagKey<Block> SCRAP_DROP_GOLD =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/gold"));

  public static final TagKey<Block> SCRAP_DROP_INSULATION =
      TagKey.create(
          Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/insulation"));

  public static final TagKey<Block> SCRAP_DROP_IRON =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/iron"));

  public static final TagKey<Block> SCRAP_DROP_LUMINOUS =
      TagKey.create(
          Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/luminous"));

  public static final TagKey<Block> SCRAP_DROP_METAL =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/metal"));

  public static final TagKey<Block> SCRAP_DROP_MINERAL =
      TagKey.create(
          Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/mineral"));

  public static final TagKey<Block> SCRAP_DROP_PLASTIC =
      TagKey.create(
          Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/plastic"));

  public static final TagKey<Block> SCRAP_DROP_RUBBER =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/rubber"));

  public static final TagKey<Block> SCRAP_DROP_TECH =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/tech"));

  public static final TagKey<Block> SCRAP_DROP_WOOD =
      TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, "scrap_drops/wood"));

  private ModBlockTags() {}
}
