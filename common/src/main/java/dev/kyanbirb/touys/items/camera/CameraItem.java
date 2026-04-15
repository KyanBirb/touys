package dev.kyanbirb.touys.items.camera;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CameraItem extends Item {
    public CameraItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pLevel.isClientSide()) {
            CameraScreen.open(pPlayer);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
