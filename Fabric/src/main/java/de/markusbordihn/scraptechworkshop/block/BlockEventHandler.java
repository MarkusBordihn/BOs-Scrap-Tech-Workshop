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

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.HitResult;

public class BlockEventHandler {

  public static void register() {
    UseBlockCallback.EVENT.register(
        (player, level, hand, hitResult) -> {
          if (level instanceof ServerLevel serverLevel
              && player instanceof ServerPlayer serverPlayer
              && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = hitResult.getBlockPos().relative(hitResult.getDirection());
            BlockEvents.handleBlockPlaceEvent(
                level.getBlockState(blockPos).getBlock(), blockPos, serverLevel, serverPlayer);
          }
          return InteractionResult.PASS;
        });

    PlayerBlockBreakEvents.AFTER.register(
        (level, player, blockPos, blockState, blockEntity) -> {
          if (level instanceof ServerLevel serverLevel
              && player instanceof ServerPlayer serverPlayer) {
            BlockEvents.handleBlockBreakEvent(
                blockState.getBlock(), blockPos, serverLevel, serverPlayer);
          }
        });
  }
}
