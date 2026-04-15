package dev.kyanbirb.touys.compatibility;

import dev.kyanbirb.touys.SableTouys;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;

public interface SubLevelLocker {
    void unlockSubLevel(ServerSubLevel subLevel);
    void lockSubLevel(ServerSubLevel subLevel);
    boolean isSubLevelLocked(ServerSubLevel subLevel);
    int getPriority();

    static SubLevelLocker get() {
        return SableTouys.LOCKER;
    }

    static SubLevelLocker set(SubLevelLocker subLevelLocker) {
        if(SableTouys.LOCKER == null || SableTouys.LOCKER.getPriority() < subLevelLocker.getPriority()) {
            SableTouys.LOCKER = subLevelLocker;
        }
        return SableTouys.LOCKER;
    }

}
