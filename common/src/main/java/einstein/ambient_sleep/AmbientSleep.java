package einstein.ambient_sleep;

import einstein.ambient_sleep.init.BiomeParticles;
import einstein.ambient_sleep.init.ModPackets;
import einstein.ambient_sleep.init.ModParticles;
import einstein.ambient_sleep.init.ModSounds;
import einstein.ambient_sleep.util.ParticleManager;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmbientSleep {

    public static final String MOD_ID = "ambient_sleep";
    public static final String MOD_NAME = "Ambient Sleep";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        ModSounds.init();
        ModParticles.init();
        ModPackets.init();
        BiomeParticles.init();
        ParticleManager.init();
    }

    public static void clientSetup() {
        ModPackets.init();
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}