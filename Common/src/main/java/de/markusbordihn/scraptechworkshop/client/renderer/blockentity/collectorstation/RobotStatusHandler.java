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
import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.data.robot.RobotRenderInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface RobotStatusHandler {

  static RobotStatusHandler getHandler(CollectorStationStatus status) {
    return switch (status) {
      case NO_POWER -> new NoPowerStatusHandler();
      case NO_STORAGE -> new NoStorageStatusHandler();
      case CHARGING -> new ChargingStatusHandler();
      case PROCESSING -> new ProcessingStatusHandler();
      default -> null;
    };
  }

  RobotRenderInfo calculateRenderInfo(
      CollectorStationBlockEntity blockEntity,
      Level level,
      BlockPos blockPos,
      Direction facing,
      float stationCenterX,
      float stationCenterZ,
      float animationTime,
      float partialTick);

  void spawnParticles(
      Level level, BlockPos blockPos, RobotRenderInfo renderInfo, float partialTick);
}
