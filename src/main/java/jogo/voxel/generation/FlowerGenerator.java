package jogo.voxel.generation;

import jogo.voxel.VoxelWorld;
import jogo.voxel.VoxelPalette;
import jogo.gameobject.Flower;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerador de flores para o mundo.
 * Responsável por: distribuição, validação, plantação de flores.
 * NOTA: Apenas cria objetos Flower, SEM voxels (renderização em VoxelWorld)
 */
public class FlowerGenerator {

    private final VoxelWorld world;
    private final Random random;
    private final List<Flower> plantedFlowers;

    private static final float FLOWER_SPAWN_CHANCE = 0.10f;
    private static final int MIN_FLOWER_DISTANCE = 3;
    private static final int MARGIN = 4;

    public FlowerGenerator(VoxelWorld world, long seed) {
        this.world = world;
        this.random = new Random(seed);
        this.plantedFlowers = new ArrayList<>();
    }

    public void generateFlowers(int sizeX, int sizeZ) {
        System.out.println("🌸 A gerar flores...");
        int floresGeradas = 0;

        for (int x = MARGIN; x < sizeX - MARGIN; x++) {
            for (int z = MARGIN; z < sizeZ - MARGIN; z++) {
                if (tryPlantFlower(x, z)) {
                    floresGeradas++;
                }
            }
        }

        System.out.println("✅ Flores geradas: " + floresGeradas);
    }

    private boolean tryPlantFlower(int x, int z) {
        int groundY = getHighestSolidY(x, z);
        if (groundY < 0 || groundY >= 255) return false;

        byte blockAtGround = world.getBlock(x, groundY, z);
        if (blockAtGround != VoxelPalette.GRASS_ID) {
            return false;
        }

        if (groundY + 2 >= 255) return false;

        if (hasNearbyFlower(x, z, MIN_FLOWER_DISTANCE)) {
            return false;
        }

        if (random.nextFloat() >= FLOWER_SPAWN_CHANCE) {
            return false;
        }

        // ✅ APENAS CRIA O OBJETO FLOWER (sem voxels)
        Flower flower = new Flower("Rosa " + plantedFlowers.size(), x, groundY, z);
        plantedFlowers.add(flower);

        System.out.println("🌸 Flor plantada: " + flower.toString());
        return true;
    }

    private int getHighestSolidY(int x, int z) {
        for (int y = 255; y >= 0; y--) {
            byte block = world.getBlock(x, y, z);
            if (block != VoxelPalette.AIR_ID &&
                    block != VoxelPalette.LEAVES_ID &&
                    block != VoxelPalette.WOOD_ID) {
                return y;
            }
        }
        return -1;
    }

    private boolean hasNearbyFlower(int x, int z, int radius) {
        for (Flower flower : plantedFlowers) {
            float dx = flower.getX() - x;
            float dz = flower.getZ() - z;
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance < radius) {
                return true;
            }
        }
        return false;
    }

    public List<Flower> getPlantedFlowers() {
        return new ArrayList<>(plantedFlowers);
    }

    public int getFlowerCount() {
        return plantedFlowers.size();
    }
}
