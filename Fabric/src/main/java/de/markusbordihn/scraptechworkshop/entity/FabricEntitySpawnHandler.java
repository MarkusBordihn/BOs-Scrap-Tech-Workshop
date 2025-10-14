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
import de.markusbordihn.scraptechworkshop.registry.entity.MixedScrapRobotEntityRegistry;
import de.markusbordihn.scraptechworkshop.spawner.RobotSpawnConfig;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FabricEntitySpawnHandler {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final RobotSpawnConfig CONFIG = RobotSpawnConfig.load("mixed_scrap_robot");

  private FabricEntitySpawnHandler() {}

  public static void registerSpawns() {
    if (!CONFIG.enabled) {
      log.info("Mixed Scrap Robot spawning is disabled in spawn config");
      return;
    }

    if (MixedScrapRobotEntityRegistry.MIXED_SCRAP_ROBOT_ENTITY_TYPE == null) {
      log.error("Mixed Scrap Robot entity type is null, cannot register spawns");
      return;
    }

    BiomeModifications.addSpawn(
        BiomeSelectors.tag(parseBiomeTag(CONFIG.biomes)),
        MobCategory.CREATURE,
        MixedScrapRobotEntityRegistry.MIXED_SCRAP_ROBOT_ENTITY_TYPE,
        CONFIG.weight,
        CONFIG.minGroup,
        CONFIG.maxGroup);

    log.info(
        "Registered Mixed Scrap Robot spawning (weight: {}, group: {}-{}, biomes: {}, minDistance: {})",
        CONFIG.weight,
        CONFIG.minGroup,
        CONFIG.maxGroup,
        CONFIG.biomes,
        CONFIG.minDistanceFromCenter);
  }

  private static TagKey<Biome> parseBiomeTag(String tag) {
    String tagPath = tag.startsWith("#") ? tag.substring(1) : tag;
    String[] parts = tagPath.split(":");
    if (parts.length == 2) {
      return TagKey.create(Registries.BIOME, new ResourceLocation(parts[0], parts[1]));
    }
    return TagKey.create(Registries.BIOME, new ResourceLocation("minecraft", "is_overworld"));
  }

  public static int getMinDistanceFromCenter() {
    return CONFIG.minDistanceFromCenter;
  }

  public static int getDespawnDistance() {
    return CONFIG.despawnDistance;
  }
}
