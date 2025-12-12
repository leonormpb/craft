package jogo.voxel.generation;

import jogo.voxel.VoxelWorld;
import jogo.voxel.VoxelPalette;

/**
 * Classe abstrata que representa uma árvore plantável no mundo.
 */
public abstract class Tree {

    protected int x;
    protected int z;
    protected int yBase;  // Altura do CHÃO (onde está a grama)
    protected VoxelWorld world;

    public Tree(int x, int z, int yBase, VoxelWorld world) {
        this.x = x;
        this.z = z;
        this.yBase = yBase;
        this.world = world;
    }

    public int getX() { return x; }
    public int getZ() { return z; }
    public int getYBase() { return yBase; }

    public abstract void plant();
    public abstract int getHeight();
    public abstract int getMaxRadius();

    protected void placeBlock(int x, int y, int z, byte blockId) {
        // Verifica limites RIGOROSOS
        if (x < 0 || x >= 256 || y < 0 || y >= 256 || z < 0 || z >= 256) {
            return;  // Ignora blocos fora do mapa
        }
        world.setBlock(x, y, z, blockId);
    }

    protected void placeLeavesLayer(int centerX, int y, int centerZ, int radius) {
        // Verifica se Y está válido
        if (y < 0 || y >= 256) return;

        for (int lx = centerX - radius; lx <= centerX + radius; lx++) {
            for (int lz = centerZ - radius; lz <= centerZ + radius; lz++) {

                // Verifica limites
                if (lx < 0 || lx >= 256 || lz < 0 || lz >= 256) continue;

                // Não substitui o tronco
                if (world.getBlock(lx, y, lz) == VoxelPalette.WOOD_ID) continue;

                // Calcula distância
                int dx = lx - centerX;
                int dz = lz - centerZ;
                double distance = Math.sqrt(dx * dx + dz * dz);

                // Só coloca se for círculo
                if (distance <= radius - 0.5) {
                    byte current = world.getBlock(lx, y, lz);
                    if (current == VoxelPalette.AIR_ID) {
                        world.setBlock(lx, y, lz, VoxelPalette.LEAVES_ID);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s{pos=(%d, %d, %d)}",
                this.getClass().getSimpleName(), x, yBase, z);
    }
}
