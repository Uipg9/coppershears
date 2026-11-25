package dev.brian.coppershears.mixin;

import dev.brian.coppershears.CopperShearsHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Vanilla checks shears by identity ({@code stack.is(Items.SHEARS)}) in Sheep, MushroomCow, SnowGolem,
 * Bogged, BeehiveBlock, PumpkinBlock, TripWireBlock, Entity (leash), CopperGolem, SulfurCube, etc.
 * That call resolves to the {@code TypedInstance.is(T)} default method (erased to {@code is(Object)}),
 * which cannot be injected into, so this mixin gives ItemStack its own override of it.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	public boolean is(Object item) {
		return CopperShearsHook.matches((ItemStack) (Object) this, item);
	}
}
