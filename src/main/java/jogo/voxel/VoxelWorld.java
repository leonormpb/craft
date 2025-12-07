package jogo.voxel;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import jogo.util.ProcTextures;
import jogo.util.Hit;
import jogo.fastnoise.FastNoiseLite;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VoxelWorld {
    private final AssetManager assetManager;
    private final int sizeX, sizeY, sizeZ;
    private final VoxelPalette palette;

    private final Node node = new Node("VoxelWorld");
    private final Map<Byte, Geometry> geoms = new HashMap<>();
    private final Map<Byte, Material> materials = new HashMap<>();

    private boolean lit = true;       // Shading: On by default
    private boolean wireframe = false; // Wireframe: Off by default
    private boolean culling = true;   // Culling: On by default
    private int groundHeight = 8; // baseline Y level

    // Chunked world data
    private final int chunkSize = Chunk.SIZE;
    private final int chunkCountX, chunkCountY, chunkCountZ;
    private final Chunk[][][] chunks;

    // Seed do mundo
    private static final int WORLD_SEED = 12345;

    public VoxelWorld(AssetManager assetManager, int sizeX, int sizeY, int sizeZ) {
        this.assetManager = assetManager;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.palette = VoxelPalette.defaultPalette();
        // Remove old vox array
        // this.vox = new byte[sizeX][sizeY][sizeZ];
        this.chunkCountX = (int) Math.ceil(sizeX / (float) chunkSize);
        this.chunkCountY = (int) Math.ceil(sizeY / (float) chunkSize);
        this.chunkCountZ = (int) Math.ceil(sizeZ / (float) chunkSize);
        this.chunks = new Chunk[chunkCountX][chunkCountY][chunkCountZ];
        for (int cx = 0; cx < chunkCountX; cx++)
            for (int cy = 0; cy < chunkCountY; cy++)
                for (int cz = 0; cz < chunkCountZ; cz++)
                    chunks[cx][cy][cz] = new Chunk(cx, cy, cz);
        initMaterials();
    }

    // Helper to get chunk and local coordinates
    private Chunk getChunk(int x, int y, int z) {
        int cx = x / chunkSize;
        int cy = y / chunkSize;
        int cz = z / chunkSize;
        if (cx < 0 || cy < 0 || cz < 0 || cx >= chunkCountX || cy >= chunkCountY || cz >= chunkCountZ) return null;
        return chunks[cx][cy][cz];
    }

    private int lx(int x) {
        return x % chunkSize;
    }

    private int ly(int y) {
        return y % chunkSize;
    }

    private int lz(int z) {
        return z % chunkSize;
    }

    // Block access
    public byte getBlock(int x, int y, int z) {
        Chunk c = getChunk(x, y, z);
        if (c == null) return VoxelPalette.AIR_ID;
        if (!inBounds(x, y, z)) return VoxelPalette.AIR_ID;
        return c.get(lx(x), ly(y), lz(z));
    }

    public void setBlock(int x, int y, int z, byte id) {
        Chunk c = getChunk(x, y, z);
        if (c != null) {
            c.set(lx(x), ly(y), lz(z), id);
            c.markDirty();
            // If on chunk edge, mark neighbor dirty
            if (lx(x) == 0) markNeighborChunkDirty(x - 1, y, z);
            if (lx(x) == chunkSize - 1) markNeighborChunkDirty(x + 1, y, z);
            if (ly(y) == 0) markNeighborChunkDirty(x, y - 1, z);
            if (ly(y) == chunkSize - 1) markNeighborChunkDirty(x, y + 1, z);
            if (lz(z) == 0) markNeighborChunkDirty(x, y, z - 1);
            if (lz(z) == chunkSize - 1) markNeighborChunkDirty(x, y, z + 1);
        }
    }

    private void markNeighborChunkDirty(int x, int y, int z) {
        Chunk n = getChunk(x, y, z);
        if (n != null) n.markDirty();
    }

    public boolean breakAt(int x, int y, int z) {
        if (!inBounds(x, y, z)) return false;
        setBlock(x, y, z, VoxelPalette.AIR_ID);
        return true;
    }

    public Node getNode() {
        return node;
    }


    public int getTopSolidY(int x, int z) {
        if (x < 0 || z < 0 || x >= sizeX || z >= sizeZ) return -1;
        for (int y = sizeY - 1; y >= 0; y--) {
            if (palette.get(getBlock(x, y, z)).isSolid()) return y;
        }
        return -1;
    }

    public Vector3f getRecommendedSpawn() {
        int cx = sizeX / 2;
        int cz = sizeZ / 2;
        int ty = getTopSolidY(cx, cz);
        if (ty < 0) ty = groundHeight;
        return new Vector3f(cx + 0.5f, ty + 3.0f, cz + 0.5f);
    }

    private void initMaterials() {
        // Single material for STONE blocks
        Texture2D tex = ProcTextures.checker(128, 4, ColorRGBA.Gray, ColorRGBA.DarkGray);
        materials.put(VoxelPalette.STONE_ID, makeLitTex(tex, 0.08f, 16f));
    }

    private Material makeLitTex(Texture2D tex, float spec, float shininess) {
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setTexture("DiffuseMap", tex);
        m.setBoolean("UseMaterialColors", true);
        m.setColor("Diffuse", ColorRGBA.White);
        m.setColor("Specular", ColorRGBA.White.mult(spec));
        m.setFloat("Shininess", shininess);
        applyRenderFlags(m);
        return m;
    }

    private void applyRenderFlags(Material m) {
        m.getAdditionalRenderState().setFaceCullMode(culling ? RenderState.FaceCullMode.Back : RenderState.FaceCullMode.Off);
        m.getAdditionalRenderState().setWireframe(wireframe);
    }

    public void buildMeshes() {
        node.detachAllChildren();
        for (int cx = 0; cx < chunkCountX; cx++) {
            for (int cy = 0; cy < chunkCountY; cy++) {
                for (int cz = 0; cz < chunkCountZ; cz++) {
                    Chunk chunk = chunks[cx][cy][cz];
                    chunk.buildMesh(assetManager, palette);
                    node.attachChild(chunk.getNode());
                }
            }
        }
    }

    public void buildPhysics(PhysicsSpace space) {
        // Build per-chunk static rigid bodies instead of a single world body
        if (space == null) return;
        for (int cx = 0; cx < chunkCountX; cx++) {
            for (int cy = 0; cy < chunkCountY; cy++) {
                for (int cz = 0; cz < chunkCountZ; cz++) {
                    Chunk chunk = chunks[cx][cy][cz];
                    chunk.updatePhysics(space);
                }
            }
        }
    }

    public Optional<Hit> pickFirstSolid(Camera cam, float maxDistance) {
        Vector3f origin = cam.getLocation();
        Vector3f dir = cam.getDirection().normalize();

        int x = (int) Math.floor(origin.x);
        int y = (int) Math.floor(origin.y);
        int z = (int) Math.floor(origin.z);

        float tMaxX, tMaxY, tMaxZ;
        float tDeltaX, tDeltaY, tDeltaZ;
        int stepX = dir.x > 0 ? 1 : -1;
        int stepY = dir.y > 0 ? 1 : -1;
        int stepZ = dir.z > 0 ? 1 : -1;

        float nextVoxelBoundaryX = x + (stepX > 0 ? 1 : 0);
        float nextVoxelBoundaryY = y + (stepY > 0 ? 1 : 0);
        float nextVoxelBoundaryZ = z + (stepZ > 0 ? 1 : 0);

        tMaxX = (dir.x != 0) ? (nextVoxelBoundaryX - origin.x) / dir.x : Float.POSITIVE_INFINITY;
        tMaxY = (dir.y != 0) ? (nextVoxelBoundaryY - origin.y) / dir.y : Float.POSITIVE_INFINITY;
        tMaxZ = (dir.z != 0) ? (nextVoxelBoundaryZ - origin.z) / dir.z : Float.POSITIVE_INFINITY;

        tDeltaX = (dir.x != 0) ? stepX / dir.x : Float.POSITIVE_INFINITY;
        tDeltaY = (dir.y != 0) ? stepY / dir.y : Float.POSITIVE_INFINITY;
        tDeltaZ = (dir.z != 0) ? stepZ / dir.z : Float.POSITIVE_INFINITY;

        float t = 0f;
        // starting inside a solid block
        if (inBounds(x, y, z) && isSolid(x, y, z)) {
            return Optional.of(new Hit(new Vector3i(x, y, z), new Vector3f(0, 0, 0), 0f));
        }

        Vector3f lastNormal = new Vector3f(0, 0, 0);

        while (t <= maxDistance) {
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    x += stepX;
                    t = tMaxX;
                    tMaxX += tDeltaX;
                    lastNormal.set(-stepX, 0, 0);
                } else {
                    z += stepZ;
                    t = tMaxZ;
                    tMaxZ += tDeltaZ;
                    lastNormal.set(0, 0, -stepZ);
                }
            } else {
                if (tMaxY < tMaxZ) {
                    y += stepY;
                    t = tMaxY;
                    tMaxY += tDeltaY;
                    lastNormal.set(0, -stepY, 0);
                } else {
                    z += stepZ;
                    t = tMaxZ;
                    tMaxZ += tDeltaZ;
                    lastNormal.set(0, 0, -stepZ);
                }
            }

            if (!inBounds(x, y, z)) {
                if (t > maxDistance) break;
                continue;
            }
            if (isSolid(x, y, z)) {
                return Optional.of(new Hit(new Vector3i(x, y, z), lastNormal.clone(), t));
            }
        }
        return Optional.empty();
    }

    private boolean isSolid(int x, int y, int z) {
        if (!inBounds(x, y, z)) return false;
        return palette.get(getBlock(x, y, z)).isSolid();
    }

    private boolean inBounds(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ;
    }

    public void setLit(boolean lit) {
        if (this.lit == lit) return;
        this.lit = lit;
        for (var e : geoms.entrySet()) {
            Geometry g = e.getValue();
            var oldMat = g.getMaterial();
            com.jme3.texture.Texture tex = oldMat.getTextureParam("DiffuseMap") != null
                    ? oldMat.getTextureParam("DiffuseMap").getTextureValue()
                    : (oldMat.getTextureParam("ColorMap") != null ? oldMat.getTextureParam("ColorMap").getTextureValue() : null);
            Material newMat;
            if (this.lit) {
                newMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                if (tex != null) newMat.setTexture("DiffuseMap", tex);
                newMat.setBoolean("UseMaterialColors", true);
                newMat.setColor("Diffuse", ColorRGBA.White);
                newMat.setColor("Specular", ColorRGBA.White.mult(0.08f));
                newMat.setFloat("Shininess", 16f);
            } else {
                newMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                if (tex != null) newMat.setTexture("ColorMap", tex);
            }
            applyRenderFlags(newMat);
            g.setMaterial(newMat);
        }
    }

    public void setWireframe(boolean wireframe) {
        if (this.wireframe == wireframe) return;
        this.wireframe = wireframe;

        for (Geometry g : geoms.values()) applyRenderFlags(g.getMaterial());
    }

    public void setCulling(boolean culling) {
        if (this.culling == culling) return;
        this.culling = culling;
        for (Geometry g : geoms.values()) applyRenderFlags(g.getMaterial());
    }

    public boolean isLit() {
        return lit;
    }

    public boolean isWireframe() {
        return wireframe;
    }

    public boolean isCulling() {
        return culling;
    }

    public void toggleRenderDebug() {
        System.out.println("Toggled render debug");
        setLit(!isLit());
        setWireframe(!isWireframe());
        setCulling(!isCulling());
    }

    public int getGroundHeight() {
        return groundHeight;
    }

    public VoxelPalette getPalette() {
        return palette;
    }

    /**
     * Rebuilds meshes only for dirty chunks. Call this once per frame in your update loop.
     */
    public void rebuildDirtyChunks(PhysicsSpace physicsSpace) {
        int rebuilt = 0;
        for (int cx = 0; cx < chunkCountX; cx++) {
            for (int cy = 0; cy < chunkCountY; cy++) {
                for (int cz = 0; cz < chunkCountZ; cz++) {
                    Chunk chunk = chunks[cx][cy][cz];
                    if (chunk.isDirty()) {
                        System.out.println("Rebuilding chunk: " + cx + "," + cy + "," + cz);
                        chunk.buildMesh(assetManager, palette);
                        chunk.updatePhysics(physicsSpace);
                        chunk.clearDirty();
                        rebuilt++;
                    }
                }
            }
        }
        if (rebuilt > 0) System.out.println("Chunks rebuilt this frame: " + rebuilt);
        if (rebuilt > 0 && physicsSpace != null) {
            physicsSpace.update(0); // Force physics space to process changes
            System.out.println("Physics space forced update after chunk physics changes.");
        }
    }

    /**
     * Clears the dirty flag on all chunks. Call after initial buildMeshes().
     */
    public void clearAllDirtyFlags() {
        for (int cx = 0; cx < chunkCountX; cx++)
            for (int cy = 0; cy < chunkCountY; cy++)
                for (int cz = 0; cz < chunkCountZ; cz++)
                    chunks[cx][cy][cz].clearDirty();
    }

    // simple int3
    public static class Vector3i {
        public final int x, y, z;

        public Vector3i(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3i(Vector3f vec3f) {
            this.x = (int) vec3f.x;
            this.y = (int) vec3f.y;
            this.z = (int) vec3f.z;
        }
    }

    public void generateLayers() {


        FastNoiseLite noise = new FastNoiseLite(WORLD_SEED);
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        noise.SetFrequency(0.015f);  // controla o nível de detalhe

        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeZ; z++) {

                // Altura gerada por ruído
                float n = noise.GetNoise(x, z);          // -1 a 1
                float normalized = (n + 1f) * 0.5f;      // 0 a 1
                int height = (int) (normalized * 20) + 20; // altura base (20–60)

                if (height >= sizeY) height = sizeY - 2;

                // 1 — camada de pedra (fundos)
                for (int y = 0; y < height - 4; y++) {
                    setBlock(x, y, z, VoxelPalette.STONE_ID);
                }

                // 2 — camadas de dirt
                for (int y = height - 4; y < height; y++) {
                    if (y >= 0)
                        setBlock(x, y, z, VoxelPalette.DIRT_ID);
                }

                // 3 — superfície
                setBlock(x, height, z, VoxelPalette.SAND_ID);
                setBlock(x, height - 4, z, VoxelPalette.DIRT_ID);

                // 4 — o resto ar
                for (int y = height + 1; y < sizeY; y++) {
                    setBlock(x, y, z, VoxelPalette.AIR_ID);

                    // 🌳 colocar uma árvore aleatória
                if (Math.random() < 0.02) {
                    placeTree(x, z);
                }
            }



            // descobre altura do terreno neste x,z
            int baseY = getTopSolidY(x, z);
            if (baseY < 0) return; // fora do mapa ou erro

            // não põe árvores em areia
            byte topBlock = getBlock(x, baseY, z);
            if (topBlock == VoxelPalette.SAND_ID) return;

            // altura do tronco
            int trunkHeight = 4 + (int) (Math.random() * 3); // 4 a 6 blocos

            // tronco
            for (int y = 1; y <= trunkHeight; y++) {
                setBlock(x, baseY + y, z, VoxelPalette.WOOD_ID);
            }

            int topY = baseY + trunkHeight;

            // copa da árvore (folhas)
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    for (int dy = -1; dy <= 2; dy++) {

                        // dá forma arredondada
                        if (Math.abs(dx) + Math.abs(dz) + Math.abs(dy) > 4) continue;

                        int fx = x + dx;
                        int fy = topY + dy;
                        int fz = z + dz;

                        if (inBounds(fx, fy, fz) && getBlock(fx, fy, fz) == VoxelPalette.AIR_ID) {
                            setBlock(fx, fy, fz, VoxelPalette.LEAVES_ID);
                        }
                    }
                }
            }

}