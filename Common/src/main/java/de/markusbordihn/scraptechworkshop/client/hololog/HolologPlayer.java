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
import de.markusbordihn.scraptechworkshop.data.hololog.HolologPlaybackContext;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HolologPlayer {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private final HolologData hololog;
  private final HolologPlaybackContext context;
  private final Consumer<String> textDisplay;
  private final Runnable onComplete;
  private final UUID playerId;

  private PlaybackState state = PlaybackState.STOPPED;
  private int currentLine = 0;
  private boolean endEffectsPlayed = false;
  private boolean voiceOverStarted = false;
  private int ticksForCurrentLine = 0;

  private HolologPlayer(
      HolologData hololog,
      HolologPlaybackContext context,
      Consumer<String> textDisplay,
      Runnable onComplete,
      UUID playerId) {
    this.hololog = hololog;
    this.context = context;
    this.textDisplay = textDisplay;
    this.onComplete = onComplete;
    this.playerId = playerId;
  }

  public static UUID play(
      HolologData hololog, HolologPlaybackContext context, Consumer<String> textDisplay) {
    return play(hololog, context, textDisplay, null);
  }

  public static UUID play(
      HolologData hololog,
      HolologPlaybackContext context,
      Consumer<String> textDisplay,
      Runnable onComplete) {
    UUID id = UUID.randomUUID();
    HolologPlayer player = new HolologPlayer(hololog, context, textDisplay, onComplete, id);
    UUID registeredId = HolologPlayerManager.register(player, hololog.id());

    if (HolologPlayerManager.get(registeredId) == player) {
      log.info("Player registered successfully with ID: {}, starting playback", registeredId);
      player.start();
    } else {
      log.warn(
          "Player registration failed or duplicate detected for hololog: {}, ID: {}",
          hololog.id(),
          registeredId);
    }

    return registeredId;
  }

  public static void stop(UUID id) {
    HolologPlayer player = HolologPlayerManager.get(id);
    if (player != null) {
      player.stop();
      HolologPlayerAudio.stopVoiceOver(id);
      HolologPlayerManager.remove(id);
    }
  }

  public static int getCurrentLine(ResourceLocation holologId) {
    return HolologPlayerManager.getCurrentLine(holologId);
  }

  public static void stopAll() {
    HolologPlayerAudio.stopAll();
    HolologPlayerManager.stopAll();
  }

  public static void tickAll() {
    HolologPlayerManager.tickAll();
  }

  private void start() {
    state = PlaybackState.PLAYING;
    currentLine = 0;
    endEffectsPlayed = false;
    voiceOverStarted = false;
    ticksForCurrentLine = 0;

    log.info(
        "Starting hololog playback: {} (lines: {}, default delay: {})",
        hololog.id(),
        hololog.lines().size(),
        hololog.lineDelayTicks());

    HolologPlayerEffects.playEffects(hololog.start(), context, context.getEffectPosition());

    if (textDisplay != null) {
      String titleText = Component.literal(hololog.title()).getString();
      if (!hololog.subtitle().isEmpty()) {
        titleText += " - " + hololog.subtitle();
      }
      textDisplay.accept(titleText);
      log.debug("Displayed title: {}", titleText);
    }
  }

  void tick() {
    if (state != PlaybackState.PLAYING) {
      return;
    }

    Level level = context.getLevel();
    if (level == null || !(level instanceof ClientLevel)) {
      log.warn("Level is null or not ClientLevel, stopping playback");
      state = PlaybackState.STOPPED;
      return;
    }

    ticksForCurrentLine++;

    if (currentLine < hololog.lines().size()) {
      HolologData.HolologLine line = hololog.lines().get(currentLine);
      int delay = line.lineDelayTicks() > 0 ? line.lineDelayTicks() : hololog.lineDelayTicks();

      if (ticksForCurrentLine >= delay) {
        // Start voice-over when first line is displayed (synchronized with content)
        if (!voiceOverStarted && hololog.hasVoiceOver()) {
          HolologPlayerAudio.playVoiceOver(playerId, hololog.voiceOver(), context);
          voiceOverStarted = true;
          log.debug("Started voice-over synchronized with first line");
        }

        log.debug(
            "Displaying line {}/{}: '{}' (delay: {} ticks, waited: {} ticks)",
            currentLine + 1,
            hololog.lines().size(),
            line.text().isEmpty() ? "<pause>" : line.text(),
            delay,
            ticksForCurrentLine);

        if (textDisplay != null && !line.text().isEmpty()) {
          textDisplay.accept(line.text());
        }

        HolologPlayerEffects.playEffects(line.effects(), context, context.getEffectPosition());

        currentLine++;
        ticksForCurrentLine = 0;
      }
    }

    if (currentLine >= hololog.lines().size()) {
      if (!endEffectsPlayed) {
        HolologPlayerEffects.playEffects(hololog.end(), context, context.getEffectPosition());
        endEffectsPlayed = true;

        if (onComplete != null) {
          log.info("Hololog playback completed: {}", hololog.id());
          onComplete.run();
        }
      }
      state = PlaybackState.STOPPED;
    }
  }

  void stop() {
    state = PlaybackState.STOPPED;
    HolologPlayerAudio.stopVoiceOver(playerId);
  }

  boolean isPlaying() {
    return state == PlaybackState.PLAYING;
  }

  boolean isStopped() {
    return state == PlaybackState.STOPPED;
  }

  boolean hasPlayedEndEffects() {
    return endEffectsPlayed;
  }

  int getCurrentLine() {
    return currentLine;
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
    PLAYING
  }
}
