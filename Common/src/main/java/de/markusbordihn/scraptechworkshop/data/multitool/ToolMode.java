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

public enum ToolMode {
  DEFAULT("default", 0), // Battery level will be used for model data
  PICKAXE("pickaxe", 7),
  AXE("axe", 6),
  SHOVEL("shovel", 8),
  HOE("hoe", 9),
  SWORD("sword", 10),
  NONE("none", 0);

  private final String id;
  private final int modelData;

  ToolMode(String id, int modelData) {
    this.id = id;
    this.modelData = modelData;
  }

  public static ToolMode fromId(String id) {
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

  public boolean isToolMode() {
    return this != DEFAULT && this != NONE;
  }
}
