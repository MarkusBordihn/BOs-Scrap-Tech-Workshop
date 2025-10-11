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

package de.markusbordihn.scraptechworkshop.entity.scraprobot;

import de.markusbordihn.scraptechworkshop.entity.BaseRobotEntity;
import de.markusbordihn.scraptechworkshop.item.ModItemTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class MixedScrapRobotEntity extends BaseRobotEntity {

  public static final String ID = "mixed_scrap_robot";

  public MixedScrapRobotEntity(
      final EntityType<? extends BaseRobotEntity> entityType, final Level level) {
    super(entityType, level);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return createBaseAttributes();
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(0, new FloatGoal(this));
    this.goalSelector.addGoal(
        1, new TemptGoal(this, 1.0D, Ingredient.of(ModItemTags.MIXED_SCRAP), false));
    this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
    this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
    this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    this.goalSelector.addGoal(5, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));
  }

  @Override
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    ItemStack itemStack = player.getItemInHand(hand);

    if (itemStack.isEmpty()) {
      return super.mobInteract(player, hand);
    }

    boolean isValidFood = itemStack.is(ModItemTags.MIXED_SCRAP);

    if (!this.level().isClientSide && isValidFood) {
      if (!player.getAbilities().instabuild) {
        itemStack.shrink(1);
      }
      this.heal(2.0F);
    }

    if (isValidFood) {
      this.nodYes();
      this.playSound(SoundEvents.PLAYER_BURP, 1.0F, 1.0F);
    } else {
      this.shakeNo();
      this.playSound(SoundEvents.VILLAGER_NO, 1.0F, 1.0F);
    }

    return InteractionResult.sidedSuccess(this.level().isClientSide);
  }

  @Override
  public boolean removeWhenFarAway(double distanceToClosestPlayer) {
    return false;
  }
}
