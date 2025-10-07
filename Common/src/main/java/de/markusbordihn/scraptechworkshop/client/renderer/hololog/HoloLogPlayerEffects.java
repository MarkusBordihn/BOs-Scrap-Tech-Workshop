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

package de.markusbordihn.scraptechworkshop.client.renderer.hololog;

import de.markusbordihn.scraptechworkshop.data.hololog.ContextType;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogEffects;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogParticle;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogPlaybackContext;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class HoloLogPlayerEffects {

  private static final double PARTICLE_SPAWN_SPREAD = 0.5;
  private static final double PARTICLE_VELOCITY_HORIZONTAL = 0.02;
  private static final double PARTICLE_VELOCITY_VERTICAL = 0.05;

  private HoloLogPlayerEffects() {}

  public static void playEffects(
      final HoloLogEffects effects, final HoloLogPlaybackContext context, final Vec3 effectPos) {
    if (!(context.getLevel() instanceof ClientLevel)) {
      return;
    }

    for (HoloLogSound sound : effects.sfx()) {
      playSound(sound.id(), sound.volume(), sound.pitch(), context);
    }

    for (HoloLogParticle particle : effects.fx()) {
      spawnParticles(particle, effectPos, context);
    }
  }

  public static void playSound(
      final ResourceLocation soundId,
      final float volume,
      final float pitch,
      final HoloLogPlaybackContext context) {
    SoundEvent sound = SoundEvent.createVariableRangeEvent(soundId);

    if (context.contextType() == de.markusbordihn.scraptechworkshop.data.hololog.ContextType.UI) {
      net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
      if (mc.player != null) {
        Vec3 playerPos = mc.player.position();
        Level level = context.getLevel();
        level.playLocalSound(
            playerPos.x, playerPos.y, playerPos.z, sound, SoundSource.BLOCKS, volume, pitch, false);
      }
    } else {
      Vec3 pos = context.getEffectPosition();
      Level level = context.getLevel();
      level.playLocalSound(pos.x, pos.y, pos.z, sound, SoundSource.BLOCKS, volume, pitch, false);
    }
  }

  public static void spawnParticle(
      final ParticleOptions particle,
      final Vec3 pos,
      final Vec3 speed,
      final HoloLogPlaybackContext context) {
    if (context.getLevel() instanceof ClientLevel clientLevel) {
      clientLevel.addParticle(particle, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
    }
  }

  private static void spawnParticles(
      final HoloLogParticle particle, final Vec3 pos, final HoloLogPlaybackContext context) {
    ParticleOptions particleType = getParticleType(particle);

    // For UI context, spawn particles at player position
    Vec3 spawnBasePos = pos;
    if (context.contextType() == ContextType.UI) {
      Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
      if (minecraft.player != null) {
        spawnBasePos = minecraft.player.position().add(0, 1.5, 0);
      }
    }

    for (int i = 0; i < particle.count(); i++) {
      Vec3 spawnPos =
          spawnBasePos.add(
              (Math.random() - 0.5) * PARTICLE_SPAWN_SPREAD,
              (Math.random() - 0.5) * PARTICLE_SPAWN_SPREAD,
              (Math.random() - 0.5) * PARTICLE_SPAWN_SPREAD);

      Vec3 velocity =
          new Vec3(
              (Math.random() - 0.5) * PARTICLE_VELOCITY_HORIZONTAL,
              Math.random() * PARTICLE_VELOCITY_VERTICAL,
              (Math.random() - 0.5) * PARTICLE_VELOCITY_HORIZONTAL);

      spawnParticle(particleType, spawnPos, velocity, context);
    }
  }

  private static ParticleOptions getParticleType(final HoloLogParticle particle) {
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

  private static Vector3f parseColor(final String hexColor) {
    if (hexColor == null || hexColor.isEmpty()) {
      return new Vector3f(1.0f, 1.0f, 1.0f);
    }

    try {
      int color = Integer.parseInt(hexColor.replace("#", ""), 16);
      return new Vector3f(
          FastColor.ARGB32.red(color | 0xFF000000) / 255.0f,
          FastColor.ARGB32.green(color | 0xFF000000) / 255.0f,
          FastColor.ARGB32.blue(color | 0xFF000000) / 255.0f);
    } catch (NumberFormatException e) {
      return new Vector3f(1.0f, 1.0f, 1.0f);
    }
  }
}
