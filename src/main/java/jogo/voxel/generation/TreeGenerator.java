package jogo.voxel.generation;

import jogo.voxel.VoxelWorld;
import jogo.voxel.VoxelPalette;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerador de árvores para o mundo.
 */
public class TreeGenerator {

    private final VoxelWorld world;
    private final Random random;
    private final List<Tree> plantedTrees;

    private static final int TREE_SPAWN_CHANCE = 5;
    private static final int MIN_TREE_DISTANCE = 5;

    public TreeGenerator(VoxelWorld world, long seed) {
        this.world = world;
        this.random = new Random(seed);
        this.plantedTrees = new ArrayList<>();
    }

    public void generateTrees(int sizeX, int sizeZ) {
        System.out.println("🌳 A gerar árvores...");
        int arvoresGeradas = 0;

        // Pausa antes de gerar árvores - meshes devem estar prontos
        try { Thread.sleep(100); } catch (Exception e) {}

        for (int x = 5; x < sizeX - 5; x += 1) {
            for (int z = 5; z < sizeZ - 5; z += 1) {

                // 1. Encontra o Y do bloco sólido SUPERIOR
                int topSolidY = -1;
                for (int y = 255; y >= 0; y--) {
                    byte block = world.getBlock(x, y, z);
                    if (block != VoxelPalette.AIR_ID) {
                        topSolidY = y;
                        break;
                    }
                }

                if (topSolidY < 5 || topSolidY > 240) continue;

                // 2. Verifica se é GRAMA
                if (world.getBlock(x, topSolidY, z) != VoxelPalette.GRASS_ID) {
                    continue;
                }

                // 3. Verifica se há espaço para árvore
                if (topSolidY + 7 >= 255) continue;

                // 4. Verifica se há árvore perto
                if (hasNearbyTree(x, z, MIN_TREE_DISTANCE)) {
                    continue;
                }

                // 5. Chance de spawn
                if (random.nextInt(100) >= TREE_SPAWN_CHANCE) {
                    continue;
                }

                // 6. PLANTA NO topSolidY (raiz da árvore no chão!)
                System.out.println("✅ Árvore: x=" + x + " y=" + topSolidY + " z=" + z);
                Tree tree = new OakTree(x, topSolidY, z, world);
                tree.plant();
                plantedTrees.add(tree);
                arvoresGeradas++;
            }
        }

        System.out.println("🌳 Árvores geradas: " + arvoresGeradas);
    }

    private boolean hasNearbyTree(int x, int z, int radius) {
        for (Tree tree : plantedTrees) {
            int dx = tree.getX() - x;
            int dz = tree.getZ() - z;
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance < radius) {
                return true;
            }
        }
        return false;
    }

    public int getTreeCount() {
        return plantedTrees.size();
    }
}
