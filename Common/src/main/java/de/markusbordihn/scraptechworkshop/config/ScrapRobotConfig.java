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

package de.markusbordihn.scraptechworkshop.config;

import java.io.File;
import java.util.Properties;

public class ScrapRobotConfig extends Config {

  public static final String CONFIG_FILE_NAME = "scrap_robot.cfg";
  public static final String CONFIG_FILE_HEADER =
"""
 Scrap Robot Configuration

 This configuration file allows you to define spawn settings for Scrap Robots.

 Spawn Settings:
 - spawn_enabled: Enable/disable natural spawning of scrap robots
 - spawn_weight: Spawn weight (higher = more common, vanilla mobs are usually 10-100)
 - spawn_min_group: Minimum number of robots per spawn
 - spawn_max_group: Maximum number of robots per spawn
 - spawn_biome_type: Biome type filter (ALL, OVERWORLD, NETHER, END, PLAINS, FOREST, DESERT, etc.)
 - despawn_distance: Distance at which robots can despawn (0 = never despawn)

 Performance:
 - Lower spawn_weight for better performance
 - Keep spawn groups small (1-2) to prevent lag
 - Consider disabling if you have performance issues

""";

  // Mixed Scrap Robot Spawn Settings
  public static boolean mixedScrapRobotSpawnEnabled = true;
  public static int mixedScrapRobotSpawnWeight = 3;
  public static int mixedScrapRobotSpawnMinGroup = 1;
  public static int mixedScrapRobotSpawnMaxGroup = 1;
  public static String mixedScrapRobotBiomeType = "OVERWORLD";
  public static int mixedScrapRobotDespawnDistance = 0;

  private ScrapRobotConfig() {}

  public static void registerConfig() {
    File configFile = getConfigFile(CONFIG_FILE_NAME);
    registerConfigFile(CONFIG_FILE_NAME, CONFIG_FILE_HEADER);

    // Load config
    Properties properties = readConfigFile(configFile);
    Properties unmodifiedProperties = new Properties();
    unmodifiedProperties.putAll(properties);

    // Parse spawn settings
    mixedScrapRobotSpawnEnabled =
        parseConfigValue(properties, "spawn_enabled", mixedScrapRobotSpawnEnabled);
    mixedScrapRobotSpawnWeight =
        parseConfigValue(properties, "spawn_weight", mixedScrapRobotSpawnWeight);
    mixedScrapRobotSpawnMinGroup =
        parseConfigValue(properties, "spawn_min_group", mixedScrapRobotSpawnMinGroup);
    mixedScrapRobotSpawnMaxGroup =
        parseConfigValue(properties, "spawn_max_group", mixedScrapRobotSpawnMaxGroup);
    mixedScrapRobotBiomeType =
        parseConfigValue(properties, "spawn_biome_type", mixedScrapRobotBiomeType);
    mixedScrapRobotDespawnDistance =
        parseConfigValue(properties, "despawn_distance", mixedScrapRobotDespawnDistance);

    // Validate settings
    if (mixedScrapRobotSpawnWeight < 0) {
      mixedScrapRobotSpawnWeight = 0;
      log.warn("Invalid spawn_weight, setting to 0 (disabled)");
    }
    if (mixedScrapRobotSpawnMinGroup < 1) {
      mixedScrapRobotSpawnMinGroup = 1;
    }
    if (mixedScrapRobotSpawnMaxGroup < mixedScrapRobotSpawnMinGroup) {
      mixedScrapRobotSpawnMaxGroup = mixedScrapRobotSpawnMinGroup;
    }

    // Update config file if needed
    updateConfigFileIfChanged(configFile, CONFIG_FILE_HEADER, properties, unmodifiedProperties);
  }
}
