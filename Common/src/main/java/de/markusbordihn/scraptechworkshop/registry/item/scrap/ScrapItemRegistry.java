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

package de.markusbordihn.scraptechworkshop.registry.item.scrap;

import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import de.markusbordihn.scraptechworkshop.item.scrap.CeramicScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.CrystalScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.FastenerScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.LuminousScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.MetalScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.PlasticScrapItem;
import de.markusbordihn.scraptechworkshop.item.scrap.TechScrapItem;
import net.minecraft.world.item.Item;

public class ScrapItemRegistry {

  // Metal Scrap Items (actual metals)
  public static final MetalScrapItem COPPER_SCRAP_ITEM =
      new MetalScrapItem(new Item.Properties(), ScrapType.COPPER);
  public static final MetalScrapItem GOLD_SCRAP_ITEM =
      new MetalScrapItem(new Item.Properties(), ScrapType.GOLD);
  public static final MetalScrapItem IRON_SCRAP_ITEM =
      new MetalScrapItem(new Item.Properties(), ScrapType.IRON);
  public static final MetalScrapItem METAL_SCRAP_ITEM =
      new MetalScrapItem(new Item.Properties(), ScrapType.METAL);

  // Specialized Scrap Items (different material types with their own classes)
  public static final CeramicScrapItem CERAMIC_SCRAP_ITEM =
      new CeramicScrapItem(new Item.Properties());
  public static final CrystalScrapItem CRYSTAL_SCRAP_ITEM =
      new CrystalScrapItem(new Item.Properties());
  public static final FastenerScrapItem FASTENER_SCRAP_ITEM =
      new FastenerScrapItem(new Item.Properties(), ScrapType.FASTENER);
  public static final LuminousScrapItem LUMINOUS_SCRAP_ITEM =
      new LuminousScrapItem(new Item.Properties());
  public static final PlasticScrapItem PLASTIC_SCRAP_ITEM =
      new PlasticScrapItem(new Item.Properties());

  // Additional Tech Scrap Items for recycler system
  public static final TechScrapItem TECH_SCRAP_ITEM = new TechScrapItem(new Item.Properties());
  public static final TechScrapItem CIRCUIT_SCRAP_ITEM = new TechScrapItem(new Item.Properties());
  public static final TechScrapItem COIL_SCRAP_ITEM = new TechScrapItem(new Item.Properties());
  public static final TechScrapItem CAPACITOR_SCRAP_ITEM = new TechScrapItem(new Item.Properties());
  public static final TechScrapItem ENERGY_CELL_SCRAP_ITEM =
      new TechScrapItem(new Item.Properties());

  /*
    // Additional Material Scrap Items
    public static final PlasticScrapItem RUBBER_SCRAP_ITEM =
        new PlasticScrapItem(new Item.Properties());
    public static final CeramicScrapItem GLASS_SCRAP_ITEM =
        new CeramicScrapItem(new Item.Properties());
    public static final MetalScrapItem FASTENER_SCRAP_ITEM =
        new MetalScrapItem(new Item.Properties(), ScrapType.METAL);

    // Organic and Textile Scrap Items
    public static final PlasticScrapItem FIBER_SCRAP_ITEM =
        new PlasticScrapItem(new Item.Properties());
    public static final PlasticScrapItem WOOD_SCRAP_ITEM =
        new PlasticScrapItem(new Item.Properties());
    public static final PlasticScrapItem BIO_SCRAP_ITEM = new PlasticScrapItem(new Item.Properties());

    // Optional advanced scrap items (for future expansion)
    public static final MetalScrapItem GILDED_SCRAP_ITEM =
        new MetalScrapItem(new Item.Properties(), ScrapType.GOLD);
  */
  private ScrapItemRegistry() {}
}
