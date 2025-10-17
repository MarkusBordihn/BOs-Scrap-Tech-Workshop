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

package de.markusbordihn.scraptechworkshop.item.tool;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.multitool.DisplayMode;
import de.markusbordihn.scraptechworkshop.data.multitool.ScrapMultitoolData;
import de.markusbordihn.scraptechworkshop.data.multitool.ToolMode;
import de.markusbordihn.scraptechworkshop.item.ModItems;
import de.markusbordihn.scraptechworkshop.item.component.EnergyCellItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class CreativeScrapMultitoolItem extends ScrapMultitoolItem {

  public CreativeScrapMultitoolItem(final Properties properties) {
    super(properties.rarity(Rarity.EPIC));
  }

  @Override
  public void onCraftedBy(ItemStack itemStack, Level level, Player player) {
    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    ItemStack battery = new ItemStack(ModItems.ENERGY_CELL.get());
    if (battery.getItem() instanceof EnergyCellItem energyCell) {
      energyCell.setEnergy(battery, EnergyCellItem.CAPACITY_MAH);
    }

    data = data.withBattery(battery);
    data.saveToItemStack(itemStack);
    syncEnergyDisplay(itemStack);

    DisplayMode displayMode = new DisplayMode(itemStack);
    displayMode.updateModel(ToolMode.fromId(data.activeMode()), data.getBatteryLevel());
  }

  @Override
  public boolean consumeEnergy(ItemStack itemStack, int amount) {
    return true;
  }

  @Override
  public boolean hasEnergy(ItemStack itemStack, int amount) {
    return true;
  }

  @Override
  public boolean isBarVisible(ItemStack itemStack) {
    return false;
  }

  @Override
  public void appendHoverText(
      ItemStack itemStack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
    tooltipComponents.add(
        Component.translatable(Constants.ITEM_PREFIX + "scrap_multitool.description"));
    tooltipComponents.add(
        Component.literal("Energy: ∞ (Creative)").withStyle(style -> style.withColor(0x00FFFF)));

    ScrapMultitoolData data = ScrapMultitoolData.fromItemStack(itemStack);
    ToolMode activeMode = ToolMode.fromId(data.activeMode());
    tooltipComponents.add(
        Component.translatable(activeMode.getTranslationKey())
            .withStyle(style -> style.withColor(activeMode.getColor())));
    tooltipComponents.add(
        Component.literal("Shift + Right-click to configure")
            .withStyle(style -> style.withItalic(true)));
  }
}
