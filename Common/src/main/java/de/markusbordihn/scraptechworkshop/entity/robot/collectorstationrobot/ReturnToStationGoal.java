package de.markusbordihn.scraptechworkshop.entity.robot.collectorstationrobot;

import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.pathfinding.ClientSideMovement;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class ReturnToStationGoal extends Goal {

  private static final double TELEPORT_DISTANCE_XZ = 64.0;
  private static final double TELEPORT_RADIUS = 16.0;
  private static final double REACH_DISTANCE = 0.25;
  private static final double LOOK_AHEAD_DISTANCE = 3.0;
  private static final int STUCK_THRESHOLD = 100;

  private final CollectorStationRobotEntity robot;
  private final double speedModifier;
  private int stuckCounter = 0;
  private int rotationDelay = 0;
  private double targetX;
  private double targetY;
  private double targetZ;

  public ReturnToStationGoal(CollectorStationRobotEntity robot, double speedModifier) {
    this.robot = robot;
    this.speedModifier = speedModifier;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    return robot.getStatus() == CollectorStationStatus.RETURNING
        && robot.getStationPos() != null
        && !robot.getStationPos().equals(BlockPos.ZERO);
  }

  @Override
  public boolean canContinueToUse() {
    BlockPos stationPos = robot.getStationPos();
    if (stationPos == null) {
      return false;
    }
    double distance = robot.position().distanceTo(new Vec3(targetX, targetY, targetZ));
    return robot.getStatus() == CollectorStationStatus.RETURNING && distance > REACH_DISTANCE;
  }

  @Override
  public void start() {
    this.stuckCounter = 0;
    this.rotationDelay = 10;

    BlockPos stationPos = robot.getStationPos();
    Direction facing = robot.getFacing();
    if (stationPos == null || facing == null) {
      return;
    }

    // Calculate target position in front of the station (lower block)
    BlockPos targetBlockPos = stationPos.offset(facing.getNormal());
    this.targetX = targetBlockPos.getX() + 0.5;
    this.targetY = targetBlockPos.getY(); // Ground level
    this.targetZ = targetBlockPos.getZ() + 0.5;

    if (shouldTeleport()) {
      teleportNearStation();
      this.rotationDelay = 20;
    }
  }

  @Override
  public void stop() {
    robot.setDeltaMovement(Vec3.ZERO);
    robot.setSpeed(0);
    robot.setMovingFromGoal(false);
  }

  @Override
  public void tick() {
    if (robot.getStationPos() == null) {
      return;
    }

    double distance = robot.position().distanceTo(new Vec3(targetX, targetY, targetZ));
    if (distance < REACH_DISTANCE) {
      stop();
      return;
    }

    if (rotationDelay > 0) {
      rotationDelay--;
      robot.setDeltaMovement(0, robot.getDeltaMovement().y, 0);
      robot.setSpeed(0);
      robot.setMovingFromGoal(false);
      robot.getLookControl().setLookAt(targetX, targetY, targetZ);
      return;
    }

    boolean isMoving =
        ClientSideMovement.moveTowards(robot, targetX, targetY, targetZ, speedModifier * 0.04);
    robot.setMovingFromGoal(isMoving);
    if (!isMoving) {
      handleStuck();
    } else {
      stuckCounter = 0;
      updateLookDirection();
    }
  }

  private boolean shouldTeleport() {
    double distanceXZ =
        Math.sqrt(Math.pow(targetX - robot.getX(), 2) + Math.pow(targetZ - robot.getZ(), 2));
    return distanceXZ > TELEPORT_DISTANCE_XZ;
  }

  private void teleportNearStation() {
    double angle = robot.getRandom().nextDouble() * 2 * Math.PI;
    double distance =
        TELEPORT_RADIUS * 0.5 + robot.getRandom().nextDouble() * TELEPORT_RADIUS * 0.5;
    double teleportX = targetX + Math.cos(angle) * distance;
    double teleportZ = targetZ + Math.sin(angle) * distance;
    Vec3 safePos =
        ClientSideMovement.findGroundPosition(robot.level(), teleportX, targetY, teleportZ, 5);
    if (safePos != null) {
      robot.setPos(safePos.x, safePos.y, safePos.z);
    } else {
      safePos = ClientSideMovement.findGroundPosition(robot.level(), targetX, targetY, targetZ, 5);
      if (safePos != null) {
        robot.setPos(safePos.x, safePos.y, safePos.z);
      }
    }
  }

  private void handleStuck() {
    stuckCounter++;
    if (stuckCounter >= STUCK_THRESHOLD && shouldTeleport()) {
      teleportNearStation();
      stuckCounter = 0;
      rotationDelay = 10;
    }
  }

  private void updateLookDirection() {
    robot.getMoveControl().setWantedPosition(targetX, targetY, targetZ, speedModifier);
    double dx = targetX - robot.getX();
    double dy = targetY - robot.getY();
    double dz = targetZ - robot.getZ();

    double dist = Math.sqrt(dx * dx + dz * dz);
    if (dist > 0.1) {
      double lookX = robot.getX() + (dx / dist) * LOOK_AHEAD_DISTANCE;
      double lookY = robot.getEyeY() + Math.max(0, dy * 0.3);
      double lookZ = robot.getZ() + (dz / dist) * LOOK_AHEAD_DISTANCE;
      robot.getLookControl().setLookAt(lookX, lookY, lookZ);
    }
  }
}
