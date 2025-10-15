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

package de.markusbordihn.scraptechworkshop.block.entity.recycler;

import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RecyclerState {
  public final Level level;
  public final BlockPos pos;
  public final NonNullList<ItemStack> items;
  public final int maxProgress;
  public final int speedMultiplier;
  public int progress;
  public int noRecipeTimer;
  public int doneTimer;
  public RecyclerRecipe currentRecipe;

  public RecyclerState(
      Level level,
      BlockPos pos,
      NonNullList<ItemStack> items,
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
    return items.get(RecyclerSlots.INPUT_SLOT);
  }
}
