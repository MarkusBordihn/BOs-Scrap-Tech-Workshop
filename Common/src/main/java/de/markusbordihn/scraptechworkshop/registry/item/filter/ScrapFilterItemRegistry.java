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

package de.markusbordihn.scraptechworkshop.registry.item.filter;

import de.markusbordihn.scraptechworkshop.data.floatingscrapcollector.ScrapFilterType;
import de.markusbordihn.scraptechworkshop.item.ScrapFilterItem;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScrapFilterItemRegistry {

  public static final ScrapFilterItem BASIC_SCRAP_FILTER =
      new ScrapFilterItem(ScrapFilterType.BASIC, 50);

  public static final ScrapFilterItem FINE_MESH_FILTER =
      new ScrapFilterItem(ScrapFilterType.FINE_MESH, 75);

  public static final ScrapFilterItem MAGNETIC_COIL_FILTER =
      new ScrapFilterItem(ScrapFilterType.MAGNETIC_COIL, 100);

  public static final ScrapFilterItem ELECTRO_CONDENSATOR_FILTER =
      new ScrapFilterItem(ScrapFilterType.ELECTRO_CONDENSATOR, 125);

  public static final ScrapFilterItem JUNK_FILTER =
      new ScrapFilterItem(ScrapFilterType.JUNK_FILTER, 150);

  private static final Map<String, ScrapFilterItem> FILTER_REGISTRY = new LinkedHashMap<>();

  static {
    FILTER_REGISTRY.put("basic_scrap_filter", BASIC_SCRAP_FILTER);
    FILTER_REGISTRY.put("fine_mesh_filter", FINE_MESH_FILTER);
    FILTER_REGISTRY.put("magnetic_coil_filter", MAGNETIC_COIL_FILTER);
    FILTER_REGISTRY.put("electro_condensator_filter", ELECTRO_CONDENSATOR_FILTER);
    FILTER_REGISTRY.put("junk_filter", JUNK_FILTER);
  }

  private ScrapFilterItemRegistry() {}

  public static Map<String, ScrapFilterItem> getAllFilterItems() {
    return FILTER_REGISTRY;
  }

  public static ScrapFilterItem getFilterItem(String id) {
    return FILTER_REGISTRY.get(id);
  }
}
