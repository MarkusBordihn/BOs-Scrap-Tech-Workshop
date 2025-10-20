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

package de.markusbordihn.scraptechworkshop.data.wire;

import net.minecraft.world.item.Rarity;

public enum WireType {
  RECLAIMED_COPPER("reclaimed_copper_wire", true, 50, Rarity.COMMON),
  REFINED_COPPER("refined_copper_wire", true, 100, Rarity.UNCOMMON);

  private final String id;
  private final boolean canConductPower;
  private final int powerCapacity;
  private final Rarity rarity;

  WireType(String id, boolean canConductPower, int powerCapacity, Rarity rarity) {
    this.id = id;
    this.canConductPower = canConductPower;
    this.powerCapacity = powerCapacity;
    this.rarity = rarity;
  }

  public String getItemId() {
    return id;
  }

  public boolean canConductPower() {
    return canConductPower;
  }

  public int powerCapacity() {
    return powerCapacity;
  }

  public Rarity rarity() {
    return rarity;
  }
}
