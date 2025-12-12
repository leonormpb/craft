package jogo.voxel.generation;

import jogo.voxel.VoxelWorld;
import jogo.voxel.VoxelPalette;
import jogo.voxel.generation.Tree;

/**
 * Árvore de carvalho padrão do jogo.
 * Estrutura: tronco reto + copa arredondada.
 */
public class OakTree extends Tree {

    private static final int TRUNK_HEIGHT = 5;
    private static final int MAX_LEAVES_RADIUS = 2;

    public OakTree(int x, int z, int yBase, VoxelWorld world) {
        super(x, z, yBase, world);
    }

    @Override
    public void plant() {
        // 1. Planta o tronco
        plantTrunk();

        // 2. Planta a folhagem
        plantFoliage();
    }

    /**
     * Planta o tronco da árvore.
     */
    private void plantTrunk() {
        for (int i = 1; i <= TRUNK_HEIGHT; i++) {
            placeBlock(x, yBase + i, z, VoxelPalette.WOOD_ID);
        }
    }

    /**
     * Planta a folhagem (copa da árvore).
     * Usa múltiplas camadas com raios decrescentes.
     */
    private void plantFoliage() {
        // Camada 1: raio 2 (base da copa, logo acima do tronco)
        placeLeavesLayer(x, yBase + 3, z, 2);

        // Camada 2: raio 2 (meio da copa)
        placeLeavesLayer(x, yBase + 4, z, 2);

        // Camada 3: raio 2 (alta)
        placeLeavesLayer(x, yBase + 5, z, 2);

        // Camada 4: raio 1 (topo)
        placeLeavesLayer(x, yBase + 6, z, 1);
    }


    @Override
    public int getHeight() {
        return TRUNK_HEIGHT;
    }

    @Override
    public int getMaxRadius() {
        return MAX_LEAVES_RADIUS;
    }
}
