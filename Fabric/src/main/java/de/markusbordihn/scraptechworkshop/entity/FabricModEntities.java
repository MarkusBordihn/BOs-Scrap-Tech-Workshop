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
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class FabricModEntities {

  private FabricModEntities() {}

  public static void registerEntities() {
    // Register HolologHumanoid Entity
    EntityType<HolologHumanoidEntity> holologHumanoidEntityType =
        EntityType.Builder.of(HolologHumanoidEntity::new, MobCategory.MISC)
            .sized(0.6f, 1.8f)
            .clientTrackingRange(10)
            .build(HolologHumanoidEntity.ID);

    Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        new ResourceLocation(Constants.MOD_ID, HolologHumanoidEntity.ID),
        holologHumanoidEntityType);

    // Register default attributes
    FabricDefaultAttributeRegistry.register(
        holologHumanoidEntityType, HolologHumanoidEntity.createAttributes());

    // Set the entity type in the common registry
    HolologEntityRegistry.setHolologHumanoidEntityType(holologHumanoidEntityType);
  }
}
