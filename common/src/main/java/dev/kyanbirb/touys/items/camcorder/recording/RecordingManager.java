package dev.kyanbirb.touys.items.camcorder.recording;

import dev.kyanbirb.touys.TouysConfig;
import dev.kyanbirb.touys.compatibility.SubLevelLocker;
import dev.kyanbirb.touys.components.Frame;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.fixed.FixedConstraintConfiguration;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecordingManager {
    public static final Map<ResourceKey<Level>, Map<UUID, RecordingSession>> RECORDING_SESSIONS = new Object2ObjectOpenHashMap<>();
    public static final Map<ResourceKey<Level>, Map<UUID, ReplaySession>> REPLAY_SESSIONS = new Object2ObjectOpenHashMap<>();

    public static void tick(ServerLevel level) {
        if(level.tickRateManager().isFrozen() || SubLevelPhysicsSystem.get(level).getPaused()) {
            return;
        }

        Map<UUID, RecordingSession> recordings = getRecordings(level);
        Map<UUID, ReplaySession> replays = getReplays(level);

        recordings.entrySet().removeIf(entry -> updateRecordings(level, entry));
        replays.entrySet().removeIf(entry -> updateReplays(level, entry));
    }

    public static void physicsTick(SubLevelPhysicsSystem system) {
        ServerLevel level = system.getLevel();
        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        Map<UUID, ReplaySession> sessions = getReplays(level);

        Iterator<Map.Entry<UUID, ReplaySession>> iterator = sessions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ReplaySession> entry = iterator.next();
            ReplaySession session = entry.getValue();
            ServerSubLevel subLevel = (ServerSubLevel) container.getSubLevel(entry.getKey());
            if(subLevel == null) {
                iterator.remove();
                continue;
            }

            if(session.constraint != null) {
                session.constraint.remove();
                session.constraint = null;
            }

            PhysicsPipeline pipeline = system.getPipeline();
            Frame frame = session.frames.get(Math.min(session.ticks, session.frames.size() - 1));
            Pose3d pose = session.currentPose;
            pose.position().set(frame.position());
            pose.orientation().set(frame.orientation());

            session.constraint = pipeline.addConstraint(null, subLevel, new FixedConstraintConfiguration(
                    pose.position(),
                    subLevel.logicalPose().rotationPoint(),
                    pose.orientation()
            ));
        }
    }

    public static RecordingSession startRecording(SubLevel subLevel) {
        RecordingSession session = new RecordingSession(subLevel.getUniqueId());
        getRecordings(subLevel.getLevel())
                .put(subLevel.getUniqueId(), session);
        return session;
    }

    @Nullable
    public static List<Frame> getRecording(SubLevel subLevel) {
        RecordingSession session = getRecordings(subLevel.getLevel())
                .get(subLevel.getUniqueId());
        if(session != null) {
            return session.recording;
        }
        return null;
    }

    public static void stopRecording(SubLevel subLevel) {
        if(subLevel != null) {
            RecordingSession session = getRecordings(subLevel.getLevel())
                    .get(subLevel.getUniqueId());
            if(session != null) {
                RECORDING_SESSIONS.get(subLevel.getLevel().dimension()).remove(subLevel.getUniqueId());
            }
        }
    }

    public static ReplaySession startReplay(ServerSubLevel subLevel, List<Frame> recording) {
        Map<UUID, ReplaySession> replays = getReplays(subLevel.getLevel());
        if(replays.containsKey(subLevel.getUniqueId())) {
            stopReplay(subLevel);
        }
        ReplaySession session = new ReplaySession(recording);
        if(TouysConfig.TAPE_LOCKING_BEHAVIOR.get().canUnlock()) {
            session.wasLocked = SubLevelLocker.get().isSubLevelLocked(subLevel);
            SubLevelLocker.get().unlockSubLevel(subLevel);
        }

        getReplays(subLevel.getLevel()).put(subLevel.getUniqueId(), session);
        return session;
    }

    public static void stopReplay(ServerSubLevel subLevel) {
        ReplaySession session = getReplays(subLevel.getLevel())
                .get(subLevel.getUniqueId());
        if(session != null) {
            if(session.constraint != null) {
                session.constraint.remove();
            }
            REPLAY_SESSIONS.get(subLevel.getLevel().dimension()).remove(subLevel.getUniqueId());
        }
    }

    private static Map<UUID, RecordingSession> getRecordings(Level level) {
        return RECORDING_SESSIONS.computeIfAbsent(level.dimension(), (key) -> new Object2ObjectOpenHashMap<>());
    }

    private static Map<UUID, ReplaySession> getReplays(Level level) {
        return REPLAY_SESSIONS.computeIfAbsent(level.dimension(), (key) -> new Object2ObjectOpenHashMap<>());
    }

    private static boolean updateRecordings(ServerLevel level, Map.Entry<UUID, RecordingSession> entry) {
        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        ServerSubLevel subLevel = (ServerSubLevel) container.getSubLevel(entry.getKey());
        RecordingSession session = entry.getValue();

        if(subLevel == null) {
            session.inactiveTicks++;

            int maxInactiveTicks = TouysConfig.RECORDING_INACTIVE_TICKS.getAsInt();
            return maxInactiveTicks != -1 && session.inactiveTicks >= maxInactiveTicks;
        } else {
            session.inactiveTicks = 0;

            int maxDuration = TouysConfig.RECORDING_MAX_DURATION.getAsInt();
            if(maxDuration == -1 || session.recording.size() < maxDuration) {
                session.recording.add(new Frame(new Pose3d(subLevel.logicalPose())));
            }

            return false;
        }
    }

    private static boolean updateReplays(ServerLevel level, Map.Entry<UUID, ReplaySession> entry) {
        ServerSubLevelContainer container = ServerSubLevelContainer.getContainer(level);
        ServerSubLevel subLevel = (ServerSubLevel) container.getSubLevel(entry.getKey());
        ReplaySession session = entry.getValue();

        if(subLevel == null || session.ticks >= session.frames.size()) {
            session.constraint.remove();

            if(subLevel != null && session.wasLocked && TouysConfig.TAPE_LOCKING_BEHAVIOR.get().restoresLock()) {
                SubLevelLocker.get().lockSubLevel(subLevel);
                return false;
            }

            return true;
        }

        session.ticks++;
        return false;
    }



}
