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

package de.markusbordihn.scraptechworkshop.client.hololog;

import de.markusbordihn.scraptechworkshop.data.hololog.HolologData;
import de.markusbordihn.scraptechworkshop.data.hololog.HolologPlaybackContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class HolologPlayerEffects {

  private static final double PARTICLE_SPAWN_SPREAD = 0.5;
  private static final double PARTICLE_VELOCITY_HORIZONTAL = 0.02;
  private static final double PARTICLE_VELOCITY_VERTICAL = 0.05;

  private HolologPlayerEffects() {}

  public static void playEffects(
      HolologData.HolologEffects effects, HolologPlaybackContext context, Vec3 effectPos) {
    if (!(context.getLevel() instanceof ClientLevel)) {
      return;
    }

    for (HolologData.HolologSound sound : effects.sfx()) {
      context.playSound(sound.id(), sound.volume(), sound.pitch());
    }

    for (HolologData.HolologParticle particle : effects.fx()) {
      spawnParticles(particle, effectPos, context);
    }
  }

  private static void spawnParticles(
      HolologData.HolologParticle particle, Vec3 pos, HolologPlaybackContext context) {
    ParticleOptions particleType = getParticleType(particle);

    for (int i = 0; i < particle.count(); i++) {
      Vec3 spawnPos =
          pos.add(
              (Math.random() - 0.5) * PARTICLE_SPAWN_SPREAD,
              (Math.random() - 0.5) * PARTICLE_SPAWN_SPREAD,
              (Math.random() - 0.5) * PARTICLE_SPAWN_SPREAD);

      Vec3 velocity =
          new Vec3(
              (Math.random() - 0.5) * PARTICLE_VELOCITY_HORIZONTAL,
              Math.random() * PARTICLE_VELOCITY_VERTICAL,
              (Math.random() - 0.5) * PARTICLE_VELOCITY_HORIZONTAL);

      context.spawnParticle(particleType, spawnPos, velocity);
    }
  }

  private static ParticleOptions getParticleType(HolologData.HolologParticle particle) {
    String particleId = particle.id().getPath();

    if ("dust".equals(particleId) && particle.color() != null) {
      Vector3f color = parseColor(particle.color());
      return new DustParticleOptions(color, particle.scale());
    }

    return switch (particleId) {
      case "smoke" -> ParticleTypes.SMOKE;
      case "portal" -> ParticleTypes.PORTAL;
      case "crit" -> ParticleTypes.CRIT;
      case "flash" -> ParticleTypes.FLASH;
      default -> ParticleTypes.END_ROD;
    };
  }

  private static Vector3f parseColor(String hexColor) {
    if (hexColor == null || hexColor.isEmpty()) {
      return new Vector3f(1.0f, 1.0f, 1.0f);
    }

    try {
      int color = Integer.parseInt(hexColor.replace("#", ""), 16);
      float r = FastColor.ARGB32.red(color | 0xFF000000) / 255.0f;
      float g = FastColor.ARGB32.green(color | 0xFF000000) / 255.0f;
      float b = FastColor.ARGB32.blue(color | 0xFF000000) / 255.0f;
      return new Vector3f(r, g, b);
    } catch (NumberFormatException e) {
      return new Vector3f(1.0f, 1.0f, 1.0f);
    }
  }
}
