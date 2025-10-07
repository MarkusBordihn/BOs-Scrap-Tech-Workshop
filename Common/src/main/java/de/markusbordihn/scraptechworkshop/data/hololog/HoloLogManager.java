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
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloLogManager {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private HoloLogManager() {}

  public static Optional<HoloLogData> loadHoloLog(final ResourceLocation holoLogId) {
    if (holoLogId == null) {
      log.warn("Cannot load hololog: holoLogId is null");
      return Optional.empty();
    }

    ResourceLocation localizedId = HoloLogParser.getLocalizedId(holoLogId);

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

    return holoLogData;
  }

  public static boolean isValidHoloLog(final ResourceLocation holoLogId) {
    if (holoLogId == null) {
      return false;
    }

    ResourceLocation localizedId = HoloLogParser.getLocalizedId(holoLogId);
    Optional<HoloLogData> holoLogData = HoloLogParser.getHoloLog(localizedId);
    return holoLogData.isPresent() && !holoLogData.get().lines().isEmpty();
  }
}
