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

package net.fabricmc.fabric.test.screenhandler;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.test.screenhandler.block.BoxBlock;
import net.fabricmc.fabric.test.screenhandler.block.BoxBlockEntity;
import net.fabricmc.fabric.test.screenhandler.item.BagItem;
import net.fabricmc.fabric.test.screenhandler.item.PositionedBagItem;
import net.fabricmc.fabric.test.screenhandler.screen.BagScreenHandler;
import net.fabricmc.fabric.test.screenhandler.screen.BoxScreenHandler;
import net.fabricmc.fabric.test.screenhandler.screen.PositionedBagScreenHandler;

public class ScreenHandlerTest implements ModInitializer {
	public static final String ID = "fabric-screen-handler-api-v1-testmod";

	public static final Item BAG = new BagItem(new Item.Settings().maxCount(1));
	public static final Item POSITIONED_BAG = new PositionedBagItem(new Item.Settings().maxCount(1));
	public static final Block BOX = new BoxBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD));
	public static final Item BOX_ITEM = new BlockItem(BOX, new Item.Settings());
	public static final BlockEntityType<BoxBlockEntity> BOX_ENTITY = FabricBlockEntityTypeBuilder.create(BoxBlockEntity::new, BOX).build();
	public static final ScreenHandlerType<BagScreenHandler> BAG_SCREEN_HANDLER = new ScreenHandlerType<>(BagScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
	public static final ScreenHandlerType<PositionedBagScreenHandler> POSITIONED_BAG_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(PositionedBagScreenHandler::new);
	public static final ScreenHandlerType<BoxScreenHandler> BOX_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(BoxScreenHandler::new);

	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, id("bag"), BAG);
		Registry.register(Registries.ITEM, id("positioned_bag"), POSITIONED_BAG);
		Registry.register(Registries.BLOCK, id("box"), BOX);
		Registry.register(Registries.ITEM, id("box"), BOX_ITEM);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id("box"), BOX_ENTITY);
		Registry.register(Registries.SCREEN_HANDLER, id("bag"), BAG_SCREEN_HANDLER);
		Registry.register(Registries.SCREEN_HANDLER, id("positioned_bag"), POSITIONED_BAG_SCREEN_HANDLER);
		Registry.register(Registries.SCREEN_HANDLER, id("box"), BOX_SCREEN_HANDLER);
	}
}
