package dev.brian.coppershears;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ShearsMatchTest {
	private static final Object SHEARS = new Object();
	private static final Object COPPER = new Object();
	private static final Object STICK = new Object();

	@Test
	void sameItemAlwaysMatches() {
		assertTrue(ShearsMatch.matches(STICK, STICK, SHEARS, COPPER));
		assertTrue(ShearsMatch.matches(SHEARS, SHEARS, SHEARS, COPPER));
		assertTrue(ShearsMatch.matches(COPPER, COPPER, SHEARS, COPPER));
	}

	@Test
	void copperShearsCountAsVanillaShears() {
		assertTrue(ShearsMatch.matches(COPPER, SHEARS, SHEARS, COPPER));
	}

	@Test
	void vanillaShearsAreNotCopperShears() {
		assertFalse(ShearsMatch.matches(SHEARS, COPPER, SHEARS, COPPER));
	}

	@Test
	void otherItemsDoNotMatch() {
		assertFalse(ShearsMatch.matches(STICK, SHEARS, SHEARS, COPPER));
		assertFalse(ShearsMatch.matches(COPPER, STICK, SHEARS, COPPER));
	}

	@Test
	void unregisteredCopperShearsNeverWidenTheMatch() {
		assertFalse(ShearsMatch.matches(STICK, SHEARS, SHEARS, null));
	}

	@Test
	void durabilityIsBelowIronShears() {
		assertTrue(ShearsMatch.DURABILITY == 150);
	}
}
