package io.github.goliath7700.worldgen;

import de.articdive.jnoise.core.api.functions.Combiner;
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator;
import de.articdive.jnoise.modules.combination.CombinationModule;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;

public class WorldGenerator {
    public static void WorldGenerate(InstanceContainer instanceContainer) {
        // Noise used for the height
        JNoise simplexNoise2 = JNoise.newBuilder()
                .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(100).build())
                .scale(0.01)
                .build();
        JNoise simplexNoise = JNoise.newBuilder()
                .fastSimplex(FastSimplexNoiseGenerator.newBuilder().build())
                .scale(0.005)
                .build();
        JNoise noise = JNoise.newBuilder()
                .combination(CombinationModule.newBuilder().setA(simplexNoise).setB(simplexNoise2).setCombiner(Combiner.ADD))// Low frequency for smooth terrain
                .build();

        // Set the Generator
        instanceContainer.setGenerator(unit -> {
            unit.modifier().fillHeight(-64, 0, Block.WATER);
            Point start = unit.absoluteStart();
            for (int x = 0; x < unit.size().x(); x++) {
                for (int z = 0; z < unit.size().z(); z++) {
                    Point bottom = start.add(x, 0, z);

                    synchronized (noise) { // Synchronization is necessary for JNoise
                        double height = noise.evaluateNoise(bottom.x(), bottom.z()) * 16;
                        // * 16 means the height will be between -16 and +16
                        unit.modifier().fill(bottom, bottom.add(1, 0, 1).withY(height), Block.OAK_PLANKS);
                    }
                }
            }
        });
    }
}
