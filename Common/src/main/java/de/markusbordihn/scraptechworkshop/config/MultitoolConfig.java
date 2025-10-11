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

public class MultitoolConfig extends Config {

  public static final String CONFIG_FILE_NAME = "multitool.cfg";
  public static final String CONFIG_FILE_HEADER =
"""
 Multitool Configuration

 This configuration file allows you to define the general settings for the Scrap Multitool.
 energyMax: Maximum energy capacity (default: 10000)
 energyPerUse: Energy consumed per general use (default: 1)
 energyPerBlock: Energy consumed per block mined (default: 2)
 energyPerAttack: Energy consumed per attack (default: 5)
 poweredSpeedPickaxe: Mining speed when in pickaxe mode (default: 6.0)
 poweredSpeedAxe: Mining speed when in axe mode (default: 6.0)
 poweredSpeedShovel: Mining speed when in shovel mode (default: 6.0)
 poweredSpeedHoe: Mining speed when in hoe mode (default: 4.0)
 poweredSpeedSword: Attack speed when in sword mode (default: 6.0)
 poweredSpeedDefault: Default powered speed (default: 6.0)
 raycastDistance: Distance for automatic tool mode detection in blocks (default: 5)

""";

  public static final String MULTITOOL_ID = "scrap_multitool";

  // Mode constants
  public static final String MODE_NORMAL = "normal";
  public static final String MODE_PICKAXE = "pickaxe";
  public static final String MODE_AXE = "axe";
  public static final String MODE_SHOVEL = "shovel";
  public static final String MODE_HOE = "hoe";
  public static final String MODE_SWORD = "sword";

  // Default values
  public static final int DEFAULT_ENERGY_MAX = 10000;
  public static final int DEFAULT_ENERGY_PER_USE = 1;
  public static final int DEFAULT_ENERGY_PER_BLOCK = 2;
  public static final int DEFAULT_ENERGY_PER_ATTACK = 5;
  public static final float DEFAULT_POWERED_SPEED_PICKAXE = 6.0f;
  public static final float DEFAULT_POWERED_SPEED_AXE = 6.0f;
  public static final float DEFAULT_POWERED_SPEED_SHOVEL = 6.0f;
  public static final float DEFAULT_POWERED_SPEED_HOE = 4.0f;
  public static final float DEFAULT_POWERED_SPEED_SWORD = 6.0f;
  public static final float DEFAULT_POWERED_SPEED_DEFAULT = 6.0f;
  public static final int DEFAULT_RAYCAST_DISTANCE = 5;

  // Configurable values
  public static int energyMax = DEFAULT_ENERGY_MAX;
  public static int energyPerUse = DEFAULT_ENERGY_PER_USE;
  public static int energyPerBlock = DEFAULT_ENERGY_PER_BLOCK;
  public static int energyPerAttack = DEFAULT_ENERGY_PER_ATTACK;
  public static float poweredSpeedPickaxe = DEFAULT_POWERED_SPEED_PICKAXE;
  public static float poweredSpeedAxe = DEFAULT_POWERED_SPEED_AXE;
  public static float poweredSpeedShovel = DEFAULT_POWERED_SPEED_SHOVEL;
  public static float poweredSpeedHoe = DEFAULT_POWERED_SPEED_HOE;
  public static float poweredSpeedSword = DEFAULT_POWERED_SPEED_SWORD;
  public static float poweredSpeedDefault = DEFAULT_POWERED_SPEED_DEFAULT;
  public static int raycastDistance = DEFAULT_RAYCAST_DISTANCE;

  public static void registerConfig() {
    registerConfigFile(CONFIG_FILE_NAME, CONFIG_FILE_HEADER);
    parseConfigFile();
  }

  public static void parseConfigFile() {
    File configFile = getConfigFile(CONFIG_FILE_NAME);
    Properties properties = readConfigFile(configFile);
    Properties unmodifiedProperties = (Properties) properties.clone();

    // Config entries
    energyMax = Math.max(1000, parseConfigValue(properties, "energyMax", energyMax));
    energyPerUse = Math.max(0, parseConfigValue(properties, "energyPerUse", energyPerUse));
    energyPerBlock = Math.max(1, parseConfigValue(properties, "energyPerBlock", energyPerBlock));
    energyPerAttack = Math.max(1, parseConfigValue(properties, "energyPerAttack", energyPerAttack));

    poweredSpeedPickaxe =
        Math.max(1.0f, parseConfigValue(properties, "poweredSpeedPickaxe", poweredSpeedPickaxe));
    poweredSpeedAxe =
        Math.max(1.0f, parseConfigValue(properties, "poweredSpeedAxe", poweredSpeedAxe));
    poweredSpeedShovel =
        Math.max(1.0f, parseConfigValue(properties, "poweredSpeedShovel", poweredSpeedShovel));
    poweredSpeedHoe =
        Math.max(1.0f, parseConfigValue(properties, "poweredSpeedHoe", poweredSpeedHoe));
    poweredSpeedSword =
        Math.max(1.0f, parseConfigValue(properties, "poweredSpeedSword", poweredSpeedSword));
    poweredSpeedDefault =
        Math.max(1.0f, parseConfigValue(properties, "poweredSpeedDefault", poweredSpeedDefault));
    raycastDistance =
        Math.max(1, Math.min(10, parseConfigValue(properties, "raycastDistance", raycastDistance)));

    // Update config file if needed
    updateConfigFileIfChanged(configFile, CONFIG_FILE_HEADER, properties, unmodifiedProperties);
  }

  public static float getPoweredSpeed(String mode) {
    return switch (mode) {
      case MODE_PICKAXE -> poweredSpeedPickaxe;
      case MODE_AXE -> poweredSpeedAxe;
      case MODE_SHOVEL -> poweredSpeedShovel;
      case MODE_HOE -> poweredSpeedHoe;
      case MODE_SWORD -> poweredSpeedSword;
      default -> poweredSpeedDefault;
    };
  }
}
