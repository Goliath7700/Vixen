package io.github.goliath7700;

import io.github.goliath7700.events.BlockBreakListener;
import io.github.goliath7700.events.PlayerListener;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;

public class Main {
    void main() {
        System.out.println("Hello World!");
        // Server
        MinecraftServer server = MinecraftServer.init(new Auth.Online());

        // World Instance
        InstanceManager instanceManager = server.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        // World Gen
        instanceContainer.setGenerator(unit -> {
            unit.modifier().fillHeight(-64, -63, Block.BEDROCK);
            unit.modifier().fillHeight(-63, -61, Block.DIRT);
            unit.modifier().fillHeight(-61, -60, Block.GRASS_BLOCK);
        });
        // Lighting
        instanceContainer.setChunkSupplier(LightingChunk::new);

        // Event Handler for Player spawning
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, -60, 0));
        });

        // Events
        globalEventHandler.addListener(PlayerBlockBreakEvent.class, BlockBreakListener::onBlockBreak);
        globalEventHandler.addListener(PickupItemEvent.class, PlayerListener::onPlayerPickUp);

        // Start

        server.start("0.0.0.0",25565);
    }
}
