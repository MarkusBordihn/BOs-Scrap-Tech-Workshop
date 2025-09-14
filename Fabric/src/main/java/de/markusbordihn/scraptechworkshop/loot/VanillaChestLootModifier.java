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

package de.markusbordihn.scraptechworkshop.loot;

import de.markusbordihn.scraptechworkshop.data.loot.VanillaChestLootData;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class VanillaChestLootModifier {

  public static void register() {
    LootTableEvents.MODIFY.register(
        (resourceManager, lootManager, id, tableBuilder, source) -> {
          ResourceLocation injectionTable = VanillaChestLootData.getInjectionTable(id);
          if (injectionTable != null) {
            try {
              LootPool.Builder pool =
                  LootPool.lootPool()
                      .add(LootTableReference.lootTableReference(injectionTable))
                      .setRolls(ConstantValue.exactly(1));
              tableBuilder.pool(pool.build());

              VanillaChestLootData.logSuccessfulInjection(id, injectionTable);
            } catch (Exception e) {
              VanillaChestLootData.logFailedInjection(id, injectionTable, e.getMessage());
            }
          }
        });
  }
}
