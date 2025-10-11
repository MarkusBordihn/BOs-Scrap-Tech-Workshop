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

package de.markusbordihn.scraptechworkshop.block.collectorstation;

import de.markusbordihn.scraptechworkshop.block.MultiBlockStructure;
import de.markusbordihn.scraptechworkshop.block.entity.collectorstation.CollectorStationBlockEntity;
import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
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

public class CollectorStationBlock extends BaseEntityBlock implements MultiBlockStructure {

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<CollectorStationStatus> STATE =
      EnumProperty.create("state", CollectorStationStatus.class);

  // VoxelShapes for upper block
  private static final VoxelShape SHAPE_UPPER_NORTH = Block.box(0.0, 0.0, 1.0, 16.0, 12.0, 13.0);
  private static final VoxelShape SHAPE_UPPER_SOUTH = Block.box(0.0, 0.0, 3.0, 16.0, 12.0, 15.0);
  private static final VoxelShape SHAPE_UPPER_WEST = Block.box(1.0, 0.0, 0.0, 13.0, 12.0, 16.0);
  private static final VoxelShape SHAPE_UPPER_EAST = Block.box(3.0, 0.0, 0.0, 15.0, 12.0, 16.0);

  // VoxelShapes for lower block
  private static final VoxelShape SHAPE_LOWER_NORTH = Block.box(0.0, 0.0, 1.0, 16.0, 16.0, 13.0);
  private static final VoxelShape SHAPE_LOWER_SOUTH = Block.box(0.0, 0.0, 3.0, 16.0, 16.0, 15.0);
  private static final VoxelShape SHAPE_LOWER_WEST = Block.box(1.0, 0.0, 0.0, 13.0, 16.0, 16.0);
  private static final VoxelShape SHAPE_LOWER_EAST = Block.box(3.0, 0.0, 0.0, 15.0, 16.0, 16.0);

  public CollectorStationBlock(final Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(STATE, CollectorStationStatus.NO_POWER)
            .setValue(HALF, DoubleBlockHalf.LOWER));
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    Direction facing = state.getValue(FACING);

    if (isUpperBlock(state)) {
      return switch (facing) {
        case SOUTH -> SHAPE_UPPER_SOUTH;
        case WEST -> SHAPE_UPPER_WEST;
        case EAST -> SHAPE_UPPER_EAST;
        default -> SHAPE_UPPER_NORTH;
      };
    }

    return switch (facing) {
      case SOUTH -> SHAPE_LOWER_SOUTH;
      case WEST -> SHAPE_LOWER_WEST;
      case EAST -> SHAPE_LOWER_EAST;
      default -> SHAPE_LOWER_NORTH;
    };
  }

  @Override
  public RenderShape getRenderShape(BlockState blockState) {
    return isUpperBlock(blockState) ? RenderShape.INVISIBLE : RenderShape.MODEL;
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
    if (isUpperBlock(blockState)) {
      return null;
    }
    return createTickerHelper(
        blockEntityType, CollectorStationBlockEntity.TYPE, CollectorStationBlockEntity::tick);
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

    if (isUpperBlock(blockState)) {
      blockPos = blockPos.below();
    }

    if (level.getBlockEntity(blockPos) instanceof CollectorStationBlockEntity stationEntity) {
      openCollectorStationMenu(player, stationEntity);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public void setPlacedBy(
      Level level,
      BlockPos blockPos,
      BlockState blockState,
      LivingEntity placer,
      ItemStack itemStack) {
    super.setPlacedBy(level, blockPos, blockState, placer, itemStack);
    if (!level.isClientSide) {
      placeMultiBlock(level, blockPos, blockState, this);
    }
  }

  private void openCollectorStationMenu(Player player, CollectorStationBlockEntity stationEntity) {
    MenuManager.openMenu(player, stationEntity);
  }

  @Override
  public void onRemove(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      BlockState newState,
      boolean isMoving) {
    if (!blockState.is(newState.getBlock())) {
      if (!isUpperBlock(blockState)) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof CollectorStationBlockEntity stationEntity) {
          Containers.dropContents(level, blockPos, stationEntity.getContainer());
        }
      }
      removeMultiBlock(level, blockPos, blockState);
      super.onRemove(blockState, level, blockPos, newState, isMoving);
    }
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return isUpperBlock(blockState) ? null : new CollectorStationBlockEntity(blockPos, blockState);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    if (context.getLevel().getBlockState(context.getClickedPos().above()).canBeReplaced()) {
      return this.defaultBlockState()
          .setValue(FACING, context.getHorizontalDirection().getOpposite())
          .setValue(STATE, CollectorStationStatus.NO_POWER)
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
    builder.add(FACING, STATE, HALF);
  }
}
