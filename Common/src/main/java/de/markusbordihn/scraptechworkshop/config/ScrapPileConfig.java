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

public class ScrapPileConfig extends Config {

  public static final String CONFIG_FILE_NAME = "scrap_pile.cfg";
  public static final String CONFIG_FILE_HEADER =
"""
 Scrap Pile Configuration

 This configuration file allows you to define the general settings for the Scrap Pile system.

 Spawn Settings:
 - spawn_enabled: Enable/disable natural spawning of scrap piles
 - spawn_attempts_per_tick: Number of spawn attempts per game tick (keep low for performance)
 - spawn_chunk_cap: Maximum number of scrap piles per chunk
 - spawn_y_max: Maximum Y level for spawning (underground spawning)
 - spawn_interval_ticks: Time between spawn attempts in ticks (600 = 30 seconds, 1200 = 1 minute)
 - spawn_darkness_required: Whether spawning requires darkness

 Pile Settings:
 - max_size: Maximum size of a scrap pile (1-4)
 - decay_enabled: Enable/disable time-based decay
 - decay_chance: Chance per random tick for decay (higher = faster decay)
 - auto_pickup_enabled: Enable pickup when walking over piles
 - auto_pickup_delay_ticks: Delay in ticks between automatic pickups (default: 20 = 1 second, 0 = instant complete pickup)
 - auto_merge_enabled: Enable automatic merging of adjacent piles
 - silk_touch_enabled: Allow silk touch harvesting
 - fortune_enabled: Enable fortune enchantment effects

 Loot Settings:
 - Various weight settings for different scrap types

""";

  // Spawn settings
  public static boolean spawnEnabled = true;
  public static int spawnAttemptsPerTick = 1;
  public static int spawnChunkCap = 2;
  public static int spawnYMax = 40;
  public static int spawnIntervalTicks = 600;
  public static boolean spawnDarknessRequired = true;

  // Pile settings
  public static int maxSize = 4;
  public static boolean decayEnabled = true;
  public static int decayChance = 24000; // Higher value = slower decay
  public static boolean autoPickupEnabled = true;
  public static int autoPickupDelayTicks = 20; // 1 second delay between pickups
  public static boolean autoMergeEnabled = false;
  public static boolean silkTouchEnabled = true;
  public static boolean fortuneEnabled = true;

  // Loot weights (higher = more common)
  public static int alloyScrapWeight = 12;
  public static int metalScrapWeight = 40;
  public static int ironScrapWeight = 30;
  public static int copperScrapWeight = 25;
  public static int goldScrapWeight = 10;
  public static int techScrapWeight = 20;
  public static int circuitScrapWeight = 15;
  public static int coilScrapWeight = 15;
  public static int capacitorScrapWeight = 8;
  public static int energyCellScrapWeight = 5;
  public static int crystalScrapWeight = 5;
  public static int insulationScrapWeight = 18;
  public static int luminousScrapWeight = 3;
  public static int plasticScrapWeight = 25;
  public static int woodScrapWeight = 30;

  protected ScrapPileConfig() {}

  public static void registerConfig() {
    registerConfigFile(CONFIG_FILE_NAME, CONFIG_FILE_HEADER);
    loadConfig();
  }

  public static void loadConfig() {
    parseConfigFile();
  }

  public static void parseConfigFile() {
    File configFile = getConfigFile(CONFIG_FILE_NAME);
    Properties properties = readConfigFile(configFile);
    Properties unmodifiedProperties = (Properties) properties.clone();

    // Parse spawn settings
    spawnEnabled = parseConfigValue(properties, "spawn_enabled", spawnEnabled);
    spawnAttemptsPerTick =
        parseConfigValue(properties, "spawn_attempts_per_tick", spawnAttemptsPerTick);
    spawnChunkCap = parseConfigValue(properties, "spawn_chunk_cap", spawnChunkCap);
    spawnYMax = parseConfigValue(properties, "spawn_y_max", spawnYMax);
    spawnIntervalTicks = parseConfigValue(properties, "spawn_interval_ticks", spawnIntervalTicks);
    spawnDarknessRequired =
        parseConfigValue(properties, "spawn_darkness_required", spawnDarknessRequired);

    // Parse pile settings
    maxSize = parseConfigValue(properties, "max_size", maxSize);
    decayEnabled = parseConfigValue(properties, "decay_enabled", decayEnabled);
    decayChance = parseConfigValue(properties, "decay_chance", decayChance);
    autoPickupEnabled = parseConfigValue(properties, "auto_pickup_enabled", autoPickupEnabled);
    autoPickupDelayTicks =
        parseConfigValue(properties, "auto_pickup_delay_ticks", autoPickupDelayTicks);
    autoMergeEnabled = parseConfigValue(properties, "auto_merge_enabled", autoMergeEnabled);
    silkTouchEnabled = parseConfigValue(properties, "silk_touch_enabled", silkTouchEnabled);
    fortuneEnabled = parseConfigValue(properties, "fortune_enabled", fortuneEnabled);

    // Parse loot weights
    alloyScrapWeight = parseConfigValue(properties, "alloy_scrap_weight", alloyScrapWeight);
    metalScrapWeight = parseConfigValue(properties, "metal_scrap_weight", metalScrapWeight);
    ironScrapWeight = parseConfigValue(properties, "iron_scrap_weight", ironScrapWeight);
    copperScrapWeight = parseConfigValue(properties, "copper_scrap_weight", copperScrapWeight);
    goldScrapWeight = parseConfigValue(properties, "gold_scrap_weight", goldScrapWeight);
    techScrapWeight = parseConfigValue(properties, "tech_scrap_weight", techScrapWeight);
    circuitScrapWeight = parseConfigValue(properties, "circuit_scrap_weight", circuitScrapWeight);
    coilScrapWeight = parseConfigValue(properties, "coil_scrap_weight", coilScrapWeight);
    capacitorScrapWeight =
        parseConfigValue(properties, "capacitor_scrap_weight", capacitorScrapWeight);
    energyCellScrapWeight =
        parseConfigValue(properties, "energy_cell_scrap_weight", energyCellScrapWeight);
    crystalScrapWeight = parseConfigValue(properties, "crystal_scrap_weight", crystalScrapWeight);
    insulationScrapWeight =
        parseConfigValue(properties, "insulation_scrap_weight", insulationScrapWeight);
    luminousScrapWeight =
        parseConfigValue(properties, "luminous_scrap_weight", luminousScrapWeight);
    plasticScrapWeight = parseConfigValue(properties, "plastic_scrap_weight", plasticScrapWeight);
    woodScrapWeight = parseConfigValue(properties, "wood_scrap_weight", woodScrapWeight);

    // Update config file if needed
    updateConfigFileIfChanged(configFile, CONFIG_FILE_HEADER, properties, unmodifiedProperties);
  }
}
