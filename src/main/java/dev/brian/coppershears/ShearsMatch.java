package dev.brian.coppershears;

/** Pure rule behind the {@code ItemStack.is(Item)} widening: copper shears count as vanilla shears. */
public final class ShearsMatch {
	/** Uses before breaking. Vanilla iron shears have 238. */
	public static final int DURABILITY = 150;

	private ShearsMatch() {
	}

	public static boolean matches(Object actual, Object wanted, Object vanillaShears, Object copperShears) {
		if (actual == wanted) {
			return true;
		}
		return copperShears != null && wanted == vanillaShears && actual == copperShears;
	}
}
