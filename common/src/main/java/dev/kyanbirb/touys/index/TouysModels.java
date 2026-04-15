package dev.kyanbirb.touys.index;

import dev.kyanbirb.touys.SableTouys;
import net.minecraft.client.resources.model.ModelResourceLocation;

import java.util.LinkedHashSet;
import java.util.Set;

public class TouysModels {
    public static final Set<ModelResourceLocation> ALL = new LinkedHashSet<>();

    public static final ModelResourceLocation
            ORB = partial("orb"),
            ORB_BOTTOM = partial("orb_bottom"),
            ORB_TOP = partial("orb_top"),
            BUBBLE = partial("bubble"),
            DOMINO = partial("domino");

    private static ModelResourceLocation partial(String path) {
        ModelResourceLocation location = SableTouys.modelPath("partial/" + path);
        ALL.add(location);
        return location;
    }

    public static void init() {
    }
}
