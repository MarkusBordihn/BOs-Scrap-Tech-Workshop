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

package de.markusbordihn.scraptechworkshop.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ClientSideMovement {

  private static final double REACH_DISTANCE = 0.5;
  private static final double GRAVITY = -0.08;
  private static final double JUMP_VELOCITY = 0.42;

  public static boolean moveTowards(
      Mob entity, double targetX, double targetY, double targetZ, double speed) {
    double dx = targetX - entity.getX();
    double dy = targetY - entity.getY();
    double dz = targetZ - entity.getZ();
    double distanceXZ = Math.sqrt(dx * dx + dz * dz);
    if (distanceXZ < REACH_DISTANCE) {
      entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
      entity.setSpeed(0);
      return false;
    }

    if (isObstacleAhead(entity, dx, dz, distanceXZ)) {
      entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
      entity.setSpeed(0);
      return false;
    }

    double moveX = (dx / distanceXZ) * speed;
    double moveZ = (dz / distanceXZ) * speed;
    double moveY = handleVerticalMovement(entity, targetY, dy, dx, dz, distanceXZ);

    entity.setDeltaMovement(moveX, moveY, moveZ);
    entity.move(net.minecraft.world.entity.MoverType.SELF, entity.getDeltaMovement());
    entity.setSpeed((float) speed);

    return true;
  }

  private static boolean isObstacleAhead(Mob entity, double dx, double dz, double distance) {
    Level level = entity.level();
    BlockPos currentPos = entity.blockPosition();

    int offsetX = (int) Math.round(dx / distance);
    int offsetZ = (int) Math.round(dz / distance);
    if (offsetX == 0 && offsetZ == 0) {
      return false;
    }

    BlockPos checkPos = currentPos.offset(offsetX, 0, offsetZ);
    BlockState blockAtLevel = level.getBlockState(checkPos);
    BlockState blockAbove = level.getBlockState(checkPos.above());

    boolean levelBlocked =
        !blockAtLevel.isAir()
            && blockAtLevel.isSolidRender(level, checkPos)
            && blockAtLevel.canOcclude();
    boolean aboveBlocked = !blockAbove.isAir() && blockAbove.isSolidRender(level, checkPos.above());

    return levelBlocked && aboveBlocked;
  }

  private static double handleVerticalMovement(
      Mob entity, double targetY, double dy, double dx, double dz, double distanceXZ) {
    if (!entity.onGround()) {
      return entity.getDeltaMovement().y + GRAVITY;
    }

    if (dy > 0.3) {
      Level level = entity.level();
      BlockPos currentPos = entity.blockPosition();

      int offsetX = (int) Math.round(dx / distanceXZ);
      int offsetZ = (int) Math.round(dz / distanceXZ);
      BlockPos frontPos = currentPos.offset(offsetX, 0, offsetZ);

      BlockState frontBlock = level.getBlockState(frontPos);
      BlockState aboveBlock = level.getBlockState(frontPos.above());
      BlockState above2Block = level.getBlockState(frontPos.above(2));

      boolean isSolidFront =
          !frontBlock.isAir()
              && frontBlock.isSolidRender(level, frontPos)
              && frontBlock.canOcclude();

      if (isSolidFront
          && aboveBlock.isAir()
          && above2Block.isAir()
          && Math.abs(entity.getDeltaMovement().y) < 0.05) {
        return JUMP_VELOCITY;
      }
    }

    if (dy < -0.5) {
      return dy * 0.1;
    }

    return 0;
  }

  public static Vec3 findGroundPosition(
      Level level, double x, double startY, double z, int searchRadius) {
    BlockPos centerPos = new BlockPos((int) x, (int) startY, (int) z);

    for (int i = 0; i <= searchRadius; i++) {
      BlockPos checkPos = centerPos.below(i);
      if (isValidGroundPosition(level, checkPos)) {
        return new Vec3(x, checkPos.getY() + 1.0, z);
      }
    }

    for (int i = 1; i <= searchRadius / 2; i++) {
      BlockPos checkPos = centerPos.above(i);
      if (isValidGroundPosition(level, checkPos)) {
        return new Vec3(x, checkPos.getY() + 1.0, z);
      }
    }

    return new Vec3(x, startY, z);
  }

  private static boolean isValidGroundPosition(Level level, BlockPos pos) {
    BlockState ground = level.getBlockState(pos);
    BlockState above = level.getBlockState(pos.above());
    BlockState above2 = level.getBlockState(pos.above(2));

    boolean isGroundSolid =
        !ground.isAir()
            && (ground.isSolidRender(level, pos) || ground.canOcclude())
            && ground.getFluidState().isEmpty();

    return isGroundSolid && above.isAir() && above2.isAir();
  }
}
