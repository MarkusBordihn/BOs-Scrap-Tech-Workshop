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

package de.markusbordihn.scraptechworkshop.data.loot;

import de.markusbordihn.scraptechworkshop.Constants;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaChestLootData {

  private static final String LOG_PREFIX = "[Loot Table Injection]";

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final Map<ResourceLocation, ResourceLocation> LOOT_TABLE_INJECTIONS =
      Map.ofEntries(
          Map.entry(
              new ResourceLocation("minecraft", "chests/abandoned_mineshaft"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/abandoned_mineshaft")),
          Map.entry(
              new ResourceLocation("minecraft", "chests/simple_dungeon"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/simple_dungeon")),
          Map.entry(
              new ResourceLocation("minecraft", "chests/stronghold_corridor"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/stronghold_corridor")),
          Map.entry(
              new ResourceLocation("minecraft", "chests/ancient_city"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/ancient_city")),
          Map.entry(
              new ResourceLocation("minecraft", "chests/end_city_treasure"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/end_city_treasure")),
          Map.entry(
              new ResourceLocation("minecraft", "chests/ruined_portal"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/ruined_portal")),
          Map.entry(
              new ResourceLocation("minecraft", "chests/bastion_treasure"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/bastion_treasure")),
          Map.entry(
              new ResourceLocation("minecraft", "chests/shipwreck_supply"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/shipwreck_supply")),
          Map.entry(
              new ResourceLocation("minecraft", "chests/ocean_ruin_cold"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/ocean_ruin_cold")),
          Map.entry(
              new ResourceLocation("minecraft", "chests/ocean_ruin_warm"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/ocean_ruin_warm")),
          Map.entry(
              new ResourceLocation("minecraft", "chests/desert_pyramid"),
              new ResourceLocation(Constants.MOD_ID, "inject/chests/desert_pyramid")));

  private VanillaChestLootData() {
    // Utility class
  }

  public static ResourceLocation getInjectionTable(ResourceLocation targetTable) {
    return LOOT_TABLE_INJECTIONS.get(targetTable);
  }

  public static boolean shouldModifyLootTable(ResourceLocation targetTable) {
    return LOOT_TABLE_INJECTIONS.containsKey(targetTable);
  }

  public static Map<ResourceLocation, ResourceLocation> getAllInjections() {
    return LOOT_TABLE_INJECTIONS;
  }

  public static void logSuccessfulInjection(
      ResourceLocation targetTable, ResourceLocation injectionTable) {
    log.debug("{} Successfully injected '{}' into '{}'", LOG_PREFIX, injectionTable, targetTable);
  }

  public static void logFailedInjection(
      ResourceLocation targetTable, ResourceLocation injectionTable, String reason) {
    log.warn(
        "{} Failed to inject '{}' into '{}': {}", LOG_PREFIX, injectionTable, targetTable, reason);
  }

  public static void logSkippedInjection(ResourceLocation targetTable, String reason) {
    log.debug("{} Skipped '{}': {}", LOG_PREFIX, targetTable, reason);
  }
}
