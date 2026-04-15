package dev.kyanbirb.touys.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

public class SubLevelHighlighter {
    private static final Object2IntOpenHashMap<UUID> HIGHLIGHTS = new Object2IntOpenHashMap<>();
    private static final ObjectArraySet<UUID> RENDERED = new ObjectArraySet<>();

    public static void highlight(UUID subLevel, int color) {
        HIGHLIGHTS.put(subLevel, color);
        RENDERED.remove(subLevel);
    }

    public static void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, DeltaTracker deltaTracker, Camera camera) {
        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());
        ClientSubLevelContainer container = SubLevelContainer.getContainer(Minecraft.getInstance().level);

        for (Object2IntMap.Entry<UUID> entry : HIGHLIGHTS.object2IntEntrySet()) {
            UUID id = entry.getKey();
            int color = entry.getIntValue();
            float a = (color >> 24 & 0xFF) / 255.0f;
            float r = (color >> 16 & 0xFF) / 255.0f;
            float g = (color >> 8 & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            SubLevel subLevel = container.getSubLevel(id);
            if(subLevel != null) {
                AABB aabb = subLevel.boundingBox().toMojang();
                LevelRenderer.renderLineBox(poseStack, buffer, aabb, r, g, b, a);
            }

            RENDERED.add(id);
        }
    }

    public static void tick() {
        ObjectIterator<Object2IntMap.Entry<UUID>> iterator = HIGHLIGHTS.object2IntEntrySet().fastIterator();
        while (iterator.hasNext()) {
            Object2IntMap.Entry<UUID> entry = iterator.next();
            UUID id = entry.getKey();
            if(RENDERED.contains(id)) {
                iterator.remove();
                RENDERED.remove(id);
            }
        }
    }
}
