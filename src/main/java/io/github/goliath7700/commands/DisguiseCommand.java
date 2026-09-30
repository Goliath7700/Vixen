package io.github.goliath7700.commands;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;

public class DisguiseCommand extends Command {
    public DisguiseCommand() {
        super("disguise");

        setDefaultExecutor((sender, commandContext) -> {
            sender.sendMessage("Usage: /disguise <username>");
        });

        var usernameArgument = ArgumentType.String("username");

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can disguise as other players.");
                return;
            }
            final String disguise = context.get(usernameArgument);
            player.setSkin(PlayerSkin.fromUsername(disguise));
            player.setDisplayName(Component.text(disguise));
        }, usernameArgument);
    }
}
