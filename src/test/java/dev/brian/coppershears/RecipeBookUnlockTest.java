package dev.brian.coppershears;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Every recipe must be unlocked by an advancement, or it never shows up in the recipe book. */
class RecipeBookUnlockTest {
	private static final String NS = "coppershears";
	private static final Path DATA = Path.of("src/main/resources/data", NS);

	@Test
	void everyRecipeIsUnlockedForTheRecipeBook() throws IOException {
		List<String> recipes;
		try (Stream<Path> s = Files.list(DATA.resolve("recipe"))) {
			recipes = s.map(p -> NS + ":" + p.getFileName().toString().replace(".json", "")).toList();
		}
		assertFalse(recipes.isEmpty(), "no recipes found");

		Set<String> unlocked = new HashSet<>();
		Path adv = DATA.resolve("advancement/recipes");
		assertTrue(Files.isDirectory(adv), "missing " + adv);
		try (Stream<Path> s = Files.walk(adv)) {
			for (Path p : s.filter(f -> f.toString().endsWith(".json")).toList()) {
				JsonObject json = JsonParser.parseString(Files.readString(p)).getAsJsonObject();
				assertTrue(json.toString().contains("minecraft:inventory_changed"), p + " needs an item trigger");
				assertFalse(json.toString().contains("\"recipe\":"), p + ": 26.3 recipe_unlocked uses \"recipes\"");
				json.getAsJsonObject("rewards").getAsJsonArray("recipes").forEach(r -> unlocked.add(r.getAsString()));
			}
		}
		for (String recipe : recipes) {
			assertTrue(unlocked.contains(recipe), recipe + " is never unlocked in the recipe book");
		}
	}
}
