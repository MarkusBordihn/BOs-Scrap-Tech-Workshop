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

package de.markusbordihn.scraptechworkshop.data.multitool;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public record DisplayMode(ItemStack itemStack) {

  private static final String CUSTOM_MODEL_DATA_TAG = "CustomModelData";

  public void updateModel(ToolMode activeMode, BatteryLevel batteryLevel) {
    int customModelData = getModelDataForMode(activeMode, batteryLevel);

    CompoundTag compoundTag = itemStack.getOrCreateTag();
    compoundTag.putInt(CUSTOM_MODEL_DATA_TAG, customModelData);
  }

  private int getModelDataForMode(ToolMode activeMode, BatteryLevel batteryLevel) {
    if (!hasBattery()) {
      return 1; // No battery model
    }

    // Use ToolMode's model data directly, or battery level for default mode
    if (activeMode == ToolMode.DEFAULT) {
      return getBatteryLevelModelData(batteryLevel);
    }

    return activeMode.getModelData();
  }

  private int getBatteryLevelModelData(BatteryLevel level) {
    return switch (level) {
      case BATTERY_LEVEL_0 -> 1;
      case BATTERY_LEVEL_25 -> 2;
      case BATTERY_LEVEL_50 -> 3;
      case BATTERY_LEVEL_75 -> 4;
      case BATTERY_LEVEL_100 -> 5;
    };
  }

  private boolean hasBattery() {
    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    return data.hasBattery();
  }
}
