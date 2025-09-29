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

import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public record ScrapMultitoolData(
    ItemStack battery,
    ItemStack[] modules,
    int hologramColor,
    boolean hudEnabled,
    String toolPriority,
    String activeMode) {

  public static final int MODULE_SLOTS = 4;

  private static final String BATTERY_TAG = "Battery";
  private static final String MODULES_TAG = "Modules";
  private static final String HOLOGRAM_COLOR_TAG = "HologramColor";
  private static final String HUD_ENABLED_TAG = "HudEnabled";
  private static final String TOOL_PRIORITY_TAG = "ToolPriority";
  private static final String ACTIVE_MODE_TAG = "ActiveMode";
  private static final String MULTITOOL_DATA_TAG = "MultitoolData";

  public static ScrapMultitoolData createDefault() {
    return new ScrapMultitoolData(
        ItemStack.EMPTY,
        new ItemStack[MODULE_SLOTS],
        0x00FFFF,
        true,
        "auto",
        ToolMode.DEFAULT.getId());
  }

  public static ScrapMultitoolData fromItemStack(ItemStack itemStack) {
    CompoundTag compoundTag = itemStack.getOrCreateTag();
    if (compoundTag.contains(MULTITOOL_DATA_TAG)) {
      return fromNBT(compoundTag.getCompound(MULTITOOL_DATA_TAG));
    }
    return createDefault();
  }

  public static ScrapMultitoolData fromNBT(CompoundTag compoundTag) {
    ItemStack battery = ItemStack.EMPTY;
    if (compoundTag.contains(BATTERY_TAG)) {
      battery = ItemStack.of(compoundTag.getCompound(BATTERY_TAG));
    }

    ItemStack[] modules = new ItemStack[MODULE_SLOTS];
    if (compoundTag.contains(MODULES_TAG)) {
      ListTag modulesTag = compoundTag.getList(MODULES_TAG, 10);
      for (int i = 0; i < Math.min(modulesTag.size(), MODULE_SLOTS); i++) {
        modules[i] = ItemStack.of(modulesTag.getCompound(i));
      }
    }

    return new ScrapMultitoolData(
        battery,
        modules,
        compoundTag.getInt(HOLOGRAM_COLOR_TAG),
        compoundTag.getBoolean(HUD_ENABLED_TAG),
        compoundTag.getString(TOOL_PRIORITY_TAG),
        compoundTag.contains(ACTIVE_MODE_TAG)
            ? compoundTag.getString(ACTIVE_MODE_TAG)
            : ToolMode.DEFAULT.getId());
  }

  public boolean hasBattery() {
    return !battery.isEmpty();
  }

  public CompoundTag toNBT() {
    CompoundTag compoundTag = new CompoundTag();

    if (!battery.isEmpty()) {
      compoundTag.put(BATTERY_TAG, battery.save(new CompoundTag()));
    }

    ListTag modulesTag = new ListTag();
    for (ItemStack module : modules) {
      if (module != null && !module.isEmpty()) {
        modulesTag.add(module.save(new CompoundTag()));
      } else {
        modulesTag.add(new CompoundTag());
      }
    }
    compoundTag.put(MODULES_TAG, modulesTag);

    compoundTag.putInt(HOLOGRAM_COLOR_TAG, hologramColor);
    compoundTag.putBoolean(HUD_ENABLED_TAG, hudEnabled);
    compoundTag.putString(TOOL_PRIORITY_TAG, toolPriority);
    compoundTag.putString(ACTIVE_MODE_TAG, activeMode);

    return compoundTag;
  }

  public void saveToItemStack(ItemStack itemStack) {
    CompoundTag compoundTag = itemStack.getOrCreateTag();
    compoundTag.put(MULTITOOL_DATA_TAG, toNBT());
  }

  public ScrapMultitoolData withBattery(ItemStack newBattery) {
    return new ScrapMultitoolData(
        newBattery, modules, hologramColor, hudEnabled, toolPriority, activeMode);
  }

  public ScrapMultitoolData withModule(int slot, ItemStack module) {
    if (slot < 0 || slot >= MODULE_SLOTS) return this;
    ItemStack[] newModules = modules.clone();
    newModules[slot] = module;
    return new ScrapMultitoolData(
        battery, newModules, hologramColor, hudEnabled, toolPriority, activeMode);
  }

  public ScrapMultitoolData withActiveMode(String newMode) {
    return new ScrapMultitoolData(
        battery, modules, hologramColor, hudEnabled, toolPriority, newMode);
  }

  public BatteryLevel getBatteryLevel() {
    if (!hasBattery()) {
      return BatteryLevel.BATTERY_LEVEL_0;
    }

    if (battery.getItem() instanceof EnergyCellItem batteryItem) {
      float percentage = batteryItem.getEnergyPercentage(battery);
      if (percentage >= 1.0f) return BatteryLevel.BATTERY_LEVEL_100;
      if (percentage >= 0.75f) return BatteryLevel.BATTERY_LEVEL_75;
      if (percentage >= 0.5f) return BatteryLevel.BATTERY_LEVEL_50;
      if (percentage >= 0.25f) return BatteryLevel.BATTERY_LEVEL_25;
    }
    return BatteryLevel.BATTERY_LEVEL_0;
  }
}
