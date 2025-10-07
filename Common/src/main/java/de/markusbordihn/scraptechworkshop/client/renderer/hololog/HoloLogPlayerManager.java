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

package de.markusbordihn.scraptechworkshop.client.renderer.hololog;

import de.markusbordihn.scraptechworkshop.Constants;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class HoloLogPlayerManager {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Map<UUID, HoloLogPlaybackBase> ACTIVE_PLAYERS = new HashMap<>();
  private static final Map<String, UUID> ACTIVE_HOLOLOG_KEYS = new HashMap<>();

  private HoloLogPlayerManager() {}

  private static String createKey(
      ResourceLocation holoLogId, Class<? extends HoloLogPlaybackBase> playerType) {
    return holoLogId.toString() + ":" + playerType.getSimpleName();
  }

  public static UUID register(final HoloLogPlaybackBase player, final ResourceLocation holoLogId) {
    String key = createKey(holoLogId, player.getClass());
    UUID existingPlayerId = ACTIVE_HOLOLOG_KEYS.get(key);
    if (existingPlayerId != null && ACTIVE_PLAYERS.containsKey(existingPlayerId)) {
      log.debug(
          "Hololog {} with type {} is already playing with ID {}, reusing existing player",
          holoLogId,
          player.getClass().getSimpleName(),
          existingPlayerId);
      return existingPlayerId;
    }

    UUID id = UUID.randomUUID();
    ACTIVE_PLAYERS.put(id, player);
    ACTIVE_HOLOLOG_KEYS.put(key, id);
    log.info(
        "Registered {} player {} for hololog: {}",
        player.getClass().getSimpleName(),
        id,
        holoLogId);
    return id;
  }

  public static HoloLogPlaybackBase get(final UUID playerId) {
    return ACTIVE_PLAYERS.get(playerId);
  }

  public static void stopAll() {
    ACTIVE_PLAYERS.values().forEach(player -> player.stop());
    ACTIVE_PLAYERS.clear();
    ACTIVE_HOLOLOG_KEYS.clear();
    log.debug("Stopped all hololog players");
  }

  public static void tickAll() {
    ACTIVE_PLAYERS
        .values()
        .removeIf(
            player -> {
              if (player.isPlaying()) {
                player.tick();
              }
              boolean shouldRemove = player.isStopped();
              if (shouldRemove) {
                log.debug("Removing completed hololog player");
              }
              return shouldRemove;
            });
  }
}
