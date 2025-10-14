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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModEntities {

  public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
      DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);

  public static final RegistryObject<EntityType<HoloLogHumanoidEntity>> HOLOLOG_HUMANOID =
      ENTITY_TYPES.register(
          HoloLogHumanoidEntity.ID,
          () ->
              EntityType.Builder.of(HoloLogHumanoidEntity::new, MobCategory.MISC)
                  .sized(0.6f, 1.8f)
                  .clientTrackingRange(10)
                  .build(HoloLogHumanoidEntity.ID));

  public static final RegistryObject<EntityType<CollectorStationRobotEntity>>
      COLLECTOR_STATION_ROBOT =
          ENTITY_TYPES.register(
              CollectorStationRobotEntity.ID,
              () ->
                  EntityType.Builder.of(CollectorStationRobotEntity::new, MobCategory.MISC)
                      .sized(0.6f, 0.6f)
                      .clientTrackingRange(16)
                      .updateInterval(3)
                      .build(CollectorStationRobotEntity.ID));

  public static final RegistryObject<EntityType<CollectorStationRobotStaticEntity>>
      COLLECTOR_STATION_ROBOT_STATIC =
          ENTITY_TYPES.register(
              CollectorStationRobotStaticEntity.ID,
              () ->
                  EntityType.Builder.of(CollectorStationRobotStaticEntity::new, MobCategory.MISC)
                      .sized(0.6f, 0.6f)
                      .clientTrackingRange(16)
                      .updateInterval(1)
                      .noSave()
                      .noSummon()
                      .build(CollectorStationRobotStaticEntity.ID));

  public static final RegistryObject<EntityType<MixedScrapRobotEntity>> MIXED_SCRAP_ROBOT =
      ENTITY_TYPES.register(
          MixedScrapRobotEntity.ID,
          () ->
              EntityType.Builder.of(MixedScrapRobotEntity::new, MobCategory.CREATURE)
                  .sized(0.6f, 0.6f)
                  .clientTrackingRange(10)
                  .build(MixedScrapRobotEntity.ID));

  private ForgeModEntities() {}

  public static void register(IEventBus eventBus) {
    ENTITY_TYPES.register(eventBus);

    eventBus.addListener(
        (RegisterEvent event) -> {
          if (event.getRegistryKey().equals(ForgeRegistries.ENTITY_TYPES.getRegistryKey())) {
            HoloLogEntityRegistry.setHoloLogHumanoidEntityType(HOLOLOG_HUMANOID.get());
            CollectorStationRobotEntityRegistry.setCollectorStationRobotEntityType(
                COLLECTOR_STATION_ROBOT.get());
            CollectorStationRobotEntityRegistry.setCollectorStationRobotStaticEntityType(
                COLLECTOR_STATION_ROBOT_STATIC.get());
            MixedScrapRobotEntityRegistry.setMixedScrapRobotEntityType(MIXED_SCRAP_ROBOT.get());
          }
        });

    eventBus.addListener(ForgeModEntities::registerEntityAttributes);
    eventBus.addListener(ForgeModEntities::registerSpawnPlacements);
  }

  private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
    event.put(HOLOLOG_HUMANOID.get(), HoloLogHumanoidEntity.createAttributes().build());
    event.put(
        COLLECTOR_STATION_ROBOT.get(), CollectorStationRobotEntity.createAttributes().build());
    event.put(
        COLLECTOR_STATION_ROBOT_STATIC.get(),
        CollectorStationRobotStaticEntity.createAttributes().build());
    event.put(MIXED_SCRAP_ROBOT.get(), MixedScrapRobotEntity.createAttributes().build());
  }

  private static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
    event.register(
        MIXED_SCRAP_ROBOT.get(),
        SpawnPlacements.Type.ON_GROUND,
        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
        MixedScrapRobotEntity::checkRobotSpawnRules,
        SpawnPlacementRegisterEvent.Operation.REPLACE);
  }
}
