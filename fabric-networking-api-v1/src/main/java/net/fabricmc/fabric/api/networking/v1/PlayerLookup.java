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

package net.fabricmc.fabric.api.networking.v1;

import java.util.Collection;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/**
 * Helper methods to lookup players in a server.
 *
 * <p>The word "tracking" means that an entity/chunk on the server is known to a player's client (within in view distance) and the (block) entity should notify tracking clients of changes.
 *
 * <p>These methods should only be called on the server thread and only be used on logical a server.
 *
 * @deprecated Use Quilt Networking's {@link org.quiltmc.qsl.networking.api.PlayerLookup} instead.
 */
@Deprecated
public final class PlayerLookup {
	/**
	 * Gets all the players on the minecraft server.
	 *
	 * <p>The returned collection is immutable.
	 *
	 * @param server the server
	 * @return all players on the server
	 */
	public static Collection<ServerPlayerEntity> all(MinecraftServer server) {
		return org.quiltmc.qsl.networking.api.PlayerLookup.all(server);
	}

	/**
	 * Gets all the players in a server world.
	 *
	 * <p>The returned collection is immutable.
	 *
	 * @param world the server world
	 * @return the players in the server world
	 */
	public static Collection<ServerPlayerEntity> world(ServerWorld world) {
		return org.quiltmc.qsl.networking.api.PlayerLookup.world(world);
	}

	/**
	 * Gets all players tracking a chunk in a server world.
	 *
	 * @param world the server world
	 * @param pos   the chunk in question
	 * @return the players tracking the chunk
	 */
	public static Collection<ServerPlayerEntity> tracking(ServerWorld world, ChunkPos pos) {
		return org.quiltmc.qsl.networking.api.PlayerLookup.tracking(world, pos);
	}

	/**
	 * Gets all players tracking an entity in a server world.
	 *
	 * <p>The returned collection is immutable.
	 *
	 * <p><b>Warning</b>: If the provided entity is a player, it is not
	 * guaranteed by the contract that said player is included in the
	 * resulting stream.
	 *
	 * @param entity the entity being tracked
	 * @return the players tracking the entity
	 * @throws IllegalArgumentException if the entity is not in a server world
	 */
	public static Collection<ServerPlayerEntity> tracking(Entity entity) {
		return org.quiltmc.qsl.networking.api.PlayerLookup.tracking(entity);
	}

	/**
	 * Gets all players tracking a block entity in a server world.
	 *
	 * @param blockEntity the block entity
	 * @return the players tracking the block position
	 * @throws IllegalArgumentException if the block entity is not in a server world
	 */
	public static Collection<ServerPlayerEntity> tracking(BlockEntity blockEntity) {
		return org.quiltmc.qsl.networking.api.PlayerLookup.tracking(blockEntity);
	}

	/**
	 * Gets all players tracking a block position in a server world.
	 *
	 * @param world the server world
	 * @param pos   the block position
	 * @return the players tracking the block position
	 */
	public static Collection<ServerPlayerEntity> tracking(ServerWorld world, BlockPos pos) {
		return org.quiltmc.qsl.networking.api.PlayerLookup.tracking(world, pos);
	}

	/**
	 * Gets all players around a position in a world.
	 *
	 * <p>The distance check is done in the three-dimensional space instead of in the horizontal plane.
	 *
	 * @param world  the world
	 * @param pos the position
	 * @param radius the maximum distance from the position in blocks
	 * @return the players around the position
	 */
	public static Collection<ServerPlayerEntity> around(ServerWorld world, Vec3d pos, double radius) {
		return org.quiltmc.qsl.networking.api.PlayerLookup.around(world, pos, radius);
	}

	/**
	 * Gets all players around a position in a world.
	 *
	 * <p>The distance check is done in the three-dimensional space instead of in the horizontal plane.
	 *
	 * @param world  the world
	 * @param pos    the position (can be a block pos)
	 * @param radius the maximum distance from the position in blocks
	 * @return the players around the position
	 */
	public static Collection<ServerPlayerEntity> around(ServerWorld world, Vec3i pos, double radius) {
		return org.quiltmc.qsl.networking.api.PlayerLookup.around(world, pos, radius);
	}

	private PlayerLookup() {
	}
}
