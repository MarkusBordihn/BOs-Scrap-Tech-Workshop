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

package de.markusbordihn.scraptechworkshop.block.windturbine;

import de.markusbordihn.scraptechworkshop.block.entity.windturbine.ScrapWindTurbineBlockEntity;
import de.markusbordihn.scraptechworkshop.block.multiblock.MultiBlockStructure;
import de.markusbordihn.scraptechworkshop.data.windturbine.ScrapWindTurbineStatus;
import de.markusbordihn.scraptechworkshop.menu.MenuManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ScrapWindTurbineBlock extends BaseEntityBlock implements MultiBlockStructure {

  public static final String ID = "scrap_wind_turbine";
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<ScrapWindTurbineStatus> STATUS =
      EnumProperty.create("status", ScrapWindTurbineStatus.class);

  // Upper block shape: Just the pole (2x16x2 in the center)
  private static final VoxelShape SHAPE_UPPER = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);

  // Lower block shapes: Console + pole, varies by direction
  private static final VoxelShape SHAPE_LOWER_NORTH =
      Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);
  private static final VoxelShape SHAPE_LOWER_SOUTH =
      Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);
  private static final VoxelShape SHAPE_LOWER_WEST =
      Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);
  private static final VoxelShape SHAPE_LOWER_EAST =
      Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);

  public ScrapWindTurbineBlock(final Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(STATUS, ScrapWindTurbineStatus.IDLE)
            .setValue(HALF, DoubleBlockHalf.LOWER));
  }

  public static void updateStatus(
      final Level level, final BlockPos blockPos, final ScrapWindTurbineStatus newStatus) {
    BlockState currentState = level.getBlockState(blockPos);
    if (currentState.getBlock() instanceof ScrapWindTurbineBlock
        && currentState.getValue(STATUS) != newStatus) {
      level.setBlock(blockPos, currentState.setValue(STATUS, newStatus), Block.UPDATE_ALL);
    }
  }

  @Override
  public VoxelShape getShape(
      BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
    // Upper block: only the pole (2x16x2)
    if (isUpperBlock(blockState)) {
      return SHAPE_UPPER;
    }

    // Lower block: console + pole (varies by direction)
    Direction facing = blockState.getValue(FACING);
    return switch (facing) {
      case NORTH -> SHAPE_LOWER_NORTH;
      case SOUTH -> SHAPE_LOWER_SOUTH;
      case WEST -> SHAPE_LOWER_WEST;
      case EAST -> SHAPE_LOWER_EAST;
      default -> SHAPE_LOWER_NORTH;
    };
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return isUpperBlock(blockState) ? null : new ScrapWindTurbineBlockEntity(blockPos, blockState);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
    return isUpperBlock(blockState)
        ? null
        : createTickerHelper(
            blockEntityType, ScrapWindTurbineBlockEntity.TYPE, ScrapWindTurbineBlockEntity::tick);
  }

  @Override
  public InteractionResult use(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      BlockHitResult blockHitResult) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    BlockPos mainPos = getMainBlockPos(blockPos, blockState);
    if (level.getBlockEntity(mainPos)
        instanceof ScrapWindTurbineBlockEntity windTurbineBlockEntity) {
      MenuManager.openMenu(player, windTurbineBlockEntity);
    }
    return InteractionResult.CONSUME;
  }

  @Override
  public void setPlacedBy(
      Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    if (!level.isClientSide) {
      placeMultiBlock(level, pos, state.setValue(HALF, DoubleBlockHalf.LOWER), this);
    }
  }

  @Override
  public void onRemove(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      BlockState newState,
      boolean isMoving) {
    if (!blockState.is(newState.getBlock())) {
      BlockPos mainPos = getMainBlockPos(blockPos, blockState);
      BlockEntity blockEntity = level.getBlockEntity(mainPos);
      if (blockEntity instanceof ScrapWindTurbineBlockEntity windTurbineBlockEntity) {
        Containers.dropContents(level, mainPos, windTurbineBlockEntity.getContainer());
      }
      removeMultiBlock(level, blockPos, blockState);
      super.onRemove(blockState, level, blockPos, newState, isMoving);
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockPos pos = context.getClickedPos();
    Level level = context.getLevel();

    if (pos.getY() < level.getMaxBuildHeight() - 1
        && level.getBlockState(pos.above()).canBeReplaced()) {
      return this.defaultBlockState()
          .setValue(FACING, context.getHorizontalDirection().getOpposite())
          .setValue(STATUS, ScrapWindTurbineStatus.IDLE)
          .setValue(HALF, DoubleBlockHalf.LOWER);
    }
    return null;
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
    builder.add(FACING, STATUS, HALF);
  }
}
