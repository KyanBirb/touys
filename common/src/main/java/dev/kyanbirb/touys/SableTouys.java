package dev.kyanbirb.touys;

import dev.kyanbirb.touys.compatibility.SubLevelLocker;
import dev.kyanbirb.touys.compatibility.SubLevelLockerDefaultImpl;
import dev.kyanbirb.touys.events.ClientEvents;
import dev.kyanbirb.touys.events.CommonEvents;
import dev.kyanbirb.touys.index.*;
import dev.kyanbirb.touys.network.TouysPackets;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SableTouys {
	public static final String MOD_ID = "touys";
	public static final String MOD_NAME = "Sable Touys";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
	public static SubLevelLocker LOCKER = SubLevelLocker.set(new SubLevelLockerDefaultImpl());
	public static final CreativeModeTab CREATIVE_TAB = PlatformHelper.getCreativeTabBuilder()
			.icon(() -> TouysItems.CLONE_GUN.get().getDefaultInstance())
			.title(Component.translatable("itemGroup.touys"))
			.displayItems(ClientEvents::buildCreativeTabContents)
			.build();

	public static void init() {
		TouysItems.init();
		TouysBlocks.init();
		TouysBlockEntityTypes.init();
		TouysComponents.init();
		TouysPackets.init();
		TouysSounds.init();

		SableEventPlatform.INSTANCE.onPhysicsTick(CommonEvents::physicsTick);
	}

	public static ResourceLocation path(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
	public static ModelResourceLocation modelPath(String path) {
		return PlatformHelper.createModelResourceLocation(path(path));
	}

	public static MutableComponent translate(String key, Object ...args) {
		return Component.translatable("touys." + key, args);
	}

	public static BlockHitResult getTargetedBlock(Level level, Player player, float partialTicks) {
		return level.clip(new ClipContext(
				player.getEyePosition(partialTicks),
				player.getEyePosition(partialTicks).add(player.getLookAngle().scale(TouysConfig.ITEM_RANGE.getAsInt())),
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				player
		));
	}

	public static BlockHitResult getTargetedBlock(Level level, Player player) {
		return getTargetedBlock(level, player, 1.0f);
	}

	public static @Nullable SubLevel getTargetedSubLevel(Level level, Player player) {
		return Sable.HELPER.getContaining(level, getTargetedBlock(level, player).getLocation());
	}
}
