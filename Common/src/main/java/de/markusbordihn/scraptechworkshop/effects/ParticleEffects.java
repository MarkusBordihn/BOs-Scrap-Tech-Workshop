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

package de.markusbordihn.scraptechworkshop.effects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class ParticleEffects {

  private ParticleEffects() {}

  public static void spawnScrapDustParticles(Level level, BlockPos pos) {
    if (level instanceof ServerLevel serverLevel) {
      spawnServerScrapParticles(serverLevel, pos);
    } else if (level.isClientSide) {
      spawnClientScrapParticles(level, pos);
    }
  }

  public static void spawnMetalSparkParticles(Level level, BlockPos pos) {
    if (level instanceof ServerLevel serverLevel) {
      for (int i = 0; i < 5; i++) {
        double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.6;
        double y = pos.getY() + 0.2 + level.random.nextDouble() * 0.4;
        double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.6;

        serverLevel.sendParticles(
            ParticleTypes.CRIT,
            x,
            y,
            z,
            1,
            (level.random.nextDouble() - 0.5) * 0.1,
            level.random.nextDouble() * 0.1,
            (level.random.nextDouble() - 0.5) * 0.1,
            0.02);
      }
    } else if (level.isClientSide) {
      for (int i = 0; i < 8; i++) {
        double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
        double y = pos.getY() + 0.1 + level.random.nextDouble() * 0.3;
        double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;

        level.addParticle(
            ParticleTypes.CRIT,
            x,
            y,
            z,
            (level.random.nextDouble() - 0.5) * 0.05,
            level.random.nextDouble() * 0.03,
            (level.random.nextDouble() - 0.5) * 0.05);
      }
    }
  }

  public static void spawnTechCircuitParticles(Level level, BlockPos pos) {
    if (level instanceof ServerLevel serverLevel) {
      for (int i = 0; i < 4; i++) {
        double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;
        double y = pos.getY() + 0.15 + level.random.nextDouble() * 0.3;
        double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.8;

        serverLevel.sendParticles(
            ParticleTypes.ELECTRIC_SPARK,
            x,
            y,
            z,
            1,
            (level.random.nextDouble() - 0.5) * 0.06,
            level.random.nextDouble() * 0.06,
            (level.random.nextDouble() - 0.5) * 0.06,
            0.02);
      }
    } else if (level.isClientSide) {
      for (int i = 0; i < 6; i++) {
        double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 1.0;
        double y = pos.getY() + 0.1 + level.random.nextDouble() * 0.25;
        double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 1.0;

        level.addParticle(
            ParticleTypes.ELECTRIC_SPARK,
            x,
            y,
            z,
            (level.random.nextDouble() - 0.5) * 0.04,
            level.random.nextDouble() * 0.02,
            (level.random.nextDouble() - 0.5) * 0.04);
      }
    }
  }

  public static void spawnGenericParticles(
      Level level, BlockPos pos, ParticleOptions particleType, int count, double spread) {
    if (level instanceof ServerLevel serverLevel) {
      for (int i = 0; i < count; i++) {
        double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * spread;
        double y = pos.getY() + 0.1 + level.random.nextDouble() * 0.3;
        double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * spread;

        serverLevel.sendParticles(
            particleType,
            x,
            y,
            z,
            1,
            (level.random.nextDouble() - 0.5) * 0.08,
            level.random.nextDouble() * 0.08,
            (level.random.nextDouble() - 0.5) * 0.08,
            0.02);
      }
    } else if (level.isClientSide) {
      for (int i = 0; i < count; i++) {
        double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * spread;
        double y = pos.getY() + 0.1 + level.random.nextDouble() * 0.3;
        double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * spread;

        level.addParticle(
            particleType,
            x,
            y,
            z,
            (level.random.nextDouble() - 0.5) * 0.06,
            level.random.nextDouble() * 0.04,
            (level.random.nextDouble() - 0.5) * 0.06);
      }
    }
  }

  private static void spawnServerScrapParticles(ServerLevel serverLevel, BlockPos pos) {
    for (int i = 0; i < 3; i++) {
      double x = pos.getX() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.8;
      double y = pos.getY() + 0.1 + serverLevel.random.nextDouble() * 0.3;
      double z = pos.getZ() + 0.5 + (serverLevel.random.nextDouble() - 0.5) * 0.8;

      serverLevel.sendParticles(
          ParticleTypes.POOF,
          x,
          y,
          z,
          1,
          (serverLevel.random.nextDouble() - 0.5) * 0.08,
          serverLevel.random.nextDouble() * 0.08,
          (serverLevel.random.nextDouble() - 0.5) * 0.08,
          0.02);
    }
  }

  private static void spawnClientScrapParticles(Level level, BlockPos pos) {
    for (int i = 0; i < 12; i++) {
      double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 1.4;
      double y = pos.getY() + 0.05 + level.random.nextDouble() * 0.4;
      double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 1.4;

      level.addParticle(
          ParticleTypes.SMOKE,
          x,
          y,
          z,
          (level.random.nextDouble() - 0.5) * 0.06,
          level.random.nextDouble() * 0.04,
          (level.random.nextDouble() - 0.5) * 0.06);
    }

    for (int i = 0; i < 8; i++) {
      double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 1.0;
      double y = pos.getY() + 0.1 + level.random.nextDouble() * 0.2;
      double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 1.0;

      level.addParticle(
          ParticleTypes.ASH,
          x,
          y,
          z,
          (level.random.nextDouble() - 0.5) * 0.03,
          level.random.nextDouble() * 0.02,
          (level.random.nextDouble() - 0.5) * 0.03);
    }
  }
}
