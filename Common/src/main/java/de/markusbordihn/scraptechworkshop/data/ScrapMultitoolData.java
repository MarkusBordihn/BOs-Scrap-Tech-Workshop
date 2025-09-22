package de.markusbordihn.scraptechworkshop.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public record ScrapMultitoolData(
    ItemStack battery,
    ItemStack[] modules,
    int hologramColor,
    boolean hudEnabled,
    String toolPriority) {

  public static final int MODULE_SLOTS = 4;

  public static ScrapMultitoolData createDefault() {
    return new ScrapMultitoolData(
        ItemStack.EMPTY,
        new ItemStack[MODULE_SLOTS],
        0x00FFFF, // Cyan default
        true,
        "auto");
  }

  public static ScrapMultitoolData fromNBT(CompoundTag tag) {
    ItemStack battery = ItemStack.EMPTY;
    if (tag.contains("Battery")) {
      battery = ItemStack.of(tag.getCompound("Battery"));
    }

    ItemStack[] modules = new ItemStack[MODULE_SLOTS];
    if (tag.contains("Modules")) {
      ListTag modulesTag = tag.getList("Modules", 10);
      for (int i = 0; i < Math.min(modulesTag.size(), MODULE_SLOTS); i++) {
        modules[i] = ItemStack.of(modulesTag.getCompound(i));
      }
    }

    return new ScrapMultitoolData(
        battery,
        modules,
        tag.getInt("HologramColor"),
        tag.getBoolean("HudEnabled"),
        tag.getString("ToolPriority"));
  }

  public boolean hasBattery() {
    return !battery.isEmpty();
  }

  public CompoundTag toNBT() {
    CompoundTag compoundTag = new CompoundTag();

    if (!battery.isEmpty()) {
      compoundTag.put("Battery", battery.save(new CompoundTag()));
    }

    ListTag modulesTag = new ListTag();
    for (ItemStack module : modules) {
      if (module != null && !module.isEmpty()) {
        modulesTag.add(module.save(new CompoundTag()));
      } else {
        modulesTag.add(new CompoundTag());
      }
    }
    compoundTag.put("Modules", modulesTag);

    compoundTag.putInt("HologramColor", hologramColor);
    compoundTag.putBoolean("HudEnabled", hudEnabled);
    compoundTag.putString("ToolPriority", toolPriority);

    return compoundTag;
  }

  public ScrapMultitoolData withBattery(ItemStack newBattery) {
    return new ScrapMultitoolData(newBattery, modules, hologramColor, hudEnabled, toolPriority);
  }

  public ScrapMultitoolData withModule(int slot, ItemStack module) {
    if (slot < 0 || slot >= MODULE_SLOTS) return this;
    ItemStack[] newModules = modules.clone();
    newModules[slot] = module;
    return new ScrapMultitoolData(battery, newModules, hologramColor, hudEnabled, toolPriority);
  }
}
