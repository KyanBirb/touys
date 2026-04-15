package dev.kyanbirb.touys.blocks.projector;

import com.mojang.serialization.MapCodec;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ProjectorBlock extends DirectionalBlock implements EntityBlock {
    public static final MapCodec<ProjectorBlock> CODEC = simpleCodec(ProjectorBlock::new);

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty TAPE = BooleanProperty.create("tape");

    public ProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING).add(POWERED).add(TAPE));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        state = state.setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
        state = state.setValue(POWERED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
        state = state.setValue(TAPE, false);
        return state;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        if(level.isClientSide()) {
            return;
        }

        boolean previouslyPowered = state.getValue(POWERED);
        boolean signal = level.hasNeighborSignal(pos);
        if(signal != previouslyPowered) {
            state = state.cycle(POWERED);
        }

        level.setBlock(pos, state, 2);

        if (!previouslyPowered && !level.isClientSide() && state.getValue(POWERED)) {
            ProjectorBlockEntity projector = (ProjectorBlockEntity) level.getBlockEntity(pos);
            ItemStack item = projector.getTheItem();
            if (item.getItem() instanceof Projectable<?> projectable && item.has(projectable.projectionDataType())) {
                doProjection((ServerLevel) level, projectable, item, state, pos);
            }

        }

        level.scheduleTick(pos, state.getBlock(), 2);

        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, moved);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource pRandom) {
        if(state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, false), 2);
        }

        super.tick(state, level, pos, pRandom);
    }

    private static <T> void doProjection(ServerLevel level, Projectable<T> projectable, ItemStack itemStack, BlockState blockState, BlockPos blockPos) {
        T projectionData = itemStack.get(projectable.projectionDataType());
        BlockPos hitPos = null;
        if(projectable.requiresRayCast(itemStack)) {
            BlockHitResult hitResult = doRayCastAndParticles(blockState, level, blockPos);
            if(hitResult.getType() == HitResult.Type.BLOCK) {
                hitPos = hitResult.getBlockPos();
            }
        }
        projectable.project(level, projectionData, itemStack, hitPos);
    }

    private static BlockHitResult doRayCastAndParticles(BlockState state, ServerLevel level, BlockPos pos) {
        double range = 16;
        BlockHitResult hitResult = level.clip(new ClipContext(
                pos.getCenter().relative(state.getValue(FACING), 0.5f),
                pos.getCenter().relative(state.getValue(FACING), range),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                (Entity) null
        ));

        double dist = Sable.HELPER.distanceSquaredWithSubLevels(level, hitResult.getLocation(), pos.getCenter());
        int particleDensity = 5;
        int particleRange = (int) Math.sqrt(dist);
        Vec3i direction = state.getValue(FACING).getNormal();
        for (int i = 0; i < particleRange * particleDensity; i++) {
            Vec3 spawnPos = pos.getCenter();

            Vec3 dir = new Vec3(direction.getX(), direction.getY(), direction.getZ())
                    .scale(((double) i / particleDensity) + 0.5);
            spawnPos = spawnPos.add(dir.x, dir.y, dir.z);

            DustParticleOptions data = new DustParticleOptions(
                    DustParticleOptions.REDSTONE_PARTICLE_COLOR,
                    1.0f
            );

            level.sendParticles(data, spawnPos.x, spawnPos.y, spawnPos.z, 1, 0, 0, 0, 0.0f);
        }
        return hitResult;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(stack.getItem() instanceof Projectable<?>) {
            ProjectorBlockEntity projector = (ProjectorBlockEntity) level.getBlockEntity(pos);
            if(!projector.getTheItem().isEmpty()) {
                player.setItemInHand(hand, projector.getTheItem().copy());
                projector.getTheItem().shrink(1);
            }
            projector.setTheItem(stack.copyWithCount(1));
            stack.shrink(1);
            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ProjectorBlockEntity projector = (ProjectorBlockEntity) level.getBlockEntity(pos);
        if(!projector.getTheItem().isEmpty()) {
            player.addItem(projector.getTheItem().copy());
            projector.setTheItem(ItemStack.EMPTY);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ProjectorBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        Containers.dropContentsOnDestroy(pState, pNewState, pLevel, pPos);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }
}
