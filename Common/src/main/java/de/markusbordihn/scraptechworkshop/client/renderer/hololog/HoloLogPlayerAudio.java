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

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.hololog.HoloLogPlaybackContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class HoloLogPlayerAudio {
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Map<UUID, SoundInstance> ACTIVE_VOICE_OVERS = new HashMap<>();

  private HoloLogPlayerAudio() {}

  public static void playVoiceOver(
      final UUID playerId,
      final ResourceLocation voiceOverId,
      final HoloLogPlaybackContext context) {
    if (voiceOverId == null) {
      log.debug("No voice-over specified for player {}", playerId);
      return;
    }

    stopVoiceOver(playerId);

    try {
      Minecraft minecraft = Minecraft.getInstance();
      String soundEventPath = voiceOverId.getPath().replace('/', '.');
      ResourceLocation soundEventId =
          new ResourceLocation(voiceOverId.getNamespace(), soundEventPath);
      if (!minecraft.getSoundManager().getAvailableSounds().contains(soundEventId)) {
        log.error(
            "Voice-over sound '{}' is not registered in sounds.json! "
                + "Make sure to add an entry for '{}' with the sound file at 'sounds/{}.ogg'",
            soundEventId,
            soundEventId,
            voiceOverId.getPath());
        return;
      }

      SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(soundEventId);
      SimpleSoundInstance soundInstance =
          new SimpleSoundInstance(
              soundEvent.getLocation(),
              SoundSource.VOICE,
              1.0f,
              1.0f,
              SoundInstance.createUnseededRandom(),
              false,
              0,
              SoundInstance.Attenuation.NONE,
              0.0,
              0.0,
              0.0,
              true);

      minecraft.getSoundManager().play(soundInstance);
      ACTIVE_VOICE_OVERS.put(playerId, soundInstance);

      log.info(
          "Started voice-over playback: {} (sound event: {}) for player {}",
          voiceOverId,
          soundEventId,
          playerId);
    } catch (Exception e) {
      log.error("Failed to play voice-over {}: {}", voiceOverId, e.getMessage(), e);
    }
  }

  public static void stopVoiceOver(final UUID playerId) {
    SoundInstance soundInstance = ACTIVE_VOICE_OVERS.remove(playerId);
    if (soundInstance != null) {
      try {
        Minecraft.getInstance().getSoundManager().stop(soundInstance);
        log.debug("Stopped voice-over for player {}", playerId);
      } catch (Exception e) {
        log.warn("Failed to stop voice-over for player {}: {}", playerId, e.getMessage());
      }
    }
  }

  public static void stopAll() {
    for (UUID playerId : ACTIVE_VOICE_OVERS.keySet()) {
      stopVoiceOver(playerId);
    }
    ACTIVE_VOICE_OVERS.clear();
    log.debug("Stopped all voice-overs");
  }
}
