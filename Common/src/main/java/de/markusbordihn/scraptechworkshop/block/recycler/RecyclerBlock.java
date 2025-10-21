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

package de.markusbordihn.scraptechworkshop.block.recycler;

import de.markusbordihn.scraptechworkshop.block.entity.recycler.RecyclerBlockEntity;
import de.markusbordihn.scraptechworkshop.block.entity.recycler.RecyclerSlots;
import de.markusbordihn.scraptechworkshop.data.recycler.RecyclerStatus;
import de.markusbordihn.scraptechworkshop.menu.MenuManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RecyclerBlock extends BaseEntityBlock {

  public static final String ID = "recycler";
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
  public static final EnumProperty<RecyclerStatus> STATUS =
      EnumProperty.create("status", RecyclerStatus.class);
  private static final VoxelShape SHAPE_NORTH_SOUTH =
      Block.box(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 16.0D);
  private static final VoxelShape SHAPE_EAST_WEST =
      Block.box(0.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);

  public RecyclerBlock(final Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(POWERED, Boolean.FALSE)
            .setValue(STATUS, RecyclerStatus.IDLE));
  }

  public static void updateStatus(
      final Level level, final BlockPos blockPos, final RecyclerStatus newStatus) {
    BlockState currentState = level.getBlockState(blockPos);
    if (currentState.getBlock() instanceof RecyclerBlock
        && currentState.getValue(STATUS) != newStatus) {
      level.setBlock(blockPos, currentState.setValue(STATUS, newStatus), Block.UPDATE_ALL);
    }
  }

  @Override
  public VoxelShape getShape(
      BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
    Direction facing = blockState.getValue(FACING);
    return switch (facing) {
      case NORTH, SOUTH -> SHAPE_NORTH_SOUTH;
      case EAST, WEST -> SHAPE_EAST_WEST;
      default -> SHAPE_NORTH_SOUTH;
    };
  }

  @Override
  public RenderShape getRenderShape(BlockState blockState) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new RecyclerBlockEntity(blockPos, blockState);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
    return createTickerHelper(blockEntityType, RecyclerBlockEntity.TYPE, RecyclerBlockEntity::tick);
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
        && level.getBlockEntity(blockPos) instanceof RecyclerBlockEntity recyclerBlockEntity) {
      openRecyclerMenu(player, recyclerBlockEntity);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  private void openRecyclerMenu(Player player, RecyclerBlockEntity recyclerBlockEntity) {
    MenuManager.openMenu(player, recyclerBlockEntity);
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
      if (blockEntity instanceof RecyclerBlockEntity recyclerBlockEntity) {
        Containers.dropContents(level, blockPos, recyclerBlockEntity.getContainer());
      }
      super.onRemove(blockState, level, blockPos, newState, isMoving);
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState()
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(POWERED, Boolean.FALSE)
        .setValue(STATUS, RecyclerStatus.IDLE);
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
    builder.add(FACING, POWERED, STATUS);
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState blockState) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (blockEntity instanceof RecyclerBlockEntity recyclerBlockEntity) {
      return recyclerBlockEntity.getRedstoneSignal();
    }
    return 0;
  }

  @Override
  public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
    if (level.isClientSide || !(entity instanceof ItemEntity itemEntity)) {
      return;
    }

    if (!(level.getBlockEntity(blockPos) instanceof RecyclerBlockEntity recyclerBlockEntity)) {
      return;
    }

    ItemStack droppedStack = itemEntity.getItem();
    if (droppedStack.isEmpty()) {
      return;
    }

    ItemStack inputStack = recyclerBlockEntity.getContainer().getItem(RecyclerSlots.INPUT_SLOT);
    if (inputStack.isEmpty()) {
      recyclerBlockEntity.getContainer().setItem(RecyclerSlots.INPUT_SLOT, droppedStack.copy());
      itemEntity.discard();
    } else if (ItemStack.isSameItemSameTags(inputStack, droppedStack)) {
      int spaceAvailable = inputStack.getMaxStackSize() - inputStack.getCount();
      if (spaceAvailable > 0) {
        int toTransfer = Math.min(spaceAvailable, droppedStack.getCount());
        inputStack.grow(toTransfer);
        droppedStack.shrink(toTransfer);
        if (droppedStack.isEmpty()) {
          itemEntity.discard();
        }
      }
    }
  }
}
