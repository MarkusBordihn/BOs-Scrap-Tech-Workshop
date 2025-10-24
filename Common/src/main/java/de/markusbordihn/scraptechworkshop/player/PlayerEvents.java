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

package de.markusbordihn.scraptechworkshop.player;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.holocube.HoloCubePlayerData;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloCubeItem;
import de.markusbordihn.scraptechworkshop.registry.item.HoloLogItemRegistry;
import de.markusbordihn.scraptechworkshop.saveddata.HoloCubeStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerEvents {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final ResourceLocation INTRO_HOLOCUBE =
      new ResourceLocation(Constants.MOD_ID, "holologs/intro/introduction");

  private PlayerEvents() {}

  public static void handleServerStopping() {
    HoloCubeStorage.reset();
  }

  public static void handlePlayerJoin(ServerPlayer player) {
    ServerLevel level = player.serverLevel();
    HoloCubeStorage storage = HoloCubeStorage.get(level);

    if (!storage.hasReceivedHoloCube(player.getUUID(), INTRO_HOLOCUBE)) {
      HoloCubeItem holoCubeItem = HoloLogItemRegistry.getHoloCubeItemByHoloLog(INTRO_HOLOCUBE);
      if (holoCubeItem == null) {
        log.error("Could not find HoloCube item for HoloLog: {}", INTRO_HOLOCUBE);
        return;
      }

      ItemStack holoCube = new ItemStack(holoCubeItem);

      if (player.addItem(holoCube)) {
        HoloCubePlayerData data = HoloCubePlayerData.create(player.getUUID(), INTRO_HOLOCUBE);
        storage.addHoloCube(data);
        log.info(
            "Gave intro HoloCube to player: {} ({})",
            player.getName().getString(),
            player.getUUID());
      } else {
        log.warn(
            "Could not add intro HoloCube to player inventory: {}", player.getName().getString());
      }
    }
  }
}
