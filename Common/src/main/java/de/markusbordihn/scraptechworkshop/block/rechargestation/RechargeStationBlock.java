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

package de.markusbordihn.scraptechworkshop.block.rechargestation;

import de.markusbordihn.scraptechworkshop.block.entity.rechargestation.RechargeStationBlockEntity;
import de.markusbordihn.scraptechworkshop.data.rechargestation.RechargeStationStatus;
import de.markusbordihn.scraptechworkshop.menu.MenuManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RechargeStationBlock extends BaseEntityBlock {

  public static final String ID = "recharge_station";
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<RechargeStationStatus> STATUS =
      EnumProperty.create("status", RechargeStationStatus.class);
  private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

  public RechargeStationBlock(final Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(STATUS, RechargeStationStatus.IDLE));
  }

  @Override
  public VoxelShape getShape(
      BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new RechargeStationBlockEntity(blockPos, blockState);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
    return createTickerHelper(
        blockEntityType, RechargeStationBlockEntity.TYPE, RechargeStationBlockEntity::tick);
  }

  @Override
  public InteractionResult use(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      BlockHitResult blockHitResult) {
    if (!level.isClientSide
        && level.getBlockEntity(blockPos)
            instanceof RechargeStationBlockEntity rechargeStationBlockEntity) {
      openRechargeStationMenu(player, rechargeStationBlockEntity);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  private void openRechargeStationMenu(
      final Player player, final RechargeStationBlockEntity rechargeStationBlockEntity) {
    MenuManager.openMenu(player, rechargeStationBlockEntity);
  }

  @Override
  public void onRemove(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      BlockState newState,
      boolean isMoving) {
    if (!blockState.is(newState.getBlock())) {
      BlockEntity blockEntity = level.getBlockEntity(blockPos);
      if (blockEntity instanceof RechargeStationBlockEntity rechargeStationBlockEntity) {
        Containers.dropContents(level, blockPos, rechargeStationBlockEntity.getContainer());
      }
      super.onRemove(blockState, level, blockPos, newState, isMoving);
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState()
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(STATUS, RechargeStationStatus.IDLE);
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
    builder.add(FACING, STATUS);
  }
}
