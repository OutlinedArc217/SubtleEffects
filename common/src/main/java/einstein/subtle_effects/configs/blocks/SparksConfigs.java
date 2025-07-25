package einstein.subtle_effects.configs.blocks;

import einstein.subtle_effects.init.ModConfigs;
import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import org.jetbrains.annotations.NotNull;

@Translation(prefix = ModConfigs.BASE_KEY + "blocks.sparks")
public class SparksConfigs extends ConfigSection {

    public boolean removeVanillaCampfireSparks = true;
    public boolean candleSparks = true;
    public boolean furnaceSparks = true;
    public boolean fireSparks = true;
    public boolean campfireSparks = true;
    public boolean torchSparks = true;
    public ValidatedInt lanternSparksDensity = new ValidatedInt(5, 10, 0);
    public LavaSparksDisplayType lavaSparksDisplayType = LavaSparksDisplayType.ON;
    public ValidatedDouble lavaSparksDensity = new ValidatedDouble(1, 1, 0.1);

    public enum LavaSparksDisplayType implements EnumTranslatable {
        OFF,
        ON,
        NOT_NETHER;

        @NotNull
        @Override
        public String prefix() {
            return ModConfigs.BASE_KEY + "blocks.sparks.lavaSparksDisplayType";
        }
    }
}
