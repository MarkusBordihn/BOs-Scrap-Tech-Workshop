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

package de.markusbordihn.scraptechworkshop.registry.item.hololog;

import de.markusbordihn.scraptechworkshop.item.hololog.HoloCubeItem;
import de.markusbordihn.scraptechworkshop.item.hololog.HoloPadItem;
import de.markusbordihn.scraptechworkshop.registry.block.hololog.HoloLogBlockRegistry;
import net.minecraft.world.item.Item;

public class HoloLogItemRegistry {

  public static final HoloCubeItem HOLO_CUBE_ITEM =
      new HoloCubeItem(HoloLogBlockRegistry.HOLO_CUBE_BLOCK, new Item.Properties());

  public static final HoloPadItem HOLO_PAD_ITEM = new HoloPadItem(new Item.Properties());

  private HoloLogItemRegistry() {}
}
