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

public record BatteryStatus(
    EnergyStatus status, EnergySourceType sourceType, boolean rechargeable, int cycleCount) {

  public static final String RECHARGEABLE_TAG = "Rechargeable";
  public static final String CYCLE_COUNT_TAG = "CycleCount";
  public static final String ENERGY_SOURCE_TAG = "EnergySource";
  public static final String ENERGY_STATUS_TAG = "EnergyStatus";

  public BatteryStatus(ItemStack itemStack) {
    this(readFromItemStack(itemStack));
  }

  private BatteryStatus(BatteryValues values) {
    this(values.status, values.sourceType, values.rechargeable, values.cycleCount);
  }

  private static BatteryValues readFromItemStack(ItemStack itemStack) {
    CompoundTag tag = itemStack.getTag();

    EnergyStatus status;
    EnergySourceType sourceType;
    boolean rechargeable;
    int cycleCount;

    if (tag != null) {
      status = EnergyStatus.fromId(tag.getString(ENERGY_STATUS_TAG));
      sourceType = EnergySourceType.fromId(tag.getString(ENERGY_SOURCE_TAG));
      rechargeable = tag.getBoolean(RECHARGEABLE_TAG);
      cycleCount = tag.getInt(CYCLE_COUNT_TAG);
    } else {
      status = EnergyStatus.READY;
      sourceType = EnergySourceType.BATTERY;
      rechargeable = false;
      cycleCount = 0;
    }

    return new BatteryValues(status, sourceType, rechargeable, cycleCount);
  }

  public void writeToItemStack(ItemStack itemStack) {
    CompoundTag tag = itemStack.getOrCreateTag();
    tag.putString(ENERGY_STATUS_TAG, status.getId());
    tag.putString(ENERGY_SOURCE_TAG, sourceType.getId());
    tag.putBoolean(RECHARGEABLE_TAG, rechargeable);
    tag.putInt(CYCLE_COUNT_TAG, cycleCount);
  }

  public BatteryStatus withStatus(EnergyStatus newStatus) {
    return new BatteryStatus(newStatus, sourceType, rechargeable, cycleCount);
  }

  public BatteryStatus withCycleIncrement() {
    return new BatteryStatus(status, sourceType, rechargeable, cycleCount + 1);
  }

  public boolean canCharge() {
    return rechargeable && status != EnergyStatus.FAULTY && status != EnergyStatus.OVERCHARGED;
  }

  public boolean canDischarge() {
    return status != EnergyStatus.EMPTY && status != EnergyStatus.FAULTY;
  }

  public boolean isFaulty() {
    return status == EnergyStatus.FAULTY;
  }

  private record BatteryValues(
      EnergyStatus status, EnergySourceType sourceType, boolean rechargeable, int cycleCount) {}
}
