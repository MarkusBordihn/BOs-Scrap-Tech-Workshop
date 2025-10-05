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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.markusbordihn.scraptechworkshop.Constants;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HolologParser {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Gson GSON = new Gson();
  private static final Map<ResourceLocation, HolologData> CACHE = new HashMap<>();

  private HolologParser() {}

  public static ResourceLocation getLocalizedId(ResourceLocation id) {
    try {
      String[] parts = id.getPath().split("/", 2);
      if (parts.length < 2) {
        return id;
      }

      String languageCode =
          Minecraft.getInstance().getLanguageManager().getSelected().split("_")[0];
      ResourceLocation localizedId =
          new ResourceLocation(id.getNamespace(), parts[0] + "/" + languageCode + "/" + parts[1]);

      if (holologExists(localizedId)) {
        return localizedId;
      }

      if (!languageCode.equals("en")) {
        ResourceLocation englishId =
            new ResourceLocation(id.getNamespace(), parts[0] + "/en/" + parts[1]);
        if (holologExists(englishId)) {
          log.debug("Using English fallback hololog: {}", englishId);
          return englishId;
        }
      }

      log.debug("Using original hololog path: {}", id);
      return id;
    } catch (Exception e) {
      log.debug("Cannot access client (server-side?), using original path: {}", id);
      return id;
    }
  }

  private static boolean holologExists(ResourceLocation id) {
    if (CACHE.containsKey(id)) {
      return true;
    }

    try {
      Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
      ResourceManager resourceManager = minecraft.getResourceManager();

      ResourceLocation resourcePath =
          new ResourceLocation(id.getNamespace(), id.getPath() + ".json");
      Optional<Resource> resource = resourceManager.getResource(resourcePath);

      if (resource.isEmpty()) {
        String classpathPath = "/assets/" + id.getNamespace() + "/" + id.getPath() + ".json";
        try (InputStream stream = HolologParser.class.getResourceAsStream(classpathPath)) {
          return stream != null;
        }
      }

      return resource.isPresent();
    } catch (Exception e) {
      return false;
    }
  }

  public static Optional<HolologData> getHololog(ResourceLocation id) {
    if (CACHE.containsKey(id)) {
      return Optional.of(CACHE.get(id));
    }

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
        stream = HolologParser.class.getResourceAsStream(classpathPath);
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
          Optional<HolologData> data = parseHolologJson(json);
          data.ifPresent(
              hololog -> {
                CACHE.put(id, hololog);
                log.debug("Cached hololog: {}", id);
              });
          return data;
        } finally {
          stream.close();
        }
      }
    } catch (Exception e) {
      log.error("Failed to load hololog {}: {}", id, e.getMessage(), e);
    }

    return Optional.empty();
  }

  private static Optional<HolologData> parseHolologJson(JsonObject json) {
    if (json == null) {
      log.warn("Empty or invalid JSON");
      return Optional.empty();
    }

    ResourceLocation id = new ResourceLocation(json.get("id").getAsString());
    String title = json.get("title").getAsString();
    String subtitle = json.has("subtitle") ? json.get("subtitle").getAsString() : "";
    String titleColor = json.has("titleColor") ? json.get("titleColor").getAsString() : "#FFFFFF";
    String subtitleColor =
        json.has("subtitleColor") ? json.get("subtitleColor").getAsString() : "#AAAAAA";
    int lineDelayTicks = json.has("lineDelayTicks") ? json.get("lineDelayTicks").getAsInt() : 40;
    int charDelayTicks = json.has("charDelayTicks") ? json.get("charDelayTicks").getAsInt() : 1;

    ResourceLocation voiceOver = null;
    if (json.has("voiceOver")) {
      voiceOver = new ResourceLocation(json.get("voiceOver").getAsString());
    }

    HolologData.HolologDisplayEntity displayEntity = null;
    if (json.has("displayEntity")) {
      displayEntity = parseDisplayEntity(json.getAsJsonObject("displayEntity"));
    }

    HolologData.HolologEffects start =
        json.has("start")
            ? parseEffects(json.getAsJsonObject("start"))
            : HolologData.HolologEffects.EMPTY;

    List<HolologData.HolologLine> lines = new ArrayList<>();
    if (json.has("lines")) {
      JsonArray linesArray = json.getAsJsonArray("lines");
      for (JsonElement lineElement : linesArray) {
        JsonObject lineObj = lineElement.getAsJsonObject();
        String text = lineObj.get("text").getAsString();

        HolologData.HolologDisplayEntity lineDisplayEntity = null;
        if (lineObj.has("displayEntity")) {
          lineDisplayEntity = parseDisplayEntity(lineObj.getAsJsonObject("displayEntity"));
        }

        HolologData.HolologEffects effects =
            lineObj.has("sfx") || lineObj.has("fx")
                ? parseEffects(lineObj)
                : HolologData.HolologEffects.EMPTY;
        lines.add(new HolologData.HolologLine(text, lineDisplayEntity, effects));
      }
    }

    HolologData.HolologEffects end =
        json.has("end")
            ? parseEffects(json.getAsJsonObject("end"))
            : HolologData.HolologEffects.EMPTY;

    return Optional.of(
        new HolologData(
            id,
            title,
            subtitle,
            titleColor,
            subtitleColor,
            lineDelayTicks,
            charDelayTicks,
            voiceOver,
            displayEntity,
            start,
            lines,
            end));
  }

  private static HolologData.HolologDisplayEntity parseDisplayEntity(JsonObject json) {
    HolologData.DisplayType type;
    try {
      type =
          HolologData.DisplayType.valueOf(
              json.has("type") ? json.get("type").getAsString().toUpperCase() : "ENTITY");
    } catch (IllegalArgumentException e) {
      log.warn(
          "Invalid display type '{}', defaulting to ENTITY",
          json.has("type") ? json.get("type").getAsString() : "null");
      type = HolologData.DisplayType.ENTITY;
    }

    ResourceLocation id = new ResourceLocation(json.get("id").getAsString());
    float scale = json.has("scale") ? json.get("scale").getAsFloat() : 0.5f;
    float rotationSpeed = json.has("rotationSpeed") ? json.get("rotationSpeed").getAsFloat() : 1.0f;

    return new HolologData.HolologDisplayEntity(type, id, scale, rotationSpeed);
  }

  private static HolologData.HolologEffects parseEffects(JsonObject json) {
    List<HolologData.HolologSound> sounds = new ArrayList<>();
    if (json.has("sfx")) {
      JsonArray sfxArray = json.getAsJsonArray("sfx");
      for (JsonElement sfxElement : sfxArray) {
        JsonObject sfxObj = sfxElement.getAsJsonObject();
        ResourceLocation id = new ResourceLocation(sfxObj.get("id").getAsString());
        float volume = sfxObj.has("volume") ? sfxObj.get("volume").getAsFloat() : 1.0f;
        float pitch = sfxObj.has("pitch") ? sfxObj.get("pitch").getAsFloat() : 1.0f;
        sounds.add(new HolologData.HolologSound(id, volume, pitch));
      }
    }

    List<HolologData.HolologParticle> particles = new ArrayList<>();
    if (json.has("fx")) {
      JsonArray fxArray = json.getAsJsonArray("fx");
      for (JsonElement fxElement : fxArray) {
        JsonObject fxObj = fxElement.getAsJsonObject();
        ResourceLocation id = new ResourceLocation(fxObj.get("id").getAsString());
        int count = fxObj.has("count") ? fxObj.get("count").getAsInt() : 5;
        String color = fxObj.has("color") ? fxObj.get("color").getAsString() : null;
        float scale = fxObj.has("scale") ? fxObj.get("scale").getAsFloat() : 1.0f;
        particles.add(new HolologData.HolologParticle(id, count, color, scale));
      }
    }

    return new HolologData.HolologEffects(sounds, particles);
  }

  public static void clearCache() {
    CACHE.clear();
  }
}
