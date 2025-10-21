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
import de.markusbordihn.scraptechworkshop.Constants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecyclerRecipeSerializer implements RecipeSerializer<RecyclerRecipe> {

  public static final RecyclerRecipeSerializer INSTANCE = new RecyclerRecipeSerializer();
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
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
    RecyclerMatch match = parseMatch(matchJson);
    if (match == null) {
      log.error("Recipe '{}' has invalid match - skipping recipe", recipeId);
      return null;
    }

    JsonObject primaryOutputJson = GsonHelper.getAsJsonObject(json, PRIMARY_OUTPUT_FIELD);
    RecyclerOutput primaryOutput = parseOutput(primaryOutputJson);
    if (primaryOutput == null) {
      log.error("Recipe '{}' has invalid primary output - skipping recipe", recipeId);
      return null;
    }

    List<RecyclerByproduct> byproducts = new ArrayList<>();
    if (json.has(BYPRODUCTS_FIELD)) {
      JsonArray byproductsArray = GsonHelper.getAsJsonArray(json, BYPRODUCTS_FIELD);
      for (int i = 0; i < byproductsArray.size(); i++) {
        JsonObject byproductJson = byproductsArray.get(i).getAsJsonObject();
        RecyclerByproduct byproduct = parseByproduct(byproductJson);
        if (byproduct != null) {
          byproducts.add(byproduct);
        }
      }
    }

    int processTime =
        GsonHelper.getAsInt(json, PROCESS_TIME_FIELD, DEFAULT_PROCESS_TIME_SECONDS) * 20;

    if (processTime < 1) {
      log.error("Process time must be at least 1 tick, got: {} - using default", processTime / 20);
      processTime = DEFAULT_PROCESS_TIME_SECONDS * 20;
    }

    int weight = GsonHelper.getAsInt(json, WEIGHT_FIELD, DEFAULT_WEIGHT);

    return new RecyclerRecipe(recipeId, match, primaryOutput, byproducts, processTime, weight);
  }

  private RecyclerMatch parseMatch(final JsonObject matchJson) {
    if (matchJson.has(ITEM_FIELD)) {
      String itemId = GsonHelper.getAsString(matchJson, ITEM_FIELD);
      ResourceLocation itemLocation = new ResourceLocation(itemId);
      Item item = BuiltInRegistries.ITEM.get(itemLocation);

      if (item == null || item == Items.AIR) {
        log.error("Invalid item '{}' in recipe match field - item does not exist", itemId);
        return null;
      }

      return new RecyclerMatch(item);
    } else if (matchJson.has(TAG_FIELD)) {
      String tagId = GsonHelper.getAsString(matchJson, TAG_FIELD);
      TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation(tagId));
      return new RecyclerMatch(tag);
    } else {
      log.error("Recipe match must have either 'item' or 'tag' field");
      return null;
    }
  }

  private RecyclerOutput parseOutput(final JsonObject outputJson) {
    String itemId = GsonHelper.getAsString(outputJson, ITEM_FIELD);
    ResourceLocation itemLocation = new ResourceLocation(itemId);
    Item item = BuiltInRegistries.ITEM.get(itemLocation);

    if (item == null || item == Items.AIR) {
      log.error("Invalid item '{}' in recipe output field - item does not exist", itemId);
      return null;
    }

    int minCount = DEFAULT_MIN_COUNT;
    int maxCount = DEFAULT_MAX_COUNT;

    if (outputJson.has(COUNT_FIELD)) {
      minCount = maxCount = GsonHelper.getAsInt(outputJson, COUNT_FIELD);
      if (minCount < 1) {
        log.error("Output count must be at least 1, got: {} - using default", minCount);
        minCount = maxCount = DEFAULT_MIN_COUNT;
      }
    } else {
      if (outputJson.has(MIN_FIELD) || outputJson.has(MAX_FIELD)) {
        minCount = GsonHelper.getAsInt(outputJson, MIN_FIELD, DEFAULT_MIN_COUNT);
        maxCount = GsonHelper.getAsInt(outputJson, MAX_FIELD, DEFAULT_MAX_COUNT);

        if (minCount < 1) {
          log.error("Output min count must be at least 1, got: {} - using default", minCount);
          minCount = DEFAULT_MIN_COUNT;
        }
        if (maxCount < minCount) {
          log.error(
              "Output max count ({}) must be >= min count ({}) - using min as max",
              maxCount,
              minCount);
          maxCount = minCount;
        }
      }
    }

    boolean durabilityScaling =
        GsonHelper.getAsBoolean(outputJson, DURABILITY_SCALING_FIELD, DEFAULT_DURABILITY_SCALING);

    return new RecyclerOutput(item, minCount, maxCount, durabilityScaling);
  }

  private RecyclerByproduct parseByproduct(final JsonObject byproductJson) {
    String itemId = GsonHelper.getAsString(byproductJson, ITEM_FIELD);
    ResourceLocation itemLocation = new ResourceLocation(itemId);
    Item item = BuiltInRegistries.ITEM.get(itemLocation);

    if (item == null || item == Items.AIR) {
      log.error("Invalid item '{}' in recipe byproduct field - item does not exist", itemId);
      return null;
    }

    double chance = GsonHelper.getAsDouble(byproductJson, CHANCE_FIELD, DEFAULT_CHANCE);

    if (chance < 0.0 || chance > 1.0) {
      log.error("Byproduct chance must be between 0.0 and 1.0, got: {} - clamping", chance);
      chance = Math.max(0.0, Math.min(1.0, chance));
    }

    int minCount = DEFAULT_MIN_COUNT;
    int maxCount = DEFAULT_MIN_COUNT;

    if (byproductJson.has(COUNT_FIELD)) {
      minCount = maxCount = GsonHelper.getAsInt(byproductJson, COUNT_FIELD);
      if (minCount < 1) {
        log.error("Byproduct count must be at least 1, got: {} - using default", minCount);
        minCount = maxCount = DEFAULT_MIN_COUNT;
      }
    } else {
      if (byproductJson.has(MIN_FIELD) || byproductJson.has(MAX_FIELD)) {
        minCount = GsonHelper.getAsInt(byproductJson, MIN_FIELD, DEFAULT_MIN_COUNT);
        maxCount = GsonHelper.getAsInt(byproductJson, MAX_FIELD, minCount);

        if (minCount < 1) {
          log.error("Byproduct min count must be at least 1, got: {} - using default", minCount);
          minCount = DEFAULT_MIN_COUNT;
        }
        if (maxCount < minCount) {
          log.error(
              "Byproduct max count ({}) must be >= min count ({}) - using min as max",
              maxCount,
              minCount);
          maxCount = minCount;
        }
      }
    }

    return new RecyclerByproduct(item, chance, minCount, maxCount);
  }

  @Override
  public RecyclerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
    boolean isItemMatch = buffer.readBoolean();
    RecyclerMatch match;
    if (isItemMatch) {
      Item item = buffer.readById(BuiltInRegistries.ITEM);
      match = new RecyclerMatch(item);
    } else {
      ResourceLocation tagId = buffer.readResourceLocation();
      TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), tagId);
      match = new RecyclerMatch(tag);
    }

    Item primaryItem = buffer.readById(BuiltInRegistries.ITEM);
    int minCount = buffer.readVarInt();
    int maxCount = buffer.readVarInt();
    boolean durabilityScaling = buffer.readBoolean();
    RecyclerOutput primaryOutput =
        new RecyclerOutput(primaryItem, minCount, maxCount, durabilityScaling);

    int byproductCount = buffer.readVarInt();
    List<RecyclerByproduct> byproducts = new ArrayList<>();
    for (int i = 0; i < byproductCount; i++) {
      Item byproductItem = buffer.readById(BuiltInRegistries.ITEM);
      double chance = buffer.readDouble();
      int byproductMin = buffer.readVarInt();
      int byproductMax = buffer.readVarInt();
      byproducts.add(new RecyclerByproduct(byproductItem, chance, byproductMin, byproductMax));
    }

    int processTime = buffer.readVarInt();
    int weight = buffer.readVarInt();

    return new RecyclerRecipe(recipeId, match, primaryOutput, byproducts, processTime, weight);
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer, RecyclerRecipe recipe) {
    RecyclerMatch match = recipe.match();
    buffer.writeBoolean(match.isItemMatch());
    if (match.isItemMatch()) {
      buffer.writeId(BuiltInRegistries.ITEM, match.getItem());
    } else {
      buffer.writeResourceLocation(match.getTag().location());
    }

    RecyclerOutput primaryOutput = recipe.primaryOutput();
    buffer.writeId(BuiltInRegistries.ITEM, primaryOutput.getItem());
    buffer.writeVarInt(primaryOutput.getMinCount());
    buffer.writeVarInt(primaryOutput.getMaxCount());
    buffer.writeBoolean(primaryOutput.isDurabilityScaling());

    List<RecyclerByproduct> byproducts = recipe.byproducts();
    buffer.writeVarInt(byproducts.size());
    for (RecyclerByproduct byproduct : byproducts) {
      buffer.writeId(BuiltInRegistries.ITEM, byproduct.getItem());
      buffer.writeDouble(byproduct.getChance());
      buffer.writeVarInt(byproduct.getMinCount());
      buffer.writeVarInt(byproduct.getMaxCount());
    }

    buffer.writeVarInt(recipe.processTime());
    buffer.writeVarInt(recipe.weight());
  }
}
