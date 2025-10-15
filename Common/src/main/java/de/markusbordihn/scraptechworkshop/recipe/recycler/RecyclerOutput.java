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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RecyclerOutput {
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
