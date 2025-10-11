package de.markusbordihn.scraptechworkshop.entity.collectorstationrobot;

import de.markusbordihn.scraptechworkshop.data.collectorstation.CollectorStationStatus;
import de.markusbordihn.scraptechworkshop.pathfinding.ClientSideMovement;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class RandomCollectGoal extends Goal {

  private static final int MIN_MOVE_INTERVAL = 100;
  private static final int MAX_MOVE_INTERVAL = 200;
  private static final int MIN_PAUSE_INTERVAL = 40;
  private static final int MAX_PAUSE_INTERVAL = 80;
  private static final int STUCK_THRESHOLD = 20;
  private static final double REACH_DISTANCE = 1.0;
  private static final double LOOK_AHEAD_DISTANCE = 3.0;

  private final CollectorStationRobotEntity robot;
  private final double speedModifier;
  private final int searchRadius;
  private double wantedX;
  private double wantedY;
  private double wantedZ;
  private int stateTimer;
  private boolean isFirstTarget = true;
  private boolean isPaused = false;
  private int stuckCounter = 0;
  private int rotationDelay = 0;

  public RandomCollectGoal(
      CollectorStationRobotEntity robot, double speedModifier, int searchRadius) {
    this.robot = robot;
    this.speedModifier = speedModifier;
    this.searchRadius = searchRadius;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    return robot.getStatus() == CollectorStationStatus.COLLECTING && robot.getStationPos() != null;
  }

  @Override
  public boolean canContinueToUse() {
    return robot.getStatus() == CollectorStationStatus.COLLECTING;
  }

  @Override
  public void start() {
    this.isFirstTarget = true;
    this.isPaused = false;
    this.stuckCounter = 0;
    pickNewTarget();
  }

  @Override
  public void stop() {
    robot.setDeltaMovement(0, robot.getDeltaMovement().y, 0);
    robot.setSpeed(0);
    robot.setMovingFromGoal(false);
  }

  @Override
  public void tick() {
    stateTimer--;

    if (isPaused) {
      robot.setMovingFromGoal(false);
      spawnDiggingParticles();
      if (stateTimer <= 0) {
        isPaused = false;
        pickNewTarget();
      }
      return;
    }

    if (hasReachedTarget() || stateTimer <= 0) {
      robot.setMovingFromGoal(false);
      startPause();
      return;
    }

    if (rotationDelay > 0) {
      rotationDelay--;
      robot.setDeltaMovement(0, robot.getDeltaMovement().y, 0);
      robot.setSpeed(0);
      robot.setMovingFromGoal(false);
      robot.getLookControl().setLookAt(wantedX, wantedY, wantedZ);
      return;
    }

    boolean isMoving =
        ClientSideMovement.moveTowards(robot, wantedX, wantedY, wantedZ, speedModifier * 0.1);
    robot.setMovingFromGoal(isMoving);

    if (!isMoving && !hasReachedTarget()) {
      stuckCounter++;
      if (stuckCounter >= STUCK_THRESHOLD) {
        stuckCounter = 0;
        pickNewTarget();
      }
    } else {
      stuckCounter = 0;
      if (isMoving) {
        updateLookDirection();
      }
    }
  }

  private void startPause() {
    isPaused = true;
    stateTimer =
        MIN_PAUSE_INTERVAL
            + robot.level().getRandom().nextInt(MAX_PAUSE_INTERVAL - MIN_PAUSE_INTERVAL);
    robot.setDeltaMovement(0, robot.getDeltaMovement().y, 0);
    robot.setSpeed(0);
  }

  private boolean hasReachedTarget() {
    double dx = wantedX - robot.getX();
    double dz = wantedZ - robot.getZ();
    return Math.sqrt(dx * dx + dz * dz) < REACH_DISTANCE;
  }

  private void pickNewTarget() {
    Vec3 targetPos = getRandomPosition();
    if (targetPos != null) {
      this.wantedX = targetPos.x;
      this.wantedY = targetPos.y;
      this.wantedZ = targetPos.z;
      this.rotationDelay = 10;
      this.stateTimer =
          MIN_MOVE_INTERVAL
              + robot.level().getRandom().nextInt(MAX_MOVE_INTERVAL - MIN_MOVE_INTERVAL);
    }
  }

  private Vec3 getRandomPosition() {
    BlockPos stationPos = robot.getStationPos();
    if (stationPos == null) {
      return null;
    }

    double targetX;
    double targetZ;

    if (isFirstTarget) {
      isFirstTarget = false;
      Direction facing = robot.getFacing();
      double initialDistance = 5.0 + robot.level().getRandom().nextDouble() * 5.0;
      targetX = stationPos.getX() + 0.5 + facing.getStepX() * initialDistance;
      targetZ = stationPos.getZ() + 0.5 + facing.getStepZ() * initialDistance;
    } else {
      double angle = robot.level().getRandom().nextDouble() * Math.PI * 2;
      double distance = 3.0 + robot.level().getRandom().nextDouble() * (searchRadius * 0.7);
      targetX = stationPos.getX() + 0.5 + Math.cos(angle) * distance;
      targetZ = stationPos.getZ() + 0.5 + Math.sin(angle) * distance;
    }

    return ClientSideMovement.findGroundPosition(
        robot.level(), targetX, stationPos.getY(), targetZ, 5);
  }

  private void updateLookDirection() {
    robot.getMoveControl().setWantedPosition(wantedX, wantedY, wantedZ, speedModifier);

    double dx = wantedX - robot.getX();
    double dy = wantedY - robot.getY();
    double dz = wantedZ - robot.getZ();
    double dist = Math.sqrt(dx * dx + dz * dz);

    if (dist > 0.1) {
      double lookX = robot.getX() + (dx / dist) * LOOK_AHEAD_DISTANCE;
      double lookY = robot.getEyeY() + Math.max(0, dy * 0.3);
      double lookZ = robot.getZ() + (dz / dist) * LOOK_AHEAD_DISTANCE;
      robot.getLookControl().setLookAt(lookX, lookY, lookZ);
    }
  }

  private void spawnDiggingParticles() {
    if (robot.level().getGameTime() % 5 == 0) {
      double offsetX = (robot.level().getRandom().nextDouble() - 0.5) * 0.4;
      double offsetZ = (robot.level().getRandom().nextDouble() - 0.5) * 0.4;
      robot
          .level()
          .addParticle(
              ParticleTypes.POOF,
              robot.getX() + offsetX,
              robot.getY() + 0.1,
              robot.getZ() + offsetZ,
              0,
              0.02,
              0);
    }
  }
}
