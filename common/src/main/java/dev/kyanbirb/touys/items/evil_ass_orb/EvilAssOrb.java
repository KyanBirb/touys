package dev.kyanbirb.touys.items.evil_ass_orb;

import dev.kyanbirb.touys.SableTouys;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.joml.Vector3i;

public class EvilAssOrb extends Item {
	public EvilAssOrb(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
		return 72000;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack pStack) {
		return UseAnim.NONE;
	}


	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		pPlayer.startUsingItem(pUsedHand);
		return super.use(pLevel, pPlayer, pUsedHand);
	}

	@Override
	public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
		super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
		if(pLivingEntity instanceof Player player) {
			SubLevel subLevel = SableTouys.getTargetedSubLevel(pLevel, player);

			int speed = 0;
			if(subLevel != null) {
				BoundingBox3ic box = subLevel.getPlot().getBoundingBox();
				Vector3i size = (Vector3i) box.size(new Vector3i());
				size.add(1, 1, 1, size);

				RandomSource random = pLevel.getRandom();

				int particles = box.volume() * 5;
				particles = Math.min(200, particles);
				for (int i = 0; i < particles; i++) {
					double x = box.minX() + random.nextFloat() * ((double) size.x());
					double y = box.minY() + random.nextFloat() * ((double) size.y());
					double z = box.minZ() + random.nextFloat() * ((double) size.z());
					pLevel.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
				}

				player.playSound(SoundEvents.FIRECHARGE_USE, 0.3f, 1.0f);
				speed = 5;

				if(!pLevel.isClientSide()) {
					SubLevelContainer.getContainer(pLevel).removeSubLevel(subLevel, SubLevelRemovalReason.REMOVED);
				}
			}

			if(pLevel.isClientSide()) {
				updateOpenTicks(1 + speed, player);
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		if(pLevel.isClientSide()) {
			if(pEntity instanceof Player player) {
				if(!player.getUseItem().getItem().equals(pStack.getItem())) {
					updateOpenTicks(-1, player);
				}
			}
		}
	}

	private static void updateOpenTicks(int dir, Player player) {
		if(player != Minecraft.getInstance().player) return;
		EvilAssOrbRenderer.lastOpenTicks = EvilAssOrbRenderer.openTicks;
		EvilAssOrbRenderer.openTicks = Mth.clamp(EvilAssOrbRenderer.openTicks + dir, 0, 5);
	}

	@Override
	public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
		super.releaseUsing(pStack, pLevel, pLivingEntity, pTimeCharged);
	}
}
