# Copper Shears: working notes for Claude

- Minecraft 26.3 Fabric mod. Java 25. Unobfuscated: use Mojang names, never add mappings/yarn.
- TDD: write a failing JUnit test in `src/test` first, then implement. In-game checks go in
  `src/client/.../*ClientGameTest.java`.
- Shell is Windows PowerShell: use `.\gradlew.bat`. Filter Gradle output to errors only.
- Verify with `.\scripts\verify.ps1` (build + unit tests + client gametest). Read the gametest
  screenshots in `build\run\clientGameTest\screenshots\` to check what actually rendered.
- Never kill processes to stop the client: `runClientGameTest` exits by itself.
- Mod Menu is dev-only (`localRuntime`); do not make it a hard dependency.
- Shears compat: vanilla checks `stack.is(Items.SHEARS)` by identity; `ItemStackMixin` overrides
  `is(Object)` (the erased `TypedInstance.is(T)` default) so copper shears match. Loot uses the
  `minecraft:tool/can_shear` predicate, overridden in `data/minecraft/predicate/tool/can_shear.json`.
