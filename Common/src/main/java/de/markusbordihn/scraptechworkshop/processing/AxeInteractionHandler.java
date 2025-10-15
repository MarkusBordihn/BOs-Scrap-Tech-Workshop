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

import de.markusbordihn.scraptechworkshop.data.block.StrippableBlocks;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class AxeInteractionHandler {

  public static InteractionResult processAxeInteraction(final UseOnContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState state = level.getBlockState(pos);

    Optional<BlockState> strippedState = StrippableBlocks.getStrippedState(state);
    if (strippedState.isPresent()) {
      return stripLog(context, level, pos, strippedState.get());
    }

    Optional<BlockState> scrapedState = WeatheringCopper.getPrevious(state);
    if (scrapedState.isPresent()) {
      return scrapeCopper(context, level, pos, scrapedState.get());
    }

    Optional<BlockState> unwaxedState =
        Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock()))
            .map(block -> block.withPropertiesOf(state));
    return unwaxedState
        .map(blockState -> removeWax(context, level, pos, blockState))
        .orElse(InteractionResult.PASS);
  }

  public static InteractionResult processCycleableWood(
      final UseOnContext context, final BlockState blockState) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();

    Optional<BlockState> strippedState = StrippableBlocks.getStrippedState(blockState);
    if (strippedState.isPresent()) {
      return stripLog(context, level, pos, strippedState.get());
    }

    Optional<BlockState> unstrippedState = StrippableBlocks.getUnstrippedState(blockState);
    return unstrippedState
        .map(state -> addBark(context, level, pos, state))
        .orElse(InteractionResult.PASS);
  }

  private static InteractionResult addBark(
      final UseOnContext context,
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState) {
    Player player = context.getPlayer();
    level.playSound(player, blockPos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 0.9F);

    if (!level.isClientSide) {
      level.setBlock(blockPos, blockState, 11);
      level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, blockState));

      if (player instanceof ServerPlayer serverPlayer) {
        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(
            serverPlayer, blockPos, context.getItemInHand());
      }
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  private static InteractionResult stripLog(
      final UseOnContext context,
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState) {
    Player player = context.getPlayer();
    level.playSound(player, blockPos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

    if (!level.isClientSide) {
      level.setBlock(blockPos, blockState, 11);
      level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, blockState));

      if (player instanceof ServerPlayer serverPlayer) {
        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(
            serverPlayer, blockPos, context.getItemInHand());
      }
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  private static InteractionResult scrapeCopper(
      final UseOnContext context,
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState) {
    Player player = context.getPlayer();
    level.playSound(player, blockPos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
    level.levelEvent(player, 3005, blockPos, 0);

    if (!level.isClientSide) {
      level.setBlock(blockPos, blockState, 11);
      level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, blockState));

      if (player instanceof ServerPlayer serverPlayer) {
        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(
            serverPlayer, blockPos, context.getItemInHand());
      }
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  private static InteractionResult removeWax(
      final UseOnContext context,
      final Level level,
      final BlockPos blockPos,
      final BlockState blockState) {
    Player player = context.getPlayer();
    level.playSound(player, blockPos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
    level.levelEvent(player, 3004, blockPos, 0);

    if (!level.isClientSide) {
      level.setBlock(blockPos, blockState, 11);
      level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, blockState));

      if (player instanceof ServerPlayer serverPlayer) {
        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(
            serverPlayer, blockPos, context.getItemInHand());
      }
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }
}
