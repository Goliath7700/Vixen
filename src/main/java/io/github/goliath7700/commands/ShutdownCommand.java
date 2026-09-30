package io.github.goliath7700.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class ShutdownCommand extends Command {
    public ShutdownCommand() {
        super("shutdown", "stop");

        setDefaultExecutor((sender, command) -> {
            MinecraftServer.getSchedulerManager().shutdown();
        });
    }
}
