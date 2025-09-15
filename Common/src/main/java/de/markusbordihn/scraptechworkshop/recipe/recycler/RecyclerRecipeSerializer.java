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

  private static final String MATCH_FIELD = "match";
  private static final String PRIMARY_OUTPUT_FIELD = "primary_output";
  private static final String BYPRODUCTS_FIELD = "byproducts";
  private static final String PROCESS_TIME_FIELD = "process_time";
  private static final String WEIGHT_FIELD = "weight";
  private static final String ITEM_FIELD = "item";
  private static final String TAG_FIELD = "tag";
  private static final String COUNT_FIELD = "count";
  private static final String MIN_FIELD = "min";
  private static final String MAX_FIELD = "max";
  private static final String DURABILITY_SCALING_FIELD = "durability_scaling";
  private static final String CHANCE_FIELD = "chance";

  private static final int DEFAULT_WEIGHT = 0;
  private static final int DEFAULT_MIN_COUNT = 1;
  private static final int DEFAULT_MAX_COUNT = 3;
  private static final int DEFAULT_PROCESS_TIME_SECONDS = 10;
  private static final double DEFAULT_CHANCE = 0.0;
  private static final boolean DEFAULT_DURABILITY_SCALING = false;

  private RecyclerRecipeSerializer() {}

  @Override
  public RecyclerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
    JsonObject matchJson = GsonHelper.getAsJsonObject(json, MATCH_FIELD);
    RecyclerRecipe.RecyclerMatch match = parseMatch(matchJson);

    JsonObject primaryOutputJson = GsonHelper.getAsJsonObject(json, PRIMARY_OUTPUT_FIELD);
    RecyclerRecipe.RecyclerOutput primaryOutput = parseOutput(primaryOutputJson);

    List<RecyclerRecipe.RecyclerByproduct> byproducts = new ArrayList<>();
    if (json.has(BYPRODUCTS_FIELD)) {
      JsonArray byproductsArray = GsonHelper.getAsJsonArray(json, BYPRODUCTS_FIELD);
      for (int i = 0; i < byproductsArray.size(); i++) {
        JsonObject byproductJson = byproductsArray.get(i).getAsJsonObject();
        byproducts.add(parseByproduct(byproductJson));
      }
    }

    int processTime =
        GsonHelper.getAsInt(json, PROCESS_TIME_FIELD, DEFAULT_PROCESS_TIME_SECONDS) * 20;
    int weight = GsonHelper.getAsInt(json, WEIGHT_FIELD, DEFAULT_WEIGHT);

    return new RecyclerRecipe(recipeId, match, primaryOutput, byproducts, processTime, weight);
  }

  private RecyclerRecipe.RecyclerMatch parseMatch(JsonObject matchJson) {
    if (matchJson.has(ITEM_FIELD)) {
      String itemId = GsonHelper.getAsString(matchJson, ITEM_FIELD);
      Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemId));
      return new RecyclerRecipe.RecyclerMatch(item);
    } else if (matchJson.has(TAG_FIELD)) {
      String tagId = GsonHelper.getAsString(matchJson, TAG_FIELD);
      TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation(tagId));
      return new RecyclerRecipe.RecyclerMatch(tag);
    } else {
      throw new IllegalArgumentException("Match must have either 'item' or 'tag'");
    }
  }

  private RecyclerRecipe.RecyclerOutput parseOutput(JsonObject outputJson) {
    String itemId = GsonHelper.getAsString(outputJson, ITEM_FIELD);
    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemId));

    int minCount = DEFAULT_MIN_COUNT;
    int maxCount = DEFAULT_MAX_COUNT;

    if (outputJson.has(COUNT_FIELD)) {
      minCount = maxCount = GsonHelper.getAsInt(outputJson, COUNT_FIELD);
    } else {
      if (outputJson.has(MIN_FIELD) || outputJson.has(MAX_FIELD)) {
        minCount = GsonHelper.getAsInt(outputJson, MIN_FIELD, DEFAULT_MIN_COUNT);
        maxCount = GsonHelper.getAsInt(outputJson, MAX_FIELD, DEFAULT_MAX_COUNT);
      }
    }

    boolean durabilityScaling =
        GsonHelper.getAsBoolean(outputJson, DURABILITY_SCALING_FIELD, DEFAULT_DURABILITY_SCALING);

    return new RecyclerRecipe.RecyclerOutput(item, minCount, maxCount, durabilityScaling);
  }

  private RecyclerRecipe.RecyclerByproduct parseByproduct(JsonObject byproductJson) {
    String itemId = GsonHelper.getAsString(byproductJson, ITEM_FIELD);
    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemId));

    double chance = GsonHelper.getAsDouble(byproductJson, CHANCE_FIELD, DEFAULT_CHANCE);

    int minCount = DEFAULT_MIN_COUNT;
    int maxCount = DEFAULT_MIN_COUNT;

    if (byproductJson.has(COUNT_FIELD)) {
      minCount = maxCount = GsonHelper.getAsInt(byproductJson, COUNT_FIELD);
    } else {
      if (byproductJson.has(MIN_FIELD) || byproductJson.has(MAX_FIELD)) {
        minCount = GsonHelper.getAsInt(byproductJson, MIN_FIELD, DEFAULT_MIN_COUNT);
        maxCount = GsonHelper.getAsInt(byproductJson, MAX_FIELD, minCount);
      }
    }

    return new RecyclerRecipe.RecyclerByproduct(item, chance, minCount, maxCount);
  }

  @Override
  public RecyclerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
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

    Item primaryItem = buffer.readById(BuiltInRegistries.ITEM);
    int minCount = buffer.readVarInt();
    int maxCount = buffer.readVarInt();
    boolean durabilityScaling = buffer.readBoolean();
    RecyclerRecipe.RecyclerOutput primaryOutput =
        new RecyclerRecipe.RecyclerOutput(primaryItem, minCount, maxCount, durabilityScaling);

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
    RecyclerRecipe.RecyclerMatch match = recipe.match();
    buffer.writeBoolean(match.isItemMatch());
    if (match.isItemMatch()) {
      buffer.writeId(BuiltInRegistries.ITEM, match.getItem());
    } else {
      buffer.writeResourceLocation(match.getTag().location());
    }

    RecyclerRecipe.RecyclerOutput primaryOutput = recipe.primaryOutput();
    buffer.writeId(BuiltInRegistries.ITEM, primaryOutput.getItem());
    buffer.writeVarInt(primaryOutput.getMinCount());
    buffer.writeVarInt(primaryOutput.getMaxCount());
    buffer.writeBoolean(primaryOutput.isDurabilityScaling());

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
