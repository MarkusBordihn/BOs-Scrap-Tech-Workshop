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

package de.markusbordihn.scraptechworkshop.recipe.recycler;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RecyclerRecipeSelector {

  private static final String SPECIFIC_FOLDER = "specific";
  private static final String COMMON_FOLDER = "common";
  private static final String FALLBACK_FOLDER = "fallback";

  private RecyclerRecipeSelector() {}

  public static Optional<RecyclerRecipe> selectBestRecipe(Level level, ItemStack inputStack) {
    if (level == null || inputStack.isEmpty()) {
      return Optional.empty();
    }

    Collection<RecyclerRecipe> allRecipes =
        level.getRecipeManager().getAllRecipesFor(RecyclerRecipeType.INSTANCE);

    List<RecyclerRecipe> matchingRecipes =
        allRecipes.stream()
            .filter(recipe -> recipe.matchesInput(inputStack))
            .collect(Collectors.toList());

    if (matchingRecipes.isEmpty()) {
      return Optional.empty();
    }

    List<RecyclerRecipe> specificRecipes =
        matchingRecipes.stream()
            .filter(recipe -> getFolderPriority(recipe) == 3)
            .collect(Collectors.toList());

    List<RecyclerRecipe> commonRecipes =
        matchingRecipes.stream()
            .filter(recipe -> getFolderPriority(recipe) == 2)
            .collect(Collectors.toList());

    List<RecyclerRecipe> fallbackRecipes =
        matchingRecipes.stream()
            .filter(recipe -> getFolderPriority(recipe) == 1)
            .collect(Collectors.toList());

    Optional<RecyclerRecipe> selected = selectBestFromGroup(specificRecipes);
    if (selected.isPresent()) {
      return selected;
    }

    selected = selectBestFromGroup(commonRecipes);
    if (selected.isPresent()) {
      return selected;
    }

    return selectBestFromGroup(fallbackRecipes);
  }

  private static int getFolderPriority(RecyclerRecipe recipe) {
    String path = recipe.getId().getPath();
    if (path.contains("/" + SPECIFIC_FOLDER + "/")) {
      return 3;
    } else if (path.contains("/" + COMMON_FOLDER + "/")) {
      return 2;
    } else if (path.contains("/" + FALLBACK_FOLDER + "/")) {
      return 1;
    }
    return 2;
  }

  private static Optional<RecyclerRecipe> selectBestFromGroup(List<RecyclerRecipe> recipes) {
    if (recipes.isEmpty()) {
      return Optional.empty();
    }

    return recipes.stream().max(Comparator.comparing(recipe -> recipe.getId().toString()));
  }

  public static boolean hasRecipeForInput(Level level, ItemStack inputStack) {
    return selectBestRecipe(level, inputStack).isPresent();
  }

  public static List<RecyclerRecipe> getAllMatchingRecipes(Level level, ItemStack inputStack) {
    if (level == null || inputStack.isEmpty()) {
      return List.of();
    }

    Collection<RecyclerRecipe> allRecipes =
        level.getRecipeManager().getAllRecipesFor(RecyclerRecipeType.INSTANCE);

    List<RecyclerRecipe> matchingRecipes =
        allRecipes.stream()
            .filter(recipe -> recipe.matchesInput(inputStack))
            .collect(Collectors.toList());

    return matchingRecipes.stream()
        .sorted(
            Comparator.comparingInt((RecyclerRecipe recipe) -> getFolderPriority(recipe))
                .reversed()
                .thenComparing(recipe -> recipe.getId().toString()))
        .collect(Collectors.toList());
  }
}
