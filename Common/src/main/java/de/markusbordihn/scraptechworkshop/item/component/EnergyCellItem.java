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

package de.markusbordihn.scraptechworkshop.item.component;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class EnergyCellItem extends Item {

  public static final String ID = "energy_cell";
  public static final int ENERGY_MAX = 5000;

  private final int initialEnergy;

  public EnergyCellItem(Properties properties) {
    this(properties, ENERGY_MAX);
  }

  public EnergyCellItem(Properties properties, int initialEnergy) {
    super(properties.stacksTo(1).durability(ENERGY_MAX).rarity(Rarity.UNCOMMON));
    this.initialEnergy = Math.max(1, Math.min(initialEnergy, ENERGY_MAX));
  }

  public int getEnergy(ItemStack itemStack) {
    return ENERGY_MAX - itemStack.getDamageValue();
  }

  public ItemStack getDefaultInstance() {
    ItemStack stack = new ItemStack(this);
    setEnergy(stack, initialEnergy);
    return stack;
  }

  public void setEnergy(ItemStack itemStack, int energy) {
    itemStack.setDamageValue(ENERGY_MAX - Math.max(1, Math.min(energy, ENERGY_MAX)));
  }

  public void consumeEnergy(ItemStack itemStack, int amount) {
    int currentEnergy = getEnergy(itemStack);
    setEnergy(itemStack, Math.max(1, currentEnergy - amount));
  }

  public boolean hasEnergy(ItemStack itemStack, int amount) {
    return getEnergy(itemStack) >= amount;
  }

  public float getEnergyPercentage(ItemStack itemStack) {
    return (float) getEnergy(itemStack) / ENERGY_MAX;
  }

  public ItemStack createEmptyBattery() {
    return new ItemStack(ModItems.EMPTY_ENERGY_CELL.get());
  }

  @Override
  public boolean isBarVisible(ItemStack itemStack) {
    return true;
  }

  @Override
  public int getBarColor(ItemStack itemStack) {
    float energyRatio = getEnergyPercentage(itemStack);
    return energyRatio > 0.6f ? 0x00FF00 : energyRatio > 0.3f ? 0xFFFF00 : 0xFF0000;
  }

  @Override
  public void appendHoverText(
      ItemStack itemStack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + "energy_cell.description"));

    int energy = getEnergy(itemStack);
    tooltipComponents.add(
        Component.literal("Energy: " + energy + "/" + ENERGY_MAX)
            .withStyle(style -> style.withColor(getBarColor(itemStack))));

    super.appendHoverText(itemStack, level, tooltipComponents, isAdvanced);
  }

  @Override
  public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    return false;
  }
}
