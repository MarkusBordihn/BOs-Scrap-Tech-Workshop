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

public record HoloLogData(
    ResourceLocation id,
    String title,
    String subtitle,
    String color,
    String titleColor,
    String subtitleColor,
    float lineDelay,
    float charDelay,
    ResourceLocation voiceOver,
    float voiceOverDelay,
    float endDelay,
    HoloLogDisplayEntity displayEntity,
    HoloLogDisplayRecipe displayRecipe,
    HoloLogEffects start,
    List<HoloLogLine> lines,
    HoloLogEffects end) {

  // JSON field names
  private static final String FIELD_ID = "id";
  private static final String FIELD_TITLE = "title";
  private static final String FIELD_SUBTITLE = "subtitle";
  private static final String FIELD_COLOR = "color";
  private static final String FIELD_TITLE_COLOR = "titleColor";
  private static final String FIELD_SUBTITLE_COLOR = "subtitleColor";
  private static final String FIELD_LINE_DELAY = "lineDelay";
  private static final String FIELD_CHAR_DELAY = "charDelay";
  private static final String FIELD_VOICE_OVER = "voiceOver";
  private static final String FIELD_VOICE_OVER_DELAY = "voiceOverDelay";
  private static final String FIELD_END_DELAY = "endDelay";
  private static final String FIELD_DISPLAY_ENTITY = "displayEntity";
  private static final String FIELD_DISPLAY_BLOCK = "displayBlock";
  private static final String FIELD_DISPLAY_ITEM = "displayItem";
  private static final String FIELD_DISPLAY_HOLO_ENTITY = "displayHoloEntity";
  private static final String FIELD_DISPLAY_RECIPE = "displayRecipe";
  private static final String FIELD_START = "start";
  private static final String FIELD_END = "end";
  private static final String FIELD_LINES = "lines";
  private static final String FIELD_TEXT = "text";
  private static final String FIELD_PLAY_SOUND = "playSound";
  private static final String FIELD_SHOW_PARTICLE = "showParticle";

  // Default values
  private static final String DEFAULT_SUBTITLE = "";
  private static final String DEFAULT_COLOR = "#00FFFF";
  private static final String DEFAULT_TITLE_COLOR = "#00FFFF";
  private static final String DEFAULT_SUBTITLE_COLOR = "#AAAAAA";
  private static final float DEFAULT_LINE_DELAY = 2.0f;
  private static final float DEFAULT_CHAR_DELAY = 0.05f;
  private static final float DEFAULT_VOICE_OVER_DELAY = 0.0f;

  public static HoloLogData fromJson(final JsonObject json) {
    ResourceLocation id = new ResourceLocation(json.get(FIELD_ID).getAsString());
    String title = json.get(FIELD_TITLE).getAsString();
    String subtitle =
        json.has(FIELD_SUBTITLE) ? json.get(FIELD_SUBTITLE).getAsString() : DEFAULT_SUBTITLE;
    String color = json.has(FIELD_COLOR) ? json.get(FIELD_COLOR).getAsString() : DEFAULT_COLOR;
    String titleColor =
        json.has(FIELD_TITLE_COLOR)
            ? json.get(FIELD_TITLE_COLOR).getAsString()
            : DEFAULT_TITLE_COLOR;
    String subtitleColor =
        json.has(FIELD_SUBTITLE_COLOR)
            ? json.get(FIELD_SUBTITLE_COLOR).getAsString()
            : DEFAULT_SUBTITLE_COLOR;
    float lineDelay =
        json.has(FIELD_LINE_DELAY) ? json.get(FIELD_LINE_DELAY).getAsFloat() : DEFAULT_LINE_DELAY;
    float charDelay =
        json.has(FIELD_CHAR_DELAY) ? json.get(FIELD_CHAR_DELAY).getAsFloat() : DEFAULT_CHAR_DELAY;

    ResourceLocation voiceOver =
        json.has(FIELD_VOICE_OVER)
            ? new ResourceLocation(json.get(FIELD_VOICE_OVER).getAsString())
            : null;
    float voiceOverDelay =
        json.has(FIELD_VOICE_OVER_DELAY)
            ? json.get(FIELD_VOICE_OVER_DELAY).getAsFloat()
            : DEFAULT_VOICE_OVER_DELAY;
    float endDelay = json.has(FIELD_END_DELAY) ? json.get(FIELD_END_DELAY).getAsFloat() : lineDelay;

    HoloLogDisplayEntity displayEntity = parseDisplayEntity(json);
    HoloLogDisplayRecipe displayRecipe =
        json.has(FIELD_DISPLAY_RECIPE)
            ? HoloLogDisplayRecipe.fromJson(json.getAsJsonObject(FIELD_DISPLAY_RECIPE))
            : null;
    HoloLogEffects start =
        json.has(FIELD_START)
            ? HoloLogEffects.fromJson(json.getAsJsonObject(FIELD_START))
            : HoloLogEffects.EMPTY;
    List<HoloLogLine> lines = parseLines(json, lineDelay);
    HoloLogEffects end =
        json.has(FIELD_END)
            ? HoloLogEffects.fromJson(json.getAsJsonObject(FIELD_END))
            : HoloLogEffects.EMPTY;

    return new HoloLogData(
        id,
        title,
        subtitle,
        color,
        titleColor,
        subtitleColor,
        lineDelay,
        charDelay,
        voiceOver,
        voiceOverDelay,
        endDelay,
        displayEntity,
        displayRecipe,
        start,
        lines,
        end);
  }

  private static HoloLogDisplayEntity parseDisplayEntity(final JsonObject json) {
    if (json.has(FIELD_DISPLAY_ENTITY)) {
      return HoloLogDisplayEntity.fromJson(
          json.getAsJsonObject(FIELD_DISPLAY_ENTITY), DisplayType.ENTITY);
    } else if (json.has(FIELD_DISPLAY_BLOCK)) {
      return HoloLogDisplayEntity.fromJson(
          json.getAsJsonObject(FIELD_DISPLAY_BLOCK), DisplayType.BLOCK);
    } else if (json.has(FIELD_DISPLAY_ITEM)) {
      return HoloLogDisplayEntity.fromJson(
          json.getAsJsonObject(FIELD_DISPLAY_ITEM), DisplayType.ITEM);
    } else if (json.has(FIELD_DISPLAY_HOLO_ENTITY)) {
      return HoloLogDisplayEntity.fromJson(
          json.getAsJsonObject(FIELD_DISPLAY_HOLO_ENTITY), DisplayType.HOLO_ENTITY);
    }
    return null;
  }

  private static List<HoloLogLine> parseLines(final JsonObject json, final float defaultLineDelay) {
    List<HoloLogLine> lines = new ArrayList<>();
    float currentTime = 0.0f;

    if (json.has(FIELD_LINES)) {
      JsonArray linesArray = json.getAsJsonArray(FIELD_LINES);
      for (JsonElement lineElement : linesArray) {
        JsonObject lineJson = lineElement.getAsJsonObject();
        String text =
            lineJson.has(FIELD_TEXT) ? lineJson.get(FIELD_TEXT).getAsString() : DEFAULT_SUBTITLE;
        float lineSpecificDelay =
            lineJson.has(FIELD_LINE_DELAY) ? lineJson.get(FIELD_LINE_DELAY).getAsFloat() : -1.0f;

        HoloLogDisplayEntity lineDisplayEntity = parseDisplayEntity(lineJson);
        HoloLogDisplayRecipe lineDisplayRecipe =
            lineJson.has(FIELD_DISPLAY_RECIPE)
                ? HoloLogDisplayRecipe.fromJson(lineJson.getAsJsonObject(FIELD_DISPLAY_RECIPE))
                : null;
        HoloLogEffects effects =
            (lineJson.has(FIELD_PLAY_SOUND) || lineJson.has(FIELD_SHOW_PARTICLE))
                ? HoloLogEffects.fromJson(lineJson)
                : HoloLogEffects.EMPTY;

        HoloLogLine line =
            new HoloLogLine(
                text,
                lineDisplayEntity,
                lineDisplayRecipe,
                effects,
                lineSpecificDelay,
                currentTime);
        lines.add(line);

        float effectiveDelay = lineSpecificDelay > 0 ? lineSpecificDelay : defaultLineDelay;
        currentTime += effectiveDelay;
      }
    }

    return lines;
  }

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
