/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 * Copyright 2023 The Quilt Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.item;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;

@Mixin(CatEntity.class)
class CatEntityMixin {
	@Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent;"))
	private @Nullable FoodComponent getStackAwareFoodComponent(Item instance, PlayerEntity player, Hand hand) {
		return player.getStackInHand(hand).getFoodComponent();
	}

	@Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z"))
	private boolean isStackAwareFood(Item instance, PlayerEntity player, Hand hand) {
		return player.getStackInHand(hand).isFood();
	}
}
