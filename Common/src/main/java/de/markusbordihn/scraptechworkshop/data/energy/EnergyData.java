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

package de.markusbordihn.scraptechworkshop.data.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public record EnergyData(int current, int maximum, int transferRate) {

  public static final String NBT_ENERGY = "Energy";
  public static final String NBT_MAX_ENERGY = "MaxEnergy";
  public static final String NBT_TRANSFER_RATE = "TransferRate";
  public static final int DEFAULT_TRANSFER_RATE = 100;

  public EnergyData(ItemStack itemStack) {
    this(itemStack, itemStack.getMaxDamage());
  }

  public EnergyData(ItemStack itemStack, int maxEnergy) {
    this(readFromItemStack(itemStack, maxEnergy));
  }

  private EnergyData(EnergyValues values) {
    this(values.current, values.maximum, values.transferRate);
  }

  private static EnergyValues readFromItemStack(ItemStack itemStack, int maxEnergy) {
    CompoundTag tag = itemStack.getTag();

    int current;
    int maximum;
    int transferRate;

    if (tag != null) {
      if (tag.contains(NBT_ENERGY)) {
        current = tag.getInt(NBT_ENERGY);
      } else {
        current = Math.max(1, maxEnergy - itemStack.getDamageValue());
      }

      maximum = tag.contains(NBT_MAX_ENERGY) ? tag.getInt(NBT_MAX_ENERGY) : maxEnergy;
      transferRate =
          tag.contains(NBT_TRANSFER_RATE) ? tag.getInt(NBT_TRANSFER_RATE) : DEFAULT_TRANSFER_RATE;
    } else {
      current = Math.max(1, maxEnergy - itemStack.getDamageValue());
      maximum = maxEnergy;
      transferRate = DEFAULT_TRANSFER_RATE;
    }

    return new EnergyValues(current, maximum, transferRate);
  }

  // Write energy data to ItemStack
  public void writeToItemStack(ItemStack itemStack) {
    CompoundTag tag = itemStack.getOrCreateTag();
    tag.putInt(NBT_ENERGY, current);
    tag.putInt(NBT_MAX_ENERGY, maximum);
    tag.putInt(NBT_TRANSFER_RATE, transferRate);

    // Also update damage value for compatibility with vanilla durability bar
    itemStack.setDamageValue(maximum - Math.max(1, current));
  }

  public boolean hasEnergy(int amount) {
    return current >= amount;
  }

  public float getPercentage() {
    if (current <= 0 || maximum <= 0) {
      return 0.0f;
    }
    return (float) current / maximum;
  }

  public int getDisplayEnergy() {
    return Math.round(getPercentage() * 100);
  }

  public EnergyData consume(int amount) {
    return new EnergyData(Math.max(0, current - amount), maximum, transferRate);
  }

  public EnergyData add(int amount) {
    return new EnergyData(Math.min(maximum, current + amount), maximum, transferRate);
  }

  public EnergyData withCurrent(int newCurrent) {
    return new EnergyData(Math.max(0, Math.min(maximum, newCurrent)), maximum, transferRate);
  }

  public boolean isEmpty() {
    return current <= 0;
  }

  public boolean isFull() {
    return current >= maximum;
  }

  private record EnergyValues(int current, int maximum, int transferRate) {}
}
