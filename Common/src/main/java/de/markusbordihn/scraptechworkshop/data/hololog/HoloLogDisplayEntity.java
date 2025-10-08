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

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public record HoloLogDisplayEntity(
    DisplayType type,
    ResourceLocation id,
    float scale,
    float rotationSpeed,
    float rotationX,
    float rotationY,
    float rotationZ,
    ResourceLocation texture,
    boolean slim) {

  // JSON field names
  private static final String FIELD_ID = "id";
  private static final String FIELD_SCALE = "scale";
  private static final String FIELD_ROTATION_SPEED = "rotationSpeed";
  private static final String FIELD_ROTATION_X = "rotationX";
  private static final String FIELD_ROTATION_Y = "rotationY";
  private static final String FIELD_ROTATION_Z = "rotationZ";
  private static final String FIELD_TEXTURE = "texture";
  private static final String FIELD_SLIM = "slim";

  // Default values
  private static final float DEFAULT_SCALE = 0.5f;
  private static final float DEFAULT_ROTATION_SPEED_ENTITY = 0.0f;
  private static final float DEFAULT_ROTATION_SPEED_NON_ENTITY = 1.0f;
  private static final float DEFAULT_ROTATION = 0.0f;

  public static final HoloLogDisplayEntity DEFAULT_VILLAGER =
      new HoloLogDisplayEntity(
          DisplayType.ENTITY,
          new ResourceLocation("minecraft", "villager"),
          DEFAULT_SCALE,
          DEFAULT_ROTATION_SPEED_NON_ENTITY,
          DEFAULT_ROTATION,
          DEFAULT_ROTATION,
          DEFAULT_ROTATION,
          null,
          false);

  public static HoloLogDisplayEntity fromJson(JsonObject json, DisplayType type) {
    ResourceLocation id = new ResourceLocation(json.get(FIELD_ID).getAsString());
    float scale = json.has(FIELD_SCALE) ? json.get(FIELD_SCALE).getAsFloat() : DEFAULT_SCALE;
    float rotationSpeed =
        (type != DisplayType.ENTITY && json.has(FIELD_ROTATION_SPEED))
            ? json.get(FIELD_ROTATION_SPEED).getAsFloat()
            : (type == DisplayType.ENTITY
                ? DEFAULT_ROTATION_SPEED_ENTITY
                : DEFAULT_ROTATION_SPEED_NON_ENTITY);
    float rotationX =
        json.has(FIELD_ROTATION_X) ? json.get(FIELD_ROTATION_X).getAsFloat() : DEFAULT_ROTATION;
    float rotationY =
        json.has(FIELD_ROTATION_Y) ? json.get(FIELD_ROTATION_Y).getAsFloat() : DEFAULT_ROTATION;
    float rotationZ =
        json.has(FIELD_ROTATION_Z) ? json.get(FIELD_ROTATION_Z).getAsFloat() : DEFAULT_ROTATION;
    ResourceLocation texture =
        json.has(FIELD_TEXTURE)
            ? new ResourceLocation(json.get(FIELD_TEXTURE).getAsString())
            : null;
    boolean slim = json.has(FIELD_SLIM) && json.get(FIELD_SLIM).getAsBoolean();

    return new HoloLogDisplayEntity(
        type, id, scale, rotationSpeed, rotationX, rotationY, rotationZ, texture, slim);
  }
}
