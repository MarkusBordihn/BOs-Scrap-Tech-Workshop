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

import de.markusbordihn.scraptechworkshop.Constants;
import java.io.File;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapFishingConfig extends Config {

  public static final String CONFIG_FILE_NAME = "scrap_fishing.cfg";
  public static final String CONFIG_FILE_HEADER =
"""
 Scrap Fishing Configuration

 This configuration file allows you to define the settings for the Scrap Fishing system.

 Scrap Chances (percentage 0-100):
 - vanilla_rod_scrap_chance: Chance to catch scrap with vanilla fishing rod (default: 15%)
 - scrap_rod_scrap_chance: Chance to catch scrap with scrap fishing rod (default: 35%)
 - magnet_rod_scrap_chance: Chance to catch scrap with magnet fishing rod (default: 50%)

 Vanilla Rod Loot Weights (higher = more common):
 - vanilla_plastic_weight: Weight for plastic scrap (default: 40)
 - vanilla_rubber_weight: Weight for rubber scrap (default: 30)
 - vanilla_fastener_weight: Weight for fastener scrap (default: 20)
 - vanilla_metal_weight: Weight for metal scrap (default: 15)
 - vanilla_iron_weight: Weight for iron scrap (default: 10)
 - vanilla_circuit_weight: Weight for circuit scrap (default: 5)
 - vanilla_glass_weight: Weight for glass scrap (default: 10)

 Scrap Rod Loot Weights (higher = more common):
 - scrap_rod_metal_weight: Weight for metal scrap (default: 35)
 - scrap_rod_iron_weight: Weight for iron scrap (default: 30)
 - scrap_rod_copper_weight: Weight for copper scrap (default: 25)
 - scrap_rod_circuit_weight: Weight for circuit scrap (default: 20)
 - scrap_rod_capacitor_weight: Weight for capacitor scrap (default: 15)
 - scrap_rod_plastic_weight: Weight for plastic scrap (default: 20)
 - scrap_rod_rubber_weight: Weight for rubber scrap (default: 15)
 - scrap_rod_glass_weight: Weight for glass scrap (default: 10)

 Magnet Rod Loot Weights (higher = more common):
 - magnet_rod_circuit_weight: Weight for circuit scrap (default: 35)
 - magnet_rod_capacitor_weight: Weight for capacitor scrap (default: 30)
 - magnet_rod_coil_weight: Weight for coil scrap (default: 25)
 - magnet_rod_metal_weight: Weight for metal scrap (default: 30)
 - magnet_rod_iron_weight: Weight for iron scrap (default: 25)
 - magnet_rod_copper_weight: Weight for copper scrap (default: 20)
 - magnet_rod_alloy_weight: Weight for alloy scrap (default: 20)
 - magnet_rod_plastic_weight: Weight for plastic scrap (default: 5)

 Sound Settings:
 - enable_sounds: Enable sound effects for scrap fishing (default: true)
 - sound_volume: Volume for scrap fishing sounds 0.0-1.0 (default: 0.5)

""";

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Scrap chances
  public static int vanillaRodScrapChance = 15;
  public static int scrapRodScrapChance = 35;
  public static int magnetRodScrapChance = 50;

  // Vanilla rod loot weights
  public static int vanillaPlasticWeight = 40;
  public static int vanillaRubberWeight = 30;
  public static int vanillaFastenerWeight = 20;
  public static int vanillaMetalWeight = 15;
  public static int vanillaIronWeight = 10;
  public static int vanillaCircuitWeight = 5;
  public static int vanillaGlassWeight = 10;

  // Scrap rod loot weights (better than vanilla, focuses on metals and basic tech)
  public static int scrapRodMetalWeight = 35;
  public static int scrapRodIronWeight = 30;
  public static int scrapRodCopperWeight = 25;
  public static int scrapRodCircuitWeight = 20;
  public static int scrapRodCapacitorWeight = 15;
  public static int scrapRodPlasticWeight = 20;
  public static int scrapRodRubberWeight = 15;
  public static int scrapRodGlassWeight = 10;

  // Magnet rod loot weights (best quality, focuses on advanced tech and rare items)
  public static int magnetRodCircuitWeight = 35;
  public static int magnetRodCapacitorWeight = 30;
  public static int magnetRodCoilWeight = 25;
  public static int magnetRodMetalWeight = 30;
  public static int magnetRodIronWeight = 25;
  public static int magnetRodCopperWeight = 20;
  public static int magnetRodAlloyWeight = 20;
  public static int magnetRodPlasticWeight = 5;

  // Sound settings
  public static boolean enableSounds = true;
  public static float soundVolume = 0.5f;

  private ScrapFishingConfig() {}

  public static void registerConfig() {
    registerConfigFile(CONFIG_FILE_NAME, CONFIG_FILE_HEADER);
    loadConfig();
  }

  public static void loadConfig() {
    parseConfigFile();
  }

  public static void load() {
    log.info("{} Loading Scrap Fishing configuration ...", Constants.LOG_NAME);
    parseConfigFile();
  }

  public static void parseConfigFile() {
    File configFile = getConfigFile(CONFIG_FILE_NAME);
    Properties properties = readConfigFile(configFile);
    Properties unmodifiedProperties = (Properties) properties.clone();

    // Parse scrap chances
    vanillaRodScrapChance =
        Math.max(
            0,
            Math.min(
                100,
                parseConfigValue(properties, "vanilla_rod_scrap_chance", vanillaRodScrapChance)));
    scrapRodScrapChance =
        Math.max(
            0,
            Math.min(
                100, parseConfigValue(properties, "scrap_rod_scrap_chance", scrapRodScrapChance)));
    magnetRodScrapChance =
        Math.max(
            0,
            Math.min(
                100,
                parseConfigValue(properties, "magnet_rod_scrap_chance", magnetRodScrapChance)));

    // Parse vanilla rod weights
    vanillaPlasticWeight =
        Math.max(0, parseConfigValue(properties, "vanilla_plastic_weight", vanillaPlasticWeight));
    vanillaRubberWeight =
        Math.max(0, parseConfigValue(properties, "vanilla_rubber_weight", vanillaRubberWeight));
    vanillaFastenerWeight =
        Math.max(0, parseConfigValue(properties, "vanilla_fastener_weight", vanillaFastenerWeight));
    vanillaMetalWeight =
        Math.max(0, parseConfigValue(properties, "vanilla_metal_weight", vanillaMetalWeight));
    vanillaIronWeight =
        Math.max(0, parseConfigValue(properties, "vanilla_iron_weight", vanillaIronWeight));
    vanillaCircuitWeight =
        Math.max(0, parseConfigValue(properties, "vanilla_circuit_weight", vanillaCircuitWeight));
    vanillaGlassWeight =
        Math.max(0, parseConfigValue(properties, "vanilla_glass_weight", vanillaGlassWeight));

    // Parse scrap rod weights
    scrapRodMetalWeight =
        Math.max(0, parseConfigValue(properties, "scrap_rod_metal_weight", scrapRodMetalWeight));
    scrapRodIronWeight =
        Math.max(0, parseConfigValue(properties, "scrap_rod_iron_weight", scrapRodIronWeight));
    scrapRodCopperWeight =
        Math.max(0, parseConfigValue(properties, "scrap_rod_copper_weight", scrapRodCopperWeight));
    scrapRodCircuitWeight =
        Math.max(
            0, parseConfigValue(properties, "scrap_rod_circuit_weight", scrapRodCircuitWeight));
    scrapRodCapacitorWeight =
        Math.max(
            0, parseConfigValue(properties, "scrap_rod_capacitor_weight", scrapRodCapacitorWeight));
    scrapRodPlasticWeight =
        Math.max(
            0, parseConfigValue(properties, "scrap_rod_plastic_weight", scrapRodPlasticWeight));
    scrapRodRubberWeight =
        Math.max(0, parseConfigValue(properties, "scrap_rod_rubber_weight", scrapRodRubberWeight));
    scrapRodGlassWeight =
        Math.max(0, parseConfigValue(properties, "scrap_rod_glass_weight", scrapRodGlassWeight));

    // Parse magnet rod weights
    magnetRodCircuitWeight =
        Math.max(
            0, parseConfigValue(properties, "magnet_rod_circuit_weight", magnetRodCircuitWeight));
    magnetRodCapacitorWeight =
        Math.max(
            0,
            parseConfigValue(properties, "magnet_rod_capacitor_weight", magnetRodCapacitorWeight));
    magnetRodCoilWeight =
        Math.max(0, parseConfigValue(properties, "magnet_rod_coil_weight", magnetRodCoilWeight));
    magnetRodMetalWeight =
        Math.max(0, parseConfigValue(properties, "magnet_rod_metal_weight", magnetRodMetalWeight));
    magnetRodIronWeight =
        Math.max(0, parseConfigValue(properties, "magnet_rod_iron_weight", magnetRodIronWeight));
    magnetRodCopperWeight =
        Math.max(
            0, parseConfigValue(properties, "magnet_rod_copper_weight", magnetRodCopperWeight));
    magnetRodAlloyWeight =
        Math.max(0, parseConfigValue(properties, "magnet_rod_alloy_weight", magnetRodAlloyWeight));
    magnetRodPlasticWeight =
        Math.max(
            0, parseConfigValue(properties, "magnet_rod_plastic_weight", magnetRodPlasticWeight));

    // Parse sound settings
    enableSounds = parseConfigValue(properties, "enable_sounds", enableSounds);
    soundVolume =
        Math.max(0.0f, Math.min(1.0f, parseConfigValue(properties, "sound_volume", soundVolume)));

    // Update config file if needed
    updateConfigFileIfChanged(configFile, CONFIG_FILE_HEADER, properties, unmodifiedProperties);
  }
}
