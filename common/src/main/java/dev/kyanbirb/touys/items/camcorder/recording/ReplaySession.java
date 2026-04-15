package dev.kyanbirb.touys.items.camcorder.recording;

import dev.kyanbirb.touys.components.Frame;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.companion.math.Pose3d;

import java.util.List;
import java.util.Objects;

public class ReplaySession {
    public final List<Frame> frames;
    public final Pose3d currentPose = new Pose3d();
    public int ticks;
    public String name;
    public PhysicsConstraintHandle constraint = null;
    public boolean wasLocked = false;

    public ReplaySession(List<Frame> frames) {
        this.frames = frames;
    }

    @Override
    public String toString() {
        String name = Objects.requireNonNullElse(this.name, "Replay");
        return "\"" + name + "\": " + "(" + this.ticks + "/" + (this.frames.size() - 1) + ")";
    }
}
