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

package de.markusbordihn.scraptechworkshop.block.deco;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class ReplicantTestLampBlock extends Block {

  public static final String ID = "replicant_test_lamp";
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
  public static final BooleanProperty LIT = BlockStateProperties.LIT;
  public static final IntegerProperty MODE = IntegerProperty.create("mode", 0, 1);

  private static final VoxelShape SHAPE_MODE_0 = Block.box(6.0D, 0.0D, 4.0D, 10.0D, 11.0D, 11.0D);
  private static final VoxelShape SHAPE_MODE_1 = Block.box(6.0D, 0.0D, 4.0D, 10.0D, 12.0D, 11.0D);

  public ReplicantTestLampBlock(final Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(POWERED, Boolean.FALSE)
            .setValue(LIT, Boolean.TRUE)
            .setValue(MODE, 0));
  }

  @Override
  public VoxelShape getShape(
      BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
    return blockState.getValue(MODE) == 0 ? SHAPE_MODE_0 : SHAPE_MODE_1;
  }

  @Override
  public RenderShape getRenderShape(BlockState blockState) {
    return RenderShape.MODEL;
  }

  @Override
  public void animateTick(
      BlockState blockState, Level level, BlockPos blockPos, RandomSource random) {
    if (blockState.getValue(LIT) && random.nextInt(20) == 0) {
      double x = blockPos.getX() + 0.5D;
      double y = blockPos.getY() + 0.5D;
      double z = blockPos.getZ() + 0.5D;
      if (blockState.getValue(MODE) == 0) {
        level.addParticle(
            new DustParticleOptions(new Vector3f(1.0f, 0.3f, 0.3f), 0.5f),
            x + (random.nextDouble() - 0.5D) * 0.3D,
            y - 0.1D + random.nextDouble() * 0.2D,
            z + (random.nextDouble() - 0.5D) * 0.3D,
            0.0D,
            0.01D,
            0.0D);
      } else {
        level.addParticle(
            new DustParticleOptions(new Vector3f(1.0f, 0.6f, 0.2f), 0.7f),
            x + (random.nextDouble() - 0.5D) * 0.3D,
            y + random.nextDouble() * 0.4D,
            z + (random.nextDouble() - 0.5D) * 0.3D,
            0.0D,
            0.02D,
            0.0D);
      }
    }
  }

  @Override
  public InteractionResult use(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Player player,
      InteractionHand interactionHand,
      BlockHitResult hit) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    if (player.isShiftKeyDown()) {
      level.setBlock(
          blockPos,
          blockState.setValue(MODE, blockState.getValue(MODE) == 0 ? 1 : 0),
          Block.UPDATE_ALL);
      return InteractionResult.CONSUME;
    }

    if (!blockState.getValue(POWERED)) {
      level.setBlock(
          blockPos, blockState.setValue(LIT, !blockState.getValue(LIT)), Block.UPDATE_ALL);
      return InteractionResult.CONSUME;
    }

    return InteractionResult.PASS;
  }

  @Override
  public void neighborChanged(
      BlockState blockState,
      Level level,
      BlockPos blockPos,
      Block block,
      BlockPos fromPos,
      boolean isMoving) {
    if (!level.isClientSide) {
      boolean hasRedstoneSignal = level.hasNeighborSignal(blockPos);
      boolean isPowered = blockState.getValue(POWERED);

      if (hasRedstoneSignal != isPowered) {
        level.setBlock(blockPos, blockState.setValue(POWERED, hasRedstoneSignal), Block.UPDATE_ALL);
      }

      if (isPowered || hasRedstoneSignal) {
        boolean shouldBeLit = level.hasNeighborSignal(blockPos);
        if (blockState.getValue(LIT) != shouldBeLit) {
          level.setBlock(blockPos, blockState.setValue(LIT, shouldBeLit), Block.UPDATE_ALL);
        }
      }
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Level level = context.getLevel();
    BlockPos blockPos = context.getClickedPos();
    boolean hasRedstoneSignal = level.hasNeighborSignal(blockPos);

    return this.defaultBlockState()
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(POWERED, hasRedstoneSignal)
        .setValue(LIT, !hasRedstoneSignal);
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
    builder.add(FACING, POWERED, LIT, MODE);
  }
}
