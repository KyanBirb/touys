package dev.kyanbirb.touys.items.bubble_blower;

import dev.kyanbirb.touys.index.TouysComponents;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class BubbleBlowerItem extends Item {
    public BubbleBlowerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack item = pContext.getItemInHand();
        BlockPos startPos = item.get(TouysComponents.ASSEMBLY_START);
        Player player = pContext.getPlayer();
        Level level = pContext.getLevel();
        if(startPos == null) {
            item.set(TouysComponents.ASSEMBLY_START, pContext.getClickedPos());
            player.playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, 5.0f, 0.75f);
        } else {
            if(!player.isShiftKeyDown()) {
                BlockPos endPos = pContext.getClickedPos();
                if(inSameGrid(level, startPos, endPos)) {
                    assembleBlocks(level, startPos, endPos);
                    player.playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, 5.0f, 1.5f);
                    player.displayClientMessage(Component.literal("Created sub-level"), true);

                } else {
                    return InteractionResult.FAIL;
                }
            } else {
                player.displayClientMessage(Component.literal("Selection discarded"), true);
                player.playSound(SoundEvents.ITEM_PICKUP, 1.0f, 0.5f);

            }
            item.remove(TouysComponents.ASSEMBLY_START);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if(pIsSelected && pLevel.isClientSide() && pEntity instanceof Player player) {
            yellAtPlayerBecauseTheyArentDoingItRight(pLevel, player, pStack);
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    private static void yellAtPlayerBecauseTheyArentDoingItRight(Level level, Player player, ItemStack stack) {
        BlockPos start = stack.get(TouysComponents.ASSEMBLY_START);
        if(start != null && Minecraft.getInstance().hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitResult = (BlockHitResult) Minecraft.getInstance().hitResult;
            if(!inSameGrid(level, start, hitResult.getBlockPos())) {
                player.displayClientMessage(Component.literal("Invalid selection").withStyle(ChatFormatting.RED), true);
            }
        }
    }

    private static void assembleBlocks(Level level, BlockPos pos1, BlockPos pos2) {
        if(!(level instanceof ServerLevel serverLevel)) return;
        if(!inSameGrid(level, pos1, pos2)) return;
        BoundingBox boundingBox = BoundingBox.fromCorners(pos1, pos2);
        List<BlockPos> blocks = BlockPos.betweenClosedStream(boundingBox).map(BlockPos::immutable).toList();
        BlockPos anchor = blocks.getFirst();

        BoundingBox3i bounds = new BoundingBox3i(boundingBox);
        SubLevelAssemblyHelper.assembleBlocks(serverLevel, anchor, blocks, bounds);
    }

    public static boolean inSameGrid(Level level, BlockPos pos1, BlockPos pos2) {
        SubLevel subLevel1 = Sable.HELPER.getContaining(level, pos1);
        SubLevel subLevel2 = Sable.HELPER.getContaining(level, pos2);
        return subLevel1 == subLevel2;
    }
}
