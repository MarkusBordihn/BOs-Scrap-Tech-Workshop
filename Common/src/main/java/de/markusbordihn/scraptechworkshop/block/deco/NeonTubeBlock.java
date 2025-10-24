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

import de.markusbordihn.scraptechworkshop.block.MultiPlaceBlock;
import de.markusbordihn.scraptechworkshop.item.ModBlockItems;
import de.markusbordihn.scraptechworkshop.item.tool.ScrapMultitoolItem;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NeonTubeBlock extends MultiPlaceBlock {

  public static final String ID = "neon_tube";
  public static final BooleanProperty LIT = BlockStateProperties.LIT;
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
  public static final BooleanProperty HORIZONTAL = BooleanProperty.create("horizontal");

  private static final VoxelShape SHAPE_FLOOR_NORTH = Block.box(6.5, 0.0, 0.0, 9.5, 3.0, 16.0);
  private static final VoxelShape SHAPE_FLOOR_WEST = Block.box(0.0, 0.0, 6.5, 16.0, 3.0, 9.5);
  private static final VoxelShape SHAPE_CEILING_NORTH = Block.box(6.5, 13.0, 0.0, 9.5, 16.0, 16.0);
  private static final VoxelShape SHAPE_CEILING_WEST = Block.box(0.0, 13.0, 6.5, 16.0, 16.0, 9.5);

  // Wall shapes - vertical orientation
  private static final VoxelShape SHAPE_WALL_NORTH_VERTICAL =
      Block.box(6.5, 0.0, 13.0, 9.5, 16.0, 16.0);
  private static final VoxelShape SHAPE_WALL_SOUTH_VERTICAL =
      Block.box(6.5, 0.0, 0.0, 9.5, 16.0, 3.0);
  private static final VoxelShape SHAPE_WALL_WEST_VERTICAL =
      Block.box(13.0, 0.0, 6.5, 16.0, 16.0, 9.5);
  private static final VoxelShape SHAPE_WALL_EAST_VERTICAL =
      Block.box(0.0, 0.0, 6.5, 3.0, 16.0, 9.5);

  // Wall shapes - horizontal orientation
  private static final VoxelShape SHAPE_WALL_NORTH_HORIZONTAL =
      Block.box(0.0, 6.5, 13.0, 16.0, 9.5, 16.0);
  private static final VoxelShape SHAPE_WALL_SOUTH_HORIZONTAL =
      Block.box(0.0, 6.5, 0.0, 16.0, 9.5, 3.0);
  private static final VoxelShape SHAPE_WALL_WEST_HORIZONTAL =
      Block.box(13.0, 6.5, 0.0, 16.0, 9.5, 16.0);
  private static final VoxelShape SHAPE_WALL_EAST_HORIZONTAL =
      Block.box(0.0, 6.5, 0.0, 3.0, 9.5, 16.0);

  public NeonTubeBlock(final Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(LIT, Boolean.TRUE)
            .setValue(POWERED, Boolean.FALSE)
            .setValue(HORIZONTAL, Boolean.FALSE)
            .setValue(FACING, Direction.NORTH)
            .setValue(ATTACH_FACE, AttachFace.WALL));
  }

  @Override
  public VoxelShape getShape(
      BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
    AttachFace face = blockState.getValue(ATTACH_FACE);
    Direction direction = blockState.getValue(FACING);
    boolean horizontal = blockState.getValue(HORIZONTAL);

    return switch (face) {
      case FLOOR ->
          switch (direction) {
            case NORTH, SOUTH -> SHAPE_FLOOR_NORTH;
            case WEST, EAST -> SHAPE_FLOOR_WEST;
            default -> SHAPE_FLOOR_NORTH;
          };
      case CEILING ->
          switch (direction) {
            case NORTH, SOUTH -> SHAPE_CEILING_NORTH;
            case WEST, EAST -> SHAPE_CEILING_WEST;
            default -> SHAPE_CEILING_NORTH;
          };
      case WALL -> {
        if (horizontal) {
          yield switch (direction) {
            case NORTH -> SHAPE_WALL_NORTH_HORIZONTAL;
            case SOUTH -> SHAPE_WALL_SOUTH_HORIZONTAL;
            case WEST -> SHAPE_WALL_WEST_HORIZONTAL;
            case EAST -> SHAPE_WALL_EAST_HORIZONTAL;
            default -> SHAPE_WALL_NORTH_HORIZONTAL;
          };
        } else {
          yield switch (direction) {
            case NORTH -> SHAPE_WALL_NORTH_VERTICAL;
            case SOUTH -> SHAPE_WALL_SOUTH_VERTICAL;
            case WEST -> SHAPE_WALL_WEST_VERTICAL;
            case EAST -> SHAPE_WALL_EAST_VERTICAL;
            default -> SHAPE_WALL_NORTH_VERTICAL;
          };
        }
      }
    };
  }

  @Override
  public RenderShape getRenderShape(BlockState blockState) {
    return RenderShape.MODEL;
  }

  @Override
  public void animateTick(
      BlockState blockState, Level level, BlockPos blockPos, RandomSource random) {
    if (!blockState.getValue(LIT)) {
      return;
    }

    if (random.nextInt(100) == 0) {
      AttachFace face = blockState.getValue(ATTACH_FACE);
      Direction direction = blockState.getValue(FACING);

      double centerX = blockPos.getX() + 0.5;
      double centerY = blockPos.getY() + 0.5;
      double centerZ = blockPos.getZ() + 0.5;

      double particleX = centerX;
      double particleY = centerY;
      double particleZ = centerZ;
      double offsetAlongTube = (random.nextDouble() - 0.5) * 0.8;

      switch (face) {
        case FLOOR:
          particleY = blockPos.getY() + 0.15;
          if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            particleZ += offsetAlongTube;
            particleX += (random.nextDouble() - 0.5) * 0.15;
          } else {
            particleX += offsetAlongTube;
            particleZ += (random.nextDouble() - 0.5) * 0.15;
          }
          break;
        case CEILING:
          particleY = blockPos.getY() + 0.85;
          if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            particleZ += offsetAlongTube;
            particleX += (random.nextDouble() - 0.5) * 0.15;
          } else {
            particleX += offsetAlongTube;
            particleZ += (random.nextDouble() - 0.5) * 0.15;
          }
          break;
        case WALL:
          boolean horizontal = blockState.getValue(HORIZONTAL);
          if (horizontal) {
            // Horizontal orientation - particles along X or Z axis
            switch (direction) {
              case NORTH:
                particleZ = blockPos.getZ() + 0.85;
                particleX += offsetAlongTube;
                particleY += (random.nextDouble() - 0.5) * 0.15;
                break;
              case SOUTH:
                particleZ = blockPos.getZ() + 0.15;
                particleX += offsetAlongTube;
                particleY += (random.nextDouble() - 0.5) * 0.15;
                break;
              case WEST:
                particleX = blockPos.getX() + 0.85;
                particleZ += offsetAlongTube;
                particleY += (random.nextDouble() - 0.5) * 0.15;
                break;
              case EAST:
                particleX = blockPos.getX() + 0.15;
                particleZ += offsetAlongTube;
                particleY += (random.nextDouble() - 0.5) * 0.15;
                break;
              default:
                break;
            }
          } else {
            // Vertical orientation - particles along Y axis
            particleY += offsetAlongTube;
            switch (direction) {
              case NORTH:
                particleZ = blockPos.getZ() + 0.85;
                particleX += (random.nextDouble() - 0.5) * 0.15;
                break;
              case SOUTH:
                particleZ = blockPos.getZ() + 0.15;
                particleX += (random.nextDouble() - 0.5) * 0.15;
                break;
              case WEST:
                particleX = blockPos.getX() + 0.85;
                particleZ += (random.nextDouble() - 0.5) * 0.15;
                break;
              case EAST:
                particleX = blockPos.getX() + 0.15;
                particleZ += (random.nextDouble() - 0.5) * 0.15;
                break;
              default:
                break;
            }
          }
          break;
      }

      level.addParticle(ParticleTypes.END_ROD, particleX, particleY, particleZ, 0.0, 0.0, 0.0);
    }

    if (random.nextInt(800) == 0) {
      level.playLocalSound(
          blockPos.getX() + 0.5,
          blockPos.getY() + 0.5,
          blockPos.getZ() + 0.5,
          SoundEvents.BEACON_AMBIENT,
          SoundSource.BLOCKS,
          0.05F,
          random.nextFloat() * 0.1F + 1.8F,
          false);
    }
  }

  @Override
  public BlockState rotateBlock(BlockState blockState) {
    AttachFace attachFace = blockState.getValue(ATTACH_FACE);

    if (attachFace == AttachFace.WALL) {
      boolean isHorizontal = blockState.getValue(HORIZONTAL);
      return blockState.setValue(HORIZONTAL, !isHorizontal);
    } else {
      Direction currentFacing = blockState.getValue(FACING);
      Direction newFacing = currentFacing.getClockWise();
      return blockState.setValue(FACING, newFacing);
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
    ItemStack heldItem = player.getItemInHand(interactionHand);

    if (heldItem.getItem() instanceof ScrapMultitoolItem
        || heldItem.is(ModBlockItems.NEON_TUBE.get().asItem())) {
      if (!level.isClientSide) {
        level.setBlock(blockPos, this.rotateBlock(blockState), 3);
      }
      return InteractionResult.sidedSuccess(level.isClientSide);
    }

    return InteractionResult.PASS;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState blockState = super.getStateForPlacement(context);
    Level level = context.getLevel();
    BlockPos blockPos = context.getClickedPos();
    if (blockState.getValue(ATTACH_FACE) == AttachFace.WALL) {
      blockState = blockState.setValue(HORIZONTAL, false);
    }

    boolean hasPoweredNearby = hasPoweredNeonTubeNearby(level, blockPos, new HashSet<>());
    boolean hasRedstoneNearby = hasRedstoneSourceInArea(level, blockPos, new HashSet<>());
    boolean powered = hasPoweredNearby || hasRedstoneNearby;
    boolean lit = powered ? level.hasNeighborSignal(blockPos) : true;

    return blockState.setValue(POWERED, powered).setValue(LIT, lit);
  }

  private boolean hasPoweredNeonTubeNearby(Level level, BlockPos startPos, Set<BlockPos> checked) {
    if (checked.contains(startPos)) {
      return false;
    }
    checked.add(startPos);

    for (Direction direction : Direction.values()) {
      BlockPos neighborPos = startPos.relative(direction);
      BlockState neighborState = level.getBlockState(neighborPos);

      if (neighborState.getBlock() instanceof NeonTubeBlock) {
        if (neighborState.getValue(POWERED)) {
          return true;
        }
        if (checked.size() < 256 && hasPoweredNeonTubeNearby(level, neighborPos, checked)) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean hasRedstoneSourceInArea(Level level, BlockPos startPos, Set<BlockPos> checked) {
    if (checked.contains(startPos)) {
      return false;
    }
    checked.add(startPos);

    for (int xOffset = -1; xOffset <= 1; xOffset++) {
      for (int yOffset = -1; yOffset <= 1; yOffset++) {
        for (int zOffset = -1; zOffset <= 1; zOffset++) {
          BlockPos checkPos = startPos.offset(xOffset, yOffset, zOffset);
          if (checkPos.equals(startPos)) {
            continue;
          }

          BlockState checkState = level.getBlockState(checkPos);
          if (checkState.isSignalSource()) {
            return true;
          }

          for (Direction direction : Direction.values()) {
            if (level.getSignal(checkPos, direction) > 0) {
              return true;
            }
          }
        }
      }
    }

    for (Direction direction : Direction.values()) {
      BlockPos neighborPos = startPos.relative(direction);
      BlockState neighborState = level.getBlockState(neighborPos);
      if (neighborState.getBlock() instanceof NeonTubeBlock
          && checked.size() < 256
          && hasRedstoneSourceInArea(level, neighborPos, checked)) {
        return true;
      }
    }

    return false;
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
      updateChain(level, blockPos);
    }
  }

  private void updateChain(Level level, BlockPos startPos) {
    Queue<BlockPos> toCheck = new LinkedList<>();
    Set<BlockPos> checked = new HashSet<>();
    toCheck.add(startPos);
    checked.add(startPos);

    boolean hasRedstoneInChain = hasRedstoneSourceInArea(level, startPos, new HashSet<>());
    boolean anyBlockPowered = false;

    if (hasRedstoneInChain) {
      for (BlockPos pos : getAllConnectedBlocks(level, startPos)) {
        if (level.hasNeighborSignal(pos)) {
          anyBlockPowered = true;
          break;
        }
      }
    }

    while (!toCheck.isEmpty()) {
      BlockPos currentPos = toCheck.poll();
      BlockState currentState = level.getBlockState(currentPos);

      if (currentState.getBlock() instanceof NeonTubeBlock) {
        boolean powered = hasRedstoneInChain;
        boolean shouldBeLit = powered ? anyBlockPowered : true;

        if (currentState.getValue(POWERED) != powered
            || currentState.getValue(LIT) != shouldBeLit) {
          level.setBlock(
              currentPos, currentState.setValue(POWERED, powered).setValue(LIT, shouldBeLit), 3);
        }

        for (Direction direction : Direction.values()) {
          BlockPos neighborPos = currentPos.relative(direction);
          if (!checked.contains(neighborPos)) {
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof NeonTubeBlock) {
              toCheck.add(neighborPos);
              checked.add(neighborPos);
            }
          }
        }
      }
    }
  }

  private Set<BlockPos> getAllConnectedBlocks(Level level, BlockPos startPos) {
    Set<BlockPos> connected = new HashSet<>();
    Queue<BlockPos> toCheck = new LinkedList<>();
    toCheck.add(startPos);
    connected.add(startPos);

    while (!toCheck.isEmpty()) {
      BlockPos currentPos = toCheck.poll();

      for (Direction direction : Direction.values()) {
        BlockPos neighborPos = currentPos.relative(direction);
        if (!connected.contains(neighborPos)) {
          BlockState neighborState = level.getBlockState(neighborPos);
          if (neighborState.getBlock() instanceof NeonTubeBlock) {
            toCheck.add(neighborPos);
            connected.add(neighborPos);
          }
        }
      }
    }

    return connected;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(LIT, POWERED, HORIZONTAL);
  }
}
