package dev.brian.coppershears;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

public class Coppershears implements ModInitializer {
	public static final String MOD_ID = "coppershears";

	@Override
	public void onInitialize() {
		ModItems.initialize();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	/** Translation key following the vanilla convention: {@code category.modid.path}. */
	public static String langKey(String category, String path) {
		return category + "." + MOD_ID + "." + path;
	}
}
