package dev.kyanbirb.touys;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

import java.util.List;
import java.util.Set;

public abstract class TouysMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        if(mixinClassName.startsWith("dev.kyanbirb.touys.mixin.compatibility") ||
                mixinClassName.startsWith("dev.kyanbirb.touys.neoforge.mixin.compatibility") ||
                mixinClassName.startsWith("dev.kyanbirb.touys.fabric.mixin.compatibility")
        ) {
            String[] parts = mixinClassName.split("\\.");
            if(parts.length < 5) {
                return true;
            }

            String modId = parts[3].equals("mixin") ? parts[5] : parts[6];
            return PlatformHelper.isModLoaded(modId);
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

}
