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

package de.markusbordihn.scraptechworkshop.client.renderer.entity.robot.layers;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.entity.robot.BaseRobotEntity;
import de.markusbordihn.scraptechworkshop.entity.robot.collectorstationrobot.CollectorStationRobotEntity;
import de.markusbordihn.scraptechworkshop.entity.robot.collectorstationrobot.CollectorStationRobotStaticEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class CollectorStationRobotEyesLayer<T extends BaseRobotEntity, M extends EntityModel<T>>
    extends RobotEyesLayer<T, M> {

  private static final ResourceLocation EYES_IDLE =
      new ResourceLocation(
          Constants.MOD_ID, "textures/entity/robot/collector_station_robot/eyes_idle.png");

  private static final ResourceLocation EYES_COLLECTING =
      new ResourceLocation(
          Constants.MOD_ID, "textures/entity/robot/collector_station_robot/eyes_collecting.png");

  private static final ResourceLocation EYES_RETURNING =
      new ResourceLocation(
          Constants.MOD_ID, "textures/entity/robot/collector_station_robot/eyes_returning.png");

  private static final ResourceLocation EYES_CHARGING =
      new ResourceLocation(
          Constants.MOD_ID, "textures/entity/robot/collector_station_robot/eyes_charging.png");

  private static final ResourceLocation EYES_ERROR =
      new ResourceLocation(
          Constants.MOD_ID, "textures/entity/robot/collector_station_robot/eyes_error.png");

  public CollectorStationRobotEyesLayer(RenderLayerParent<T, M> parent) {
    super(parent);
  }

  @Override
  protected ResourceLocation getEyeTexture(T entity) {
    CollectorStationStatus status = getStatus(entity);
    if (status == null) {
      return EYES_IDLE;
    }

    switch (status) {
      case COLLECTING:
        return EYES_COLLECTING;
      case RETURNING:
        return EYES_RETURNING;
      case CHARGING:
        return EYES_CHARGING;
      case NO_POWER:
      case NO_STORAGE:
        return EYES_ERROR;
      default:
        return EYES_IDLE;
    }
  }

  private CollectorStationStatus getStatus(T entity) {
    if (entity instanceof CollectorStationRobotEntity) {
      return ((CollectorStationRobotEntity) entity).getStatus();
    } else if (entity instanceof CollectorStationRobotStaticEntity) {
      return ((CollectorStationRobotStaticEntity) entity).getStatus();
    }
    return null;
  }
}
