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

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.hololog.HolologData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

public class HolologPlayer {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Map<UUID, HolologPlayer> ACTIVE_PLAYERS = new HashMap<>();

  private final HolologData hololog;
  private final PlaybackContext context;
  private final Consumer<String> textDisplay;
  private final Runnable onComplete;

  private PlaybackState state = PlaybackState.STOPPED;
  private int currentTick = 0;
  private int currentLine = 0;
  private boolean endEffectsPlayed = false;

  private HolologPlayer(
      HolologData hololog,
      PlaybackContext context,
      Consumer<String> textDisplay,
      Runnable onComplete) {
    this.hololog = hololog;
    this.context = context;
    this.textDisplay = textDisplay;
    this.onComplete = onComplete;
  }

  public static UUID play(
      HolologData hololog, PlaybackContext context, Consumer<String> textDisplay) {
    return play(hololog, context, textDisplay, null);
  }

  public static UUID play(
      HolologData hololog,
      PlaybackContext context,
      Consumer<String> textDisplay,
      Runnable onComplete) {
    log.info("Starting hololog playback: {}", hololog.id());
    UUID id = UUID.randomUUID();
    HolologPlayer player = new HolologPlayer(hololog, context, textDisplay, onComplete);
    ACTIVE_PLAYERS.put(id, player);
    player.start();
    return id;
  }

  public static void stop(UUID id) {
    HolologPlayer player = ACTIVE_PLAYERS.remove(id);
    if (player != null) {
      player.state = PlaybackState.STOPPED;
    }
  }

  public static void pause(UUID id) {
    HolologPlayer player = ACTIVE_PLAYERS.get(id);
    if (player != null && player.state == PlaybackState.PLAYING) {
      player.state = PlaybackState.PAUSED;
    }
  }

  public static void resume(UUID id) {
    HolologPlayer player = ACTIVE_PLAYERS.get(id);
    if (player != null && player.state == PlaybackState.PAUSED) {
      player.state = PlaybackState.PLAYING;
    }
  }

  public static int getCurrentLine(ResourceLocation holologId) {
    for (HolologPlayer player : ACTIVE_PLAYERS.values()) {
      if (player.hololog.id().equals(holologId)) {
        return player.currentLine;
      }
    }
    return -1;
  }

  public static void stopAll() {
    for (HolologPlayer player : ACTIVE_PLAYERS.values()) {
      player.state = PlaybackState.STOPPED;
    }
    ACTIVE_PLAYERS.clear();
  }

  public static void tickAll() {
    ACTIVE_PLAYERS
        .values()
        .removeIf(
            player -> {
              if (player.state == PlaybackState.PLAYING) {
                player.tick();
              }
              return player.state == PlaybackState.STOPPED && player.endEffectsPlayed;
            });
  }

  private void start() {
    state = PlaybackState.PLAYING;
    currentTick = 0;
    currentLine = 0;
    endEffectsPlayed = false;

    playEffects(hololog.start());

    if (textDisplay != null) {
      String titleText = Component.literal(hololog.title()).getString();
      if (!hololog.subtitle().isEmpty()) {
        titleText += " - " + hololog.subtitle();
      }
      textDisplay.accept(titleText);
    }
  }

  private void tick() {
    if (state != PlaybackState.PLAYING) {
      return;
    }

    Level level = context.getLevel();
    if (level == null || !(level instanceof ClientLevel)) {
      state = PlaybackState.STOPPED;
      return;
    }

    currentTick++;
    if (currentLine < hololog.lines().size()) {
      HolologData.HolologLine line = hololog.lines().get(currentLine);

      if (currentTick % hololog.lineDelayTicks() == 0) {
        if (textDisplay != null) {
          textDisplay.accept(line.text());
        }

        playEffects(line.effects());

        currentLine++;
      }
    }

    if (currentLine >= hololog.lines().size()) {
      if (!endEffectsPlayed) {
        playEffects(hololog.end());
        endEffectsPlayed = true;

        if (onComplete != null) {
          log.info("Hololog playback completed: {}", hololog.id());
          onComplete.run();
        }
      }
      state = PlaybackState.STOPPED;
    }
  }

  private void playEffects(HolologData.HolologEffects effects) {
    if (!(context.getLevel() instanceof ClientLevel)) {
      return;
    }

    Vec3 effectPos = context.getEffectPosition();

    for (HolologData.HolologSound sound : effects.sfx()) {
      context.playSound(sound.id(), sound.volume(), sound.pitch());
    }

    for (HolologData.HolologParticle particle : effects.fx()) {
      spawnParticles(particle, effectPos);
    }
  }

  private void spawnParticles(HolologData.HolologParticle particle, Vec3 pos) {
    ParticleOptions particleType = getParticleType(particle);

    for (int i = 0; i < particle.count(); i++) {
      Vec3 spawnPos =
          pos.add(
              (Math.random() - 0.5) * 0.5,
              (Math.random() - 0.5) * 0.5,
              (Math.random() - 0.5) * 0.5);

      context.spawnParticle(
          particleType,
          spawnPos,
          new Vec3(
              (Math.random() - 0.5) * 0.02, Math.random() * 0.05, (Math.random() - 0.5) * 0.02));
    }
  }

  private ParticleOptions getParticleType(HolologData.HolologParticle particle) {
    String particleId = particle.id().getPath();

    if (particleId.equals("dust") && particle.color() != null) {
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

  private Vector3f parseColor(String hexColor) {
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

  public float getProgress() {
    if (hololog.lines().isEmpty()) {
      return 1.0f;
    }
    return (float) currentLine / hololog.lines().size();
  }

  public boolean isActive() {
    return state != PlaybackState.STOPPED;
  }

  private enum PlaybackState {
    STOPPED,
    PLAYING,
    PAUSED
  }

  public sealed interface PlaybackContext permits WorldContext, UIContext {
    Level getLevel();

    Vec3 getEffectPosition();

    default void spawnParticle(ParticleOptions particle, Vec3 pos, Vec3 speed) {
      if (getLevel() instanceof ClientLevel clientLevel) {
        clientLevel.addParticle(particle, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
      }
    }

    default void playSound(ResourceLocation soundId, float volume, float pitch) {
      SoundEvent sound = SoundEvent.createVariableRangeEvent(soundId);
      Vec3 pos = getEffectPosition();
      getLevel()
          .playLocalSound(pos.x, pos.y, pos.z, sound, SoundSource.BLOCKS, volume, pitch, false);
    }
  }

  public record WorldContext(Level level, BlockPos blockPos) implements PlaybackContext {
    @Override
    public Level getLevel() {
      return level;
    }

    @Override
    public Vec3 getEffectPosition() {
      return Vec3.atCenterOf(blockPos).add(0, 0.75, 0);
    }
  }

  public record UIContext(Level level, Vec3 position) implements PlaybackContext {
    @Override
    public Level getLevel() {
      return level;
    }

    @Override
    public Vec3 getEffectPosition() {
      return position;
    }
  }
}
