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

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MultiPlaceBlock extends Block {

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<AttachFace> ATTACH_FACE = BlockStateProperties.ATTACH_FACE;

  public MultiPlaceBlock(final Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(ATTACH_FACE, AttachFace.WALL));
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction clickedFace = context.getClickedFace();
    Direction facing;
    AttachFace attachFace;

    if (clickedFace == Direction.DOWN) {
      attachFace = AttachFace.CEILING;
      facing = context.getHorizontalDirection();
    } else if (clickedFace == Direction.UP) {
      attachFace = AttachFace.FLOOR;
      facing = context.getHorizontalDirection();
    } else {
      double relativeY = context.getClickLocation().y - Math.floor(context.getClickLocation().y);
      if (relativeY < 0.25) {
        attachFace = AttachFace.FLOOR;
        facing = clickedFace.getOpposite();
      } else if (relativeY > 0.75) {
        attachFace = AttachFace.CEILING;
        facing = clickedFace.getOpposite();
      } else {
        attachFace = AttachFace.WALL;
        facing = clickedFace;
      }
    }

    return this.defaultBlockState().setValue(ATTACH_FACE, attachFace).setValue(FACING, facing);
  }

  public BlockState rotateBlock(BlockState blockState) {
    AttachFace attachFace = blockState.getValue(ATTACH_FACE);
    Direction currentFacing = blockState.getValue(FACING);
    Direction newFacing;

    if (attachFace == AttachFace.WALL) {
      newFacing =
          switch (currentFacing) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
            default -> currentFacing;
          };
    } else {
      newFacing = currentFacing.getClockWise();
    }

    return blockState.setValue(FACING, newFacing);
  }

  @Override
  public BlockState rotate(BlockState blockState, Rotation rotation) {
    return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
  }

  @Override
  public BlockState mirror(BlockState blockState, Mirror mirror) {
    return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, ATTACH_FACE);
  }
}
