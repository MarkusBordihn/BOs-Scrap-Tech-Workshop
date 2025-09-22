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

package de.markusbordihn.scraptechworkshop.menu;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class ItemBaseScreenHandler implements ExtendedScreenHandlerFactory {

  private final ItemStack itemStack;
  private final InteractionHand hand;
  private final MenuFactory menuFactory;

  public ItemBaseScreenHandler(ItemStack itemStack, InteractionHand hand, MenuFactory menuFactory) {
    this.itemStack = itemStack;
    this.hand = hand;
    this.menuFactory = menuFactory;
  }

  @Override
  public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
    buf.writeItem(itemStack);
    buf.writeEnum(hand);
    int slotIndex = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
    buf.writeInt(slotIndex);
  }

  @Override
  public Component getDisplayName() {
    return itemStack.getHoverName();
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
    int slotIndex = hand == InteractionHand.MAIN_HAND ? playerInventory.selected : 40;
    return menuFactory.create(windowId, playerInventory, itemStack, hand, slotIndex);
  }

  @FunctionalInterface
  public interface MenuFactory {
    AbstractContainerMenu create(
        int windowId,
        Inventory playerInventory,
        ItemStack itemStack,
        InteractionHand hand,
        int slotIndex);
  }
}
