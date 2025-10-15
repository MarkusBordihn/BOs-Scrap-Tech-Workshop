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

package de.markusbordihn.scraptechworkshop.block.scrapbox;

import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ScrapBoxBlock extends Block {

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final IntegerProperty STACKING = IntegerProperty.create("stacking", 1, 4);

  private static final VoxelShape SHAPE_NORTH_1 = Block.box(8, 0, 0, 16, 8, 16);
  private static final VoxelShape SHAPE_SOUTH_1 = Block.box(0, 0, 0, 8, 8, 16);
  private static final VoxelShape SHAPE_WEST_1 = Block.box(0, 0, 0, 16, 8, 8);
  private static final VoxelShape SHAPE_EAST_1 = Block.box(0, 0, 8, 16, 8, 16);

  private static final VoxelShape SHAPE_NORTH_2 = Block.box(0, 0, 0, 16, 8, 16);
  private static final VoxelShape SHAPE_SOUTH_2 = Block.box(0, 0, 0, 16, 8, 16);
  private static final VoxelShape SHAPE_WEST_2 = Block.box(0, 0, 0, 16, 8, 16);
  private static final VoxelShape SHAPE_EAST_2 = Block.box(0, 0, 0, 16, 8, 16);

  private static final VoxelShape SHAPE_NORTH_3 = Block.box(0, 0, 0, 16, 16, 16);
  private static final VoxelShape SHAPE_SOUTH_3 = Block.box(0, 0, 0, 16, 16, 16);
  private static final VoxelShape SHAPE_WEST_3 = Block.box(0, 0, 0, 16, 16, 16);
  private static final VoxelShape SHAPE_EAST_3 = Block.box(0, 0, 0, 16, 16, 16);

  private static final VoxelShape SHAPE_FULL = Block.box(0, 0, 0, 16, 16, 16);

  protected final ScrapType scrapType;

  public ScrapBoxBlock(final Properties properties, final ScrapType scrapType) {
    super(properties);
    this.scrapType = scrapType;
    registerDefaultState(
        stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(STACKING, 1));
  }

  public ScrapBoxBlock(final ScrapType scrapType) {
    this(
        Properties.of()
            .mapColor(scrapType.getMapColor())
            .strength(1.5F, 3.0F)
            .sound(SoundType.WOOD)
            .noOcclusion()
            .lightLevel(scrapType.isLuminous() ? (state) -> 7 : (state) -> 0),
        scrapType);
  }

  public ScrapType getScrapType() {
    return scrapType;
  }

  @Override
  public InteractionResult use(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    ItemStack heldItem = player.getItemInHand(hand);
    int currentStacking = state.getValue(STACKING);

    // Handle scrap box stacking and unstacking.
    if (heldItem.isEmpty()) {
      if (currentStacking > 1) {
        if (!level.isClientSide) {
          if (!player.getAbilities().instabuild || player.isCrouching()) {
            level.setBlock(pos, state.setValue(STACKING, currentStacking - 1), Block.UPDATE_ALL);
          }
          popResource(level, pos, new ItemStack(this, 1));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
      }
      return InteractionResult.PASS;
    } else if (heldItem.getItem() instanceof BlockItem blockItem) {
      Block heldBlock = blockItem.getBlock();
      if (heldBlock instanceof ScrapBoxBlock scrapBoxBlock
          && scrapBoxBlock.getScrapType() == this.scrapType
          && currentStacking < 4) {
        if (!level.isClientSide) {
          level.setBlock(pos, state.setValue(STACKING, currentStacking + 1), Block.UPDATE_ALL);
          if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
          }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    if (!level.isClientSide && !player.isCreative()) {
      int stacking = state.getValue(STACKING);
      ItemStack itemStack = new ItemStack(this, stacking);
      popResource(level, pos, itemStack);
    }
    super.playerWillDestroy(level, pos, state, player);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, STACKING);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
    return switch (state.getValue(STACKING)) {
      case 1 ->
          switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH_1;
            case WEST -> SHAPE_WEST_1;
            case EAST -> SHAPE_EAST_1;
            default -> SHAPE_NORTH_1;
          };
      case 2 ->
          switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH_2;
            case WEST -> SHAPE_WEST_2;
            case EAST -> SHAPE_EAST_2;
            default -> SHAPE_NORTH_2;
          };
      case 3 ->
          switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH_3;
            case WEST -> SHAPE_WEST_3;
            case EAST -> SHAPE_EAST_3;
            default -> SHAPE_NORTH_3;
          };
      default -> SHAPE_FULL;
    };
  }
}
