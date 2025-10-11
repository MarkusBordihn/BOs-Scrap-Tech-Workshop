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

package de.markusbordihn.scraptechworkshop.client.entity;

import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.entity.collectorstationrobot.CollectorStationRobotEntity;
import de.markusbordihn.scraptechworkshop.registry.entity.CollectorStationRobotEntityRegistry;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class ClientCollectorStationRobotManager {

  private static final Map<BlockPos, CollectorStationRobotEntity> activeRobots = new HashMap<>();
  private static final Map<BlockPos, CollectorStationStatus> lastStatus = new HashMap<>();

  public static void updateRobot(
      ClientLevel level, BlockPos blockPos, Direction facing, CollectorStationStatus status) {

    CollectorStationRobotEntity robot = activeRobots.get(blockPos);

    if (status.isRobotActive()) {
      if (robot == null || robot.isRemoved()) {
        if (robot != null) {
          activeRobots.remove(blockPos);
        }
        robot = spawnRobot(level, blockPos, facing);
        if (robot == null) {
          return;
        }
        activeRobots.put(blockPos, robot);
      }

      robot.setStationPos(blockPos);
      robot.setFacing(facing);
      robot.setStatus(status);

      lastStatus.put(blockPos, status);

    } else {
      if (robot != null) {
        despawnRobot(level, blockPos);
        lastStatus.remove(blockPos);
      }
    }
  }

  private static CollectorStationRobotEntity spawnRobot(
      ClientLevel level, BlockPos blockPos, Direction facing) {
    CollectorStationRobotEntity robot =
        new CollectorStationRobotEntity(
            CollectorStationRobotEntityRegistry.COLLECTOR_STATION_ROBOT_ENTITY_TYPE, level);

    Vec3 spawnPos =
        new Vec3(
            blockPos.getX() + 0.5 + facing.getStepX() * 1.0,
            blockPos.getY(),
            blockPos.getZ() + 0.5 + facing.getStepZ() * 1.0);

    robot.setPos(spawnPos);
    robot.setYRot(facing.toYRot());
    robot.registerClientSideGoals();

    int entityId = robot.getId();
    level.putNonPlayerEntity(entityId, robot);

    return robot;
  }

  private static void despawnRobot(ClientLevel level, BlockPos blockPos) {
    CollectorStationRobotEntity robot = activeRobots.remove(blockPos);
    if (robot != null && !robot.isRemoved()) {
      robot.discard();
    }
  }

  public static CollectorStationRobotEntity getRobot(BlockPos blockPos) {
    return activeRobots.get(blockPos);
  }
}
