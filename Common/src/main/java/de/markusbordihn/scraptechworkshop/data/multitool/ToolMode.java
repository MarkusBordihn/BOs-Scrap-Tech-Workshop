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

package de.markusbordihn.scraptechworkshop.data.multitool;

import de.markusbordihn.scraptechworkshop.Constants;

public enum ToolMode {
  DEFAULT("default", 0, "mode.default", 0xAAAAAA),
  PICKAXE("pickaxe", 7, "mode.pickaxe", 0x888888),
  AXE("axe", 6, "mode.axe", 0xFF8800),
  SHOVEL("shovel", 8, "mode.shovel", 0xBB8844),
  HOE("hoe", 9, "mode.hoe", 0x00AA00),
  SWORD("sword", 10, "mode.sword", 0xFF0000),
  NONE("none", 0, "mode.none", 0xAAAAAA);

  private final String id;
  private final int modelData;
  private final String translationKey;
  private final int color;

  ToolMode(final String id, final int modelData, final String translationKey, final int color) {
    this.id = id;
    this.modelData = modelData;
    this.translationKey = Constants.ITEM_PREFIX + "scrap_multitool." + translationKey;
    this.color = color;
  }

  public static ToolMode fromId(final String id) {
    for (ToolMode mode : values()) {
      if (mode.id.equals(id)) {
        return mode;
      }
    }
    return DEFAULT;
  }

  public String getId() {
    return id;
  }

  public int getModelData() {
    return modelData;
  }

  public String getTranslationKey() {
    return translationKey;
  }

  public int getColor() {
    return color;
  }

  public boolean isToolMode() {
    return this != DEFAULT && this != NONE;
  }
}
