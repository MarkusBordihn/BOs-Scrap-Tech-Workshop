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

import de.markusbordihn.scraptechworkshop.block.entity.ForgeCollectorStationMenuProvider;
import de.markusbordihn.scraptechworkshop.block.entity.ForgeRecyclerMenuProvider;
import de.markusbordihn.scraptechworkshop.block.entity.collectorstation.CollectorStationBlockEntity;
import de.markusbordihn.scraptechworkshop.block.entity.recycler.RecyclerBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;

public class ForgeMenuOpener implements MenuManager.MenuOpener {

  @Override
  public void openBlockEntityMenu(Player player, BlockEntity blockEntity) {
    if (!(player instanceof ServerPlayer serverPlayer)) {
      return;
    }

    if (blockEntity instanceof CollectorStationBlockEntity collectorStation) {
      NetworkHooks.openScreen(
          serverPlayer,
          new ForgeCollectorStationMenuProvider(collectorStation),
          blockEntity.getBlockPos());
    } else if (blockEntity instanceof RecyclerBlockEntity recycler) {
      NetworkHooks.openScreen(
          serverPlayer, new ForgeRecyclerMenuProvider(recycler), blockEntity.getBlockPos());
    }
  }

  @Override
  public void openItemMenu(Player player, ItemStack itemStack, InteractionHand hand) {
    if (player instanceof ServerPlayer) {
      player.openMenu(new ScrapMultitoolMenuProvider(itemStack, hand));
    }
  }
}
