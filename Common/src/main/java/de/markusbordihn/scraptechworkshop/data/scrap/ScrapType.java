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

package de.markusbordihn.scraptechworkshop.data.scrap;

public enum ScrapType {
  ALLOY(ScrapCategory.MATERIAL),
  BIO(ScrapCategory.ORGANIC),
  CAPACITOR_SCRAP(ScrapCategory.TECHNOLOGY),
  CERAMIC(ScrapCategory.SYNTHETIC),
  CIRCUIT_SCRAP(ScrapCategory.TECHNOLOGY),
  COIL_SCRAP(ScrapCategory.MATERIAL),
  COPPER(ScrapCategory.MATERIAL),
  CRYSTAL(ScrapCategory.MINERAL),
  ENERGY_CELL_SCRAP(ScrapCategory.TECHNOLOGY),
  FASTENER(ScrapCategory.MATERIAL),
  FIBER(ScrapCategory.ORGANIC),
  GLASS(ScrapCategory.SYNTHETIC),
  GOLD(ScrapCategory.MATERIAL),
  INSULATION(ScrapCategory.SYNTHETIC),
  IRON(ScrapCategory.MATERIAL),
  LUMINOUS(ScrapCategory.SYNTHETIC),
  METAL(ScrapCategory.MATERIAL),
  MINERAL(ScrapCategory.MINERAL),
  PLASTIC(ScrapCategory.SYNTHETIC),
  RUBBER(ScrapCategory.ORGANIC),
  TECH(ScrapCategory.TECHNOLOGY),
  WOOD(ScrapCategory.ORGANIC);

  private final ScrapCategory category;

  ScrapType(ScrapCategory category) {
    this.category = category;
  }

  public ScrapCategory getCategory() {
    return category;
  }
}
