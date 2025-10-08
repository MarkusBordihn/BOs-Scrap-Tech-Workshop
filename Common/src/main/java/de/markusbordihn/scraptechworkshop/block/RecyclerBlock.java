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

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.entity.RecyclerBlockEntity;
import de.markusbordihn.scraptechworkshop.data.recycler.RecyclerStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public RecyclerBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(POWERED, Boolean.FALSE)
            .setValue(STATUS, RecyclerStatus.IDLE));
  }

  public static void updateStatus(Level level, BlockPos pos, RecyclerStatus newStatus) {
    BlockState currentState = level.getBlockState(pos);
    if (currentState.getBlock() instanceof RecyclerBlock
        && currentState.getValue(STATUS) != newStatus) {
      level.setBlock(pos, currentState.setValue(STATUS, newStatus), Block.UPDATE_ALL);
    }
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    Direction facing = state.getValue(FACING);
    return switch (facing) {
      case NORTH, SOUTH -> SHAPE_NORTH_SOUTH;
      case EAST, WEST -> SHAPE_EAST_WEST;
      default -> SHAPE_NORTH_SOUTH;
    };
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new RecyclerBlockEntity(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> blockEntityType) {
    return createTickerHelper(blockEntityType, RecyclerBlockEntity.TYPE, RecyclerBlockEntity::tick);
  }

  @Override
  public InteractionResult use(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    if (!level.isClientSide
        && level.getBlockEntity(pos) instanceof RecyclerBlockEntity recyclerBlockEntity) {
      openRecyclerMenu(player, recyclerBlockEntity);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  private void openRecyclerMenu(Player player, RecyclerBlockEntity recyclerBlockEntity) {
    if (Constants.IS_FABRIC) {
      try {
        Class<?> fabricFactoryClass;
        try {
          fabricFactoryClass =
              Class.forName("de.markusbordihn.scraptechworkshop.menu.BlockBaseScreenHandler");
        } catch (ClassNotFoundException e) {
          fabricFactoryClass =
              Class.forName("de.markusbordihn.scraptechworkshop.menu.BaseScreenHandler");
        }

        Object fabricFactory =
            fabricFactoryClass.getConstructor(BlockEntity.class).newInstance(recyclerBlockEntity);
        player.openMenu((MenuProvider) fabricFactory);
      } catch (Exception e) {
        log.error("Failed to open Recycler menu on Fabric: {}", e.getMessage());
        player.openMenu(recyclerBlockEntity);
      }
    } else if (Constants.IS_FORGE) {
      try {
        Class<?> forgeMenuProviderClass =
            Class.forName(
                "de.markusbordihn.scraptechworkshop.block.entity.ForgeRecyclerMenuProvider");
        Object menuProvider =
            forgeMenuProviderClass
                .getConstructor(RecyclerBlockEntity.class)
                .newInstance(recyclerBlockEntity);

        Class<?> networkHooksClass = Class.forName("net.minecraftforge.network.NetworkHooks");
        try {
          java.lang.reflect.Method openGuiMethod =
              networkHooksClass.getMethod(
                  "openGui", ServerPlayer.class, MenuProvider.class, BlockPos.class);

          if (player instanceof ServerPlayer serverPlayer) {
            openGuiMethod.invoke(
                null, serverPlayer, menuProvider, recyclerBlockEntity.getBlockPos());
          }
        } catch (NoSuchMethodException e1) {
          // Fallback to openScreen method
          java.lang.reflect.Method openScreenMethod =
              networkHooksClass.getMethod(
                  "openScreen", ServerPlayer.class, MenuProvider.class, BlockPos.class);

          if (player instanceof ServerPlayer serverPlayer) {
            openScreenMethod.invoke(
                null, serverPlayer, menuProvider, recyclerBlockEntity.getBlockPos());
          }
        }
      } catch (Exception e) {
        log.error(
            "[RECYCLER] Failed to use Forge NetworkHooks, falling back to standard: {}",
            e.getMessage());
        player.openMenu(recyclerBlockEntity);
      }
    } else {
      player.openMenu(recyclerBlockEntity);
    }
  }

  @Override
  public void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof RecyclerBlockEntity recyclerBlockEntity) {
        Containers.dropContents(level, pos, recyclerBlockEntity.getContainer());
      }
      super.onRemove(state, level, pos, newState, isMoving);
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
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, POWERED, STATUS);
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
    BlockEntity blockEntity = level.getBlockEntity(pos);
    if (blockEntity instanceof RecyclerBlockEntity recyclerBlockEntity) {
      return recyclerBlockEntity.getRedstoneSignal();
    }
    return 0;
  }
}
