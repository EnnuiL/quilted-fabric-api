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

package net.fabricmc.fabric.api.command.v2;

import com.mojang.brigadier.arguments.ArgumentType;
import org.quiltmc.qsl.command.mixin.ArgumentTypeInfosAccessor;

import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * @deprecated If possible, use Quilt Command API's {@link org.quiltmc.qsl.command.api.ServerArgumentType}'s registry instead.
*/
@Deprecated
public final class ArgumentTypeRegistry {
	/**
	 * Register a new argument type.
	 *
	 * @param id the identifier of the argument type
	 * @param clazz the class of the argument type
	 * @param serializer the serializer for the argument type
	 * @param <A> the argument type
	 * @param <T> the argument type properties
	 */
	public static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void registerArgumentType(
			Identifier id, Class<? extends A> clazz, ArgumentSerializer<A, T> serializer) {
		ArgumentTypeInfosAccessor.callRegister(Registries.COMMAND_ARGUMENT_TYPE, id.toString(), clazz, serializer);
	}

	private ArgumentTypeRegistry() {
	}
}
