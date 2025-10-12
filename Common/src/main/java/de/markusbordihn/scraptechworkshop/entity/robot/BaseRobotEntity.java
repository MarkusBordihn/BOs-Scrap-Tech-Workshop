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

package de.markusbordihn.scraptechworkshop.entity.robot;

import de.markusbordihn.scraptechworkshop.data.robot.HeadAnimation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public abstract class BaseRobotEntity extends PathfinderMob {

  protected BlockPos stationPos;
  protected Direction facing = Direction.NORTH;
  private HeadAnimation headAnimation = HeadAnimation.NONE;
  private int headAnimationTimer = 0;

  protected BaseRobotEntity(
      final EntityType<? extends PathfinderMob> entityType, final Level level) {
    super(entityType, level);
  }

  public static AttributeSupplier.Builder createBaseAttributes() {
    return PathfinderMob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 20.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.25D)
        .add(Attributes.FOLLOW_RANGE, 16.0D);
  }

  public HeadAnimation getHeadAnimation() {
    return this.headAnimation;
  }

  public void setHeadAnimation(HeadAnimation animation) {
    this.headAnimation = animation;
    this.headAnimationTimer = animation.getAnimationDuration();
  }

  public void nodYes() {
    setHeadAnimation(HeadAnimation.NOD_YES);
  }

  public void shakeNo() {
    setHeadAnimation(HeadAnimation.SHAKE_NO);
  }

  @Override
  public void tick() {
    super.tick();

    // Update head animation timer and reset animation when done
    if (this.headAnimationTimer > 0) {
      this.headAnimationTimer--;
      if (this.headAnimationTimer <= 0) {
        this.headAnimation = HeadAnimation.NONE;
      }
    }
  }

  @Override
  public boolean isPushable() {
    return false;
  }

  @Override
  public boolean canBeCollidedWith() {
    return true;
  }

  @Override
  public boolean isPickable() {
    return true;
  }

  public BlockPos getStationPos() {
    return this.stationPos;
  }

  public void setStationPos(BlockPos pos) {
    this.stationPos = pos;
  }

  public Direction getFacing() {
    return this.facing;
  }

  public void setFacing(Direction direction) {
    this.facing = direction;
  }
}
