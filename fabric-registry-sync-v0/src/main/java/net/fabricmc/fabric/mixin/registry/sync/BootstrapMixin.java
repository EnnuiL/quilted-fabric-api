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

package net.fabricmc.fabric.mixin.registry.sync;

import org.quiltmc.loader.api.entrypoint.EntrypointUtil;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.Bootstrap;
import net.minecraft.registry.Registries;

@Mixin(Bootstrap.class)
public class BootstrapMixin {
	@Shadow
	private static void setOutputStreams() { }

	@Redirect(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registries;bootstrap()V"))
	private static void initialize() {
		Registries.init();

		// Quilt code for initializing its main entrypoint
		setOutputStreams(); // We need to make this a bit early in case a mod uses System.out to print stuff.

		EntrypointUtil.invoke(ModInitializer.ENTRYPOINT_KEY, ModInitializer.class, ModInitializer::onInitialize);
	}
}
