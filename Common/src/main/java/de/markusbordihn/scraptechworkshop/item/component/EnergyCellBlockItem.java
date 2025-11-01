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
import de.markusbordihn.scraptechworkshop.energy.EnergyCell;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class EnergyCellBlockItem extends Item implements EnergyCell {

  public static final String ID = "energy_cell_block";
  public static final int CAPACITY_MAH = 30000;
  public static final float VOLTAGE = 11.1f;
  public static final int DEFAULT_CHARGE_RATE = 150;
  public static final int DEFAULT_DISCHARGE_RATE = 600;

  private final int initialEnergy;
  private final int chargeRate;
  private final int dischargeRate;

  public EnergyCellBlockItem(Properties properties) {
    this(properties, CAPACITY_MAH, DEFAULT_CHARGE_RATE, DEFAULT_DISCHARGE_RATE);
  }

  public EnergyCellBlockItem(Properties properties, int initialEnergy) {
    this(properties, initialEnergy, DEFAULT_CHARGE_RATE, DEFAULT_DISCHARGE_RATE);
  }

  public EnergyCellBlockItem(Properties properties, int initialEnergy, int chargeRate, int dischargeRate) {
    super(properties.stacksTo(1).durability(CAPACITY_MAH).rarity(Rarity.RARE));
    this.initialEnergy = Math.max(1, Math.min(initialEnergy, CAPACITY_MAH));
    this.chargeRate = Math.max(1, chargeRate);
    this.dischargeRate = Math.max(1, dischargeRate);
  }

  @Override
  public int getCapacity() {
    return CAPACITY_MAH;
  }

  @Override
  public int getChargeRate() {
    return chargeRate;
  }

  @Override
  public int getDischargeRate() {
    return dischargeRate;
  }

  @Override
  public ItemStack createEmptyBattery() {
    return new ItemStack(ModItems.EMPTY_ENERGY_CELL_BLOCK.get());
  }

  public ItemStack getDefaultInstance() {
    ItemStack stack = new ItemStack(this);
    setEnergy(stack, initialEnergy);
    return stack;
  }

  @Override
  public boolean isBarVisible(ItemStack itemStack) {
    return true;
  }

  @Override
  public int getBarColor(ItemStack itemStack) {
    return EnergyCell.super.getBarColor(itemStack);
  }

  @Override
  public void appendHoverText(
      ItemStack itemStack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + "energy_cell_block.description"));

    int energy = getEnergy(itemStack);
    tooltipComponents.add(
        Component.literal(energy + " / " + CAPACITY_MAH + " mAh (" + VOLTAGE + "V)")
            .withStyle(style -> style.withColor(getBarColor(itemStack))));

    super.appendHoverText(itemStack, level, tooltipComponents, isAdvanced);
  }

  @Override
  public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    return false;
  }
}
