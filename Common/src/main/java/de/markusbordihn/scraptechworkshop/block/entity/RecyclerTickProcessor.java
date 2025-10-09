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

package de.markusbordihn.scraptechworkshop.block.entity;

import de.markusbordihn.scraptechworkshop.block.RecyclerBlock;
import de.markusbordihn.scraptechworkshop.data.recycler.RecyclerStatus;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RecyclerTickProcessor {
  private static final int NO_RECIPE_COOLDOWN = 40;
  private static final int DONE_DISPLAY_TIME = 20;
  private static final int PROGRESS_DECAY_RATE = 2;

  public static TickResult processNoRecipeStatus(final RecyclerState state) {
    state.noRecipeTimer--;
    if (state.noRecipeTimer <= 0) {
      return new TickResult(RecyclerStatus.IDLE, true);
    }
    return new TickResult(RecyclerStatus.NO_RECIPE, false);
  }

  public static TickResult processDoneStatus(final RecyclerState state) {
    state.doneTimer--;
    if (state.doneTimer <= 0) {
      state.currentRecipe = RecyclerRecipeHandler.findRecipe(state.level, state.getInputStack());
      if (state.currentRecipe != null
          && RecyclerRecipeHandler.canProcessRecipe(
              state.currentRecipe, state.getInputStack(), state.items)) {
        state.progress = 0;
        return new TickResult(RecyclerStatus.WORKING, true);
      } else {
        return new TickResult(RecyclerStatus.IDLE, true);
      }
    }
    return new TickResult(RecyclerStatus.DONE, false);
  }

  public static TickResult processActiveStatus(
      final RecyclerState state, final BlockState blockState) {
    boolean hasChanged = false;
    RecyclerStatus newStatus = blockState.getValue(RecyclerBlock.STATUS);

    if (state.currentRecipe == null
        || !RecyclerRecipeHandler.canProcessRecipe(
            state.currentRecipe, state.getInputStack(), state.items)) {
      state.currentRecipe = RecyclerRecipeHandler.findRecipe(state.level, state.getInputStack());

      if (state.currentRecipe == null && !state.getInputStack().isEmpty()) {
        ejectInputItem(state, blockState);
        state.noRecipeTimer = NO_RECIPE_COOLDOWN;
        return new TickResult(RecyclerStatus.NO_RECIPE, true);
      }

      state.progress = 0;
      hasChanged = true;
    }

    if (state.currentRecipe != null
        && RecyclerRecipeHandler.canProcessRecipe(
            state.currentRecipe, state.getInputStack(), state.items)) {
      // Apply speed multiplier bonus from upgrade items
      state.progress += state.speedMultiplier;
      newStatus = RecyclerStatus.WORKING;
      hasChanged = true;

      if (state.progress >= state.maxProgress) {
        RecyclerRecipeHandler.processRecipe(
            state.currentRecipe, state.getInputStack(), state.items);
        state.progress = 0;
        state.currentRecipe = null;
        newStatus = RecyclerStatus.DONE;
        state.doneTimer = DONE_DISPLAY_TIME;
      }
    } else if (state.currentRecipe != null
        && !RecyclerRecipeHandler.canProcessRecipe(
            state.currentRecipe, state.getInputStack(), state.items)) {
      ItemStack inputStack = state.getInputStack();
      if (!inputStack.isEmpty() && state.currentRecipe.matchesInput(inputStack)) {
        newStatus = RecyclerStatus.ERROR;
      } else {
        newStatus = RecyclerStatus.IDLE;
      }
      state.progress = 0;
      hasChanged = true;
    } else if (state.progress > 0) {
      if (RecyclerRecipeHandler.findRecipe(state.level, state.getInputStack()) == null) {
        newStatus = RecyclerStatus.IDLE;
      }
      state.progress = Math.max(0, state.progress - PROGRESS_DECAY_RATE);
      hasChanged = true;

      if (state.progress == 0) {
        newStatus = RecyclerStatus.IDLE;
      }
    } else {
      newStatus = RecyclerStatus.IDLE;
    }

    return new TickResult(newStatus, hasChanged);
  }

  private static void ejectInputItem(final RecyclerState state, final BlockState blockState) {
    ItemStack inputStack = state.getInputStack();
    if (inputStack.isEmpty()) {
      return;
    }

    Direction backDirection = blockState.getValue(RecyclerBlock.FACING).getOpposite();
    BlockPos ejectPos = state.pos.relative(backDirection);

    ItemStack itemToEject = inputStack.copy();
    itemToEject.setCount(1);
    inputStack.shrink(1);

    Containers.dropItemStack(
        state.level,
        ejectPos.getX() + 0.5,
        ejectPos.getY() + 0.5,
        ejectPos.getZ() + 0.5,
        itemToEject);

    addEjectionFeedback(state.level, state.pos, ejectPos);
  }

  private static void addEjectionFeedback(
      final Level level, final BlockPos recyclerPos, final BlockPos ejectPos) {
    if (level.isClientSide) {
      return;
    }

    level.playSound(null, recyclerPos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.5f, 1.2f);

    if (level instanceof ServerLevel serverLevel) {
      serverLevel.sendParticles(
          ParticleTypes.SMOKE,
          ejectPos.getX() + 0.5,
          ejectPos.getY() + 0.5,
          ejectPos.getZ() + 0.5,
          3,
          0.2,
          0.1,
          0.2,
          0.02);
    }
  }

  public static class TickResult {
    public final RecyclerStatus newStatus;
    public final boolean hasChanged;

    public TickResult(RecyclerStatus newStatus, boolean hasChanged) {
      this.newStatus = newStatus;
      this.hasChanged = hasChanged;
    }
  }

  public static class RecyclerState {
    public final Level level;
    public final BlockPos pos;
    public final ItemStack[] items;
    public final int maxProgress;
    public final int speedMultiplier;
    public int progress;
    public int noRecipeTimer;
    public int doneTimer;
    public RecyclerRecipe currentRecipe;

    public RecyclerState(
        Level level,
        BlockPos pos,
        ItemStack[] items,
        int progress,
        int maxProgress,
        int noRecipeTimer,
        int doneTimer,
        RecyclerRecipe currentRecipe,
        int speedMultiplier) {
      this.level = level;
      this.pos = pos;
      this.items = items;
      this.progress = progress;
      this.maxProgress = maxProgress;
      this.noRecipeTimer = noRecipeTimer;
      this.doneTimer = doneTimer;
      this.currentRecipe = currentRecipe;
      this.speedMultiplier = speedMultiplier;
    }

    public ItemStack getInputStack() {
      return items[RecyclerSlots.INPUT_SLOT];
    }
  }
}
