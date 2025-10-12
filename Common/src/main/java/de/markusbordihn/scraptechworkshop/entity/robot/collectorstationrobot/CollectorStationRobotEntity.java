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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CollectorStationRobotEntity extends BaseRobotEntity {

  public static final String ID = "collector_station_robot";

  private CollectorStationStatus status = CollectorStationStatus.CHARGING;
  private int collectionRadius = 16;
  private float animationPosition = 0.0F;
  private float animationSpeed = 0.0F;
  private boolean isMovingFromGoal = false;
  private boolean isDigging = false;

  public CollectorStationRobotEntity(
      final EntityType<? extends BaseRobotEntity> entityType, final Level level) {
    super(entityType, level);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return createBaseAttributes();
  }

  @Override
  protected void registerGoals() {
    // This is a client side only entity!
  }

  public void registerClientSideGoals() {
    this.goalSelector.addGoal(0, new FloatGoal(this));
    this.goalSelector.addGoal(1, new ReturnToStationGoal(this, 1.2D));
    this.goalSelector.addGoal(2, new RandomCollectGoal(this, 0.7D, 64));
    this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
  }

  @Override
  public void tick() {
    if (!level().isClientSide) {
      return;
    }

    super.tick();
    updateWalkAnimation();
    tickAiAndNavigation();
  }

  private void updateWalkAnimation() {
    double dx = getX() - xo;
    double dz = getZ() - zo;
    float distanceMoved = (float) Math.sqrt(dx * dx + dz * dz);

    if (distanceMoved > 0.01F) {
      animationSpeed = Math.min(1.0F, animationSpeed + 0.15F);
      animationPosition += distanceMoved * 8.0F;
    } else {
      animationSpeed = Math.max(0.0F, animationSpeed - 0.15F);
    }

    this.walkAnimation.update(animationSpeed, 0.4F);
  }

  private void tickAiAndNavigation() {
    this.goalSelector.tick();
    this.navigation.tick();
    this.lookControl.tick();
    this.moveControl.tick();
  }

  private void onStatusChanged(CollectorStationStatus oldStatus, CollectorStationStatus newStatus) {
    if (newStatus == CollectorStationStatus.COLLECTING
        || newStatus == CollectorStationStatus.RETURNING) {
      return;
    }
    this.getNavigation().stop();
  }

  public CollectorStationStatus getStatus() {
    return this.status;
  }

  public void setStatus(CollectorStationStatus newStatus) {
    if (this.status != newStatus) {
      CollectorStationStatus oldStatus = this.status;
      this.status = newStatus;
      onStatusChanged(oldStatus, newStatus);
    }
  }

  public boolean isMovingFromGoal() {
    return this.isMovingFromGoal;
  }

  public void setMovingFromGoal(boolean moving) {
    this.isMovingFromGoal = moving;
  }

  public boolean isDigging() {
    return this.isDigging;
  }

  public void setDigging(boolean digging) {
    this.isDigging = digging;
  }

  @Override
  public boolean removeWhenFarAway(double distanceToClosestPlayer) {
    return true;
  }
}
