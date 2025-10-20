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

package de.markusbordihn.scraptechworkshop.data.floatingscrapcollector;

import net.minecraft.util.StringRepresentable;

public enum ScrapFilterType implements StringRepresentable {
  NONE("none"),
  BASIC("basic"),
  FINE_MESH("fine_mesh"),
  MAGNETIC_COIL("magnetic_coil"),
  ELECTRO_CONDENSATOR("electro_condensator"),
  JUNK_FILTER("junk_filter");

  private final String name;

  ScrapFilterType(String name) {
    this.name = name;
  }

  public static ScrapFilterType fromIndex(int index) {
    return switch (index) {
      case 1 -> BASIC;
      case 2 -> FINE_MESH;
      case 3 -> MAGNETIC_COIL;
      case 4 -> ELECTRO_CONDENSATOR;
      case 5 -> JUNK_FILTER;
      default -> NONE;
    };
  }

  @Override
  public String getSerializedName() {
    return this.name;
  }

  @Override
  public String toString() {
    return this.name;
  }

  public int getTier() {
    return switch (this) {
      case BASIC -> 1;
      case FINE_MESH -> 2;
      case MAGNETIC_COIL -> 3;
      case ELECTRO_CONDENSATOR -> 4;
      case JUNK_FILTER -> 5;
      default -> 0;
    };
  }
}
