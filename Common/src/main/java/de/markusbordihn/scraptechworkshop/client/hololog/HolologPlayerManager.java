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

package de.markusbordihn.scraptechworkshop.client.hololog;

import de.markusbordihn.scraptechworkshop.Constants;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class HolologPlayerManager {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Map<UUID, HolologPlayer> ACTIVE_PLAYERS = new HashMap<>();
  private static final Map<ResourceLocation, UUID> ACTIVE_HOLOLOG_IDS = new HashMap<>();

  private HolologPlayerManager() {}

  public static UUID register(HolologPlayer player, ResourceLocation holologId) {
    UUID existingPlayerId = ACTIVE_HOLOLOG_IDS.get(holologId);
    if (existingPlayerId != null && ACTIVE_PLAYERS.containsKey(existingPlayerId)) {
      log.debug(
          "Hololog {} is already playing with ID {}, reusing existing player",
          holologId,
          existingPlayerId);
      return existingPlayerId;
    }

    UUID id = UUID.randomUUID();
    ACTIVE_PLAYERS.put(id, player);
    ACTIVE_HOLOLOG_IDS.put(holologId, id);
    log.info("Registered hololog player {} for hololog: {}", id, holologId);
    return id;
  }

  public static HolologPlayer get(UUID playerId) {
    return ACTIVE_PLAYERS.get(playerId);
  }

  public static void remove(UUID playerId) {
    HolologPlayer player = ACTIVE_PLAYERS.remove(playerId);
    if (player != null) {
      ACTIVE_HOLOLOG_IDS.values().removeIf(id -> id.equals(playerId));
      log.debug("Removed hololog player: {}", playerId);
    }
  }

  public static void stopAll() {
    ACTIVE_PLAYERS.values().forEach(player -> player.stop());
    ACTIVE_PLAYERS.clear();
    ACTIVE_HOLOLOG_IDS.clear();
    log.debug("Stopped all hololog players");
  }

  public static void tickAll() {
    if (ACTIVE_PLAYERS.isEmpty()) {
      log.trace("Ticking {} active hololog players", ACTIVE_PLAYERS.size());
    }
    ACTIVE_PLAYERS
        .values()
        .removeIf(
            player -> {
              if (player.isPlaying()) {
                player.tick();
              }
              boolean shouldRemove = player.isStopped() && player.hasPlayedEndEffects();
              if (shouldRemove) {
                log.debug("Removing completed hololog player");
              }
              return shouldRemove;
            });
  }

  public static int getCurrentLine(ResourceLocation holologId) {
    UUID playerId = ACTIVE_HOLOLOG_IDS.get(holologId);
    if (playerId != null) {
      HolologPlayer player = ACTIVE_PLAYERS.get(playerId);
      if (player != null) {
        return player.getCurrentLine();
      }
    }
    return -1;
  }
}
