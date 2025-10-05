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

  // JSON field names
  private static final String FIELD_ID = "id";
  private static final String FIELD_TITLE = "title";
  private static final String FIELD_SUBTITLE = "subtitle";
  private static final String FIELD_TITLE_COLOR = "titleColor";
  private static final String FIELD_SUBTITLE_COLOR = "subtitleColor";
  private static final String FIELD_LINE_DELAY_TICKS = "lineDelayTicks";
  private static final String FIELD_CHAR_DELAY_TICKS = "charDelayTicks";
  private static final String FIELD_VOICE_OVER = "voiceOver";
  private static final String FIELD_DISPLAY_ENTITY = "displayEntity";
  private static final String FIELD_DISPLAY_BLOCK = "displayBlock";
  private static final String FIELD_DISPLAY_ITEM = "displayItem";
  private static final String FIELD_DISPLAY_HOLO_ENTITY = "displayHoloEntity";
  private static final String FIELD_START = "start";
  private static final String FIELD_LINES = "lines";
  private static final String FIELD_END = "end";
  private static final String FIELD_TEXT = "text";
  private static final String FIELD_SCALE = "scale";
  private static final String FIELD_ROTATION_SPEED = "rotationSpeed";
  private static final String FIELD_ROTATION_X = "rotationX";
  private static final String FIELD_ROTATION_Y = "rotationY";
  private static final String FIELD_ROTATION_Z = "rotationZ";
  private static final String FIELD_TEXTURE = "texture";
  private static final String FIELD_SLIM = "slim";
  private static final String FIELD_PLAY_SOUND = "playSound";
  private static final String FIELD_SHOW_PARTICLE = "showParticle";
  private static final String FIELD_VOLUME = "volume";
  private static final String FIELD_PITCH = "pitch";
  private static final String FIELD_COUNT = "count";
  private static final String FIELD_COLOR = "color";

  // Default values
  private static final String DEFAULT_TITLE_COLOR = "#FFFFFF";
  private static final String DEFAULT_SUBTITLE_COLOR = "#AAAAAA";
  private static final int DEFAULT_LINE_DELAY_TICKS = 40;
  private static final int DEFAULT_CHAR_DELAY_TICKS = 1;
  private static final float DEFAULT_SCALE = 0.5f;
  private static final float DEFAULT_ROTATION_SPEED = 1.0f;
  private static final float DEFAULT_VOLUME = 1.0f;
  private static final float DEFAULT_PITCH = 1.0f;
  private static final float DEFAULT_EFFECT_SCALE = 1.0f;
  private static final int DEFAULT_PARTICLE_COUNT = 5;

  // File extensions and paths
  private static final String JSON_EXTENSION = ".json";
  private static final String ASSETS_PATH_PREFIX = "/assets/";
  private static final String PATH_SEPARATOR = "/";
  private static final String LANGUAGE_SEPARATOR = "_";
  private static final String ENGLISH_LANGUAGE_CODE = "en";

  private HolologParser() {}

  public static ResourceLocation getLocalizedId(ResourceLocation id) {
    try {
      String[] parts = id.getPath().split(PATH_SEPARATOR, 2);
      if (parts.length < 2) {
        return id;
      }

      String languageCode =
          Minecraft.getInstance().getLanguageManager().getSelected().split(LANGUAGE_SEPARATOR)[0];
      ResourceLocation localizedId =
          new ResourceLocation(
              id.getNamespace(),
              parts[0] + PATH_SEPARATOR + languageCode + PATH_SEPARATOR + parts[1]);

      if (holologExists(localizedId)) {
        return localizedId;
      }

      if (!languageCode.equals(ENGLISH_LANGUAGE_CODE)) {
        ResourceLocation englishId =
            new ResourceLocation(
                id.getNamespace(),
                parts[0] + PATH_SEPARATOR + ENGLISH_LANGUAGE_CODE + PATH_SEPARATOR + parts[1]);
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
          new ResourceLocation(id.getNamespace(), id.getPath() + JSON_EXTENSION);
      Optional<Resource> resource = resourceManager.getResource(resourcePath);

      if (resource.isEmpty()) {
        String classpathPath =
            ASSETS_PATH_PREFIX + id.getNamespace() + PATH_SEPARATOR + id.getPath() + JSON_EXTENSION;
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
              new ResourceLocation(id.getNamespace(), id.getPath() + JSON_EXTENSION));

      InputStream stream = null;
      if (resource.isPresent()) {
        stream = resource.get().open();
        log.debug("Loaded hololog from ResourceManager: {}", id);
      } else {
        String classpathPath =
            ASSETS_PATH_PREFIX + id.getNamespace() + PATH_SEPARATOR + id.getPath() + JSON_EXTENSION;
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

    ResourceLocation id = new ResourceLocation(json.get(FIELD_ID).getAsString());
    String title = json.get(FIELD_TITLE).getAsString();
    String subtitle = json.has(FIELD_SUBTITLE) ? json.get(FIELD_SUBTITLE).getAsString() : "";
    String titleColor =
        json.has(FIELD_TITLE_COLOR)
            ? json.get(FIELD_TITLE_COLOR).getAsString()
            : DEFAULT_TITLE_COLOR;
    String subtitleColor =
        json.has(FIELD_SUBTITLE_COLOR)
            ? json.get(FIELD_SUBTITLE_COLOR).getAsString()
            : DEFAULT_SUBTITLE_COLOR;
    int lineDelayTicks =
        json.has(FIELD_LINE_DELAY_TICKS)
            ? json.get(FIELD_LINE_DELAY_TICKS).getAsInt()
            : DEFAULT_LINE_DELAY_TICKS;
    int charDelayTicks =
        json.has(FIELD_CHAR_DELAY_TICKS)
            ? json.get(FIELD_CHAR_DELAY_TICKS).getAsInt()
            : DEFAULT_CHAR_DELAY_TICKS;

    ResourceLocation voiceOver = null;
    if (json.has(FIELD_VOICE_OVER)) {
      voiceOver = new ResourceLocation(json.get(FIELD_VOICE_OVER).getAsString());
    }

    HolologData.HolologDisplayEntity displayEntity = null;
    if (json.has(FIELD_DISPLAY_ENTITY)) {
      displayEntity =
          parseDisplayEntity(
              json.getAsJsonObject(FIELD_DISPLAY_ENTITY), HolologData.DisplayType.ENTITY);
    } else if (json.has(FIELD_DISPLAY_BLOCK)) {
      displayEntity =
          parseDisplayEntity(
              json.getAsJsonObject(FIELD_DISPLAY_BLOCK), HolologData.DisplayType.BLOCK);
    } else if (json.has(FIELD_DISPLAY_ITEM)) {
      displayEntity =
          parseDisplayEntity(
              json.getAsJsonObject(FIELD_DISPLAY_ITEM), HolologData.DisplayType.ITEM);
    } else if (json.has(FIELD_DISPLAY_HOLO_ENTITY)) {
      displayEntity =
          parseDisplayEntity(
              json.getAsJsonObject(FIELD_DISPLAY_HOLO_ENTITY), HolologData.DisplayType.HOLO_ENTITY);
    }

    HolologData.HolologEffects start =
        json.has(FIELD_START)
            ? parseEffects(json.getAsJsonObject(FIELD_START))
            : HolologData.HolologEffects.EMPTY;

    List<HolologData.HolologLine> lines = new ArrayList<>();
    if (json.has(FIELD_LINES)) {
      JsonArray linesArray = json.getAsJsonArray(FIELD_LINES);
      for (JsonElement lineElement : linesArray) {
        JsonObject jsonObject = lineElement.getAsJsonObject();
        String text = jsonObject.has(FIELD_TEXT) ? jsonObject.get(FIELD_TEXT).getAsString() : "";

        int lineSpecificDelay =
            jsonObject.has(FIELD_LINE_DELAY_TICKS)
                ? jsonObject.get(FIELD_LINE_DELAY_TICKS).getAsInt()
                : -1;

        HolologData.HolologDisplayEntity lineDisplayEntity = null;
        if (jsonObject.has(FIELD_DISPLAY_ENTITY)) {
          lineDisplayEntity =
              parseDisplayEntity(
                  jsonObject.getAsJsonObject(FIELD_DISPLAY_ENTITY), HolologData.DisplayType.ENTITY);
        } else if (jsonObject.has(FIELD_DISPLAY_BLOCK)) {
          lineDisplayEntity =
              parseDisplayEntity(
                  jsonObject.getAsJsonObject(FIELD_DISPLAY_BLOCK), HolologData.DisplayType.BLOCK);
        } else if (jsonObject.has(FIELD_DISPLAY_ITEM)) {
          lineDisplayEntity =
              parseDisplayEntity(
                  jsonObject.getAsJsonObject(FIELD_DISPLAY_ITEM), HolologData.DisplayType.ITEM);
        } else if (jsonObject.has(FIELD_DISPLAY_HOLO_ENTITY)) {
          lineDisplayEntity =
              parseDisplayEntity(
                  jsonObject.getAsJsonObject(FIELD_DISPLAY_HOLO_ENTITY),
                  HolologData.DisplayType.HOLO_ENTITY);
        }

        HolologData.HolologEffects effects =
            jsonObject.has(FIELD_PLAY_SOUND) || jsonObject.has(FIELD_SHOW_PARTICLE)
                ? parseEffects(jsonObject)
                : HolologData.HolologEffects.EMPTY;
        lines.add(new HolologData.HolologLine(text, lineDisplayEntity, effects, lineSpecificDelay));
      }
    }

    HolologData.HolologEffects end =
        json.has(FIELD_END)
            ? parseEffects(json.getAsJsonObject(FIELD_END))
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

  private static HolologData.HolologDisplayEntity parseDisplayEntity(
      JsonObject json, HolologData.DisplayType defaultType) {
    ResourceLocation id = new ResourceLocation(json.get(FIELD_ID).getAsString());
    float scale = json.has(FIELD_SCALE) ? json.get(FIELD_SCALE).getAsFloat() : DEFAULT_SCALE;

    float rotationSpeed = 0.0f;
    if (defaultType != HolologData.DisplayType.ENTITY) {
      rotationSpeed =
          json.has(FIELD_ROTATION_SPEED)
              ? json.get(FIELD_ROTATION_SPEED).getAsFloat()
              : DEFAULT_ROTATION_SPEED;
    }

    float rotationX = json.has(FIELD_ROTATION_X) ? json.get(FIELD_ROTATION_X).getAsFloat() : 0.0f;
    float rotationY = json.has(FIELD_ROTATION_Y) ? json.get(FIELD_ROTATION_Y).getAsFloat() : 0.0f;
    float rotationZ = json.has(FIELD_ROTATION_Z) ? json.get(FIELD_ROTATION_Z).getAsFloat() : 0.0f;

    ResourceLocation texture = null;
    if (json.has(FIELD_TEXTURE)) {
      texture = new ResourceLocation(json.get(FIELD_TEXTURE).getAsString());
    }

    boolean slim = json.has(FIELD_SLIM) && json.get(FIELD_SLIM).getAsBoolean();

    return new HolologData.HolologDisplayEntity(
        defaultType, id, scale, rotationSpeed, rotationX, rotationY, rotationZ, texture, slim);
  }

  private static HolologData.HolologEffects parseEffects(JsonObject json) {
    List<HolologData.HolologSound> sounds = new ArrayList<>();

    if (json.has(FIELD_PLAY_SOUND)) {
      JsonArray soundArray = json.getAsJsonArray(FIELD_PLAY_SOUND);
      for (JsonElement soundElement : soundArray) {
        JsonObject soundObj = soundElement.getAsJsonObject();
        ResourceLocation id = new ResourceLocation(soundObj.get(FIELD_ID).getAsString());
        float volume =
            soundObj.has(FIELD_VOLUME) ? soundObj.get(FIELD_VOLUME).getAsFloat() : DEFAULT_VOLUME;
        float pitch =
            soundObj.has(FIELD_PITCH) ? soundObj.get(FIELD_PITCH).getAsFloat() : DEFAULT_PITCH;
        sounds.add(new HolologData.HolologSound(id, volume, pitch));
      }
    }

    List<HolologData.HolologParticle> particles = new ArrayList<>();

    if (json.has(FIELD_SHOW_PARTICLE)) {
      JsonArray particleArray = json.getAsJsonArray(FIELD_SHOW_PARTICLE);
      for (JsonElement particleElement : particleArray) {
        JsonObject particleObj = particleElement.getAsJsonObject();
        ResourceLocation id = new ResourceLocation(particleObj.get(FIELD_ID).getAsString());
        int count =
            particleObj.has(FIELD_COUNT)
                ? particleObj.get(FIELD_COUNT).getAsInt()
                : DEFAULT_PARTICLE_COUNT;
        String color =
            particleObj.has(FIELD_COLOR) ? particleObj.get(FIELD_COLOR).getAsString() : null;
        float scale =
            particleObj.has(FIELD_SCALE)
                ? particleObj.get(FIELD_SCALE).getAsFloat()
                : DEFAULT_EFFECT_SCALE;
        particles.add(new HolologData.HolologParticle(id, count, color, scale));
      }
    }

    return new HolologData.HolologEffects(sounds, particles);
  }

  public static void clearCache() {
    CACHE.clear();
  }
}
