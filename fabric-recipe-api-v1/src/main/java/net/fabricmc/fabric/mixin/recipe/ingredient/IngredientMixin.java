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

package net.fabricmc.fabric.mixin.recipe.ingredient;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientImpl;

@Mixin(Ingredient.class)
public class IngredientMixin implements FabricIngredient {
	@Inject(method = "createCodec", at = @At("RETURN"), cancellable = true)
	private static void injectCodec(boolean allowEmpty, CallbackInfoReturnable<Codec<Ingredient>> cir) {
		Codec<CustomIngredient> customIngredientCodec = CustomIngredientImpl.CODEC.dispatch(
				CustomIngredientImpl.TYPE_KEY,
				CustomIngredient::getSerializer,
				serializer -> serializer.getCodec(allowEmpty));

		cir.setReturnValue(Codecs.either(customIngredientCodec, cir.getReturnValue()).xmap(
				either -> either.map(CustomIngredient::toVanilla, ingredient -> ingredient),
				ingredient -> {
					CustomIngredient customIngredient = ingredient.getCustomIngredient();
					return customIngredient == null ? Either.right(ingredient) : Either.left(customIngredient);
				}
		));
	}

	@Inject(
			at = @At("HEAD"),
			method = "fromPacket",
			cancellable = true
	)
	private static void injectFromPacket(PacketByteBuf buf, CallbackInfoReturnable<Ingredient> cir) {
		int index = buf.readerIndex();

		if (buf.readVarInt() == CustomIngredientImpl.PACKET_MARKER) {
			Identifier type = buf.readIdentifier();
			CustomIngredientSerializer<?> serializer = CustomIngredientSerializer.get(type);

			if (serializer == null) {
				throw new IllegalArgumentException("Cannot deserialize custom ingredient of unknown type " + type);
			}

			cir.setReturnValue(serializer.read(buf).toVanilla());
		} else {
			// Reset index for vanilla's normal deserialization logic.
			buf.readerIndex(index);
		}
	}
}
