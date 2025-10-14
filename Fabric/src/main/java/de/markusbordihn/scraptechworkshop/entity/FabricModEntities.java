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
import de.markusbordihn.scraptechworkshop.entity.hololog.HoloLogHumanoidEntity;
import de.markusbordihn.scraptechworkshop.entity.robot.collectorstationrobot.CollectorStationRobotEntity;
import de.markusbordihn.scraptechworkshop.entity.robot.collectorstationrobot.CollectorStationRobotStaticEntity;
import de.markusbordihn.scraptechworkshop.entity.robot.scraprobot.MixedScrapRobotEntity;
import de.markusbordihn.scraptechworkshop.registry.entity.CollectorStationRobotEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.entity.HoloLogEntityRegistry;
import de.markusbordihn.scraptechworkshop.registry.entity.MixedScrapRobotEntityRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public class FabricModEntities {

  private FabricModEntities() {}

  public static void registerEntities() {
    // Register HolologHumanoid Entity
    EntityType<HoloLogHumanoidEntity> holologHumanoidEntityType =
        EntityType.Builder.of(HoloLogHumanoidEntity::new, MobCategory.MISC)
            .sized(0.6f, 1.8f)
            .clientTrackingRange(10)
            .build(HoloLogHumanoidEntity.ID);
    Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        new ResourceLocation(Constants.MOD_ID, HoloLogHumanoidEntity.ID),
        holologHumanoidEntityType);
    FabricDefaultAttributeRegistry.register(
        holologHumanoidEntityType, HoloLogHumanoidEntity.createAttributes());
    HoloLogEntityRegistry.setHoloLogHumanoidEntityType(holologHumanoidEntityType);

    // Register CollectorStationRobot Entity (AI-controlled)
    EntityType<CollectorStationRobotEntity> collectorStationRobotEntityType =
        EntityType.Builder.of(CollectorStationRobotEntity::new, MobCategory.MISC)
            .sized(0.6f, 0.6f)
            .clientTrackingRange(16)
            .updateInterval(3)
            .build(CollectorStationRobotEntity.ID);
    Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        new ResourceLocation(Constants.MOD_ID, CollectorStationRobotEntity.ID),
        collectorStationRobotEntityType);
    FabricDefaultAttributeRegistry.register(
        collectorStationRobotEntityType, CollectorStationRobotEntity.createAttributes());
    CollectorStationRobotEntityRegistry.setCollectorStationRobotEntityType(
        collectorStationRobotEntityType);

    // Register CollectorStationRobotStatic Entity (for rendering only)
    EntityType<CollectorStationRobotStaticEntity> collectorStationRobotStaticEntityType =
        EntityType.Builder.of(CollectorStationRobotStaticEntity::new, MobCategory.MISC)
            .sized(0.6f, 0.6f)
            .clientTrackingRange(16)
            .updateInterval(1)
            .noSave()
            .noSummon()
            .build(CollectorStationRobotStaticEntity.ID);
    Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        new ResourceLocation(Constants.MOD_ID, CollectorStationRobotStaticEntity.ID),
        collectorStationRobotStaticEntityType);
    FabricDefaultAttributeRegistry.register(
        collectorStationRobotStaticEntityType,
        CollectorStationRobotStaticEntity.createAttributes());
    CollectorStationRobotEntityRegistry.setCollectorStationRobotStaticEntityType(
        collectorStationRobotStaticEntityType);

    // Register MixedScrapRobot Entity
    EntityType<MixedScrapRobotEntity> mixedScrapRobotEntityType =
        EntityType.Builder.of(MixedScrapRobotEntity::new, MobCategory.CREATURE)
            .sized(0.6f, 0.6f)
            .clientTrackingRange(10)
            .build(MixedScrapRobotEntity.ID);
    Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        new ResourceLocation(Constants.MOD_ID, MixedScrapRobotEntity.ID),
        mixedScrapRobotEntityType);
    FabricDefaultAttributeRegistry.register(
        mixedScrapRobotEntityType, MixedScrapRobotEntity.createAttributes());
    MixedScrapRobotEntityRegistry.setMixedScrapRobotEntityType(mixedScrapRobotEntityType);

    // Register Spawn Placements
    SpawnPlacements.register(
        mixedScrapRobotEntityType,
        SpawnPlacements.Type.ON_GROUND,
        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
        MixedScrapRobotEntity::checkRobotSpawnRules);
  }
}
