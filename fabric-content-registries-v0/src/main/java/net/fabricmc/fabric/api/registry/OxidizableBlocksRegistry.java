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

package net.fabricmc.fabric.api.registry;

import java.util.Objects;

import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.ReversibleBlockEntry;
import org.quiltmc.quilted_fabric_api.impl.content.registry.util.QuiltDeferringQueues;

import net.minecraft.block.Block;

/**
 * Provides methods for registering oxidizable and waxable blocks.
 *
 * @deprecated Use Quilt Block Content Registry API's {@link org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries#OXIDIZABLE} and {@link org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries#WAXABLE} registry attachments instead.
 */
@Deprecated
public final class OxidizableBlocksRegistry {
	private OxidizableBlocksRegistry() {
	}

	/**
	 * Registers a block pair as being able to increase and decrease oxidation.
	 *
	 * @param less the variant with less oxidation
	 * @param more the variant with more oxidation
	 */
	public static void registerOxidizableBlockPair(Block less, Block more) {
		Objects.requireNonNull(less, "Oxidizable block cannot be null!");
		Objects.requireNonNull(more, "Oxidizable block cannot be null!");
		QuiltDeferringQueues.addEntry(BlockContentRegistries.OXIDIZABLE, less, new ReversibleBlockEntry(more, true));
	}

	/**
	 * Registers a block pair as being able to add and remove wax.
	 *
	 * @param unwaxed the unwaxed variant
	 * @param waxed   the waxed variant
	 */
	public static void registerWaxableBlockPair(Block unwaxed, Block waxed) {
		Objects.requireNonNull(unwaxed, "Unwaxed block cannot be null!");
		Objects.requireNonNull(waxed, "Waxed block cannot be null!");
		QuiltDeferringQueues.addEntry(BlockContentRegistries.WAXABLE, unwaxed, new ReversibleBlockEntry(waxed, true));
	}
}
