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

package de.markusbordihn.scraptechworkshop.data.hololog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record HoloLogEffects(List<HoloLogSound> sfx, List<HoloLogParticle> fx) {

  public static final HoloLogEffects EMPTY = new HoloLogEffects(List.of(), List.of());

  private static final String FIELD_PLAY_SOUND = "playSound";
  private static final String FIELD_SHOW_PARTICLE = "showParticle";
  private static final String FIELD_ID = "id";
  private static final String FIELD_VOLUME = "volume";
  private static final String FIELD_PITCH = "pitch";
  private static final String FIELD_COUNT = "count";
  private static final String FIELD_COLOR = "color";
  private static final String FIELD_SCALE = "scale";

  private static final float DEFAULT_VOLUME = 1.0f;
  private static final float DEFAULT_PITCH = 1.0f;
  private static final int DEFAULT_PARTICLE_COUNT = 5;
  private static final float DEFAULT_PARTICLE_SCALE = 1.0f;

  public static HoloLogEffects fromJson(JsonObject json) {
    List<HoloLogSound> sounds = new ArrayList<>();
    if (json.has(FIELD_PLAY_SOUND)) {
      JsonArray soundArray = json.getAsJsonArray(FIELD_PLAY_SOUND);
      for (JsonElement soundElement : soundArray) {
        JsonObject soundObj = soundElement.getAsJsonObject();
        ResourceLocation id = new ResourceLocation(soundObj.get(FIELD_ID).getAsString());
        float volume =
            soundObj.has(FIELD_VOLUME) ? soundObj.get(FIELD_VOLUME).getAsFloat() : DEFAULT_VOLUME;
        float pitch =
            soundObj.has(FIELD_PITCH) ? soundObj.get(FIELD_PITCH).getAsFloat() : DEFAULT_PITCH;
        sounds.add(new HoloLogSound(id, volume, pitch));
      }
    }

    List<HoloLogParticle> particles = new ArrayList<>();
    if (json.has(FIELD_SHOW_PARTICLE)) {
      JsonArray particleArray = json.getAsJsonArray(FIELD_SHOW_PARTICLE);
      for (JsonElement particleElement : particleArray) {
        JsonObject particleObj = particleElement.getAsJsonObject();
        ResourceLocation id = new ResourceLocation(particleObj.get(FIELD_ID).getAsString());
        int count =
            particleObj.has(FIELD_COUNT)
                ? particleObj.get(FIELD_COUNT).getAsInt()
                : DEFAULT_PARTICLE_COUNT;
        String color =
            particleObj.has(FIELD_COLOR) ? particleObj.get(FIELD_COLOR).getAsString() : null;
        float scale =
            particleObj.has(FIELD_SCALE)
                ? particleObj.get(FIELD_SCALE).getAsFloat()
                : DEFAULT_PARTICLE_SCALE;
        particles.add(new HoloLogParticle(id, count, color, scale));
      }
    }

    return new HoloLogEffects(sounds, particles);
  }
}
