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

import de.markusbordihn.scraptechworkshop.menu.RecyclerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ForgeRecyclerMenuProvider
    implements net.minecraft.world.MenuProvider,
        net.minecraftforge.network.IContainerFactory<RecyclerMenu> {

  private static final org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();

  private final RecyclerBlockEntity blockEntity;

  public ForgeRecyclerMenuProvider(RecyclerBlockEntity blockEntity) {
    this.blockEntity = blockEntity;
  }

  @Override
  public Component getDisplayName() {
    return blockEntity.getDisplayName();
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
    return new RecyclerMenu(windowId, playerInventory, blockEntity, blockEntity.getContainerData());
  }

  @Override
  public RecyclerMenu create(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    return new RecyclerMenu(windowId, playerInventory, data);
  }

  /** Writes BlockPos and container size to FriendlyByteBuf for NetworkHooks.openScreen(). */
  public void writeScreenOpeningData(
      net.minecraft.server.level.ServerPlayer player, FriendlyByteBuf buf) {
    log.debug("[RECYCLER] Writing BlockPos {} to client", blockEntity.getBlockPos());
    buf.writeBlockPos(blockEntity.getBlockPos());
    buf.writeInt(blockEntity.getContainerSize());
  }
}
