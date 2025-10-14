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

package de.markusbordihn.scraptechworkshop.spawner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.markusbordihn.scraptechworkshop.Constants;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RobotSpawnConfig {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Gson GSON = new Gson();

  public final boolean enabled;
  public final int weight;
  public final int minGroup;
  public final int maxGroup;
  public final String biomes;
  public final int minDistanceFromCenter;
  public final int despawnDistance;

  public RobotSpawnConfig(
      boolean enabled,
      int weight,
      int minGroup,
      int maxGroup,
      String biomes,
      int minDistanceFromCenter,
      int despawnDistance) {
    this.enabled = enabled;
    this.weight = weight;
    this.minGroup = minGroup;
    this.maxGroup = maxGroup;
    this.biomes = biomes;
    this.minDistanceFromCenter = minDistanceFromCenter;
    this.despawnDistance = despawnDistance;
  }

  public static RobotSpawnConfig load(String resourcePath) {
    try {
      InputStream stream =
          RobotSpawnConfig.class.getResourceAsStream(
              "/data/scrap_tech_workshop/forge/biome_modifier/" + resourcePath + "_spawn.json");

      if (stream == null) {
        log.warn("Could not find spawn config: {}, using defaults", resourcePath);
        return getDefaults();
      }

      JsonObject json =
          GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);

      int weight = 2;
      int minGroup = 1;
      int maxGroup = 1;
      String biomes = "#minecraft:is_overworld";

      if (json.has("spawners") && json.get("spawners").isJsonArray()) {
        JsonObject spawner = json.getAsJsonArray("spawners").get(0).getAsJsonObject();
        weight = spawner.has("weight") ? spawner.get("weight").getAsInt() : 2;
        minGroup = spawner.has("minCount") ? spawner.get("minCount").getAsInt() : 1;
        maxGroup = spawner.has("maxCount") ? spawner.get("maxCount").getAsInt() : 1;
      }

      if (json.has("biomes")) {
        biomes = json.get("biomes").getAsString();
      }

      return new RobotSpawnConfig(
          true,
          weight,
          minGroup,
          maxGroup,
          biomes,
          json.has("minDistanceFromCenter") ? json.get("minDistanceFromCenter").getAsInt() : 512,
          json.has("despawnDistance") ? json.get("despawnDistance").getAsInt() : 0);

    } catch (Exception e) {
      log.error("Failed to load spawn config: {}, using defaults", resourcePath, e);
      return getDefaults();
    }
  }

  private static RobotSpawnConfig getDefaults() {
    return new RobotSpawnConfig(true, 2, 1, 1, "#minecraft:is_overworld", 512, 0);
  }
}
