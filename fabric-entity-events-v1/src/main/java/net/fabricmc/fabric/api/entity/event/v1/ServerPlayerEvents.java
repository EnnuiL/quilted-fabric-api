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

package net.fabricmc.fabric.api.entity.event.v1;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.impl.base.event.QuiltCompatEvent;
import net.fabricmc.fabric.mixin.entity.event.quilt.LivingEntityAccessor;

/**
 * @deprecated Use Quilt Entity Events' {@link org.quiltmc.qsl.entity.event.api.ServerPlayerEntityCopyCallback} and {@link org.quiltmc.qsl.entity.event.api.EntityReviveEvents} instead.
 */
@Deprecated
public final class ServerPlayerEvents {
	/**
	 * An event that is called when the data from an old player is copied to a new player.
	 *
	 * <p>This event is typically called before a player is completely respawned.
	 * Mods may use this event to copy old player data to a new player.
	 */
	public static final Event<ServerPlayerEvents.CopyFrom> COPY_FROM = QuiltCompatEvent.fromQuilt(
			org.quiltmc.qsl.entity.event.api.ServerPlayerEntityCopyCallback.EVENT,
			playerCopyCallback -> (copy, original, wasDeath) -> playerCopyCallback.copyFromPlayer(original, copy, !wasDeath),
			invokerGetter -> (oldPlayer, newPlayer, alive) -> invokerGetter.get().onPlayerCopy(newPlayer, oldPlayer, !alive)
	);

	/**
	 * An event that is called after a player has been respawned.
	 *
	 * <p>Mods may use this event for reference clean up on the old player.
	 */
	public static final Event<ServerPlayerEvents.AfterRespawn> AFTER_RESPAWN = EventFactory.createArrayBacked(ServerPlayerEvents.AfterRespawn.class, callbacks -> (oldPlayer, newPlayer, alive) -> {
		for (AfterRespawn callback : callbacks) {
			callback.afterRespawn(oldPlayer, newPlayer, alive);
		}
	});

	/**
	 * An event that is called when a player takes fatal damage.
	 *
	 * @deprecated Use the more general {@link ServerLivingEntityEvents#ALLOW_DEATH} event instead and check for {@code instanceof ServerPlayerEntity}.
	 */
	@Deprecated
	public static final Event<AllowDeath> ALLOW_DEATH = QuiltCompatEvent.fromQuilt(
			org.quiltmc.qsl.entity.event.api.EntityReviveEvents.BEFORE_TOTEM,
			beforeTotemCallback -> (entity, damageSource) -> {
				if (entity instanceof ServerPlayerEntity player) {
					return !beforeTotemCallback.allowDeath(player, damageSource, ((LivingEntityAccessor) entity).getLastDamageTaken());
				}

				return false;
			},
			invokerGetter -> (player, damageSource, damageAmount) -> invokerGetter.get().tryReviveBeforeTotem(player, damageSource)
	);

	@FunctionalInterface
	public interface CopyFrom {
		/**
		 * Called when player data is copied to a new player.
		 *
		 * @param oldPlayer the old player
		 * @param newPlayer the new player
		 * @param alive whether the old player is still alive
		 */
		void copyFromPlayer(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive);
	}

	@FunctionalInterface
	public interface AfterRespawn {
		/**
		 * Called after player a has been respawned.
		 *
		 * @param oldPlayer the old player
		 * @param newPlayer the new player
		 * @param alive whether the old player is still alive
		 */
		void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive);
	}

	/**
	 * @deprecated Use the more general {@link ServerLivingEntityEvents#ALLOW_DEATH} event instead and check for {@code instanceof ServerPlayerEntity}.
	 */
	@Deprecated
	@FunctionalInterface
	public interface AllowDeath {
		/**
		 * Called when a player takes fatal damage (before totems of undying can take effect).
		 *
		 * @param player the player
		 * @param damageSource the fatal damage damageSource
		 * @param damageAmount the damageAmount of damage that has killed the player
		 * @return true if the death should go ahead, false otherwise.
		 */
		boolean allowDeath(ServerPlayerEntity player, DamageSource damageSource, float damageAmount);
	}

	private ServerPlayerEvents() {
	}
}
