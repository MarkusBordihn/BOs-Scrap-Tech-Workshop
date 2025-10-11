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

package de.markusbordihn.scraptechworkshop.client.model;

import de.markusbordihn.scraptechworkshop.entity.BaseRobotEntity;
import de.markusbordihn.scraptechworkshop.entity.collectorstationrobot.CollectorStationRobotEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class CollectorStationRobotModel<T extends BaseRobotEntity> extends BaseRobotModel<T> {

  public CollectorStationRobotModel(ModelPart root) {
    super(root);
  }

  @Override
  public void setupAnim(
      T entity,
      float limbSwing,
      float limbSwingAmount,
      float ageInTicks,
      float netHeadYaw,
      float headPitch) {
    super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

    if (!(entity instanceof CollectorStationRobotEntity collectorStationRobotEntity)) {
      return;
    }

    if (collectorStationRobotEntity.isMovingFromGoal()) {
      float wheelRotation = ageInTicks * 0.5F;
      float armSwing = Mth.cos(ageInTicks * 0.6662F) * 0.6F;
      float bodyRotation = entity.yBodyRot - entity.yBodyRotO;
      while (bodyRotation > 180.0F) bodyRotation -= 360.0F;
      while (bodyRotation < -180.0F) bodyRotation += 360.0F;
      float rotationEffect = bodyRotation * 0.2F;

      this.left_front_wheel.xRot = wheelRotation + rotationEffect;
      this.left_back_wheel.xRot = wheelRotation + rotationEffect;
      this.right_front_wheel.xRot = wheelRotation - rotationEffect;
      this.right_back_wheel.xRot = wheelRotation - rotationEffect;
      this.left_arm.xRot = armSwing;
      this.right_arm.xRot = -armSwing;
    }
  }
}
