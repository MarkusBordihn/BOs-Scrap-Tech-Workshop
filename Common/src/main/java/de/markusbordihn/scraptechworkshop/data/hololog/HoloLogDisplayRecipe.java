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

public record HoloLogDisplayRecipe(ResourceLocation recipeId, float scale) {

  // JSON field names
  private static final String FIELD_RECIPE = "recipe";
  private static final String FIELD_SCALE = "scale";

  // Default values
  private static final float DEFAULT_SCALE = 1.0f;

  public static HoloLogDisplayRecipe fromJson(final JsonObject json) {
    ResourceLocation recipeId = new ResourceLocation(json.get(FIELD_RECIPE).getAsString());
    float scale = json.has(FIELD_SCALE) ? json.get(FIELD_SCALE).getAsFloat() : DEFAULT_SCALE;
    return new HoloLogDisplayRecipe(recipeId, scale);
  }
}
