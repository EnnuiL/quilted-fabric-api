/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 * Copyright 2024 The Quilt Project
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

package net.fabricmc.fabric.api.client.networking.v1;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.base.event.QuiltCompatEvent;
import net.fabricmc.fabric.impl.networking.QuiltUtils;

/**
 * Offers access to events related to the indication of a connected server's ability to receive packets in certain channels.
 * @deprecated Use Quilt Networking's {@link org.quiltmc.qsl.networking.api.client.C2SConfigurationChannelEvents} instead.
 */
@Deprecated
public final class C2SConfigurationChannelEvents {
	/**
	 * An event for the client configuration network handler receiving an update indicating the connected server's ability to receive packets in certain channels.
	 * This event may be invoked at any time after login and up to disconnection.
	 */
	public static final Event<Register> REGISTER = QuiltCompatEvent.fromQuilt(org.quiltmc.qsl.networking.api.client.C2SConfigurationChannelEvents.REGISTER,
			register -> (handler, sender, client, channels) -> register.onChannelRegister(handler, QuiltUtils.toFabricSender(sender), client, channels),
			invokerGetter -> (handler, sender, client, channels) -> invokerGetter.get().onChannelRegister(handler, QuiltUtils.toQuiltSender(sender), client, channels)
	);

	/**
	 * An event for the client configuration network handler receiving an update indicating the connected server's lack of ability to receive packets in certain channels.
	 * This event may be invoked at any time after login and up to disconnection.
	 */
	public static final Event<Unregister> UNREGISTER = QuiltCompatEvent.fromQuilt(org.quiltmc.qsl.networking.api.client.C2SConfigurationChannelEvents.UNREGISTER,
			register -> (handler, sender, client, channels) -> register.onChannelUnregister(handler, QuiltUtils.toFabricSender(sender), client, channels),
			invokerGetter -> (handler, sender, client, channels) -> invokerGetter.get().onChannelUnregister(handler, QuiltUtils.toQuiltSender(sender), client, channels)
	);

	private C2SConfigurationChannelEvents() {
	}

	/**
	 * @see C2SConfigurationChannelEvents#REGISTER
	 */
	@FunctionalInterface
	public interface Register {
		void onChannelRegister(ClientConfigurationNetworkHandler handler, PacketSender sender, MinecraftClient client, List<Identifier> channels);
	}

	/**
	 * @see C2SConfigurationChannelEvents#UNREGISTER
	 */
	@FunctionalInterface
	public interface Unregister {
		void onChannelUnregister(ClientConfigurationNetworkHandler handler, PacketSender sender, MinecraftClient client, List<Identifier> channels);
	}
}
