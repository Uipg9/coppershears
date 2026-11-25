package dev.brian.coppershears.client;

import dev.brian.coppershears.Coppershears;
import dev.brian.coppershears.ModItems;
import com.terraformersmc.modmenu.ModMenu;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

/**
 * In-game test: boots the client, joins a world and uses the copper shears with real right-clicks
 * (sheep, pumpkin) plus server-side block breaking (leaves loot), then takes screenshots.
 */
public class CoppershearsClientGameTest implements FabricClientGameTest {
	private static final BlockPos BLOCK_POS = new BlockPos(3, 101, 5);

	@Override
	public void runTest(ClientGameTestContext context) {
		if (!FabricLoader.getInstance().isModLoaded(Coppershears.MOD_ID)) {
			throw new AssertionError(Coppershears.MOD_ID + " is not loaded");
		}

		context.waitForScreen(TitleScreen.class);
		context.waitTicks(60);

		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			context.waitTicks(100);

			// registration, identity widening, recipe
			expect(BuiltInRegistries.ITEM.containsKey(Coppershears.id("copper_shears")), "item not registered");
			expect(new ItemStack(ModItems.COPPER_SHEARS).is(Items.SHEARS), "copper shears should count as shears");
			expect(!new ItemStack(Items.SHEARS).is(ModItems.COPPER_SHEARS), "vanilla shears are not copper shears");
			expect(!new ItemStack(Items.STICK).is(Items.SHEARS), "stick is not shears");
			expect(new ItemStack(ModItems.COPPER_SHEARS).getMaxDamage() == 150, "durability should be 150");
			expect(new ItemStack(ModItems.COPPER_SHEARS).getDestroySpeed(Blocks.COBWEB.defaultBlockState()) > 1.0F,
				"copper shears should mine cobweb fast");
			boolean recipe = world.getServer().computeOnServer(server -> server.getRecipeManager()
				.byKey(ResourceKey.create(Registries.RECIPE, Coppershears.id("copper_shears"))).isPresent());
			expect(recipe, "recipe coppershears:copper_shears missing");
			pass("registration and recipe");

			world.getServer().runCommand("gamemode survival @p");
			world.getServer().runCommand("fill 0 100 0 8 100 8 stone");
			world.getServer().runCommand("fill 0 101 0 8 104 8 air");
			world.getServer().runCommand("tp @p 3.5 101 3.5 0 0");
			world.getServer().runCommand("kill @e[type=!minecraft:player]");
			context.waitTicks(10);

			// sheep: right-click drops wool and costs 1 durability
			world.getServer().runCommand("summon minecraft:sheep 3.5 101 5.5 {NoAI:1b}");
			give(world, "coppershears:copper_shears");
			context.getInput().lookAt(new BlockPos(3, 101, 5));
			context.waitTicks(5);
			context.takeScreenshot("copper_shears_held");
			rightClick(context);
			context.waitTicks(5);
			expectHeldDamage(world, 1, "after shearing the sheep");
			int wool = countDrops(world, s -> s.is(net.minecraft.tags.ItemTags.WOOL));
			expect(wool > 0, "shearing the sheep should drop wool");
			boolean sheared = world.getServer().computeOnServer(server -> server.overworld()
				.getEntitiesOfClass(net.minecraft.world.entity.animal.sheep.Sheep.class, new AABB(BLOCK_POS).inflate(4))
				.stream().allMatch(net.minecraft.world.entity.animal.sheep.Sheep::isSheared));
			expect(sheared, "sheep should be sheared");
			pass("sheep shearing");
			world.getServer().runCommand("kill @e[type=!minecraft:player]");
			context.waitTicks(5);

			// pumpkin: right-click carves it
			world.getServer().runCommand("setblock 3 101 5 minecraft:pumpkin");
			context.getInput().lookAt(BLOCK_POS);
			context.waitTicks(3);
			rightClick(context);
			boolean carved = world.getServer().computeOnServer(server ->
				server.overworld().getBlockState(BLOCK_POS).is(Blocks.CARVED_PUMPKIN));
			expect(carved, "pumpkin should be carved by copper shears");
			pass("pumpkin carving");
			world.getServer().runCommand("setblock 3 101 5 air");
			world.getServer().runCommand("kill @e[type=minecraft:item]");

			// leaves loot: the can_shear predicate must accept copper shears
			world.getServer().runCommand("setblock 3 101 5 minecraft:oak_leaves[persistent=true]");
			world.getServer().runOnServer(server -> {
				ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
				player.gameMode.destroyBlock(BLOCK_POS);
			});
			context.waitTicks(5);
			expect(countDrops(world, s -> s.is(Items.OAK_LEAVES)) > 0, "breaking leaves with copper shears should drop leaves");
			pass("leaves shear loot");
			world.getServer().runCommand("kill @e[type=minecraft:item]");

			// hotbar screenshot next to vanilla shears
			world.getServer().runCommand("item replace entity @p hotbar.1 with minecraft:shears");
			context.getInput().lookAt(0f, 20f);
			context.waitTicks(10);
			context.takeScreenshot("hotbar_shears");

			expect(context.computeOnClient(mc -> ModMenu.MODS.containsKey(Coppershears.MOD_ID)), "not in Mod Menu");
			pass("mod menu");
		}
	}

	private static void rightClick(ClientGameTestContext context) {
		context.getInput().pressKey(options -> options.keyUse);
		context.waitTicks(5);
	}

	private static void give(TestSingleplayerContext world, String stack) {
		world.getServer().runCommand("item replace entity @p weapon.mainhand with " + stack);
	}

	private static int countDrops(TestSingleplayerContext world, java.util.function.Predicate<ItemStack> match) {
		return world.getServer().computeOnServer(server -> server.overworld()
			.getEntitiesOfClass(ItemEntity.class, new AABB(BLOCK_POS).inflate(6))
			.stream().filter(e -> match.test(e.getItem())).mapToInt(e -> e.getItem().getCount()).sum());
	}

	private static void expectHeldDamage(TestSingleplayerContext world, int damage, String when) {
		String actual = world.getServer().computeOnServer(server -> {
			ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
			ItemStack held = player.getMainHandItem();
			return BuiltInRegistries.ITEM.getKey(held.getItem()) + "#" + held.getDamageValue();
		});
		String expected = "coppershears:copper_shears#" + damage;
		expect(actual.equals(expected), when + ": expected " + expected + " but held " + actual);
	}

	private static void pass(String step) {
		System.out.println("[coppershears-gametest] PASS " + step);
	}

	private static void expect(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
