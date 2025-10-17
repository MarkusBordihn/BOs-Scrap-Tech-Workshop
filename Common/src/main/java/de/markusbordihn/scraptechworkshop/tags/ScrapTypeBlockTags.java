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

package de.markusbordihn.scraptechworkshop.tags;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.scrap.ScrapType;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrapTypeBlockTags {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final Map<ScrapType, TagKey<Block>> SCRAP_TYPE_TO_TAG_MAP =
      new EnumMap<>(ScrapType.class);

  private static final Map<Block, ScrapType> BLOCK_TO_SCRAP_TYPE_CACHE = new HashMap<>();

  static {
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.ALLOY, ModBlockTags.SCRAP_DROP_ALLOY);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.BIO, ModBlockTags.SCRAP_DROP_BIO);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.CAPACITOR, ModBlockTags.SCRAP_DROP_CAPACITOR);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.CERAMIC, ModBlockTags.SCRAP_DROP_CERAMIC);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.CIRCUIT, ModBlockTags.SCRAP_DROP_CIRCUIT);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.COIL, ModBlockTags.SCRAP_DROP_COIL);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.COPPER, ModBlockTags.SCRAP_DROP_COPPER);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.CRYSTAL, ModBlockTags.SCRAP_DROP_CRYSTAL);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.ENERGY_CELL, ModBlockTags.SCRAP_DROP_ENERGY_CELL);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.FASTENER, ModBlockTags.SCRAP_DROP_FASTENER);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.FIBER, ModBlockTags.SCRAP_DROP_FIBER);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.GLASS, ModBlockTags.SCRAP_DROP_GLASS);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.GOLD, ModBlockTags.SCRAP_DROP_GOLD);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.INSULATION, ModBlockTags.SCRAP_DROP_INSULATION);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.IRON, ModBlockTags.SCRAP_DROP_IRON);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.LUMINOUS, ModBlockTags.SCRAP_DROP_LUMINOUS);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.METAL, ModBlockTags.SCRAP_DROP_METAL);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.MINERAL, ModBlockTags.SCRAP_DROP_MINERAL);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.PLASTIC, ModBlockTags.SCRAP_DROP_PLASTIC);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.RUBBER, ModBlockTags.SCRAP_DROP_RUBBER);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.TECH, ModBlockTags.SCRAP_DROP_TECH);
    SCRAP_TYPE_TO_TAG_MAP.put(ScrapType.WOOD, ModBlockTags.SCRAP_DROP_WOOD);
  }

  private ScrapTypeBlockTags() {}

  public static TagKey<Block> getBlockTagForScrapType(ScrapType scrapType) {
    return SCRAP_TYPE_TO_TAG_MAP.get(scrapType);
  }

  public static Map<ScrapType, TagKey<Block>> getAllScrapTypeBlockTags() {
    return SCRAP_TYPE_TO_TAG_MAP;
  }

  public static ScrapType getScrapTypeForBlock(Block block) {
    return BLOCK_TO_SCRAP_TYPE_CACHE.get(block);
  }

  public static void initializeCache() {
    log.info("{} Initializing Scrap Type Block Tags Cache ...", Constants.LOG_INIT_PREFIX);
    for (Map.Entry<ScrapType, TagKey<Block>> entry : SCRAP_TYPE_TO_TAG_MAP.entrySet()) {
      ScrapType scrapType = entry.getKey();
      TagKey<Block> tag = entry.getValue();
      for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
        BLOCK_TO_SCRAP_TYPE_CACHE.put(blockHolder.value(), scrapType);
      }
    }
    log.debug(
        "{} Initialized Scrap Type Block Tags Cache with {} entries: {}",
        Constants.LOG_INIT_PREFIX,
        BLOCK_TO_SCRAP_TYPE_CACHE.size(),
        BLOCK_TO_SCRAP_TYPE_CACHE);
  }
}
