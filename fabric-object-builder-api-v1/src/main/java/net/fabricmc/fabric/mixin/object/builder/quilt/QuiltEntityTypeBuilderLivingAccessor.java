/*
 * Copyright 2022 The Quilt Project
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

package net.fabricmc.fabric.mixin.object.builder.quilt;

import org.quiltmc.qsl.entity.extensions.api.QuiltEntityTypeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

@Mixin(QuiltEntityTypeBuilder.Living.class)
public interface QuiltEntityTypeBuilderLivingAccessor {
	@Invoker("<init>")
	static QuiltEntityTypeBuilder.Living callInit(SpawnGroup spawnGroup, EntityType.EntityFactory function) {
		throw new IllegalStateException("Mixin injection failed.");
	}
}
