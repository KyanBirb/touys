package dev.kyanbirb.touys.items.camcorder.recording;

import dev.kyanbirb.touys.components.Frame;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecordingSession {
    public final List<Frame> recording;
    public final UUID subLevel;
    public int inactiveTicks = 0;

    public RecordingSession(UUID subLevel) {
        this.subLevel = subLevel;
        this.recording = new ArrayList<>();
    }

    public String asString(SubLevelContainer container) {
        SubLevel realSubLevel = container.getSubLevel(this.subLevel);
        String name = "Sub-level";
        if(realSubLevel != null && realSubLevel.getName() != null) {
            name = realSubLevel.getName();
        }
        return "\"" + name + "\": " + "(" + (this.recording.size() - 1) + ")";
    }
}
