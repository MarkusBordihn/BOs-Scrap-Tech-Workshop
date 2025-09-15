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

package de.markusbordihn.scraptechworkshop.entity;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.block.entity.RecyclerBlockEntity;
import de.markusbordihn.scraptechworkshop.registry.block.RecyclerBlockRegistry;
import de.markusbordihn.scraptechworkshop.registry.block.entity.RecyclerBlockEntityRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModBlockEntities {

  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
      DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);

  // Block Entity registrations
  public static final RegistryObject<BlockEntityType<RecyclerBlockEntity>> RECYCLER_BLOCK_ENTITY =
      BLOCK_ENTITY_TYPES.register(
          RecyclerBlockEntity.ID,
          () ->
              BlockEntityType.Builder.of(
                      RecyclerBlockEntity::new, RecyclerBlockRegistry.RECYCLER_BLOCK)
                  .build(null));

  private ForgeModBlockEntities() {}

  public static void register(IEventBus eventBus) {
    BLOCK_ENTITY_TYPES.register(eventBus);

    eventBus.addListener(
        (RegisterEvent event) -> {
          if (event.getRegistryKey().equals(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey())) {
            RecyclerBlockEntityRegistry.setBlockEntityType(RECYCLER_BLOCK_ENTITY.get());
          }
        });
  }
}
