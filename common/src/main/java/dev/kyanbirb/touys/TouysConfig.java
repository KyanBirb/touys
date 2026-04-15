package dev.kyanbirb.touys;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TouysConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue ITEM_RANGE;
    public static final ModConfigSpec.IntValue RECORDING_MAX_DURATION;
    public static final ModConfigSpec.IntValue RECORDING_INACTIVE_TICKS;
    public static final ModConfigSpec.EnumValue<LockingBehavior> PHOTO_LOCKING_BEHAVIOR;
    public static final ModConfigSpec.EnumValue<LockingBehavior> TAPE_LOCKING_BEHAVIOR;

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ITEM_RANGE = builder
                .comment("The distance items will raycast for interactions")
                .defineInRange("item_range", 80, 1, 100);

        builder.push("recording");
        RECORDING_MAX_DURATION = builder
                .comment("The maximum allowed tick duration for a recording. -1 for no limit")
                .defineInRange("max_duration", -1, -1, Integer.MAX_VALUE);
        RECORDING_INACTIVE_TICKS = builder
                .comment("The amount of ticks a recording will persist for an unloaded sub-level before being discarded. -1 for no limit")
                .defineInRange("inactive_ticks", 2400, -1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("compatibility");
        PHOTO_LOCKING_BEHAVIOR = builder
                .comment("How a Photo should treat locks when activated")
                .defineEnum("photo_locking_behavior", LockingBehavior.RESTORE_LOCK);
        TAPE_LOCKING_BEHAVIOR = builder
                .comment("How a Tape should treat locks when activated")
                .defineEnum("tape_locking_behavior", LockingBehavior.RESTORE_LOCK);
        builder.pop();

        SPEC = builder.build();
    }

    public enum LockingBehavior {
        DO_NOTHING,
        RESTORE_LOCK,
        STAY_UNLOCKED;

        public boolean restoresLock() {
            return this == RESTORE_LOCK;
        }

        public boolean canUnlock() {
            return this != DO_NOTHING;
        }
    }
}
