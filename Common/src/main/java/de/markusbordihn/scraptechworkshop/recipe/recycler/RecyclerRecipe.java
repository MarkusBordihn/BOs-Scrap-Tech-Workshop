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
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
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

  public RecyclerRecipe(
      ResourceLocation id,
      RecyclerMatch match,
      RecyclerOutput primaryOutput,
      List<RecyclerByproduct> byproducts,
      int processTime,
      int weight) {
    this.id = id;
    this.match = match;
    this.primaryOutput = primaryOutput;
    this.byproducts = byproducts != null ? byproducts : new ArrayList<>();
    this.processTime = processTime;
    this.weight = weight;
  }

  @Override
  public boolean matches(Container container, Level level) {
    if (container.isEmpty()) {
      return false;
    }
    ItemStack inputStack = container.getItem(0);
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

    // Add primary output
    outputs.add(primaryOutput.createStackForInput(inputStack));

    // Add byproducts based on chance
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
      // Single byproduct mode - pick one randomly
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

  public static class RecyclerMatch {
    private final Item item;
    private final TagKey<Item> tag;

    public RecyclerMatch(Item item) {
      this.item = item;
      this.tag = null;
    }

    public RecyclerMatch(TagKey<Item> tag) {
      this.item = null;
      this.tag = tag;
    }

    public boolean matches(ItemStack stack) {
      if (item != null) {
        return stack.is(item);
      }
      if (tag != null) {
        return stack.is(tag);
      }
      return false;
    }

    public boolean isItemMatch() {
      return item != null;
    }

    public Item getItem() {
      return item;
    }

    public TagKey<Item> getTag() {
      return tag;
    }
  }

  public static class RecyclerOutput {
    private final Item item;
    private final int minCount;
    private final int maxCount;
    private final boolean durabilityScaling;

    public RecyclerOutput(Item item, int count) {
      this(item, count, count, false);
    }

    public RecyclerOutput(Item item, int minCount, int maxCount, boolean durabilityScaling) {
      this.item = item;
      this.minCount = minCount;
      this.maxCount = maxCount;
      this.durabilityScaling = durabilityScaling;
    }

    public ItemStack createStack() {
      int count =
          minCount == maxCount
              ? minCount
              : minCount + (int) (Math.random() * (maxCount - minCount + 1));
      return new ItemStack(item, count);
    }

    public ItemStack createStackForInput(ItemStack inputStack) {
      int baseCount =
          minCount == maxCount
              ? minCount
              : minCount + (int) (Math.random() * (maxCount - minCount + 1));

      if (durabilityScaling && RecyclerConfig.durabilityScaling && inputStack.isDamageableItem()) {
        double durabilityRatio =
            (double) (inputStack.getMaxDamage() - inputStack.getDamageValue())
                / inputStack.getMaxDamage();
        double scalingFactor = 0.25 + 0.75 * durabilityRatio;
        baseCount = Math.max(1, (int) Math.floor(baseCount * scalingFactor));
      }

      return new ItemStack(item, baseCount);
    }

    // Getter methods for serialization
    public Item getItem() {
      return item;
    }

    public int getMinCount() {
      return minCount;
    }

    public int getMaxCount() {
      return maxCount;
    }

    public boolean isDurabilityScaling() {
      return durabilityScaling;
    }
  }

  public static class RecyclerByproduct {
    private final Item item;
    private final double chance;
    private final int minCount;
    private final int maxCount;

    public RecyclerByproduct(Item item, double chance) {
      this(item, chance, 1, 1);
    }

    public RecyclerByproduct(Item item, double chance, int minCount, int maxCount) {
      this.item = item;
      this.chance = chance;
      this.minCount = minCount;
      this.maxCount = maxCount;
    }

    public boolean shouldProduce() {
      return Math.random() < chance;
    }

    public ItemStack createStack() {
      int count =
          minCount == maxCount
              ? minCount
              : minCount + (int) (Math.random() * (maxCount - minCount + 1));
      return new ItemStack(item, count);
    }

    public Item getItem() {
      return item;
    }

    public double getChance() {
      return chance;
    }

    // Getter methods for serialization
    public int getMinCount() {
      return minCount;
    }

    public int getMaxCount() {
      return maxCount;
    }
  }
}
