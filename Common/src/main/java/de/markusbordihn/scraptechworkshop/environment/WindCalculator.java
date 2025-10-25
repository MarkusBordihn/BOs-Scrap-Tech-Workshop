package de.markusbordihn.scraptechworkshop.environment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class WindCalculator {

  private static final int MIN_HEIGHT = 64;
  private static final int OPTIMAL_HEIGHT = 150;
  private static final int MAX_WIND_SPEED = 250;
  private static final Direction[] HORIZONTAL_DIRECTIONS = {
    Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
  };

  public static int calculateWindSpeed(Level level, BlockPos pos) {
    if (level == null || pos == null || !level.canSeeSky(pos)) {
      return 0;
    }

    int baseSpeed = 80;
    int height = pos.getY();
    if (height > MIN_HEIGHT) {
      int heightDiff = Math.min(height - MIN_HEIGHT, OPTIMAL_HEIGHT - MIN_HEIGHT);
      int heightBonus = (int) (heightDiff * 0.7);
      baseSpeed += heightBonus;
    }

    // Check for obstructions around the turbine blades
    float directObstructionPenalty = calculateDirectObstruction(level, pos);
    baseSpeed = (int) (baseSpeed * (1.0f - directObstructionPenalty));

    int opennessBonus = (int) (calculateOpenness(level, pos) * 40);
    baseSpeed += opennessBonus;

    int chunkX = pos.getX() >> 4;
    int chunkZ = pos.getZ() >> 4;
    int chunkVariation = (int) (((chunkX * 31L + chunkZ) & 0x7FFFFFFF) % 30) - 15;
    baseSpeed += chunkVariation;

    float timeModifier =
        1.0f + 0.2f * (float) Math.sin((level.getDayTime() % 24000) / 24000.0 * Math.PI * 2);
    baseSpeed = (int) (baseSpeed * timeModifier);

    return Math.max(0, Math.min(MAX_WIND_SPEED, baseSpeed));
  }

  private static float calculateDirectObstruction(Level level, BlockPos turbineBladePos) {
    int blockedDirections = 0;

    // Check the 4 horizontal directions around the turbine blades
    for (Direction direction : HORIZONTAL_DIRECTIONS) {
      BlockPos neighborPos = turbineBladePos.relative(direction);
      if (!level.getBlockState(neighborPos).isAir()) {
        blockedDirections++;
      }
    }

    return blockedDirections * 0.25f;
  }

  private static float calculateOpenness(Level level, BlockPos pos) {
    int blockedCount = 0;

    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        if (x == 0 && z == 0) continue;

        if (!level.getBlockState(pos.offset(x, 0, z)).isAir()) {
          blockedCount++;
        }
      }
    }

    return 1.0f - (blockedCount / 8.0f);
  }

  public static int calculatePowerGeneration(int windSpeed) {
    if (windSpeed < 30) {
      return 0;
    }
    return (int) (windSpeed * 0.2f);
  }
}
