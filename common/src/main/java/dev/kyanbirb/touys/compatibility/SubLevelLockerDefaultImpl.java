package dev.kyanbirb.touys.compatibility;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;

public class SubLevelLockerDefaultImpl implements SubLevelLocker {
    @Override
    public void unlockSubLevel(ServerSubLevel subLevel) {
        // no-op
    }

    @Override
    public void lockSubLevel(ServerSubLevel subLevel) {
        // no-op
    }

    @Override
    public boolean isSubLevelLocked(ServerSubLevel subLevel) {
        return false;
    }

    @Override
    public int getPriority() {
        return -1000;
    }
}
