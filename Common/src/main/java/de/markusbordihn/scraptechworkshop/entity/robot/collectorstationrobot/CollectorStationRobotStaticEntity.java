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

package de.markusbordihn.scraptechworkshop.entity.robot.collectorstationrobot;

import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.entity.robot.BaseRobotEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

public class CollectorStationRobotStaticEntity extends BaseRobotEntity {

  public static final String ID = "collector_station_robot_static";

  private CollectorStationStatus status = CollectorStationStatus.CHARGING;
  private float renderHeadYaw = 0.0f;
  private float renderHeadPitch = 0.0f;
  private float renderYaw = 0.0f;

  public CollectorStationRobotStaticEntity(
      final EntityType<? extends BaseRobotEntity> entityType, final Level level) {
    super(entityType, level);

    // Configure for static rendering
    this.setNoAi(true);
    this.setNoGravity(true);
    this.setInvulnerable(true);
    this.setSilent(true);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return createBaseAttributes();
  }

  @Override
  protected void registerGoals() {
    // No goals for static entity
  }

  @Override
  public void tick() {
    if (!level().isClientSide) {
      return;
    }

    this.yRotO = this.renderYaw;
    this.xRotO = this.getXRot();
    this.yHeadRotO = this.renderHeadYaw;
    this.yBodyRotO = this.renderYaw;

    super.tick();

    this.setYRot(this.renderYaw);
    this.yBodyRot = this.renderYaw;
    this.yHeadRot = this.renderHeadYaw;
    this.setXRot(this.renderHeadPitch);
  }

  public CollectorStationStatus getStatus() {
    return this.status;
  }

  public void setStatus(CollectorStationStatus newStatus) {
    this.status = newStatus;
  }

  public void setRenderYaw(float yaw) {
    this.renderYaw = yaw;
    this.setYRot(yaw);
    this.yBodyRot = yaw;
  }

  public void setRenderHeadRotation(float headYaw, float headPitch) {
    this.renderHeadYaw = headYaw;
    this.renderHeadPitch = headPitch;
    this.yHeadRot = headYaw;
    this.setXRot(headPitch);
  }

  @Override
  public boolean removeWhenFarAway(double distanceToClosestPlayer) {
    return false;
  }
}
