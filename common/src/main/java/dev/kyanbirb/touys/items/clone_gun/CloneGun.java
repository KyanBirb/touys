package dev.kyanbirb.touys.items.clone_gun;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.components.CopiedPlot;
import dev.kyanbirb.touys.index.TouysComponents;
import dev.kyanbirb.touys.util.SubLevelTemplate;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.List;

public class CloneGun extends Item {
	public CloneGun(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);

		if(!level.isClientSide()) {
			BlockHitResult hitResult = SableTouys.getTargetedBlock(level, player);

			if(player.isShiftKeyDown()) {
				ServerSubLevel subLevel = (ServerSubLevel) Sable.HELPER.getContaining(level, hitResult.getBlockPos());
				if(subLevel != null) {
					copySubLevel(player, subLevel, item);
				} else {
					item.remove(TouysComponents.COPIED_PLOT);
					player.displayClientMessage(SableTouys.translate("message.cleared"), true);
				}
			} else {
				if(item.has(TouysComponents.COPIED_PLOT)) {
					pasteSubLevel(level, player, item, hitResult);
				}
			}
		}

		return InteractionResultHolder.success(item);
	}

	private static void copySubLevel(Player player, ServerSubLevel subLevel, ItemStack item) {
		SubLevelTemplate template = SubLevelTemplate.of(subLevel);

		String name = subLevel.getName() != null ? subLevel.getName() : "";

		Vec3 direction = subLevel.logicalPose().transformNormalInverse(player.getLookAngle());
		Direction nearest = Direction.getNearest(direction.multiply(1, 0, 1));

		BoundingBox3ic box = subLevel.getPlot().getBoundingBox();

		item.applyComponents(DataComponentPatch.builder()
				.set(TouysComponents.COPIED_PLOT, new CopiedPlot(name, nearest, new Vector3i(box.width(), box.height(), box.length()), new Vector3d(), template))
				.build());

		name = name.isEmpty() ? "sub-level" : name;
		player.displayClientMessage(SableTouys.translate("message.copy", name), true);
	}

	private static void pasteSubLevel(Level level, Player player, ItemStack item, BlockHitResult hitResult) {
		CopiedPlot copied = item.get(TouysComponents.COPIED_PLOT);

		ServerSubLevelContainer container = (ServerSubLevelContainer) SubLevelContainer.getContainer(level);

		Vec3 pos = Sable.HELPER.projectOutOfSubLevel(level, hitResult.getLocation());

		Pose3d pose = new Pose3d();
		pose.position().set(pos.x, pos.y, pos.z);

		Quaterniond orientation = new Quaterniond();
		orientation.rotateY(copied.getRotation(player));
		pose.orientation().set(orientation);

		ServerSubLevel subLevel = copied.template().create((ServerLevel) level, pose);
		subLevel.setName(copied.name().isEmpty() ? null : copied.name());

		ServerLevelPlot subLevelPlot = subLevel.getPlot();
		BoundingBox3ic box = subLevelPlot.getBoundingBox();
		float offset = (box.maxY() - box.minY()) / 2.0f;

		container.physicsSystem().getPipeline()
				.teleport(subLevel, new Vector3d(pos.x, pos.y + offset + 0.5f, pos.z), pose.orientation());

		player.playSound(SoundEvents.ITEM_PICKUP);
	}

	@Override
	public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
		super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
		if(pStack.has(TouysComponents.COPIED_PLOT)) {
			CopiedPlot plot = pStack.get(TouysComponents.COPIED_PLOT);
			MutableComponent name = Component.literal(plot.name().isEmpty() ? "Sub-level" : plot.name())
					.withStyle(ChatFormatting.GREEN);

			pTooltipComponents.add(SableTouys.translate("tooltip.contains", name));
		}
	}
}
