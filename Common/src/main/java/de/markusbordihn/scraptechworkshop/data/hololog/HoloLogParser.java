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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.markusbordihn.scraptechworkshop.Constants;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloLogParser {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Gson GSON = new Gson();

  private HoloLogParser() {}

  public static boolean holoLogExists(final ResourceLocation id) {
    try {
      Minecraft minecraft = Minecraft.getInstance();
      ResourceManager resourceManager = minecraft.getResourceManager();

      ResourceLocation resourcePath =
          new ResourceLocation(id.getNamespace(), id.getPath() + ".json");
      Optional<Resource> resource = resourceManager.getResource(resourcePath);

      if (resource.isEmpty()) {
        String classpathPath = "/assets/" + id.getNamespace() + "/" + id.getPath() + ".json";
        try (InputStream stream = HoloLogParser.class.getResourceAsStream(classpathPath)) {
          return stream != null;
        }
      }

      return resource.isPresent();
    } catch (Exception e) {
      return false;
    }
  }

  public static Optional<HoloLogData> getHoloLog(final ResourceLocation id) {
    try {
      Minecraft minecraft = Minecraft.getInstance();
      ResourceManager resourceManager = minecraft.getResourceManager();

      Optional<Resource> resource =
          resourceManager.getResource(
              new ResourceLocation(id.getNamespace(), id.getPath() + ".json"));

      InputStream stream = null;
      if (resource.isPresent()) {
        stream = resource.get().open();
        log.debug("Loaded hololog from ResourceManager: {}", id);
      } else {
        String classpathPath = "/assets/" + id.getNamespace() + "/" + id.getPath() + ".json";
        stream = HoloLogParser.class.getResourceAsStream(classpathPath);
        if (stream != null) {
          log.debug("Loaded hololog from classpath: {}", classpathPath);
        } else {
          log.error("Hololog not found: {}", id);
        }
      }

      if (stream != null) {
        try (BufferedReader reader =
            new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
          JsonObject json = GSON.fromJson(reader, JsonObject.class);
          HoloLogData data = HoloLogData.fromJson(json);

          // Validate line lengths for HoloPad display (max 57 characters recommended)
          validateLineLengths(data, id);

          log.debug("Parsed hololog: {}", id);
          return Optional.of(data);
        } finally {
          stream.close();
        }
      }
    } catch (Exception e) {
      log.error("Failed to load hololog {}: {}", id, e.getMessage(), e);
    }

    return Optional.empty();
  }

  private static void validateLineLengths(HoloLogData data, ResourceLocation id) {
    final int MAX_LINE_LENGTH = 57;

    for (int i = 0; i < data.lines().size(); i++) {
      String text = data.lines().get(i).text();
      if (text != null && text.length() > MAX_LINE_LENGTH) {
        log.warn(
            "Hololog '{}' line {} exceeds recommended length: {} characters (max {}): \"{}\"",
            id,
            i + 1,
            text.length(),
            MAX_LINE_LENGTH,
            text);
      }
    }
  }
}
