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

package de.markusbordihn.scraptechworkshop.saveddata;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.data.holocube.HoloCubePlayerData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoloCubeStorage extends SavedData {
  public static final String DATA_NAME = Constants.MOD_ID + "_holo_cubes";
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String HOLO_CUBES_TAG = "HoloCubes";

  private static HoloCubeStorage instance;
  private final List<HoloCubePlayerData> holoCubeList;

  public HoloCubeStorage(final List<HoloCubePlayerData> holoCubes) {
    log.info("Creating new HoloCubeStorage with {} entries", holoCubes.size());
    this.holoCubeList = new ArrayList<>(holoCubes);
  }

  public HoloCubeStorage() {
    this(new ArrayList<>());
  }

  public static void init(final ServerLevel serverLevel) {
    if (serverLevel == null) {
      log.error("Cannot initialize HoloCubeStorage without a valid level!");
      return;
    }
    log.info("Initializing HoloCubeStorage with level: {}", serverLevel);
    instance = HoloCubeStorage.get(serverLevel);
  }

  public static HoloCubeStorage get() {
    if (instance == null) {
      throw new IllegalStateException("HoloCubeStorage is not initialized!");
    }
    return instance;
  }

  public static HoloCubeStorage get(final ServerLevel serverLevel) {
    if (instance == null) {
      instance =
          serverLevel
              .getDataStorage()
              .computeIfAbsent(HoloCubeStorage::load, HoloCubeStorage::new, DATA_NAME);
    }
    return instance;
  }

  public static HoloCubeStorage load(final CompoundTag compoundTag) {
    List<HoloCubePlayerData> loadedData =
        HoloCubePlayerData.CODEC
            .listOf()
            .parse(NbtOps.INSTANCE, compoundTag.get(HOLO_CUBES_TAG))
            .resultOrPartial(error -> log.error("Failed to decode holocube data: {}", error))
            .orElse(new ArrayList<>());

    return new HoloCubeStorage(loadedData);
  }

  public boolean hasReceivedHoloCube(final UUID playerUUID, final ResourceLocation holoLogId) {
    return holoCubeList.stream()
        .anyMatch(
            data -> data.playerUUID().equals(playerUUID) && data.holoLogId().equals(holoLogId));
  }

  public void addHoloCube(final HoloCubePlayerData holoCubePlayerData) {
    if (holoCubePlayerData == null) {
      log.warn("Cannot add null holocube data");
      return;
    }
    holoCubeList.add(holoCubePlayerData);
    log.info(
        "Added holocube {} for player {} at {}",
        holoCubePlayerData.holoLogId(),
        holoCubePlayerData.playerUUID(),
        holoCubePlayerData.receivedTimestamp());
    this.setDirty();
  }

  @Override
  public CompoundTag save(CompoundTag compoundTag) {
    HoloCubePlayerData.CODEC
        .listOf()
        .encodeStart(NbtOps.INSTANCE, holoCubeList)
        .resultOrPartial(error -> log.error("Failed to encode holocube data: {}", error))
        .ifPresent(tag -> compoundTag.put(HOLO_CUBES_TAG, tag));

    return compoundTag;
  }
}
