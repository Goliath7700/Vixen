package io.github.goliath7700.events;

import net.minestom.server.entity.Player;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.instance.block.Block;

public class PlayerListener {
    public static void onPlayerPickUp(PickupItemEvent event) {
        Player player = (Player) event.getLivingEntity();
        player.getInventory().addItemStack(event.getItemStack());
        event.getItemEntity().remove();
    }
}
