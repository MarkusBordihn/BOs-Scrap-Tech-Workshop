/*
* Copyright 2025 Markus Bordihn
*
* Permission is hereby granted, free of charge, to any person obtaining a copy of this s  private CollectorStationRobotEntity getOrCreateRobotEntity(Level level) {
   if (cachedRobotEntity == null && level instanceof ClientLevel clientLevel) {
     cachedRobotEntity =
         new CollectorStationRobotEntity(
             CollectorStationRobotEntityRegistry.COLLECTOR_STATION_ROBOT_ENTITY_TYPE, clientLevel);
     cachedRobotEntity.setPos(0, 0, 0);
   }
   return cachedRobotEntity;
 }d
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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.markusbordihn.scraptechworkshop.block.collectorstation.CollectorStationBlock;
import de.markusbordihn.scraptechworkshop.block.entity.collectorstation.CollectorStationBlockEntity;
import de.markusbordihn.scraptechworkshop.client.entity.ClientCollectorStationRobotManager;
import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.data.robot.RobotRenderInfo;
import de.markusbordihn.scraptechworkshop.entity.collectorstationrobot.CollectorStationRobotStaticEntity;
import de.markusbordihn.scraptechworkshop.registry.entity.CollectorStationRobotEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CollectorStationBlockEntityRenderer
    implements BlockEntityRenderer<CollectorStationBlockEntity> {

  private static final float ROBOT_SCALE = 0.75f;

  private CollectorStationRobotStaticEntity cachedRobotEntity;

  public CollectorStationBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

  @Override
  public void render(
      final CollectorStationBlockEntity blockEntity,
      final float partialTick,
      final PoseStack poseStack,
      final MultiBufferSource bufferSource,
      final int packedLight,
      final int packedOverlay) {

    Level level = blockEntity.getLevel();
    if (level == null || !(level instanceof ClientLevel clientLevel)) {
      return;
    }

    CollectorStationStatus status = blockEntity.getStatus();
    BlockPos blockPos = blockEntity.getBlockPos();
    BlockState blockState = blockEntity.getBlockState();
    Direction facing = blockState.getValue(CollectorStationBlock.FACING);

    ClientCollectorStationRobotManager.updateRobot(clientLevel, blockPos, facing, status);

    if (status.isRobotActive()) {
      var robot = ClientCollectorStationRobotManager.getRobot(blockPos);
      if (robot != null && !robot.isRemoved()) {
        return;
      }
    }

    float animationTime = (level.getGameTime() + partialTick) * 0.05f;
    float stationCenterX = 0.5f;
    float stationCenterZ = 0.5f;

    RobotStatusHandler handler = RobotStatusHandler.getHandler(status);

    // Some statuses (COLLECTING, RETURNING) have no handler - real entity handles rendering
    if (handler == null) {
      return;
    }

    RobotRenderInfo renderInfo =
        handler.calculateRenderInfo(
            blockEntity,
            level,
            blockPos,
            facing,
            stationCenterX,
            stationCenterZ,
            animationTime,
            partialTick);

    // Some status handlers return null when a real entity is handling the rendering
    if (renderInfo == null) {
      return;
    }

    handler.spawnParticles(level, blockPos, renderInfo, partialTick);

    poseStack.pushPose();
    poseStack.translate(renderInfo.offsetX(), renderInfo.offsetY(), renderInfo.offsetZ());
    poseStack.mulPose(Axis.YP.rotationDegrees(renderInfo.yaw()));

    if (renderInfo.pitch() != 0.0f) {
      poseStack.mulPose(Axis.XP.rotationDegrees(renderInfo.pitch()));
    }

    poseStack.scale(ROBOT_SCALE, ROBOT_SCALE, ROBOT_SCALE);

    CollectorStationRobotStaticEntity robotEntity = getOrCreateRobotEntity(level);
    if (robotEntity != null) {
      robotEntity.tickCount = (int) level.getGameTime();
      robotEntity.setStatus(status);

      double worldX = blockPos.getX() + renderInfo.offsetX();
      double worldY = blockPos.getY() + renderInfo.offsetY();
      double worldZ = blockPos.getZ() + renderInfo.offsetZ();
      robotEntity.setPos(worldX, worldY, worldZ);

      Minecraft minecraft = Minecraft.getInstance();
      EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
      EntityRenderer<? super Entity> renderer = dispatcher.getRenderer(robotEntity);

      renderer.render(robotEntity, 0.0f, partialTick, poseStack, bufferSource, packedLight);
    }

    poseStack.popPose();
  }

  private CollectorStationRobotStaticEntity getOrCreateRobotEntity(Level level) {
    if (cachedRobotEntity == null && level instanceof ClientLevel clientLevel) {
      cachedRobotEntity =
          new CollectorStationRobotStaticEntity(
              CollectorStationRobotEntityRegistry.COLLECTOR_STATION_ROBOT_STATIC_ENTITY_TYPE,
              clientLevel);
      cachedRobotEntity.setPos(0, 0, 0);
    }
    return cachedRobotEntity;
  }

  @Override
  public int getViewDistance() {
    return 48;
  }
}
