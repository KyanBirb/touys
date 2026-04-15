package dev.kyanbirb.touys.index;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

import static dev.kyanbirb.touys.SableTouys.path;

public class TouysSounds {
    public static final Holder<SoundEvent> CAMERA_CLICK = create("item.camera_click");

    public static void init() {

    }

    private static Holder<SoundEvent> create(String id) {
        return Holder.direct(SoundEvent.createVariableRangeEvent(path(id)));
    }
}
