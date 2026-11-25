package dev.brian.coppershears;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.DispenserBlock;

public final class ModItems {
	public static final ShearsItem COPPER_SHEARS = register();

	private ModItems() {
	}

	private static ShearsItem register() {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Coppershears.id("copper_shears"));
		// Same properties as vanilla Items.SHEARS, with less durability and copper repair.
		Item.Properties properties = new Item.Properties()
			.setId(key)
			.durability(ShearsMatch.DURABILITY)
			.component(DataComponents.TOOL, ShearsItem.createToolProperties())
			.repairable(Items.COPPER_INGOT);
		return Registry.register(BuiltInRegistries.ITEM, key, new ShearsItem(properties));
	}

	public static void initialize() {
		ResourceKey<CreativeModeTab> tools = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
			Identifier.withDefaultNamespace("tools_and_utilities"));
		CreativeModeTabEvents.modifyOutputEvent(tools).register(output -> output.insertAfter(Items.SHEARS, COPPER_SHEARS));
		DispenserBlock.registerBehavior(COPPER_SHEARS, new ShearsDispenseItemBehavior());
	}
}
