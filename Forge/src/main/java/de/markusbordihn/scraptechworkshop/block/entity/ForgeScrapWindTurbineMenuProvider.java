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

package de.markusbordihn.scraptechworkshop.block.entity;

import de.markusbordihn.scraptechworkshop.block.entity.windturbine.ScrapWindTurbineBlockEntity;
import de.markusbordihn.scraptechworkshop.menu.ScrapWindTurbineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.IContainerFactory;

@SuppressWarnings("unused")
public class ForgeScrapWindTurbineMenuProvider
    implements MenuProvider, IContainerFactory<ScrapWindTurbineMenu> {

  private final ScrapWindTurbineBlockEntity blockEntity;

  public ForgeScrapWindTurbineMenuProvider(ScrapWindTurbineBlockEntity blockEntity) {
    this.blockEntity = blockEntity;
  }

  @Override
  public Component getDisplayName() {
    return blockEntity.getDisplayName();
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
    return new ScrapWindTurbineMenu(
        windowId, playerInventory, blockEntity, blockEntity.getContainerData());
  }

  @Override
  public ScrapWindTurbineMenu create(
      int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    if (playerInventory.player.level().getBlockEntity(data.readBlockPos())
        instanceof ScrapWindTurbineBlockEntity windTurbine) {
      return new ScrapWindTurbineMenu(
          windowId, playerInventory, windTurbine, windTurbine.getContainerData());
    }
    return new ScrapWindTurbineMenu(windowId, playerInventory, data);
  }
}
