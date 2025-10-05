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

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record HolologData(
    ResourceLocation id,
    String title,
    String subtitle,
    String titleColor,
    String subtitleColor,
    int lineDelayTicks,
    int charDelayTicks,
    ResourceLocation voiceOver,
    HolologDisplayEntity displayEntity,
    HolologEffects start,
    List<HolologLine> lines,
    HolologEffects end) {

  public boolean hasVoiceOver() {
    return voiceOver != null;
  }

  public int getTotalTicks() {
    return lines.size() * lineDelayTicks;
  }

  public enum DisplayType {
    ENTITY,
    BLOCK,
    ITEM,
    HOLO_ENTITY
  }

  public record HolologEffects(List<HolologSound> sfx, List<HolologParticle> fx) {
    public static final HolologEffects EMPTY = new HolologEffects(List.of(), List.of());
  }

  public record HolologSound(ResourceLocation id, float volume, float pitch) {
    public HolologSound(ResourceLocation id) {
      this(id, 1.0f, 1.0f);
    }
  }

  public record HolologParticle(ResourceLocation id, int count, String color, float scale) {
    public HolologParticle(ResourceLocation id) {
      this(id, 5, null, 1.0f);
    }

    public HolologParticle(ResourceLocation id, int count) {
      this(id, count, null, 1.0f);
    }
  }

  public record HolologLine(
      String text, HolologDisplayEntity displayEntity, HolologEffects effects, int lineDelayTicks) {
    public HolologLine(String text) {
      this(text, null, HolologEffects.EMPTY, -1);
    }

    public HolologLine(String text, HolologEffects effects) {
      this(text, null, effects, -1);
    }

    public HolologLine(String text, HolologDisplayEntity displayEntity, HolologEffects effects) {
      this(text, displayEntity, effects, -1);
    }
  }

  public record HolologDisplayEntity(
      DisplayType type,
      ResourceLocation id,
      float scale,
      float rotationSpeed,
      float rotationX,
      float rotationY,
      float rotationZ,
      ResourceLocation texture,
      boolean slim) {
    public static final HolologDisplayEntity DEFAULT_VILLAGER =
        new HolologDisplayEntity(
            DisplayType.ENTITY,
            new ResourceLocation("minecraft", "villager"),
            0.5f,
            1.0f,
            0.0f,
            0.0f,
            0.0f,
            null,
            false);

    public HolologDisplayEntity(DisplayType type, ResourceLocation id) {
      this(type, id, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, null, false);
    }

    public HolologDisplayEntity(DisplayType type, ResourceLocation id, float scale) {
      this(type, id, scale, 1.0f, 0.0f, 0.0f, 0.0f, null, false);
    }

    public HolologDisplayEntity(
        DisplayType type, ResourceLocation id, float scale, float rotationSpeed) {
      this(type, id, scale, rotationSpeed, 0.0f, 0.0f, 0.0f, null, false);
    }
  }
}
