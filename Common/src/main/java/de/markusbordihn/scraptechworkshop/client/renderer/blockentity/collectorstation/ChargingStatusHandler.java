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
import de.markusbordihn.scraptechworkshop.config.CollectorStationConfig;
import de.markusbordihn.scraptechworkshop.data.robot.RobotRenderInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class ChargingStatusHandler implements RobotStatusHandler {

  private static final float ROBOT_CHARGING_Y = 0.35f;
  private static final float ROBOT_HOVER_AMPLITUDE = 0.03f;
  private static final float ROBOT_HOVER_SPEED = 0.15f;
  private static final float CHARGING_THRESHOLD = 0.95f;

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

    float yaw = -facing.toYRot();

    int stateTimer = blockEntity.getStateTimer();
    int chargingTime = CollectorStationConfig.chargingTime;
    float chargeProgress = Math.min(1.0f, (float) stateTimer / chargingTime);

    // Bei 95% Ladung langsam nach unten bewegen (Descent)
    if (chargeProgress >= CHARGING_THRESHOLD) {
      float descentProgress = (chargeProgress - CHARGING_THRESHOLD) / (1.0f - CHARGING_THRESHOLD);
      descentProgress = smoothStep(descentProgress);

      // Langsame Abwärtsbewegung zur Station-Oberfläche
      float targetY = 0.0f; // Auf Block-Oberfläche
      float currentY = Mth.lerp(descentProgress, ROBOT_CHARGING_Y, targetY);

      return new RobotRenderInfo(stationCenterX, currentY, stationCenterZ, yaw);
    }

    // Normale Hover-Animation während des Ladens
    float hoverOffset = Mth.sin(animationTime * ROBOT_HOVER_SPEED) * ROBOT_HOVER_AMPLITUDE;
    return new RobotRenderInfo(stationCenterX, ROBOT_CHARGING_Y + hoverOffset, stationCenterZ, yaw);
  }

  private float smoothStep(float t) {
    return t * t * (3.0f - 2.0f * t);
  }

  @Override
  public void spawnParticles(
      Level level, BlockPos blockPos, RobotRenderInfo renderInfo, float partialTick) {
    if (level.getRandom().nextFloat() < 0.3f) {
      double x = blockPos.getX() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.3;
      double y = blockPos.getY() + ROBOT_CHARGING_Y + 0.2;
      double z = blockPos.getZ() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.3;

      level.addParticle(
          net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK, x, y, z, 0.0, 0.02, 0.0);
    }
  }
}
