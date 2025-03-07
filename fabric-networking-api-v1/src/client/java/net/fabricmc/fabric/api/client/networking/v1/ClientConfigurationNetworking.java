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

import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.impl.networking.QuiltUtils;

/**
 * Offers access to configuration stage client-side networking functionalities.
 *
 * <p>Client-side networking functionalities include receiving clientbound packets,
 * sending serverbound packets, and events related to client-side network handlers.
 *
 * <p>This class should be only used on the physical client and for the logical client.
 *
 * <p>See {@link net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking} for information on how to use the packet
 * object-based API.
 *
 * @see ServerConfigurationNetworking
 * @deprecated Use Quilt Networking's {@link org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking} instead.
 */
@Deprecated
public final class ClientConfigurationNetworking {
	/**
	 * Registers a handler to a channel.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The handler runs on the network thread. After reading the buffer there, access to game state
	 * must be performed in the render thread by calling {@link ThreadExecutor#execute(Runnable)}.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterGlobalReceiver(Identifier)} to unregister the existing handler.
	 *
	 * <p>For new code, {@link #registerGlobalReceiver(PacketType, ConfigurationPacketHandler)}
	 * is preferred, as it is designed in a way that prevents thread safety issues.
	 *
	 * @param channelName the id of the channel
	 * @param channelHandler the handler
	 * @return false if a handler is already registered to the channel
	 * @see ClientConfigurationNetworking#unregisterGlobalReceiver(Identifier)
	 * @see ClientConfigurationNetworking#registerReceiver(Identifier, ConfigurationChannelHandler)
	 */
	public static boolean registerGlobalReceiver(Identifier channelName, ConfigurationChannelHandler channelHandler) {
		return org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.registerGlobalReceiver(channelName, channelHandler);
	}

	/**
	 * Registers a handler for a packet type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterGlobalReceiver(PacketType)} to unregister the existing handler.
	 *
	 * @param type the packet type
	 * @param handler the handler
	 * @return false if a handler is already registered to the channel
	 * @see ClientConfigurationNetworking#unregisterGlobalReceiver(PacketType)
	 * @see ClientConfigurationNetworking#registerReceiver(PacketType, ConfigurationPacketHandler)
	 */
	public static <T extends FabricPacket> boolean registerGlobalReceiver(PacketType<T> type, ConfigurationPacketHandler<T> handler) {
		return org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.registerGlobalReceiver(type.getId(), wrapTyped(type, handler));
	}

	/**
	 * Removes the handler of a channel.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.
	 *
	 * @param channelName the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 * @see ClientConfigurationNetworking#registerGlobalReceiver(Identifier, ConfigurationChannelHandler)
	 * @see ClientConfigurationNetworking#unregisterReceiver(Identifier)
	 */
	@Nullable
	public static ClientConfigurationNetworking.ConfigurationChannelHandler unregisterGlobalReceiver(Identifier channelName) {
		var old = org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.unregisterGlobalReceiver(channelName);

		if (old instanceof ClientConfigurationNetworking.ConfigurationChannelHandler fabric) {
			return fabric;
		} else if (old != null) {
			return (client, handler, buf, responseSender) -> {
				if (old instanceof org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.ChannelReceiver r) {
					r.receive(client, handler, buf, QuiltUtils.toQuiltSender(responseSender));
				} else {
					throw new UnsupportedOperationException("Receiver does not accept byte bufs, cannot bridge to Quilt");
				}
			};
		} else {
			return null;
		}
	}

	/**
	 * Removes the handler for a packet type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param type the packet type
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerGlobalReceiver(PacketType, ConfigurationPacketHandler)}
	 * @see ClientConfigurationNetworking#registerGlobalReceiver(PacketType, ConfigurationPacketHandler)
	 * @see ClientConfigurationNetworking#unregisterReceiver(PacketType)
	 */
	@Nullable
	public static <T extends FabricPacket> ClientConfigurationNetworking.ConfigurationPacketHandler<T> unregisterGlobalReceiver(PacketType<T> type) {
		ClientConfigurationNetworking.ConfigurationChannelHandler handler = (ClientConfigurationNetworking.ConfigurationChannelHandler) org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.unregisterGlobalReceiver(type.getId());
		return handler instanceof ConfigurationPacketWrapper<?> proxy ? (ClientConfigurationNetworking.ConfigurationPacketHandler<T>) proxy.actualHandler() : null;
	}

	/**
	 * Gets all channel names which global receivers are registered for.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * @return all channel names which global receivers are registered for.
	 */
	public static Set<Identifier> getGlobalReceivers() {
		return org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.getGlobalReceivers();
	}

