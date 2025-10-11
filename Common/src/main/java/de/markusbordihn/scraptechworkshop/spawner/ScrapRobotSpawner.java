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

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.config.ScrapRobotConfig;
import de.markusbordihn.scraptechworkshop.registry.entity.MixedScrapRobotEntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapRobotSpawner {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String LOG_PREFIX = "[Scrap Robot Spawner]";

  private ScrapRobotSpawner() {}

  public static boolean shouldSpawnInBiome(Holder<Biome> biomeHolder, String biomeTypeFilter) {
    if (biomeTypeFilter.equalsIgnoreCase("ALL")) {
      return true;
    }

    if (biomeTypeFilter.equalsIgnoreCase("OVERWORLD")) {
      return !biomeHolder.is(BiomeTags.IS_NETHER) && !biomeHolder.is(BiomeTags.IS_END);
    }

    return biomeTypeFilter.equalsIgnoreCase("OVERWORLD");
  }

  public static MobSpawnSettings.SpawnerData createMixedScrapRobotSpawn() {
    if (!ScrapRobotConfig.mixedScrapRobotSpawnEnabled
        || MixedScrapRobotEntityRegistry.MIXED_SCRAP_ROBOT_ENTITY_TYPE == null) {
      return null;
    }

    log.info(
        "{} Creating spawn data for Mixed Scrap Robot: weight={}, min={}, max={}",
        LOG_PREFIX,
        ScrapRobotConfig.mixedScrapRobotSpawnWeight,
        ScrapRobotConfig.mixedScrapRobotSpawnMinGroup,
        ScrapRobotConfig.mixedScrapRobotSpawnMaxGroup);

    return new MobSpawnSettings.SpawnerData(
        MixedScrapRobotEntityRegistry.MIXED_SCRAP_ROBOT_ENTITY_TYPE,
        ScrapRobotConfig.mixedScrapRobotSpawnWeight,
        ScrapRobotConfig.mixedScrapRobotSpawnMinGroup,
        ScrapRobotConfig.mixedScrapRobotSpawnMaxGroup);
  }

  public static void logSpawnRegistration(String biomeKey) {
    if (ScrapRobotConfig.mixedScrapRobotSpawnEnabled) {
      log.debug("{} Registered Mixed Scrap Robot spawn in biome: {}", LOG_PREFIX, biomeKey);
    }
  }
}
