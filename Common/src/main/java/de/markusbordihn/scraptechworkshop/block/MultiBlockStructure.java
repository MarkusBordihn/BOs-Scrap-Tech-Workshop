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

package de.markusbordihn.scraptechworkshop.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public interface MultiBlockStructure {

  EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

  default boolean isUpperBlock(final BlockState blockState) {
    return blockState.hasProperty(HALF) && blockState.getValue(HALF) == DoubleBlockHalf.UPPER;
  }

  default BlockPos getPartnerPos(final BlockPos blockPos, final boolean isUpper) {
    return isUpper ? blockPos.below() : blockPos.above();
  }

  default void placeMultiBlock(
      final Level level, final BlockPos blockPos, final BlockState blockState, final Block block) {
    BlockPos upperPos = blockPos.above();
    if (level.getBlockState(upperPos).canBeReplaced()) {
      BlockState upperState = blockState.setValue(HALF, DoubleBlockHalf.UPPER);
      level.setBlock(upperPos, upperState, 3);
    }
  }

  default void removeMultiBlock(
      final Level level, final BlockPos blockPos, final BlockState blockState) {
    boolean isUpper = isUpperBlock(blockState);
    BlockPos partnerPos = getPartnerPos(blockPos, isUpper);
    BlockState partnerState = level.getBlockState(partnerPos);

    if (partnerState.getBlock() == blockState.getBlock()) {
      level.setBlock(partnerPos, Blocks.AIR.defaultBlockState(), 35);
    }
  }

  default BlockPos getMainBlockPos(final BlockPos blockPos, final BlockState blockState) {
    return isUpperBlock(blockState) ? blockPos.below() : blockPos;
  }
}
