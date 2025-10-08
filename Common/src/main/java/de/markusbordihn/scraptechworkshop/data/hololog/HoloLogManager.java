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

package de.markusbordihn.scraptechworkshop.data.hololog;

import de.markusbordihn.scraptechworkshop.Constants;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloLogManager {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Map<ResourceLocation, CachedHoloLogData> CACHE = new ConcurrentHashMap<>();
  private static final Set<ResourceLocation> LOGGED_PATHS = ConcurrentHashMap.newKeySet();
  private static final long CACHE_TTL_MS = 5 * 60 * 1000;

  private HoloLogManager() {}

  private static ResourceLocation getLocalizedId(final ResourceLocation id) {
    try {
      String[] parts = id.getPath().split("/", 2);
      if (parts.length < 2) {
        return id;
      }

      String languageCode =
          Minecraft.getInstance().getLanguageManager().getSelected().split("_")[0];
      ResourceLocation localizedId =
          new ResourceLocation(id.getNamespace(), parts[0] + "/" + languageCode + "/" + parts[1]);

      if (HoloLogParser.holoLogExists(localizedId)) {
        return localizedId;
      }

      if (!languageCode.equals("en")) {
        ResourceLocation englishId =
            new ResourceLocation(id.getNamespace(), parts[0] + "/" + "en" + "/" + parts[1]);
        if (HoloLogParser.holoLogExists(englishId)) {
          if (LOGGED_PATHS.add(id)) {
            log.debug("Using English fallback hololog: {}", englishId);
          }
          return englishId;
        }
      }

      if (LOGGED_PATHS.add(id)) {
        log.debug("Using original hololog path: {}", id);
      }
      return id;
    } catch (Exception e) {
      if (LOGGED_PATHS.add(id)) {
        log.debug("Cannot access client (server-side?), using original path: {}", id);
      }
      return id;
    }
  }

  public static Optional<HoloLogData> loadHoloLog(final ResourceLocation holoLogId) {
    if (holoLogId == null) {
      log.warn("Cannot load hololog: holoLogId is null");
      return Optional.empty();
    }

    ResourceLocation localizedId = getLocalizedId(holoLogId);

    CachedHoloLogData cached = CACHE.get(localizedId);
    if (cached != null && !cached.isExpired()) {
      log.trace("Cache hit for hololog: {}", localizedId);
      return Optional.of(cached.data());
    }

    if (cached != null && cached.isExpired()) {
      log.debug("Cache expired for hololog: {}, reloading...", localizedId);
      CACHE.remove(localizedId);
    }

    Optional<HoloLogData> holoLogData = HoloLogParser.getHoloLog(localizedId);

    if (holoLogData.isEmpty()) {
      log.error("Hololog not found: {} (localized: {})", holoLogId, localizedId);
      return Optional.empty();
    }

    HoloLogData data = holoLogData.get();
    if (data.lines().isEmpty()) {
      log.error("Hololog has no lines: {} (localized: {})", holoLogId, localizedId);
      return Optional.empty();
    }

    CACHE.put(localizedId, new CachedHoloLogData(data, System.currentTimeMillis()));
    log.debug("Cached hololog: {} (TTL: {}ms)", localizedId, CACHE_TTL_MS);

    return Optional.of(data);
  }

  public static boolean isValidHoloLog(final ResourceLocation holoLogId) {
    if (holoLogId == null) {
      return false;
    }

    Optional<HoloLogData> holoLogData = loadHoloLog(holoLogId);
    return holoLogData.isPresent() && !holoLogData.get().lines().isEmpty();
  }

  public static void clearCache() {
    int size = CACHE.size();
    CACHE.clear();
    LOGGED_PATHS.clear();
    log.info("Cleared hololog cache ({} entries)", size);
  }

  public static int getCacheSize() {
    return CACHE.size();
  }

  public static void removeExpiredEntries() {
    int initialSize = CACHE.size();
    CACHE.entrySet().removeIf(entry -> entry.getValue().isExpired());
    int removed = initialSize - CACHE.size();
    if (removed > 0) {
      log.debug("Removed {} expired hololog cache entries", removed);
    }
  }

  private record CachedHoloLogData(HoloLogData data, long timestamp) {
    public boolean isExpired() {
      return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
    }
  }
}
