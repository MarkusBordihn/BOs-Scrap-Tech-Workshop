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

package de.markusbordihn.scraptechworkshop.loot;

import net.minecraft.util.RandomSource;

public final class WeightedLootTable<T> {

  private final Entry<T>[] entries;
  private final int totalWeight;

  @SafeVarargs
  public WeightedLootTable(Entry<T>... entries) {
    if (entries == null || entries.length == 0) {
      throw new IllegalArgumentException("Weighted loot table cannot be empty");
    }

    this.entries = entries.clone();
    int cumulative = 0;

    for (Entry<T> entry : this.entries) {
      if (entry == null) {
        throw new IllegalArgumentException("Entry cannot be null");
      }
      cumulative += entry.weight;
      entry.cumulativeWeight = cumulative;
    }

    this.totalWeight = cumulative;

    if (this.totalWeight <= 0) {
      throw new IllegalArgumentException("Total weight must be positive, got: " + this.totalWeight);
    }
  }

  public T generate(RandomSource random) {
    if (totalWeight <= 0) return entries[0].item;

    int randomValue = random.nextInt(totalWeight);
    int left = 0;
    int right = entries.length - 1;

    while (left < right) {
      int mid = (left + right) >>> 1;
      if (randomValue < entries[mid].cumulativeWeight) {
        right = mid;
      } else {
        left = mid + 1;
      }
    }

    return entries[left].item;
  }

  public int getTotalWeight() {
    return totalWeight;
  }

  public int size() {
    return entries.length;
  }

  public double getProbability(int entryIndex) {
    if (entryIndex < 0 || entryIndex >= entries.length) {
      throw new IndexOutOfBoundsException("Entry index out of bounds: " + entryIndex);
    }
    return (double) entries[entryIndex].weight / totalWeight * 100.0;
  }

  public Entry<T> getEntry(int index) {
    if (index < 0 || index >= entries.length) {
      throw new IndexOutOfBoundsException("Entry index out of bounds: " + index);
    }
    return entries[index];
  }

  public static final class Entry<T> {
    public final T item;
    public final int weight;
    int cumulativeWeight;

    public Entry(T item, int weight) {
      if (weight < 0) {
        throw new IllegalArgumentException("Weight cannot be negative: " + weight);
      }
      this.item = item;
      this.weight = weight;
    }
  }
}
