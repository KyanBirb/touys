package dev.kyanbirb.touys.neoforge.events;

import dev.kyanbirb.touys.SableTouys;
import dev.kyanbirb.touys.events.CommonEvents;
import dev.kyanbirb.touys.index.TouysItems;
import dev.kyanbirb.touys.items.crowbar.CrowbarItem;
import dev.kyanbirb.touys.neoforge.data.TouysBlockStates;
import dev.kyanbirb.touys.neoforge.data.TouysLangNeoForge;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = SableTouys.MOD_ID, value = Dist.CLIENT)
public class NeoForgeCommonEvents {

    @SubscribeEvent
    public static void postTick(LevelTickEvent.Post event) {
        CommonEvents.tick(event.getLevel());
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommonEvents.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    @EventBusSubscriber(modid = SableTouys.MOD_ID, value = Dist.CLIENT)
    public static class ModEvents {

        @SubscribeEvent
        public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
            event.modify(TouysItems.CROWBAR.get(), builder -> builder.set(
                    DataComponents.ATTRIBUTE_MODIFIERS, CrowbarItem.createAttributes(1.0f)
            ));
        }

        @SubscribeEvent
        public static void gatherData(GatherDataEvent event) {
            DataGenerator generator = event.getGenerator();
            PackOutput output = generator.getPackOutput();
            ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
            generator.addProvider(event.includeClient(), new TouysBlockStates(output, existingFileHelper));
            generator.addProvider(event.includeClient(), new TouysLangNeoForge(output));
        }

    }

}
