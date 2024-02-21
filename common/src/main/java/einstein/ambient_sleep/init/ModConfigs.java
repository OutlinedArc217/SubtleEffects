package einstein.ambient_sleep.init;

import einstein.ambient_sleep.AmbientSleep;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ModConfigs {

    private static final Pair<ModConfigs, ForgeConfigSpec> SPEC_PAIR = new ForgeConfigSpec.Builder().configure(ModConfigs::new);
    public static final ModConfigs INSTANCE = SPEC_PAIR.getLeft();
    public static final ForgeConfigSpec SPEC = SPEC_PAIR.getRight();
    private static final String TO_DISABLE = "0 to disable";

    // Flaming Blocks
    public final ForgeConfigSpec.BooleanValue removeVanillaCampfireSparks;
    public final ForgeConfigSpec.BooleanValue candleSparks;
    public final ForgeConfigSpec.BooleanValue furnaceSparks;
    public final ForgeConfigSpec.BooleanValue fireSparks;
    public final ForgeConfigSpec.BooleanValue campfireSparks;
    public final ForgeConfigSpec.BooleanValue torchSparks;

    // Blocks
    public final ForgeConfigSpec.BooleanValue redstoneBlockDust;
    public final ForgeConfigSpec.EnumValue<GlowstoneDustSpawnType> glowstoneBlockDust;
    public final ForgeConfigSpec.BooleanValue beehivesHaveSleepingZs;

    // Entity Snoring
    public final ForgeConfigSpec.DoubleValue playerSnoreChance;
    public final ForgeConfigSpec.DoubleValue villagerSnoreChance;
    public final ForgeConfigSpec.BooleanValue displaySleepingZsOnlyWhenSnoring;
    public final ForgeConfigSpec.BooleanValue foxesHaveSleepingZs;
    public final ForgeConfigSpec.BooleanValue adjustNametagRenderingWhenSleeping;

    // Entity Dust Clouds
    public final ForgeConfigSpec.BooleanValue fallDamageDustClouds;
    public final ForgeConfigSpec.BooleanValue sprintingDustClouds;
    public final ForgeConfigSpec.BooleanValue mobSprintingDustClouds;

    // Entities
    public final ForgeConfigSpec.BooleanValue chickenHitFeathers;
    public final ForgeConfigSpec.BooleanValue parrotHitFeathers;
    public final ForgeConfigSpec.BooleanValue enderPearlTrail;
    public final ForgeConfigSpec.DoubleValue snowballTrailDensity;
    public final ForgeConfigSpec.DoubleValue allayMagicDensity;
    public final ForgeConfigSpec.BooleanValue stomachGrowling;
    public final ForgeConfigSpec.BooleanValue snowGolemHitSnowflakes;
    public final ForgeConfigSpec.BooleanValue sheepShearFluff;

    // Biomes
    public final ForgeConfigSpec.IntValue biomeParticlesRadius;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> mushroomSporeBiomes;
    public final ForgeConfigSpec.IntValue mushroomSporeDensity;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> fireflyBiomes;
    public final ForgeConfigSpec.IntValue fireflyDensity;

    // General
    public final ForgeConfigSpec.BooleanValue enableSleepingZs;

    public ModConfigs(ForgeConfigSpec.Builder builder) {
        builder.translation(categoryKey("blocks")).push("blocks")
                .translation(categoryKey("sparks")).push("sparks");

        removeVanillaCampfireSparks = builder
                .comment("Removes the vanilla lava spark particle from campfires")
                .translation(key("remove_vanilla_campfire_sparks"))
                .define("removeVanillaCampfireSparks", true);

        candleSparks = builder
                .translation(key("candle_sparks"))
                .define("candleSparks", true);

        furnaceSparks = builder
                .translation(key("furnace_sparks"))
                .define("furnaceSparks", true);

        fireSparks = builder
                .translation(key("fire_sparks"))
                .define("fireSparks", true);

        campfireSparks = builder
                .translation(key("campfire_sparks"))
                .define("campfireSparks", true);

        torchSparks = builder
                .translation(key("torch_sparks"))
                .define("torchSparks", true);

        builder.pop();

        redstoneBlockDust = builder
                .translation(key("redstone_block_dust"))
                .define("redstoneBlockDust", true);

        glowstoneBlockDust = builder
                .translation(key("glowstone_block_dust"))
                .defineEnum("glowstoneBlockDust", GlowstoneDustSpawnType.ON);

        beehivesHaveSleepingZs = builder
                .comment("Display Z particles in front of bee hives/nests at night")
                .translation(key("beehives_have_sleeping_zs"))
                .define("beehivesHaveSleepingZs", true);

        builder.pop().translation(categoryKey("entities")).push("entities")
                .translation(categoryKey("sleeping")).push("sleeping");

        playerSnoreChance = builder
                .comment("A percentage based chance for a player to snore.", TO_DISABLE)
                .translation(key("player_snore_chance"))
                .defineInRange("playerSnoreChance", 1.0, 0, 1.0);

        villagerSnoreChance = builder
                .comment("A percentage based chance for a villager to snore.", TO_DISABLE)
                .translation(key("villager_snore_chance"))
                .defineInRange("villagerSnoreChance", 1.0, 0, 1.0);

        displaySleepingZsOnlyWhenSnoring = builder
                .comment("Only display Z particles when a mob can snore")
                .translation(key("display_sleeping_zs_only_when_snoring"))
                .define("displaySleepingZsOnlyWhenSnoring", false);

        foxesHaveSleepingZs = builder
                .comment("Display Z particles for sleeping foxes")
                .translation(key("foxes_have_sleeping_zs"))
                .define("foxesHaveSleepingZs", true);

        adjustNametagRenderingWhenSleeping = builder
                .comment("Adjust name tag rendering to be at the top of the head rather than above it when a mob is sleeping in a bed")
                .translation(key("adjust_nametag_rendering_when_sleeping"))
                .define("adjustNametagRenderingWhenSleeping", true);

        builder.pop().translation(categoryKey("dust_clouds")).push("dustClouds");

        fallDamageDustClouds = builder
                .comment("Should a cloud of dust appear when a mob takes fall damage")
                .translation(key("fall_damage_dust_clouds"))
                .define("fallDamageDustClouds", true);

        sprintingDustClouds = builder
                .comment("Should a dust cloud form behind a sprinting player")
                .translation(key("sprinting_dust_clouds"))
                .define("sprintingDustClouds", true);

        mobSprintingDustClouds = builder
                .comment("Should a dust cloud form behind charging ravagers, galloping horses, and dashing camels")
                .translation(key("mob_sprinting_dust_clouds"))
                .define("mobSprintingDustClouds", true);

        builder.pop();

        chickenHitFeathers = builder
                .comment("When a chicken takes damage from a mob or player feathers fly off")
                .translation(key("chicken_hit_feathers"))
                .define("chickenHitFeathers", true);

        parrotHitFeathers = builder
                .comment("When a parrot takes damage from a mob or player feathers fly off")
                .translation(key("parrot_hit_feathers"))
                .define("parrotHitFeathers", true);

        enderPearlTrail = builder
                .translation(key("ender_pearl_trail"))
                .define("enderPearlTrail", true);

        snowballTrailDensity = builder
                .comment("The density of the snowball particle trail", TO_DISABLE)
                .translation(key("snowball_trail_chance"))
                .defineInRange("snowballTrailChance", 0.2, 0, 1);

        allayMagicDensity = builder
                .comment("The density of particles spawning around an allay", TO_DISABLE)
                .translation(key("allay_magic_density"))
                .defineInRange("allayMagicDensity", 0.2, 0, 1.0);

        stomachGrowling = builder
                .comment("Should a stomach growl sound play every 15 sec when the player is below 3 food points (or 6 half points)")
                .translation(key("stomach_growling"))
                .define("stomachGrowling", true);

        snowGolemHitSnowflakes = builder
                .comment("When a snow golem takes damage from a mob or player snowflakes fly off")
                .translation(key("snow_golem_hit_snowflakes"))
                .define("snowGolemHitSnowflakes", true);

        sheepShearFluff = builder
                .comment("When a sheep is sheared fluff particles will fall off")
                .translation(key("sheep_shear_fluff"))
                .define("sheepShearFluff", true);

        builder.pop().translation(categoryKey("biomes")).push("biomes");

        biomeParticlesRadius = builder
                .comment("The radius around the player that biome particles will spawn in", TO_DISABLE)
                .translation(key("biome_particles_radius"))
                .defineInRange("biomeParticleRadius", 32, 0, 48);

        mushroomSporeBiomes = builder
                .comment("A list of biome IDs that mushroom spore particles will spawn in")
                .translation(key("mushroom_spore_biomes"))
                .defineListAllowEmpty(List.of("mushroomSporeBiomes"), () -> List.of("minecraft:mushroom_fields"), ModConfigs::isValidLoc);

        mushroomSporeDensity = builder
                .comment("The density of spawned mushroom spores in a biome", TO_DISABLE)
                .translation(key("mushroom_spore_density"))
                .defineInRange("mushroomSporeDensity", 10, 0, 100);

        fireflyBiomes = builder
                .comment("A list of biome IDs that firefly particles will spawn in")
                .translation(key("firefly_biomes"))
                .defineListAllowEmpty(List.of("fireflyBiomes"), () -> List.of("minecraft:swamp", "minecraft:mangrove_swamp"), ModConfigs::isValidLoc);

        fireflyDensity = builder
                .comment("The density of spawned fireflies in a biome", TO_DISABLE)
                .translation(key("firefly_density"))
                .defineInRange("fireflyDensity", 6, 0, 100);

        builder.pop();

        enableSleepingZs = builder
                .comment("When an mob is sleeping display Z particles")
                .translation(key("enable_sleeping_zs"))
                .define("enableSleepingZs", true);
    }

    private static boolean isValidLoc(Object object) {
        if (object instanceof String string) {
            return ResourceLocation.tryParse(string) != null;
        }
        return false;
    }

    private static String key(String path) {
        return "config." + AmbientSleep.MOD_ID + "." + path;
    }

    private static String categoryKey(String path) {
        return "config.category." + AmbientSleep.MOD_ID + "." + path;
    }

    public enum GlowstoneDustSpawnType {
        ON,
        OFF,
        NETHER_ONLY
    }
}
