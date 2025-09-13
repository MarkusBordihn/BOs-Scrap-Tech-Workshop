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

package de.markusbordihn.scraptechworkshop.recipe;

import de.markusbordihn.scraptechworkshop.Constants;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipeSerializer;
import de.markusbordihn.scraptechworkshop.recipe.recycler.RecyclerRecipeType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModRecipes {

  public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
      DeferredRegister.create(Registries.RECIPE_TYPE, Constants.MOD_ID);

  public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
      DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Constants.MOD_ID);

  public static final RegistryObject<RecipeType<?>> RECYCLER_RECIPE_TYPE =
      RECIPE_TYPES.register("recycler", () -> RecyclerRecipeType.INSTANCE);

  public static final RegistryObject<RecipeSerializer<?>> RECYCLER_RECIPE_SERIALIZER =
      RECIPE_SERIALIZERS.register("recycler", () -> RecyclerRecipeSerializer.INSTANCE);

  private ForgeModRecipes() {}

  public static void register(IEventBus eventBus) {
    RECIPE_TYPES.register(eventBus);
    RECIPE_SERIALIZERS.register(eventBus);
  }
}
