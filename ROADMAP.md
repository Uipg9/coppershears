# Copper Shears roadmap

## Done
- 1.0.1: recipe unlock advancement so the recipe shows in the recipe book (unit test guards every recipe).
- Item `coppershears:copper_shears` (ShearsItem, vanilla shears Tool component, durability 150, copper repair).
- Shaped recipe (2 copper ingots diagonal), creative tab after vanilla shears, lang, texture + item model.
- Identity compat: `ItemStackMixin` makes `stack.is(Items.SHEARS)` true for copper shears, covering every
  vanilla identity check (Sheep, MushroomCow, SnowGolem, Bogged, BeehiveBlock, PumpkinBlock, TripWireBlock,
  Entity leash/equipment shearing, CopperGolem, SulfurCube, LeashFenceKnotEntity).
- Loot: `minecraft:tool/can_shear` predicate overridden to accept copper shears (used by ~35 loot tables).
- Dispenser: `ShearsDispenseItemBehavior` registered for copper shears.
- Tags: `c:tools/shear`, `minecraft:enchantable/durability`, `minecraft:enchantable/mining`.

## Verified in the client gametest
- Registration, recipe present, durability 150, `is(Items.SHEARS)` widening (and not the reverse).
- Fast cobweb mining speed (Tool component).
- Right-click sheep: wool drops, sheep sheared, shears take 1 damage.
- Right-click pumpkin: carved pumpkin.
- Breaking oak leaves: leaves drop (can_shear loot).
- Texture renders in hand and hotbar (screenshots).

## Untested (expected to work through the same code paths)
- Mooshroom, snow golem, bogged, copper golem, sulfur cube shearing; beehive honeycomb; tripwire disarm;
  leash cutting; dispenser shearing; cobweb/grass/vine/dead bush drops; enchanting (Efficiency/Unbreaking).

## Not done / notes
- The `can_shear` override replaces the vanilla predicate file; another mod overriding the same file wins by
  load order. A loot-table modify event would be more robust.
- Advancements that match `minecraft:shears` by item (none critical) are not changed.
