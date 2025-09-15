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

import java.util.HashMap;
import java.util.Map;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public enum ScrapSoundType {
  STONE(SoundEvents.STONE_BREAK),
  METAL(SoundEvents.COPPER_BREAK),
  CRYSTAL(SoundEvents.AMETHYST_CLUSTER_BREAK),
  CHAIN(SoundEvents.CHAIN_BREAK),
  ANVIL(SoundEvents.ANVIL_LAND),
  IRON(SoundEvents.IRON_TRAPDOOR_CLOSE),
  GLASS(SoundEvents.GLASS_BREAK),
  SOFT(SoundEvents.WOOL_BREAK),
  ELECTRONIC(SoundEvents.REDSTONE_TORCH_BURNOUT),
  WOOD(SoundEvents.WOOD_BREAK),
  SLIME(SoundEvents.SLIME_BLOCK_BREAK);

  private static final Map<ScrapType, ScrapSoundType> SCRAP_TYPE_TO_SOUND_MAP;

  static {
    Map<ScrapType, ScrapSoundType> map = new HashMap<>();
    map.put(ScrapType.BIO, SOFT);
    map.put(ScrapType.CERAMIC, STONE);
    map.put(ScrapType.COPPER, METAL);
    map.put(ScrapType.CRYSTAL, CRYSTAL);
    map.put(ScrapType.FASTENER, CHAIN);
    map.put(ScrapType.FIBER, SOFT);
    map.put(ScrapType.GLASS, GLASS);
    map.put(ScrapType.GOLD, ANVIL);
    map.put(ScrapType.IRON, IRON);
    map.put(ScrapType.LUMINOUS, GLASS);
    map.put(ScrapType.METAL, METAL);
    map.put(ScrapType.MINERAL, STONE);
    map.put(ScrapType.PLASTIC, SOFT);
    map.put(ScrapType.RUBBER, SLIME);
    map.put(ScrapType.TECH, ELECTRONIC);
    map.put(ScrapType.WOOD, WOOD);
    SCRAP_TYPE_TO_SOUND_MAP = Map.copyOf(map);
  }

  private final SoundEvent soundEvent;

  ScrapSoundType(SoundEvent soundEvent) {
    this.soundEvent = soundEvent;
  }

  public static ScrapSoundType forScrapType(ScrapType scrapType) {
    return SCRAP_TYPE_TO_SOUND_MAP.get(scrapType);
  }

  public SoundEvent getSoundEvent() {
    return soundEvent;
  }
}
