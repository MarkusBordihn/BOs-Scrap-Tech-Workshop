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

import de.markusbordihn.scraptechworkshop.config.RecyclerConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record RecyclerRecipe(
    ResourceLocation id,
    RecyclerMatch match,
    RecyclerOutput primaryOutput,
    List<RecyclerByproduct> byproducts,
    int processTime,
    int weight)
    implements Recipe<Container> {

  private static final int INPUT_SLOT = 0;

  public RecyclerRecipe {
    if (byproducts == null) {
      byproducts = new ArrayList<>();
    }
  }

  @Override
  public boolean matches(Container container, Level level) {
    if (container.isEmpty()) {
      return false;
    }
    ItemStack inputStack = container.getItem(INPUT_SLOT);
    return !inputStack.isEmpty() && match.matches(inputStack);
  }

  @Override
  public ItemStack assemble(Container container, RegistryAccess registryAccess) {
    return getResultItem(registryAccess);
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return true;
  }

  @Override
  public ItemStack getResultItem(RegistryAccess registryAccess) {
    return primaryOutput.createStack();
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RecyclerRecipeSerializer.INSTANCE;
  }

  @Override
  public RecipeType<?> getType() {
    return RecyclerRecipeType.INSTANCE;
  }

  public boolean matchesInput(ItemStack stack) {
    return match.matches(stack);
  }

  public List<ItemStack> getOutputsForInput(ItemStack inputStack) {
    List<ItemStack> outputs = new ArrayList<>();

    outputs.add(primaryOutput.createStackForInput(inputStack));

    if (RecyclerConfig.isMultiByproductMode()) {
      int addedByproducts = 0;
      for (RecyclerByproduct byproduct : byproducts) {
        if (addedByproducts >= RecyclerConfig.maxByproductsPerOperation) {
          break;
        }
        if (byproduct.shouldProduce()) {
          outputs.add(byproduct.createStack());
          addedByproducts++;
        }
      }
    } else {
      if (!byproducts.isEmpty()) {
        for (RecyclerByproduct byproduct : byproducts) {
          if (byproduct.shouldProduce()) {
            outputs.add(byproduct.createStack());
            break;
          }
        }
      }
    }

    return outputs;
  }
}
