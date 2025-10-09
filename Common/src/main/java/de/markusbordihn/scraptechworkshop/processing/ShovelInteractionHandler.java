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

package de.markusbordihn.scraptechworkshop.processing;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ShovelInteractionHandler {

  public static InteractionResult processInteraction(final UseOnContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState state = level.getBlockState(pos);

    if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
      return extinguishCampfire(context, level, pos, state);
    }

    return InteractionResult.PASS;
  }

  public static InteractionResult createPath(
      final UseOnContext context,
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState) {
    BlockState aboveState = level.getBlockState(blockPos.above());
    if (!aboveState.isAir()) {
      return InteractionResult.PASS;
    }

    BlockState newState = Blocks.DIRT_PATH.defaultBlockState();
    Player player = context.getPlayer();

    level.playSound(player, blockPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);

    if (!level.isClientSide) {
      level.setBlock(blockPos, newState, 11);
      level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, newState));

      if (player instanceof ServerPlayer serverPlayer) {
        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(
            serverPlayer, blockPos, context.getItemInHand());
      }
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  private static InteractionResult extinguishCampfire(
      final UseOnContext context,
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState) {
    Player player = context.getPlayer();

    if (!level.isClientSide) {
      level.levelEvent(null, 1009, blockPos, 0);
    }

    CampfireBlock.dowse(player, level, blockPos, blockState);
    BlockState newState = blockState.setValue(CampfireBlock.LIT, false);
    level.setBlock(blockPos, newState, 11);

    return InteractionResult.sidedSuccess(level.isClientSide);
  }
}
