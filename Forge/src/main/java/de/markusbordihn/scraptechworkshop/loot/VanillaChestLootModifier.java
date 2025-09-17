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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.markusbordihn.scraptechworkshop.data.loot.VanillaChestLootData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class VanillaChestLootModifier extends LootModifier {

  public static final Codec<VanillaChestLootModifier> VANILLA_CHEST_LOOT_MODIFIER_CODEC =
      RecordCodecBuilder.create(
          inst -> codecStart(inst).apply(inst, VanillaChestLootModifier::new));

  public VanillaChestLootModifier(LootItemCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Override
  protected @NotNull ObjectArrayList<ItemStack> doApply(
      ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
    // Get the loot table ID from context
    ResourceLocation lootTableId = context.getQueriedLootTableId();

    // Check if this loot table should be modified using common data
    if (VanillaChestLootData.shouldModifyLootTable(lootTableId)) {
      ResourceLocation injectionTable = VanillaChestLootData.getInjectionTable(lootTableId);

      if (injectionTable != null) {
        try {
          ServerLevel level = context.getLevel();
          LootTable injectionLootTable =
              level.getServer().getLootData().getLootTable(injectionTable);

          if (injectionLootTable != null) {
            // Generate additional loot from injection table
            LootParams.Builder paramsBuilder = new LootParams.Builder(level);

            // Copy relevant parameters from the original context if they exist
            if (context.hasParam(LootContextParams.ORIGIN)) {
              paramsBuilder.withParameter(
                  LootContextParams.ORIGIN, context.getParam(LootContextParams.ORIGIN));
            }

            if (context.hasParam(LootContextParams.THIS_ENTITY)) {
              paramsBuilder.withParameter(
                  LootContextParams.THIS_ENTITY, context.getParam(LootContextParams.THIS_ENTITY));
            }

            LootParams params = paramsBuilder.create(LootContextParamSets.CHEST);
            ObjectArrayList<ItemStack> additionalLoot = injectionLootTable.getRandomItems(params);

            generatedLoot.addAll(additionalLoot);
            VanillaChestLootData.logSuccessfulInjection(lootTableId, injectionTable);
          } else {
            VanillaChestLootData.logFailedInjection(
                lootTableId, injectionTable, "Injection table not found");
          }
        } catch (Exception e) {
          VanillaChestLootData.logFailedInjection(lootTableId, injectionTable, e.getMessage());
        }
      } else {
        VanillaChestLootData.logSkippedInjection(lootTableId, "No injection table configured");
      }
    }

    return generatedLoot;
  }

  @Override
  public Codec<? extends IGlobalLootModifier> codec() {
    return VANILLA_CHEST_LOOT_MODIFIER_CODEC;
  }
}
