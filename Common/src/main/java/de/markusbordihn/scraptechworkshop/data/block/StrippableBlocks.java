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

package de.markusbordihn.scraptechworkshop.data.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StrippableBlocks {

  private static final Map<Block, Block> STRIPPABLE_BLOCKS = new HashMap<>();
  private static final Map<Block, Block> REVERSE_STRIPPABLE_BLOCKS = new HashMap<>();

  static {
    STRIPPABLE_BLOCKS.put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG);
    STRIPPABLE_BLOCKS.put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD);
    STRIPPABLE_BLOCKS.put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG);
    STRIPPABLE_BLOCKS.put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD);
    STRIPPABLE_BLOCKS.put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG);
    STRIPPABLE_BLOCKS.put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD);
    STRIPPABLE_BLOCKS.put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG);
    STRIPPABLE_BLOCKS.put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD);
    STRIPPABLE_BLOCKS.put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG);
    STRIPPABLE_BLOCKS.put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD);
    STRIPPABLE_BLOCKS.put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG);
    STRIPPABLE_BLOCKS.put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD);
    STRIPPABLE_BLOCKS.put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG);
    STRIPPABLE_BLOCKS.put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD);
    STRIPPABLE_BLOCKS.put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG);
    STRIPPABLE_BLOCKS.put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD);
    STRIPPABLE_BLOCKS.put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM);
    STRIPPABLE_BLOCKS.put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE);
    STRIPPABLE_BLOCKS.put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM);
    STRIPPABLE_BLOCKS.put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE);
    STRIPPABLE_BLOCKS.put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK);

    // Build reverse map for re-adding bark
    for (Map.Entry<Block, Block> entry : STRIPPABLE_BLOCKS.entrySet()) {
      REVERSE_STRIPPABLE_BLOCKS.put(entry.getValue(), entry.getKey());
    }
  }

  public static Optional<BlockState> getStrippedState(BlockState state) {
    Block strippedBlock = STRIPPABLE_BLOCKS.get(state.getBlock());
    if (strippedBlock != null) {
      return Optional.of(
          strippedBlock
              .defaultBlockState()
              .setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
    }
    return Optional.empty();
  }

  public static Optional<BlockState> getUnstrippedState(BlockState state) {
    Block unstrippedBlock = REVERSE_STRIPPABLE_BLOCKS.get(state.getBlock());
    if (unstrippedBlock != null) {
      return Optional.of(
          unstrippedBlock
              .defaultBlockState()
              .setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
    }
    return Optional.empty();
  }

  public static boolean isStrippable(Block block) {
    return STRIPPABLE_BLOCKS.containsKey(block);
  }

  public static boolean isStripped(Block block) {
    return REVERSE_STRIPPABLE_BLOCKS.containsKey(block);
  }

  public static boolean isCycleableWood(Block block) {
    return isStrippable(block) || isStripped(block);
  }
}
