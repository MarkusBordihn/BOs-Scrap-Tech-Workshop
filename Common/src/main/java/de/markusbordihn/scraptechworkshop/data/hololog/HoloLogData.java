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

public record HoloLogData(
    ResourceLocation id,
    String title,
    String subtitle,
    String titleColor,
    String subtitleColor,
    float lineDelay,
    float charDelay,
    ResourceLocation voiceOver,
    HoloLogDisplayEntity displayEntity,
    HoloLogEffects start,
    List<HoloLogLine> lines,
    HoloLogEffects end) {

  public boolean hasVoiceOver() {
    return voiceOver != null;
  }

  public float getTotalDuration() {
    if (lines.isEmpty()) {
      return 0.0f;
    }
    HoloLogLine lastLine = lines.get(lines.size() - 1);
    return lastLine.startTime() + (lastLine.lineDelay() > 0 ? lastLine.lineDelay() : lineDelay);
  }
}
