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

package de.markusbordihn.scraptechworkshop.block.floatingscrapcollector;

import de.markusbordihn.scraptechworkshop.block.entity.floatingscrapcollector.FloatingScrapCollectorBlockEntity;
import de.markusbordihn.scraptechworkshop.data.floatingscrapcollector.FloatingScrapCollectorStatus;
import de.markusbordihn.scraptechworkshop.data.floatingscrapcollector.ScrapFilterType;
import de.markusbordihn.scraptechworkshop.menu.MenuManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FloatingScrapCollectorBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

  public static final String ID = "floating_scrap_collector";
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  public static final EnumProperty<FloatingScrapCollectorStatus> STATUS =
      EnumProperty.create("status", FloatingScrapCollectorStatus.class);
  public static final EnumProperty<ScrapFilterType> NET_TYPE =
      EnumProperty.create("net_type", ScrapFilterType.class);
  private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 10.0D, 14.0D);

  public FloatingScrapCollectorBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false)
            .setValue(STATUS, FloatingScrapCollectorStatus.EMPTY)
            .setValue(NET_TYPE, ScrapFilterType.NONE));
  }

  @Override
  public VoxelShape getShape(
      BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public RenderShape getRenderShape(BlockState blockState) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new FloatingScrapCollectorBlockEntity(blockPos, blockState);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> blockEntityType) {
    return createTickerHelper(
        blockEntityType,
        FloatingScrapCollectorBlockEntity.TYPE,
        FloatingScrapCollectorBlockEntity::tick);
  }

  @Override
  public InteractionResult use(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    if (!level.isClientSide
        && level.getBlockEntity(blockPos)
            instanceof FloatingScrapCollectorBlockEntity blockEntity) {
      MenuManager.openMenu(player, blockEntity);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public void onRemove(
      BlockState state, Level level, BlockPos blockPos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      BlockEntity blockEntity = level.getBlockEntity(blockPos);
      if (blockEntity instanceof FloatingScrapCollectorBlockEntity collector) {
        Containers.dropContents(level, blockPos, collector.getContainer());
      }
      super.onRemove(state, level, blockPos, newState, isMoving);
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    Player player = context.getPlayer();
    FluidState fluidState = level.getFluidState(pos);

    // Check if placed in water (like a boat - partially submerged)
    boolean isInWater =
        fluidState.getType() == Fluids.WATER || fluidState.getType() == Fluids.FLOWING_WATER;

    if (!isInWater) {
      // Send warning message to player
      if (player != null && !level.isClientSide) {
        player.displayClientMessage(
            Component.translatable(
                "block.scrap_tech_workshop.floating_scrap_collector.needs_water"),
            true);
      }
      return null;
    }

    return this.defaultBlockState()
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(WATERLOGGED, true)
        .setValue(STATUS, FloatingScrapCollectorStatus.EMPTY)
        .setValue(NET_TYPE, ScrapFilterType.NONE);
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  public BlockState updateShape(
      BlockState blockState,
      Direction direction,
      BlockState neighborState,
      LevelAccessor level,
      BlockPos currentPos,
      BlockPos neighborPos) {
    if (blockState.getValue(WATERLOGGED)) {
      level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }
    return super.updateShape(blockState, direction, neighborState, level, currentPos, neighborPos);
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
    builder.add(FACING, WATERLOGGED, STATUS, NET_TYPE);
  }

  @Override
  public void animateTick(
      BlockState blockState, Level level, BlockPos blockPos, RandomSource random) {
    FloatingScrapCollectorStatus status = blockState.getValue(STATUS);

    // Only spawn particles when WORKING
    if (status != FloatingScrapCollectorStatus.WORKING) {
      return;
    }

    double centerX = blockPos.getX() + 0.5;
    double centerY = blockPos.getY() + 0.3; // Just above water surface
    double centerZ = blockPos.getZ() + 0.5;

    // Spawn particles in a 2-3 block radius around the collector
    for (int i = 0; i < 2; i++) {
      // Random position in 2.5 block radius
      double angle = random.nextDouble() * Math.PI * 2;
      double distance = 1.0 + random.nextDouble() * 1.5;
      double offsetX = Math.cos(angle) * distance;
      double offsetZ = Math.sin(angle) * distance;

      double particleX = centerX + offsetX;
      double particleY = centerY + (random.nextDouble() * 0.2 - 0.1);
      double particleZ = centerZ + offsetZ;

      // Water splash particles (items being collected from surface)
      if (random.nextInt(3) == 0) {
        level.addParticle(ParticleTypes.SPLASH, particleX, particleY, particleZ, 0.0, 0.05, 0.0);
      }

      // Bubble particles (underwater activity)
      if (random.nextInt(4) == 0) {
        level.addParticle(
            ParticleTypes.BUBBLE,
            particleX,
            particleY - 0.2,
            particleZ,
            (centerX - particleX) * 0.02,
            0.02,
            (centerZ - particleZ) * 0.02);
      }

      // Occasional fishing bobber particles for collection effect
      if (random.nextInt(10) == 0) {
        level.addParticle(
            ParticleTypes.FISHING,
            particleX,
            particleY,
            particleZ,
            (centerX - particleX) * 0.05,
            0.0,
            (centerZ - particleZ) * 0.05);
      }
    }
  }
}
