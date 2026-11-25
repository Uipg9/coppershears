package dev.brian.coppershears;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CoppershearsTest {
	@Test
	void idUsesModNamespace() {
		var id = Coppershears.id("example");
		assertEquals(Coppershears.MOD_ID, id.getNamespace());
		assertEquals("example", id.getPath());
	}

	@Test
	void langKeyFollowsMinecraftConvention() {
		assertEquals("item.coppershears.example", Coppershears.langKey("item", "example"));
	}
}
