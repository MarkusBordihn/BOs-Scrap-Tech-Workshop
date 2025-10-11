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

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipe;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipeSelector;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecyclerRecipeHandler {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static RecyclerRecipe findRecipe(final Level level, final ItemStack inputStack) {
    if (level == null || inputStack.isEmpty()) {
      return null;
    }

    RecyclerRecipe recipe = RecyclerRecipeSelector.selectBestRecipe(level, inputStack).orElse(null);
    if (recipe == null) {
      log.debug(
          "No recycler recipe found for item: {} ({})",
          inputStack.getItem(),
          inputStack.getItem().getDescriptionId());
    }
    return recipe;
  }

  public static boolean canProcessRecipe(
      final RecyclerRecipe recipe, final ItemStack inputStack, final ItemStack[] items) {
    if (recipe == null || inputStack.isEmpty() || !recipe.matchesInput(inputStack)) {
      return false;
    }

    return canInsertOutputs(recipe.getOutputsForInput(inputStack), items);
  }

  public static void processRecipe(
      final RecyclerRecipe recipe, final ItemStack inputStack, final ItemStack[] items) {
    if (recipe == null || inputStack.isEmpty()) {
      return;
    }

    List<ItemStack> outputs = recipe.getOutputsForInput(inputStack);
    outputs.forEach(output -> insertOutput(output, items));
    inputStack.shrink(1);

    log.debug("Processed item with recipe: {} -> {} outputs", inputStack.getItem(), outputs.size());
  }

  private static boolean canInsertOutputs(final List<ItemStack> outputs, final ItemStack[] items) {
    ItemStack[] simulatedItems = new ItemStack[items.length];
    for (int i = 0; i < items.length; i++) {
      simulatedItems[i] = items[i].copy();
    }

    for (ItemStack output : outputs) {
      if (!tryInsertOutput(simulatedItems, output, true)) {
        return false;
      }
    }
    return true;
  }

  private static boolean tryInsertOutput(
      final ItemStack[] itemArray, final ItemStack output, final boolean isSimulation) {
    ItemStack remaining = output.copy();

    for (int i = RecyclerSlots.FIRST_OUTPUT_SLOT;
        i <= RecyclerSlots.LAST_OUTPUT_SLOT && !remaining.isEmpty();
        i++) {
      ItemStack slotStack = itemArray[i];

      if (slotStack.isEmpty()) {
        itemArray[i] = remaining.copy();
        remaining = ItemStack.EMPTY;
      } else if (ItemStack.isSameItemSameTags(slotStack, remaining)) {
        int canAdd = slotStack.getMaxStackSize() - slotStack.getCount();
        int toAdd = Math.min(canAdd, remaining.getCount());

        slotStack.grow(toAdd);
        remaining.shrink(toAdd);
      }
    }

    return remaining.isEmpty();
  }

  private static void insertOutput(final ItemStack output, final ItemStack[] items) {
    tryInsertOutput(items, output, false);
  }
}
