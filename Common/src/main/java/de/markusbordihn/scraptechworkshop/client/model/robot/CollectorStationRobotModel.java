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

package de.markusbordihn.scraptechworkshop.client.model.robot;

import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.entity.robot.BaseRobotEntity;
import de.markusbordihn.scraptechworkshop.entity.robot.collectorstationrobot.CollectorStationRobotEntity;
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

    CollectorStationStatus status = collectorStationRobotEntity.getStatus();
    if (status == CollectorStationStatus.NO_POWER || status == CollectorStationStatus.NO_STORAGE) {
      this.left_arm.xRot = 0.4F;
      this.right_arm.xRot = 0.4F;
      return;
    }

    // Digging animation: Hands rotate alternating (like scooping)
    if (collectorStationRobotEntity.isDigging()) {
      float diggingSpeed = ageInTicks * 0.8F;
      float leftHandRotation = Mth.sin(diggingSpeed) * 0.5F;
      float rightHandRotation = Mth.sin(diggingSpeed + (float) Math.PI) * 0.5F;
      this.left_hand.yRot = leftHandRotation;
      this.right_hand.yRot = rightHandRotation;
      this.left_hand.zRot = leftHandRotation * 0.3F;
      this.right_hand.zRot = rightHandRotation * 0.3F;
      return;
    }

    // Reset hands when not digging
    this.left_hand.xRot = 0.0F;
    this.left_hand.yRot = 0.0F;
    this.left_hand.zRot = 0.0F;
    this.right_hand.xRot = 0.0F;
    this.right_hand.yRot = 0.0F;
    this.right_hand.zRot = 0.0F;

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
      this.left_arm.xRot = Mth.clamp(armSwing, -0.087F, 0.436F);
      this.right_arm.xRot = Mth.clamp(-armSwing, -0.087F, 0.436F);
    }
  }
}
