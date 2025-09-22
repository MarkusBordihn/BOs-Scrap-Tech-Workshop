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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ScrapMultitoolMenuProvider implements MenuProvider {

  private final ItemStack multitoolStack;
  private final InteractionHand hand;

  public ScrapMultitoolMenuProvider(ItemStack multitoolStack, InteractionHand hand) {
    this.multitoolStack = multitoolStack;
    this.hand = hand;
  }

  @Override
  public Component getDisplayName() {
    return multitoolStack.getHoverName();
  }

  @Override
  public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
    int slotIndex = hand == InteractionHand.MAIN_HAND ? playerInventory.selected : 40;
    return new ScrapMultitoolMenu(id, playerInventory, multitoolStack, hand, slotIndex);
  }

  /**
   * Writes additional data to the buffer for Forge. This method is called via reflection from
   * Forge's IForgeMenuType.
   */
  public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
    buf.writeItem(multitoolStack);
    buf.writeEnum(hand);
    int slotIndex = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
    buf.writeInt(slotIndex);
  }
}
