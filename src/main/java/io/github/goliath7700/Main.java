package io.github.goliath7700;

import de.articdive.jnoise.core.api.functions.Combiner;
import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.core.api.modifiers.NoiseModifier;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator;
import de.articdive.jnoise.generators.noisegen.opensimplex.SuperSimplexNoiseGenerator;
import de.articdive.jnoise.generators.noisegen.random.white.WhiteNoiseGenerator;
import de.articdive.jnoise.modules.combination.CombinationModule;
import de.articdive.jnoise.modules.octavation.fractal_functions.FractalFunction;
import de.articdive.jnoise.pipeline.JNoise;
import io.github.goliath7700.commands.DisguiseCommand;
import io.github.goliath7700.commands.FlightCommand;
import io.github.goliath7700.commands.ShutdownCommand;
import io.github.goliath7700.events.BlockListener;
import io.github.goliath7700.events.PlayerListener;
import io.github.goliath7700.worldgen.WorldGenerator;
import io.github.togar2.pvp.MinestomPvP;
import io.github.togar2.pvp.feature.CombatFeature;
import io.github.togar2.pvp.feature.CombatFeatureSet;
import io.github.togar2.pvp.feature.CombatFeatures;
import io.github.togar2.pvp.feature.FeatureType;
import io.github.togar2.pvp.feature.provider.DifficultyProvider;
import io.github.togar2.pvp.utils.CombatVersion;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;

import java.nio.file.Path;
import java.util.List;

import static io.github.goliath7700.worldgen.WorldGenerator.WorldGenerate;

public class Main {
    void main() {
        System.out.println("Hello World!");
        // Server
        MinecraftServer server = MinecraftServer.init(new Auth.Online());

        // World Instance
        Path worldPath = Path.of("worlds/overworld");
        InstanceManager instanceManager = server.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer(new AnvilLoader(worldPath, DimensionType.OVERWORLD.key()));

        // Scheduler
        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildShutdownTask(() -> {
            System.out.println("Shutting down the server!");
            instanceContainer.saveChunksToStorage();
            MinecraftServer.stopCleanly();
            System.exit(0);
        });

        // OLD World Gen
//        instanceContainer.setGenerator(unit -> {
//            unit.modifier().fillHeight(-64, -63, Block.BEDROCK);
//            unit.modifier().fillHeight(-63, -61, Block.DIRT);
//            unit.modifier().fillHeight(-61, -60, Block.GRASS_BLOCK);
//        });

        // NEW World Gen
        WorldGenerate(instanceContainer);

        // Lighting
        instanceContainer.setChunkSupplier(LightingChunk::new);

        // Event Handler for Player spawning
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 120, 0));
        });

        // Events
        globalEventHandler.addListener(PlayerBlockBreakEvent.class, BlockListener::onBlockBreak);
        globalEventHandler.addListener(PickupItemEvent.class, PlayerListener::onPlayerPickUp);
        globalEventHandler.addListener(PlayerBlockInteractEvent.class, PlayerListener::onPlayerBlockInteract);
        globalEventHandler.addListener(EntityAttackEvent.class, PlayerListener::onPlayerFallingBlockHit);
        globalEventHandler.addListener(PlayerEntityInteractEvent.class, PlayerListener::onPlayerFallingBlockInteract);

        // Commands
        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new DisguiseCommand());
        commandManager.register(new ShutdownCommand());
        commandManager.register(new FlightCommand());

        // PVP
        MinestomPvP.init();

        CombatFeatureSet legacyVanilla = CombatFeatures.getVanilla(CombatVersion.LEGACY, DifficultyProvider.DEFAULT)
                .remove(CombatFeatures.VANILLA_FALL.featureType())
                .remove(CombatFeatures.VANILLA_EXHAUSTION.featureType())
                .build();
        MinecraftServer.getGlobalEventHandler().addChild(legacyVanilla.createNode());
        // Start

        server.start("0.0.0.0",25565);

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            // memory logging
            Runtime rt = Runtime.getRuntime();
            long used = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
            long max = rt.maxMemory() / 1024 / 1024;
            System.out.printf("[Memory] %d MB used / %d MB max%n", used, max);
        }).repeat(TaskSchedule.seconds(5)).schedule();
    }
}
