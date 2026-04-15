package dev.kyanbirb.touys.neoforge.compatibility.simulated;

import dev.kyanbirb.touys.compatibility.SubLevelLocker;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler;

public class SimulatedSubLevelLockerImpl implements SubLevelLocker {

    @Override
    public void unlockSubLevel(ServerSubLevel subLevel) {
        PhysicsStaffServerHandler handler = PhysicsStaffServerHandler.get(subLevel.getLevel());
        handler.removeLock(subLevel);
    }

    @Override
    public void lockSubLevel(ServerSubLevel subLevel) {
        PhysicsStaffServerHandler handler = PhysicsStaffServerHandler.get(subLevel.getLevel());
        if(!handler.isLocked(subLevel)) {
            handler.toggleLock(subLevel.getUniqueId());
        }
    }

    @Override
    public boolean isSubLevelLocked(ServerSubLevel subLevel) {
        PhysicsStaffServerHandler handler = PhysicsStaffServerHandler.get(subLevel.getLevel());
        return handler.isLocked(subLevel);
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
