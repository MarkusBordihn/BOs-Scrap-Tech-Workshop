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
import java.util.Set;

public class RecyclerConfig extends Config {

  public static final String CONFIG_FILE_NAME = "recycler.cfg";
  public static final String CONFIG_FILE_HEADER =
"""
 Recycler Configuration

 This configuration file allows you to define the general settings for the Recycler system.
 processTime: Time in ticks for processing items (default: 200)
 enableEnergy: Whether the recycler requires energy (default: false)
 energyPerOperation: Energy required per operation when energy is enabled (default: 200)
 durabilityScaling: Whether processing time scales with item durability (default: true)
 byproductMode: Mode for byproduct generation - "multi" or "single" (default: "multi")
 maxByproductsPerOperation: Maximum number of byproducts per operation (default: 2)
 deniedItems: Comma-separated list of item IDs that cannot be recycled
 allowedItems: Comma-separated list of item IDs that can be recycled (empty = all allowed)
 progressUpdateInterval: Interval in ticks for progress updates (default: 5)

""";

  // Default values
  public static final int DEFAULT_PROCESS_TIME = 200;
  public static final boolean DEFAULT_ENABLE_ENERGY = false;
  public static final int DEFAULT_ENERGY_PER_OPERATION = 200;
  public static final boolean DEFAULT_DURABILITY_SCALING = true;
  public static final String DEFAULT_BYPRODUCT_MODE = "multi";
  public static final int DEFAULT_MAX_BYPRODUCTS_PER_OPERATION = 2;
  public static final int DEFAULT_PROGRESS_UPDATE_INTERVAL = 5;

  // Configurable values
  public static int processTime = DEFAULT_PROCESS_TIME;
  public static boolean enableEnergy = DEFAULT_ENABLE_ENERGY;
  public static int energyPerOperation = DEFAULT_ENERGY_PER_OPERATION;
  public static boolean durabilityScaling = DEFAULT_DURABILITY_SCALING;
  public static String byproductMode = DEFAULT_BYPRODUCT_MODE;
  public static int maxByproductsPerOperation = DEFAULT_MAX_BYPRODUCTS_PER_OPERATION;
  public static Set<String> deniedItems = Set.of();
  public static Set<String> allowedItems = Set.of();
  public static int progressUpdateInterval = DEFAULT_PROGRESS_UPDATE_INTERVAL;

  public static void registerConfig() {
    registerConfigFile(CONFIG_FILE_NAME, CONFIG_FILE_HEADER);
    parseConfigFile();
  }

  public static void parseConfigFile() {
    File configFile = getConfigFile(CONFIG_FILE_NAME);
    Properties properties = readConfigFile(configFile);
    Properties unmodifiedProperties = (Properties) properties.clone();

    // Config entries
    processTime = parseConfigValue(properties, "processTime", processTime);
    enableEnergy = parseConfigValue(properties, "enableEnergy", enableEnergy);
    energyPerOperation = parseConfigValue(properties, "energyPerOperation", energyPerOperation);
    durabilityScaling = parseConfigValue(properties, "durabilityScaling", durabilityScaling);
    byproductMode = parseConfigValue(properties, "byproductMode", byproductMode);
    maxByproductsPerOperation =
        parseConfigValue(properties, "maxByproductsPerOperation", maxByproductsPerOperation);
    deniedItems = parseConfigValue(properties, "deniedItems", deniedItems);
    allowedItems = parseConfigValue(properties, "allowedItems", allowedItems);
    progressUpdateInterval =
        parseConfigValue(properties, "progressUpdateInterval", progressUpdateInterval);

    // Update config file if needed
    updateConfigFileIfChanged(configFile, CONFIG_FILE_HEADER, properties, unmodifiedProperties);
  }

  public static boolean isItemDenied(String itemId) {
    return deniedItems.contains(itemId);
  }

  public static boolean isItemAllowed(String itemId) {
    return allowedItems.isEmpty() || allowedItems.contains(itemId);
  }

  public static boolean isMultiByproductMode() {
    return "multi".equals(byproductMode);
  }
}
