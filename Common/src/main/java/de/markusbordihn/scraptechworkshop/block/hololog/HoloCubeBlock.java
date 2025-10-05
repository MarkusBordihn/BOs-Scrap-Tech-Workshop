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

package de.markusbordihn.scraptechworkshop.block.hololog;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.entity.HoloCubeBlockEntity;
import de.markusbordihn.scraptechworkshop.data.hololog.HolologStatus;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloCubeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloCubeBlock extends BaseEntityBlock {

  public static final String ID = "holocube";
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<HolologStatus> STATUS =
      EnumProperty.create("status", HolologStatus.class);
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final VoxelShape SHAPE = Shapes.box(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875);

  public HoloCubeBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(STATUS, HolologStatus.READY));
  }

  public static void updateStatus(Level level, BlockPos pos, HolologStatus newStatus) {
    BlockState currentState = level.getBlockState(pos);
    if (currentState.getBlock() instanceof HoloCubeBlock
        && currentState.getValue(STATUS) != newStatus) {
      level.setBlock(pos, currentState.setValue(STATUS, newStatus), Block.UPDATE_ALL);
    }
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, STATUS);
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new HoloCubeBlockEntity(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    return level.isClientSide
        ? createTickerHelper(type, HoloCubeBlockEntity.TYPE, HoloCubeBlockEntity::tick)
        : null;
  }

  @Override
  public InteractionResult use(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {

    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    if (!(level.getBlockEntity(pos) instanceof HoloCubeBlockEntity blockEntity)) {
      log.warn("No HoloCubeBlockEntity found at {}", pos);
      return InteractionResult.FAIL;
    }

    ResourceLocation holologId = blockEntity.getHolologId();
    if (holologId == null) {
      player.displayClientMessage(Component.literal("No hololog data found!"), true);
      log.warn("[{}] No hololog ID found", blockEntity.getCubeUUID());
      return InteractionResult.FAIL;
    }

    HolologStatus currentStatus = state.getValue(STATUS);
    HolologStatus newStatus = currentStatus.cycle();

    // Check distance when activating (changing to PLAYING)
    if (currentStatus != HolologStatus.PLAYING && newStatus == HolologStatus.PLAYING) {
      double distance = player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      if (distance > 32.0 * 32.0) {
        player.displayClientMessage(
            Component.literal("You must be within 32 blocks to activate the hololog!"), true);
        log.debug(
            "[{}] Player {} too far away ({} blocks) to activate",
            blockEntity.getCubeUUID(),
            player.getName().getString(),
            Math.sqrt(distance));
        return InteractionResult.FAIL;
      }
    }

    log.debug(
        "[{}] Player {} changed status from {} to {}",
        blockEntity.getCubeUUID(),
        player.getName().getString(),
        currentStatus,
        newStatus);
    level.setBlock(pos, state.setValue(STATUS, newStatus), Block.UPDATE_ALL);
    return InteractionResult.SUCCESS;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState()
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(STATUS, HolologStatus.READY);
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }

  @Override
  public void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      if (level.isClientSide
          && level.getBlockEntity(pos) instanceof HoloCubeBlockEntity blockEntity) {
        blockEntity.cleanup();
      }
      super.onRemove(state, level, pos, newState, isMoving);
    }
  }

  @Override
  public void playerWillDestroy(
      Level level,
      BlockPos pos,
      BlockState state,
      net.minecraft.world.entity.player.Player player) {
    if (!level.isClientSide && !player.isCreative()) {
      if (level.getBlockEntity(pos) instanceof HoloCubeBlockEntity blockEntity) {
        ResourceLocation holologId = blockEntity.getHolologId();
        if (holologId != null) {
          ItemStack stack = HoloCubeItem.create(holologId);
          popResource(level, pos, stack);
        }
      }
    }
    super.playerWillDestroy(level, pos, state, player);
  }

  @Override
  public void setPlacedBy(
      Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);

    if (level.getBlockEntity(pos) instanceof HoloCubeBlockEntity blockEntity) {
      ResourceLocation holologId = HoloCubeItem.getHolologId(stack);
      if (holologId != null) {
        blockEntity.setHolologId(holologId);
        log.info("[{}] Placed with hololog: {}", blockEntity.getCubeUUID(), holologId);
      } else {
        log.warn("[{}] Placed without hololog ID", blockEntity.getCubeUUID());
      }
    }
  }
}
