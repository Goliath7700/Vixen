package io.github.goliath7700.commands;

import io.github.togar2.pvp.MinestomPvP;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class FlightCommand extends Command {
    public FlightCommand() {
        super("flight");

        setDefaultExecutor((sender, command) -> {
            if (sender instanceof Player) {
                ((Player) sender).setAllowFlying(!((Player) sender).isAllowFlying());
                ((Player) sender).setFlying(false);
                ((Player) sender).setFlyingSpeed(0.05F);
            }
        });


        // All default arguments are available in the ArgumentType class
        // Each argument has an identifier which should be unique. It is used internally to create the nodes
        var numberArgument = ArgumentType.Float("flight_speed");

        // Finally, create the syntax with the callback, and an infinite number of arguments
        addSyntax((sender, context) -> {
            final float flightSpeed = context.get(numberArgument);
            if (flightSpeed > 2) {
                sender.sendMessage("Flight Speed must be a floating point number less than or equal to 2!");
                return;
            }
            if (sender instanceof Player) {
                ((Player) sender).setAllowFlying(true);
                ((Player) sender).setFlyingSpeed(flightSpeed);
            }
        }, numberArgument);
    }
}
