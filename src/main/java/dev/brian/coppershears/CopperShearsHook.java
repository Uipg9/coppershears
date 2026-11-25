package dev.brian.coppershears;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Called from {@code ItemStackMixin}; kept outside the mixin package so it can reference mod classes. */
public final class CopperShearsHook {
	private CopperShearsHook() {
	}

	public static boolean matches(ItemStack stack, Object wanted) {
		return ShearsMatch.matches(stack.typeHolder().value(), wanted, Items.SHEARS, ModItems.COPPER_SHEARS);
	}
}
