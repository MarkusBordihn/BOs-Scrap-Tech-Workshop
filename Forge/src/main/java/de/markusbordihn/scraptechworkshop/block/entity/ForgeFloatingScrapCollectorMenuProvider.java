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

import de.markusbordihn.scraptechworkshop.block.entity.floatingscrapcollector.FloatingScrapCollectorBlockEntity;
import de.markusbordihn.scraptechworkshop.menu.FloatingScrapCollectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.IContainerFactory;

public class ForgeFloatingScrapCollectorMenuProvider
    implements MenuProvider, IContainerFactory<FloatingScrapCollectorMenu> {

  private final FloatingScrapCollectorBlockEntity blockEntity;

  public ForgeFloatingScrapCollectorMenuProvider(FloatingScrapCollectorBlockEntity blockEntity) {
    this.blockEntity = blockEntity;
  }

  @Override
  public Component getDisplayName() {
    return blockEntity.getDisplayName();
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
    return new FloatingScrapCollectorMenu(
        windowId, playerInventory, blockEntity, blockEntity.getContainerData());
  }

  @Override
  public FloatingScrapCollectorMenu create(
      int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    BlockPos pos = data.readBlockPos();
    BlockEntity entity = playerInventory.player.level().getBlockEntity(pos);
    if (entity instanceof FloatingScrapCollectorBlockEntity collector) {
      return new FloatingScrapCollectorMenu(
          windowId, playerInventory, collector, collector.getContainerData());
    }
    return new FloatingScrapCollectorMenu(windowId, playerInventory, data);
  }
}
