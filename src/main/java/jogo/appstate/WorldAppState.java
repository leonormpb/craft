package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import jogo.gameobject.GameObject;
import jogo.voxel.VoxelWorld;
import jogo.gameobject.item.Item;
import jogo.gameobject.item.ItemFactory;


import jogo.gameobject.character.npc.ally.Ally;
import jogo.gameobject.character.Player;

public class WorldAppState extends BaseAppState {

    private final Node rootNode;
    private final AssetManager assetManager;
    private final PhysicsSpace physicsSpace;
    private final Camera cam;
    private final InputAppState input;
    private PlayerAppState playerAppState;

    // world root for easy cleanup
    private Node worldNode;
    private VoxelWorld voxelWorld;
    private com.jme3.math.Vector3f spawnPosition;

    public WorldAppState(Node rootNode, AssetManager assetManager, PhysicsSpace physicsSpace, Camera cam, InputAppState input) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;
        this.cam = cam;
        this.input = input;
    }

    public void registerPlayerAppState(PlayerAppState playerAppState) {
        this.playerAppState = playerAppState;
    }

    @Override
    protected void initialize(Application app) {
        worldNode = new Node("World");
        rootNode.attachChild(worldNode);

        // Lighting
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.20f)); // slightly increased ambient
        worldNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.35f, -1.3f, -0.25f).normalizeLocal()); // more top-down to reduce harsh contrast
        sun.setColor(ColorRGBA.White.mult(0.85f)); // slightly dimmer sun
        worldNode.addLight(sun);

        // Voxel world 16x16x16 (reduced size for simplicity)
        voxelWorld = new VoxelWorld(assetManager, 320, 32, 320);
        voxelWorld.generateLayers();
        voxelWorld.buildMeshes();
        voxelWorld.clearAllDirtyFlags();

        voxelWorld.renderFlowers();  // ✅ ADICIONA ISTO - Renderiza as flores DEPOIS

        worldNode.attachChild(voxelWorld.getNode());
        voxelWorld.buildPhysics(physicsSpace);

        // compute recommended spawn
        spawnPosition = voxelWorld.getRecommendedSpawn();
    }

    public com.jme3.math.Vector3f getRecommendedSpawnPosition() {
        return spawnPosition != null ? spawnPosition.clone() : new com.jme3.math.Vector3f(25.5f, 12f, 25.5f);
    }

    public VoxelWorld getVoxelWorld() {
        return voxelWorld;
    }

    @Override
    public void update(float tpf) {
        // Partir blocos
        if (input != null && input.isMouseCaptured() && input.consumeBreakRequested()) {
            var pick = voxelWorld.pickFirstSolid(cam, 6f);
            pick.ifPresent(hit -> {
                VoxelWorld.Vector3i cell = hit.cell;

                // Obter tipo do bloco ANTES de partir
                int blockId = voxelWorld.getBlock(cell.x, cell.y, cell.z);

                // Partir o bloco
                if (voxelWorld.breakAt(cell.x, cell.y, cell.z)) {
                    voxelWorld.rebuildDirtyChunks(physicsSpace);
                    playerAppState.refreshPhysics();

                    // Criar item e adicionar ao inventário
                    addItemToPlayerInventory(blockId);
                }
            });
        }

        // Abrir/fechar inventário
        if (input != null && input.consumeShowInventoryRequested()) {
            HudAppState hudState = getState(HudAppState.class);
            if (hudState != null) {
                hudState.toggleInventory();
            }
        }

        // Toggle shading
        if (input != null && input.consumeToggleShadingRequested()) {
            voxelWorld.toggleRenderDebug();
        }

        // Atualizar IA dos aliados (já existe no teu código)
         // updateAlliesAI(tpf);
    }

    /**
     * Atualiza a IA de todos os aliados no mundo.
     */


    /**
     * Adiciona item ao inventário do jogador quando bloco é partido.
     * @param blockId ID do bloco que foi partido
     */
    private void addItemToPlayerInventory(int blockId) {
        if (blockId == 0) {
            return; // Air não dropa nada
        }

        // Criar item baseado no bloco
        jogo.gameobject.item.Item droppedItem = jogo.gameobject.item.ItemFactory.createFromBlockId(blockId, 1);

        if (droppedItem != null) {
            // Obter player
            Player player = getPlayerFromAppState();

            if (player != null) {
                // Adicionar ao inventário
                boolean added = player.getInventory().addItem(droppedItem);

                if (added) {
                    System.out.println("✓ Coletado: " + droppedItem.getName() + " (ID: " + blockId + ")");

                    // Atualizar UI se inventário estiver aberto
                    HudAppState hudState = getState(HudAppState.class);
                    if (hudState != null && hudState.isInventoryVisible()) {
                        hudState.updateInventoryView();
                    }
                } else {
                    System.out.println("✗ Inventário cheio! Item perdido: " + droppedItem.getName());
                }
            } else {
                System.err.println("⚠️ Player não encontrado para adicionar item");
            }
        }
    }






    @Override
    protected void cleanup(Application app) {
        if (worldNode != null) {
            // Remove all physics controls under worldNode
            worldNode.depthFirstTraversal(spatial -> {
                RigidBodyControl rbc = spatial.getControl(RigidBodyControl.class);
                if (rbc != null) {
                    physicsSpace.remove(rbc);
                    spatial.removeControl(rbc);
                }
            });
            worldNode.removeFromParent();
            worldNode = null;
        }
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }

    public Ally spawnAlly(Player targetPlayer) {
        if (targetPlayer == null || voxelWorld == null) return null;

        // 1. Posição Alvo (2 blocos ao lado do jogador)
        float targetX = targetPlayer.getPosition().x + 2.0f;
        float targetZ = targetPlayer.getPosition().z + 2.0f;

        // 2. Detetar o chão (CRUCIAL PARA NÃO CAIR NO INFINITO)
        // O cast para (int) é necessário porque o mundo é voxel (inteiros)
        int groundY = voxelWorld.getTopSolidY((int)targetX, (int)targetZ);

        float finalY;
        if (groundY == -1) {
            // Chunk não carregado ou buraco: usa a altura do player como fallback
            finalY = targetPlayer.getPosition().y;
        } else {
            // Nasce 2 blocos acima do chão sólido
            finalY = groundY + 2.0f;
        }

        // 3. Criar e posicionar
        Ally ally = new Ally("AllyHeart", targetPlayer);
        ally.setPosition(new jogo.framework.math.Vec3(targetX, finalY, targetZ));

        System.out.println("Ally criado em: " + targetX + ", " + finalY + ", " + targetZ);

        return ally;
    }

    /**
     * Obtém o Player do PlayerAppState.
     */
    private Player getPlayerFromAppState() {
        if (playerAppState != null) {
            return playerAppState.getPlayer();
        }
        return null;
    }

}
