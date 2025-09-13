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

package de.markusbordihn.scraptechworkshop.recipe.recycler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.markusbordihn.scraptechworkshop.config.RecyclerConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecyclerRecipeSerializer implements RecipeSerializer<RecyclerRecipe> {

  public static final RecyclerRecipeSerializer INSTANCE = new RecyclerRecipeSerializer();

  private RecyclerRecipeSerializer() {}

  @Override
  public RecyclerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
    // Parse match (item or tag)
    JsonObject matchJson = GsonHelper.getAsJsonObject(json, "match");
    RecyclerRecipe.RecyclerMatch match = parseMatch(matchJson);

    // Parse primary output
    JsonObject primaryOutputJson = GsonHelper.getAsJsonObject(json, "primary_output");
    RecyclerRecipe.RecyclerOutput primaryOutput = parseOutput(primaryOutputJson);

    // Parse byproducts (optional)
    List<RecyclerRecipe.RecyclerByproduct> byproducts = new ArrayList<>();
    if (json.has("byproducts")) {
      JsonArray byproductsArray = GsonHelper.getAsJsonArray(json, "byproducts");
      for (int i = 0; i < byproductsArray.size(); i++) {
        JsonObject byproductJson = byproductsArray.get(i).getAsJsonObject();
        byproducts.add(parseByproduct(byproductJson));
      }
    }

    // Parse process time (optional, defaults to config)
    int processTime = GsonHelper.getAsInt(json, "process_time", RecyclerConfig.processTime);

    // Parse weight (optional, for recipe priority)
    int weight = GsonHelper.getAsInt(json, "weight", 0);

    return new RecyclerRecipe(recipeId, match, primaryOutput, byproducts, processTime, weight);
  }

  private RecyclerRecipe.RecyclerMatch parseMatch(JsonObject matchJson) {
    if (matchJson.has("item")) {
      String itemId = GsonHelper.getAsString(matchJson, "item");
      Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemId));
      return new RecyclerRecipe.RecyclerMatch(item);
    } else if (matchJson.has("tag")) {
      String tagId = GsonHelper.getAsString(matchJson, "tag");
      TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation(tagId));
      return new RecyclerRecipe.RecyclerMatch(tag);
    } else {
      throw new IllegalArgumentException("Match must have either 'item' or 'tag'");
    }
  }

  private RecyclerRecipe.RecyclerOutput parseOutput(JsonObject outputJson) {
    String itemId = GsonHelper.getAsString(outputJson, "item");
    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemId));

    int minCount, maxCount;
    if (outputJson.has("count")) {
      minCount = maxCount = GsonHelper.getAsInt(outputJson, "count");
    } else {
      minCount = GsonHelper.getAsInt(outputJson, "min", 1);
      maxCount = GsonHelper.getAsInt(outputJson, "max", minCount);
    }

    boolean durabilityScaling = GsonHelper.getAsBoolean(outputJson, "durability_scaling", false);

    return new RecyclerRecipe.RecyclerOutput(item, minCount, maxCount, durabilityScaling);
  }

  private RecyclerRecipe.RecyclerByproduct parseByproduct(JsonObject byproductJson) {
    String itemId = GsonHelper.getAsString(byproductJson, "item");
    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemId));

    double chance = GsonHelper.getAsDouble(byproductJson, "chance", 0.0);

    int minCount, maxCount;
    if (byproductJson.has("count")) {
      minCount = maxCount = GsonHelper.getAsInt(byproductJson, "count");
    } else {
      minCount = GsonHelper.getAsInt(byproductJson, "min", 1);
      maxCount = GsonHelper.getAsInt(byproductJson, "max", minCount);
    }

    return new RecyclerRecipe.RecyclerByproduct(item, chance, minCount, maxCount);
  }

  @Override
  public RecyclerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
    // Read match
    boolean isItemMatch = buffer.readBoolean();
    RecyclerRecipe.RecyclerMatch match;
    if (isItemMatch) {
      Item item = buffer.readById(BuiltInRegistries.ITEM);
      match = new RecyclerRecipe.RecyclerMatch(item);
    } else {
      ResourceLocation tagId = buffer.readResourceLocation();
      TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), tagId);
      match = new RecyclerRecipe.RecyclerMatch(tag);
    }

    // Read primary output
    Item primaryItem = buffer.readById(BuiltInRegistries.ITEM);
    int minCount = buffer.readVarInt();
    int maxCount = buffer.readVarInt();
    boolean durabilityScaling = buffer.readBoolean();
    RecyclerRecipe.RecyclerOutput primaryOutput =
        new RecyclerRecipe.RecyclerOutput(primaryItem, minCount, maxCount, durabilityScaling);

    // Read byproducts
    int byproductCount = buffer.readVarInt();
    List<RecyclerRecipe.RecyclerByproduct> byproducts = new ArrayList<>();
    for (int i = 0; i < byproductCount; i++) {
      Item byproductItem = buffer.readById(BuiltInRegistries.ITEM);
      double chance = buffer.readDouble();
      int byproductMin = buffer.readVarInt();
      int byproductMax = buffer.readVarInt();
      byproducts.add(
          new RecyclerRecipe.RecyclerByproduct(byproductItem, chance, byproductMin, byproductMax));
    }

    int processTime = buffer.readVarInt();
    int weight = buffer.readVarInt();

    return new RecyclerRecipe(recipeId, match, primaryOutput, byproducts, processTime, weight);
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer, RecyclerRecipe recipe) {
    // Write match
    RecyclerRecipe.RecyclerMatch match = recipe.match();
    buffer.writeBoolean(match.isItemMatch());
    if (match.isItemMatch()) {
      buffer.writeId(BuiltInRegistries.ITEM, match.getItem());
    } else {
      buffer.writeResourceLocation(match.getTag().location());
    }

    // Write primary output
    RecyclerRecipe.RecyclerOutput primaryOutput = recipe.primaryOutput();
    buffer.writeId(BuiltInRegistries.ITEM, primaryOutput.getItem());
    buffer.writeVarInt(primaryOutput.getMinCount());
    buffer.writeVarInt(primaryOutput.getMaxCount());
    buffer.writeBoolean(primaryOutput.isDurabilityScaling());

    // Write byproducts
    List<RecyclerRecipe.RecyclerByproduct> byproducts = recipe.byproducts();
    buffer.writeVarInt(byproducts.size());
    for (RecyclerRecipe.RecyclerByproduct byproduct : byproducts) {
      buffer.writeId(BuiltInRegistries.ITEM, byproduct.getItem());
      buffer.writeDouble(byproduct.getChance());
      buffer.writeVarInt(byproduct.getMinCount());
      buffer.writeVarInt(byproduct.getMaxCount());
    }

    buffer.writeVarInt(recipe.processTime());
    buffer.writeVarInt(recipe.weight());
  }
}
