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

package de.markusbordihn.scraptechworkshop.block.scrap;

import de.markusbordihn.scraptechworkshop.config.ScrapPileConfig;
import de.markusbordihn.scraptechworkshop.data.ScrapPileVariant;
import de.markusbordihn.scraptechworkshop.loot.ScrapLootTables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ScrapPileBlock extends Block implements SimpleWaterloggedBlock {

  public static final String ID = "scrap_pile";
  public static final IntegerProperty SIZE = IntegerProperty.create("size", 1, 4);
  public static final EnumProperty<ScrapPileVariant> VARIANT =
      EnumProperty.create("variant", ScrapPileVariant.class);
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  private static final VoxelShape SHAPE_SIZE_1 = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
  private static final VoxelShape SHAPE_SIZE_2 = Block.box(1.5D, 0.0D, 1.5D, 14.5D, 4.0D, 14.5D);
  private static final VoxelShape SHAPE_SIZE_3 = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 4.0D, 15.0D);
  private static final VoxelShape SHAPE_SIZE_4 = Block.box(0.5D, 0.0D, 0.5D, 15.5D, 4.0D, 15.5D);

  public ScrapPileBlock(final Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(SIZE, 1)
            .setValue(VARIANT, ScrapPileVariant.MIXED)
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, Boolean.FALSE));
  }

  private static VoxelShape getShapeForSize(final int size) {
    return switch (size) {
      case 2 -> SHAPE_SIZE_2;
      case 3 -> SHAPE_SIZE_3;
      case 4 -> SHAPE_SIZE_4;
      default -> SHAPE_SIZE_1;
    };
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
    if (tool != null
        && ScrapPileConfig.silkTouchEnabled
        && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
      return Collections.singletonList(new ItemStack(this.asItem()));
    }

    List<ItemStack> drops = new ArrayList<>();
    ScrapPileVariant variant = state.getValue(VARIANT);
    int size = state.getValue(SIZE);
    RandomSource random = RandomSource.create();

    int fortuneLevel = 0;
    if (tool != null && ScrapPileConfig.fortuneEnabled) {
      fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
    }

    for (int i = 0; i < size; i++) {
      ItemStack scrap = ScrapLootTables.generateRandomScrap(variant, random);
      if (!scrap.isEmpty()) {
        drops.add(scrap);

        for (int j = 0; j < fortuneLevel; j++) {
          if (random.nextFloat() < 0.33f) {
            drops.add(scrap.copy());
          }
        }
      }
    }

    return drops;
  }

  @Override
  public VoxelShape getCollisionShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return getShapeForSize(state.getValue(SIZE));
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return getShapeForSize(state.getValue(SIZE));
  }

  @Override
  public InteractionResult use(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    if (!level.isClientSide) {
      if (player.isShiftKeyDown()) {
        return ScrapPileConfig.autoMergeEnabled
                && ScrapPileMerger.attemptMergeWithNeighbors(level, pos, state)
            ? InteractionResult.SUCCESS
            : InteractionResult.PASS;
      } else {
        ScrapPileCollector.handleScrapCollection(level, pos, state, player);
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
    if (!level.isClientSide
        && entity instanceof Player player
        && ScrapPileConfig.autoPickupEnabled
        && (ScrapPileConfig.autoPickupDelayTicks == 0
            || !ScrapPileCooldownManager.isPlayerOnCooldown(player))) {

      if (state.getValue(SIZE) == 1 || ScrapPileConfig.autoPickupDelayTicks == 0) {
        while (level.getBlockState(pos).getBlock() == this) {
          ScrapPileCollector.handleScrapCollection(level, pos, level.getBlockState(pos), player);
        }
      } else {
        ScrapPileCollector.handleScrapCollection(level, pos, state, player);
      }

      if (ScrapPileConfig.autoPickupDelayTicks > 0) {
        ScrapPileCooldownManager.setPlayerCooldown(player);
      }
    }
    super.stepOn(level, pos, state, entity);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    final FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
    return this.defaultBlockState()
        .setValue(SIZE, 1)
        .setValue(VARIANT, ScrapPileVariant.MIXED)
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
  }

  @Override
  public void onPlace(
      BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    if (!level.isClientSide
        && !isMoving
        && oldState.getBlock() != this
        && ScrapPileConfig.autoMergeEnabled) {
      level.scheduleTick(pos, this, 1);
    }
  }

  @Override
  public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (ScrapPileConfig.autoMergeEnabled) {
      ScrapPileMerger.attemptMergeWithNeighbors(level, pos, state);
    }
  }

  @Override
  public boolean isRandomlyTicking(BlockState state) {
    return ScrapPileConfig.decayEnabled;
  }

  @Override
  public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (ScrapPileConfig.decayEnabled && random.nextInt(ScrapPileConfig.decayChance) == 0) {
      ScrapPileDecay.handleDecay(state, level, pos, random);
    }
  }

  @Override
  public BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighborState,
      LevelAccessor level,
      BlockPos currentPos,
      BlockPos neighborPos) {
    if (state.getValue(WATERLOGGED)) {
      level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }
    return state;
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED)
        ? Fluids.WATER.getSource(false)
        : Fluids.EMPTY.defaultFluidState();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(SIZE, VARIANT, FACING, WATERLOGGED);
  }
}
