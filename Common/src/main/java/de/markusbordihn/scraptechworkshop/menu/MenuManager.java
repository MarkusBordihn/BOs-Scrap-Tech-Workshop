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

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Central manager for opening menus across different mod loaders. */
public class MenuManager {

  private static final Logger log = LogManager.getLogger("ScrapTechWorkshop");
  private static MenuOpener menuOpener;

  private MenuManager() {}

  public static void setMenuOpener(MenuOpener opener) {
    if (menuOpener != null) {
      log.warn("MenuOpener already set! Overwriting with: {}", opener.getClass().getSimpleName());
    }
    menuOpener = opener;
    log.info("MenuOpener initialized: {}", opener.getClass().getSimpleName());
  }

  public static void openMenu(Player player, BlockEntity blockEntity) {
    if (menuOpener == null) {
      log.error("MenuOpener not initialized! Call setMenuOpener() during mod initialization.");
      return;
    }
    menuOpener.openBlockEntityMenu(player, blockEntity);
  }

  public static void openMenu(Player player, ItemStack itemStack, InteractionHand hand) {
    if (menuOpener == null) {
      log.error("MenuOpener not initialized! Call setMenuOpener() during mod initialization.");
      return;
    }
    menuOpener.openItemMenu(player, itemStack, hand);
  }

  public interface MenuOpener {
    void openBlockEntityMenu(Player player, BlockEntity blockEntity);

    void openItemMenu(Player player, ItemStack itemStack, InteractionHand hand);
  }
}
