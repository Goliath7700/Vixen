package io.github.goliath7700.events;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.time.Duration;

public class BlockListener {

    public static void onBlockBreak(PlayerBlockBreakEvent event) {
        var block = event.getBlock();
        var instance = event.getInstance();
        var position = event.getBlockPosition();

        Material material = block.material();
        if (material == null) return;

        ItemStack dropStack = ItemStack.of(material, 16);

        ItemEntity itemEntity = new ItemEntity(dropStack);

        Vec dropPos = position.add(0.5, 0.5, 0.5);
        itemEntity.setInstance(instance, dropPos);
        itemEntity.setVelocity(new Vec(
                (Math.random() - 0.5) * 2,
                3,
                (Math.random() - 0.5) * 2
        ));
        itemEntity.setPickupDelay(Duration.ofMillis(500));
    }
}