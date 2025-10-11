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

public class CollectorStationConfig extends Config {

  public static final String CONFIG_FILE_NAME = "collector_station.cfg";
  public static final String CONFIG_FILE_HEADER =
"""
 Collector Station Configuration

 This configuration file allows you to define the general settings for Collector Stations.
 collectionRadius: Radius in blocks where the robot will collect scraps (default: 16)
 collectingTime: Time in ticks for robot to collect scrap (default: 12000 = 10 minutes)
 returningTime: Time in ticks for robot to return with scrap (default: 120 = 6 seconds)
 processingTime: Time in ticks to process collected scrap (default: 40 = 2 seconds)
 chargingTime: Time in ticks for robot to charge after returning (default: 1200 = 1 minute)
 checkInterval: Time in ticks between status checks (default: 20 = 1 second)
 energyPerCycle: Energy consumed per collection cycle (default: 100)
 maxStorageSlots: Number of storage slots in the station (default: 24)
 requiresBattery: Whether the station requires a battery to operate (default: true)

""";

  // Default values - Timings in ticks (20 ticks = 1 second)
  public static final int DEFAULT_COLLECTION_RADIUS = 16;
  public static final int DEFAULT_COLLECTING_TIME = 12000;
  public static final int DEFAULT_RETURNING_TIME = 300;
  public static final int DEFAULT_PROCESSING_TIME = 40;
  public static final int DEFAULT_CHARGING_TIME = 1200;
  public static final int DEFAULT_CHECK_INTERVAL = 20;
  public static final int DEFAULT_ENERGY_PER_CYCLE = 100;
  public static final int DEFAULT_MAX_STORAGE_SLOTS = 24;
  public static final boolean DEFAULT_REQUIRES_BATTERY = true;

  // Configurable values
  public static int collectionRadius = DEFAULT_COLLECTION_RADIUS;
  public static int collectingTime = DEFAULT_COLLECTING_TIME;
  public static int returningTime = DEFAULT_RETURNING_TIME;
  public static int processingTime = DEFAULT_PROCESSING_TIME;
  public static int chargingTime = DEFAULT_CHARGING_TIME;
  public static int checkInterval = DEFAULT_CHECK_INTERVAL;
  public static int energyPerCycle = DEFAULT_ENERGY_PER_CYCLE;
  public static int maxStorageSlots = DEFAULT_MAX_STORAGE_SLOTS;
  public static boolean requiresBattery = DEFAULT_REQUIRES_BATTERY;

  public static void registerConfig() {
    registerConfigFile(CONFIG_FILE_NAME, CONFIG_FILE_HEADER);
    File configFile = getConfigFile(CONFIG_FILE_NAME);
    if (configFile == null) {
      return;
    }

    Properties properties = readConfigFile(configFile);
    Properties unmodifiedProperties = (Properties) properties.clone();

    // Load configuration values
    collectionRadius = parseConfigValue(properties, "collectionRadius", DEFAULT_COLLECTION_RADIUS);
    collectingTime = parseConfigValue(properties, "collectingTime", DEFAULT_COLLECTING_TIME);
    returningTime = parseConfigValue(properties, "returningTime", DEFAULT_RETURNING_TIME);
    processingTime = parseConfigValue(properties, "processingTime", DEFAULT_PROCESSING_TIME);
    chargingTime = parseConfigValue(properties, "chargingTime", DEFAULT_CHARGING_TIME);
    checkInterval = parseConfigValue(properties, "checkInterval", DEFAULT_CHECK_INTERVAL);
    energyPerCycle = parseConfigValue(properties, "energyPerCycle", DEFAULT_ENERGY_PER_CYCLE);
    maxStorageSlots = parseConfigValue(properties, "maxStorageSlots", DEFAULT_MAX_STORAGE_SLOTS);
    requiresBattery = parseConfigValue(properties, "requiresBattery", DEFAULT_REQUIRES_BATTERY);

    // Update configuration file if needed
    updateConfigFileIfChanged(configFile, CONFIG_FILE_HEADER, properties, unmodifiedProperties);
  }
}
