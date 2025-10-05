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
import de.markusbordihn.scraptechworkshop.entity.hololog.HolologHumanoidEntity;
import de.markusbordihn.scraptechworkshop.registry.entity.HolologEntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModEntities {

  public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
      DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);

  // Entity registrations
  public static final RegistryObject<EntityType<HolologHumanoidEntity>> HOLOLOG_HUMANOID =
      ENTITY_TYPES.register(
          HolologHumanoidEntity.ID,
          () ->
              EntityType.Builder.of(HolologHumanoidEntity::new, MobCategory.MISC)
                  .sized(0.6f, 1.8f)
                  .clientTrackingRange(10)
                  .build(HolologHumanoidEntity.ID));

  private ForgeModEntities() {}

  public static void register(IEventBus eventBus) {
    ENTITY_TYPES.register(eventBus);

    eventBus.addListener(
        (RegisterEvent event) -> {
          if (event.getRegistryKey().equals(ForgeRegistries.ENTITY_TYPES.getRegistryKey())) {
            HolologEntityRegistry.setHolologHumanoidEntityType(HOLOLOG_HUMANOID.get());
          }
        });

    eventBus.addListener(ForgeModEntities::registerEntityAttributes);
  }

  private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
    event.put(HOLOLOG_HUMANOID.get(), HolologHumanoidEntity.createAttributes().build());
  }
}
