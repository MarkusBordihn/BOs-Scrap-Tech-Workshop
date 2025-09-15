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

public class ScrapDropConfig extends Config {

  public static final String CONFIG_FILE_NAME = "scrap_drop.cfg";
  public static final String CONFIG_FILE_HEADER =
"""
 Scrap Drop Configuration

 This configuration file allows you to define the general settings for the Scrap Drop system.
 Drop chances are in percentage (0-100). Set to 0 to disable drops for that type.
 Sound effects can be enabled/disabled and volume adjusted (0.0-1.0).

""";

  public static int bioScrapDropChance = 1;
  public static int ceramicScrapDropChance = 1;
  public static int copperScrapDropChance = 2;
  public static int crystalScrapDropChance = 1;
  public static int fastenerScrapDropChance = 1;
  public static int fiberScrapDropChance = 1;
  public static int glassScrapDropChance = 1;
  public static int goldScrapDropChance = 2;
  public static int ironScrapDropChance = 2;
  public static int luminousScrapDropChance = 3;
  public static int metalScrapDropChance = 1;
  public static int mineralScrapDropChance = 1;
  public static int plasticScrapDropChance = 1;
  public static int rubberScrapDropChance = 1;
  public static int techScrapDropChance = 2;
  public static int woodScrapDropChance = 1;

  public static boolean enableDropSounds = true;
  public static float dropSoundVolume = 0.3f;

  public static void registerConfig() {
    registerConfigFile(CONFIG_FILE_NAME, CONFIG_FILE_HEADER);
    parseConfigFile();
  }

  public static void parseConfigFile() {
    File configFile = getConfigFile(CONFIG_FILE_NAME);
    Properties properties = readConfigFile(configFile);
    Properties unmodifiedProperties = (Properties) properties.clone();

    // Config entries
    bioScrapDropChance = parseConfigValue(properties, "bioScrapDropChance", bioScrapDropChance);
    ceramicScrapDropChance =
        parseConfigValue(properties, "ceramicScrapDropChance", ceramicScrapDropChance);
    copperScrapDropChance =
        parseConfigValue(properties, "copperScrapDropChance", copperScrapDropChance);
    crystalScrapDropChance =
        parseConfigValue(properties, "crystalScrapDropChance", crystalScrapDropChance);
    fastenerScrapDropChance =
        parseConfigValue(properties, "fastenerScrapDropChance", fastenerScrapDropChance);
    fiberScrapDropChance =
        parseConfigValue(properties, "fiberScrapDropChance", fiberScrapDropChance);
    glassScrapDropChance =
        parseConfigValue(properties, "glassScrapDropChance", glassScrapDropChance);
    goldScrapDropChance = parseConfigValue(properties, "goldScrapDropChance", goldScrapDropChance);
    ironScrapDropChance = parseConfigValue(properties, "ironScrapDropChance", ironScrapDropChance);
    luminousScrapDropChance =
        parseConfigValue(properties, "luminousScrapDropChance", luminousScrapDropChance);
    metalScrapDropChance =
        parseConfigValue(properties, "metalScrapDropChance", metalScrapDropChance);
    mineralScrapDropChance =
        parseConfigValue(properties, "mineralScrapDropChance", mineralScrapDropChance);
    plasticScrapDropChance =
        parseConfigValue(properties, "plasticScrapDropChance", plasticScrapDropChance);
    rubberScrapDropChance =
        parseConfigValue(properties, "rubberScrapDropChance", rubberScrapDropChance);
    techScrapDropChance = parseConfigValue(properties, "techScrapDropChance", techScrapDropChance);
    woodScrapDropChance = parseConfigValue(properties, "woodScrapDropChance", woodScrapDropChance);

    enableDropSounds = parseConfigValue(properties, "enableDropSounds", enableDropSounds);
    dropSoundVolume = parseConfigValue(properties, "dropSoundVolume", dropSoundVolume);

    // Update config file if needed
    updateConfigFileIfChanged(configFile, CONFIG_FILE_HEADER, properties, unmodifiedProperties);
  }
}
