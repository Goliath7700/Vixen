package io.github.goliath7700.worldgen;

import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import org.apache.commons.math3.analysis.interpolation.HermiteInterpolator;

public class WorldGenerator {
    // Noise used for the height
    // RN I have two noise maps that are combined
    static final JNoise noise1 = JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().setSeed(100).build())
            .scale(0.005)
            .build();
    static final JNoise noise2 = JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().build())
            .scale(0.001)
            .build();
    static final JNoise continentalnessNoise = JNoise.newBuilder()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder().build())
            .scale(0.0005)
            .build();

    public static void WorldGenerate(InstanceContainer instanceContainer) {

        int seaLevel = 74;

        // Continentalness Spline
        HermiteInterpolator terrainSpline = new HermiteInterpolator();
        terrainSpline.addSamplePoint(-1.0, new double[] {-40.0}, new double[] {  0.0 });
        terrainSpline.addSamplePoint(-0.3, new double[] {  0.0}, new double[] { 30.0 });
        terrainSpline.addSamplePoint( 0.2, new double[] { 10.0}, new double[] { 10.0 });
        terrainSpline.addSamplePoint( 0.7, new double[] { 40.0}, new double[] {-50.0 });
        terrainSpline.addSamplePoint( 1.0, new double[] {120.0}, new double[] {  0.0 });

        // Set the Generator
        instanceContainer.setGenerator(unit -> {
            unit.modifier().fillHeight(-64, 0, Block.WATER);
            Point start = unit.absoluteStart();
            //OLD CODE
//            for (int x = 0; x < unit.size().x(); x++) {
//                for (int z = 0; z < unit.size().z(); z++) {
//                    Point bottom = start.add(x, 0, z);
//
//                     // Synchronization is necessary for JNoise // Removed it, but might regret it
//                        double height = noise.evaluateNoise(bottom.x(), bottom.z()) * 16;
//                        // * 16 means the height will be between -16 and +16
//                        unit.modifier().fill(bottom, bottom.add(1, 0, 1).withY(height), Block.OAK_PLANKS);
//
//                }
//            }

            int sizeX = unit.size().blockX();
            int sizeZ = unit.size().blockZ();

            // Pre-compute heights for this unit
            // All this does is add the noise maps
            double[] heights = new double[sizeX * sizeZ];
            for (int x = 0; x < sizeX; x++) {
                for (int z = 0; z < sizeZ; z++) {
                    double worldX = start.x() + x;
                    double worldZ = start.z() + z;

                    double continentalNoise = continentalnessNoise.evaluateNoise(worldX, worldZ);
                    double continentalOffset = terrainSpline.value(continentalNoise)[0];

                    heights[x * sizeZ + z] = (noise1.evaluateNoise(worldX, worldZ) * 0.5
                            + noise2.evaluateNoise(worldX, worldZ) * 4) * 16 + continentalOffset;
                }
            }

            // ALl this does is place the blocks down
            unit.modifier().setAllRelative((x, y, z) -> {
                double height = heights[x * sizeZ + z];

                // Only place planks if we're below the height
                if (y < height + 63) {
                    if (y < 50) {
                        return Block.STONE;
                    }
                    if (y < seaLevel + 5) {
                        return Block.SAND;
                    }
                    return Block.GRASS_BLOCK;
                }

                // Keep Ocean
                if (y < seaLevel) {
                    return Block.WATER;
                }

                return Block.AIR;
            });
        });
    }
}