	/**
	 * Registers a handler to a channel.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(Identifier)} to unregister the existing handler.
	 *
	 * <p>For example, if you only register a receiver using this method when a {@linkplain ClientLoginNetworking#registerGlobalReceiver(Identifier, ClientLoginNetworking.LoginQueryRequestHandler)}
	 * login query has been received, you should use {@link ClientPlayConnectionEvents#INIT} to register the channel handler.
	 *
	 * <p>For new code, {@link #registerReceiver(PacketType, ConfigurationPacketHandler)}
	 * is preferred, as it is designed in a way that prevents thread safety issues.
	 *
	 * @param channelName the id of the channel
	 * @return false if a handler is already registered to the channel
	 * @throws IllegalStateException if the client is not connected to a server
	 * @see ClientPlayConnectionEvents#INIT
	 */
	public static boolean registerReceiver(Identifier channelName, ConfigurationChannelHandler channelHandler) {
		return org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.registerReceiver(channelName, channelHandler);
	}

	/**
	 * Registers a handler for a packet type.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(PacketType)} to unregister the existing handler.
	 *
	 * <p>For example, if you only register a receiver using this method when a {@linkplain ClientLoginNetworking#registerGlobalReceiver(Identifier, ClientLoginNetworking.LoginQueryRequestHandler)}
	 * login query has been received, you should use {@link ClientPlayConnectionEvents#INIT} to register the channel handler.
	 *
	 * @param type the packet type
	 * @param handler the handler
	 * @return {@code false} if a handler is already registered for the type
	 * @throws IllegalStateException if the client is not connected to a server
	 * @see ClientPlayConnectionEvents#INIT
	 */
	public static <T extends FabricPacket> boolean registerReceiver(PacketType<T> type, ConfigurationPacketHandler<T> handler) {
		return org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.registerReceiver(type.getId(), wrapTyped(type, handler));
	}

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channelName} is guaranteed not to have a handler after this call.
	 *
	 * @param channelName the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	@Nullable
	public static ClientConfigurationNetworking.ConfigurationChannelHandler unregisterReceiver(Identifier channelName) throws IllegalStateException {
		var old = org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.unregisterReceiver(channelName);

		if (old instanceof ClientConfigurationNetworking.ConfigurationChannelHandler fabric) {
			return fabric;
		} else if (old != null) {
			return (client, handler, buf, responseSender) -> {
				if (old instanceof org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.ChannelReceiver r) {
					r.receive(client, handler, buf, QuiltUtils.toQuiltSender(responseSender));
				} else {
					throw new UnsupportedOperationException("Receiver does not accept byte bufs, cannot bridge to Quilt");
				}
			};
		} else {
			return null;
		}
	}

	/**
	 * Removes the handler for a packet type.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param type the packet type
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerReceiver(PacketType, ConfigurationPacketHandler)}
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	@Nullable
	public static <T extends FabricPacket> ClientConfigurationNetworking.ConfigurationPacketHandler<T> unregisterReceiver(PacketType<T> type) {
		final var receiver = org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.unregisterReceiver(type.getId());

		if (receiver != null) {
			return unwrapTyped(receiver);
		}

		throw new IllegalStateException("Cannot unregister receiver while not in game!");
	}

	/**
	 * Gets all the channel names that the client can receive packets on.
	 *
	 * @return All the channel names that the client can receive packets on
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static Set<Identifier> getReceived() throws IllegalStateException {
		return org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.getReceived();
	}

	/**
	 * Gets all channel names that the connected server declared the ability to receive a packets on.
	 *
	 * @return All the channel names the connected server declared the ability to receive a packets on
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static Set<Identifier> getSendable() throws IllegalStateException {
		return org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.getSendable();
	}

	/**
	 * Checks if the connected server declared the ability to receive a packet on a specified channel name.
	 *
	 * @param channelName the channel name
	 * @return {@code true} if the connected server has declared the ability to receive a packet on the specified channel.
	 * False if the client is not in game.
	 */
	public static boolean canSend(Identifier channelName) throws IllegalArgumentException {
		return org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.canSend(channelName);
	}

	/**
	 * Checks if the connected server declared the ability to receive a packet on a specified channel name.
	 * This returns {@code false} if the client is not in game.
	 *
	 * @param type the packet type
	 * @return {@code true} if the connected server has declared the ability to receive a packet on the specified channel
	 */
	public static boolean canSend(PacketType<?> type) {
		return canSend(type.getId());
	}

	/**
	 * Creates a packet which may be sent to the connected server.
	 *
	 * @param channelName the channel name
	 * @param buf the packet byte buf which represents the payload of the packet
	 * @return a new packet
	 */
	public static Packet<ServerCommonPacketListener> createC2SPacket(Identifier channelName, PacketByteBuf buf) {
		return org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.createC2SPacket(channelName, buf);
	}

	/**
	 * Gets the packet sender which sends packets to the connected server.
	 *
	 * @return the client's packet sender
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static PacketSender getSender() throws IllegalStateException {
		return QuiltUtils.toFabricSender(org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.getSender());
	}

	/**
	 * Sends a packet to the connected server.
	 *
	 * @param channelName the channel of the packet
	 * @param buf the payload of the packet
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static void send(Identifier channelName, PacketByteBuf buf) throws IllegalStateException {
		org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.send(channelName, buf);
	}

	/**
	 * Sends a packet to the connected server.
	 *
	 * @param packet the packet
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static <T extends FabricPacket> void send(T packet) {
		Objects.requireNonNull(packet, "Packet cannot be null");
		Objects.requireNonNull(packet.getType(), "Packet#getType cannot return null");

		ClientConfigurationNetworking.getSender().sendPacket(packet);
	}

	private ClientConfigurationNetworking() {
	}

	private record ConfigurationPacketWrapper<T extends FabricPacket>(PacketType<T> type, ClientConfigurationNetworking.ConfigurationPacketHandler<T> actualHandler) implements ClientConfigurationNetworking.ConfigurationChannelHandler {
		@Override
		public void receive(MinecraftClient client, ClientConfigurationNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
			T packet = type.read(buf);

			if (client.isOnThread()) {
				// Do not submit to the render thread if we're already running there.
				// Normally, packets are handled on the network IO thread - though it is
				// not guaranteed (for example, with 1.19.4 S2C packet bundling)
				// Since we're handling it right now, connection check is redundant.
				actualHandler.receive(packet, responseSender);
			} else {
				client.execute(() -> {
					if (((org.quiltmc.qsl.networking.mixin.accessor.AbstractClientNetworkHandlerAccessor) handler).getConnection().isOpen()) {
						actualHandler.receive(packet, responseSender);
					}
				});
			}
		}
	}

	private static <T extends FabricPacket> ClientConfigurationNetworking.ConfigurationChannelHandler wrapTyped(PacketType<T> type, ClientConfigurationNetworking.ConfigurationPacketHandler<T> actualHandler) {
		return new ClientConfigurationNetworking.ConfigurationPacketWrapper<>(type, actualHandler);
	}

	@Nullable
	@SuppressWarnings({"unchecked"})
	private static <T extends FabricPacket> ClientConfigurationNetworking.ConfigurationPacketHandler<T> unwrapTyped(@Nullable org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.CustomChannelReceiver<?> receiver) {
		if (receiver == null) return null;
		if (receiver instanceof ClientConfigurationNetworking.ConfigurationPacketWrapper<?> wrapper) return (ClientConfigurationNetworking.ConfigurationPacketHandler<T>) wrapper.actualHandler();
		return null;
	}

	@Deprecated
	@FunctionalInterface
	public interface ConfigurationChannelHandler extends org.quiltmc.qsl.networking.api.client.ClientConfigurationNetworking.ChannelReceiver {
		@Override
		default void receive(MinecraftClient client, ClientConfigurationNetworkHandler handler, PacketByteBuf buf, org.quiltmc.qsl.networking.api.PacketSender responseSender) {
			this.receive(client, handler, buf, QuiltUtils.toFabricSender(responseSender));
		}

		/**
		 * Handles an incoming packet.
		 *
		 * <p>This method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
		 * Modification to the game should be {@linkplain net.minecraft.util.thread.ThreadExecutor#submit(Runnable) scheduled} using the provided Minecraft client instance.
		 *
		 * <p>An example usage of this is to display an overlay message:
		 * <pre>{@code
		 * ClientConfigurationNetworking.registerReceiver(new Identifier("mymod", "overlay"), (client, handler, buf, responseSender) -> {
		 * 	String message = buf.readString(32767);
		 *
		 * 	// All operations on the server or world must be executed on the server thread
		 * 	client.execute(() -> {
		 * 		client.inGameHud.setOverlayMessage(message, true);
		 * 	});
		 * });
		 * }</pre>
		 *  @param client the client
		 * @param handler the network handler that received this packet
		 * @param buf the payload of the packet
		 * @param responseSender the packet sender
		 */
		void receive(MinecraftClient client, ClientConfigurationNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
	}

	/**
	 * A thread-safe packet handler utilizing {@link FabricPacket}.
	 * @param <T> the type of the packet
	 */
	@FunctionalInterface
	public interface ConfigurationPacketHandler<T extends FabricPacket> {
		/**
		 * Handles the incoming packet. This is called on the render thread, and can safely
		 * call client methods.
		 *
		 * <p>An example usage of this is to display an overlay message:
		 * <pre>{@code
		 * // See FabricPacket for creating the packet
		 * ClientConfigurationNetworking.registerReceiver(OVERLAY_PACKET_TYPE, (player, packet, responseSender) -> {
		 * 	MinecraftClient.getInstance().inGameHud.setOverlayMessage(packet.message(), true);
		 * });
		 * }</pre>
		 *
		 *
		 * @param packet the packet
		 * @param responseSender the packet sender
		 * @see FabricPacket
		 */
		void receive(T packet, PacketSender responseSender);
	}
}
