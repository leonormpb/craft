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
import jogo.gameobject.character.Ally;
import jogo.gameobject.character.Player;
import jogo.voxel.VoxelWorld;

import java.util.ArrayList;
import java.util.List;

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

    // ✅ ADICIONADO: Lista de objetos do mundo (GameObjects)
    private List<GameObject> worldObjects;

    public WorldAppState(Node rootNode, AssetManager assetManager, PhysicsSpace physicsSpace, Camera cam, InputAppState input) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;
        this.cam = cam;
        this.input = input;
        this.worldObjects = new ArrayList<>();  // ✅ Inicializar a lista
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
        ambient.setColor(ColorRGBA.White.mult(0.20f));
        worldNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.35f, -1.3f, -0.25f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.85f));
        worldNode.addLight(sun);

        // Voxel world
        voxelWorld = new VoxelWorld(assetManager, 320, 32, 320);
        voxelWorld.generateLayers();
        voxelWorld.buildMeshes();
        voxelWorld.clearAllDirtyFlags();
        voxelWorld.renderFlowers();

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
        if (input != null && input.isMouseCaptured() && input.consumeBreakRequested()) {
            var pick = voxelWorld.pickFirstSolid(cam, 6f);
            pick.ifPresent(hit -> {
                VoxelWorld.Vector3i cell = hit.cell;
                if (voxelWorld.breakAt(cell.x, cell.y, cell.z)) {
                    voxelWorld.rebuildDirtyChunks(physicsSpace);
                    playerAppState.refreshPhysics();
                }
            });
        }
        if (input != null && input.consumeToggleShadingRequested()) {
            voxelWorld.toggleRenderDebug();
        }

        // ✅ Atualizar IA dos aliados
        updateAlliesAI(tpf);
    }

    @Override
    protected void cleanup(Application app) {
        if (worldNode != null) {
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
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    /**
     * Adiciona um objeto ao mundo (ex: Ally, Item, etc).
     *
     * @param obj O GameObject a adicionar
     */
    public void addGameObject(GameObject obj) {
        worldObjects.add(obj);
    }

    /**
     * Remove um objeto do mundo.
     *
     * @param obj O GameObject a remover
     */
    public void removeGameObject(GameObject obj) {
        worldObjects.remove(obj);
    }

    /**
     * Atualiza a IA de todos os aliados no mundo.
     * Chamado a cada frame a partir de update().
     *
     * @param tpf Tempo por frame
     */
    public void updateAlliesAI(float tpf) {
        // ✅ CORRIGIDO: Acessar o Player através do PlayerAppState
        // Como Player é private, precisamos de um getter ou acessar via reflexão
        // Solução melhor: adicionar um getter no PlayerAppState

        Player player = getPlayerFromAppState();

        if (player == null) {
            System.err.println("[WorldAppState] Aviso: Player não disponível para IA dos aliados");
            return;
        }

        // Atualizar cada aliado
        for (GameObject obj : worldObjects) {
            if (obj instanceof Ally) {
                Ally ally = (Ally) obj;
                // Executar a estratégia passando o jogador
                ally.executeStrategy(player, tpf);
            }
        }
    }


    /**
     * Spawna um aliado próximo do jogador.
     *
     * @param name   Nome do aliado
     * @param health Saúde inicial
     * @param speed  Velocidade
     */
    public void spawnAlly(String name, float health, float speed) {
        // Obter posição do jogador (se disponível)
        Vector3f spawnPos = spawnPosition.clone();

        // Adicionar offset aleatório
        float offsetX = (float) (Math.random() * 4 - 2);
        float offsetZ = (float) (Math.random() * 4 - 2);
        spawnPos.addLocal(offsetX, 0, offsetZ);

        // Criar aliado com FollowStrategy
        jogo.gameobject.npc.AIStrategy strategy = new jogo.gameobject.npc.FollowStrategy();
        Ally newAlly = new Ally(name, health, speed, strategy);

        // Adicionar ao mundo
        addGameObject(newAlly);
        System.out.println("[WorldAppState] Aliado '" + name + "' spawned em: " + spawnPos);
    }

    private Player getPlayerFromAppState() {
        if (playerAppState == null) {
            return null;
        }

        // ✅ OPÇÃO 1: Se você adicionar um getter no PlayerAppState
        // return playerAppState.getPlayer();

        // ✅ OPÇÃO 2: Usar reflexão (sem modificar PlayerAppState)
        try {
            java.lang.reflect.Field playerField = PlayerAppState.class.getDeclaredField("player");
            playerField.setAccessible(true);
            return (Player) playerField.get(playerAppState);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("[WorldAppState] Erro ao obter Player via reflexão: " + e.getMessage());
            return null;
        }
    }
}
