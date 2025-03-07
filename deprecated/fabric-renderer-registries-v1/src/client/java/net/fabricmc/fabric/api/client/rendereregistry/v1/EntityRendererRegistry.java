/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.fabric.api.client.rendereregistry.v1;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

/**
 * Helper class for registering EntityRenderers.
 *
 * @deprecated This module has been moved into fabric-rendering-v1. Use {@link net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry} instead
 */
@Deprecated
public interface EntityRendererRegistry {
	EntityRendererRegistry INSTANCE = new EntityRendererRegistry() {
		@Override
		public <T extends Entity> void register(EntityType<? extends T> entityType, EntityRendererFactory<T> factory) {
			net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(entityType, factory);
		}
	};

	/**
	 * Register a BlockEntityRenderer for a BlockEntityType. Can be called clientside before the world is rendered.
	 *
	 * @param entityType the {@link EntityType} to register a renderer for
	 * @param entityRendererFactory a {@link EntityRendererFactory} that creates a {@link EntityRenderer}, called
	 *                            when {@link EntityRenderDispatcher} is initialized or immediately if the dispatcher
	 *                            class is already loaded
	 * @param <E> the {@link Entity}
	 */
	<E extends Entity> void register(EntityType<? extends E> entityType, EntityRendererFactory<E> entityRendererFactory);
}
