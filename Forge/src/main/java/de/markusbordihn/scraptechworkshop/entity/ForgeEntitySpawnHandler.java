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

package de.markusbordihn.scraptechworkshop.entity;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.config.ScrapRobotConfig;
import de.markusbordihn.scraptechworkshop.spawner.ScrapRobotSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEntitySpawnHandler {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String LOG_PREFIX = "[Entity Spawn Handler]";
  private static boolean spawnsRegistered = false;

  private ForgeEntitySpawnHandler() {}

  @SubscribeEvent
  public static void onLevelLoad(LevelEvent.Load event) {
    if (spawnsRegistered) {
      return;
    }

    registerSpawns();
    spawnsRegistered = true;
  }

  private static void registerSpawns() {
    if (!ScrapRobotConfig.mixedScrapRobotSpawnEnabled) {
      log.info("{} Mixed Scrap Robot spawning is disabled in config", LOG_PREFIX);
      return;
    }

    MobSpawnSettings.SpawnerData spawnerData = ScrapRobotSpawner.createMixedScrapRobotSpawn();
    if (spawnerData == null) {
      log.warn("{} Failed to create Mixed Scrap Robot spawner data", LOG_PREFIX);
      return;
    }

    log.info(
        "{} Registered Mixed Scrap Robot natural spawning (weight: {}, group: {}-{})",
        LOG_PREFIX,
        ScrapRobotConfig.mixedScrapRobotSpawnWeight,
        ScrapRobotConfig.mixedScrapRobotSpawnMinGroup,
        ScrapRobotConfig.mixedScrapRobotSpawnMaxGroup);
  }
}
