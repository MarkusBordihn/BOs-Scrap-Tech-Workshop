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

package de.markusbordihn.scraptechworkshop.client.renderer.blockentity.collectorstation;

import de.markusbordihn.scraptechworkshop.block.entity.collectorstation.CollectorStationBlockEntity;
import de.markusbordihn.scraptechworkshop.data.robot.RobotRenderInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NoStorageStatusHandler implements RobotStatusHandler {

  private static final float ROBOT_GROUND_Y = 0.0f;

  @Override
  public RobotRenderInfo calculateRenderInfo(
      CollectorStationBlockEntity blockEntity,
      Level level,
      BlockPos blockPos,
      Direction facing,
      float stationCenterX,
      float stationCenterZ,
      float animationTime,
      float partialTick) {

    Player nearestPlayer =
        level.getNearestPlayer(
            blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 8.0, false);

    float yaw = -facing.toYRot();
    if (nearestPlayer != null) {
      Vec3 robotPos =
          new Vec3(blockPos.getX() + 0.5, blockPos.getY() + ROBOT_GROUND_Y, blockPos.getZ() + 0.5);
      Vec3 playerPos = nearestPlayer.getEyePosition(partialTick);
      Vec3 lookVector = playerPos.subtract(robotPos).normalize();
      yaw = (float) Math.toDegrees(Mth.atan2(lookVector.x, lookVector.z));
    }

    return new RobotRenderInfo(stationCenterX, ROBOT_GROUND_Y, stationCenterZ, yaw);
  }

  @Override
  public void spawnParticles(
      Level level, BlockPos blockPos, RobotRenderInfo renderInfo, float partialTick) {
    if (level.getRandom().nextFloat() < 0.15f) {
      double x = blockPos.getX() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.2;
      double y = blockPos.getY() + ROBOT_GROUND_Y + 0.3;
      double z = blockPos.getZ() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.2;

      level.addParticle(ParticleTypes.ANGRY_VILLAGER, x, y, z, 0.0, 0.05, 0.0);
    }
  }
}
