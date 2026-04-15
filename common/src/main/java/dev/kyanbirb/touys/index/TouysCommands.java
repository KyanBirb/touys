package dev.kyanbirb.touys.index;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public class TouysCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        /*LiteralArgumentBuilder<CommandSourceStack> touysBuilder = Commands.literal("touys")
                .requires(stack -> stack.hasPermission(2));
        dispatcher.register(touysBuilder);*/
    }

}
