package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import jogo.engine.GameRegistry;
import jogo.engine.RenderIndex;
import jogo.framework.math.Vec3;
import jogo.gameobject.GameObject;
import jogo.gameobject.character.Player;
import jogo.gameobject.character.npc.ally.Ally;
import jogo.gameobject.item.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RenderAppState extends BaseAppState {

    private final Node rootNode;
    private final AssetManager assetManager;
    private final GameRegistry registry;
    private final RenderIndex renderIndex;

    private Node gameNode;
    private final Map<GameObject, Spatial> instances = new HashMap<>();

    public RenderAppState(Node rootNode, AssetManager assetManager, GameRegistry registry, RenderIndex renderIndex) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.registry = registry;
        this.renderIndex = renderIndex;
    }

    @Override
    protected void initialize(Application app) {
        gameNode = new Node("GameObjects");
        rootNode.attachChild(gameNode);
    }

    @Override
    public void update(float tpf) {
        // Ensure each registered object has a spatial and sync position
        var current = registry.getAll();
        Set<GameObject> alive = new HashSet<>(current);

        for (GameObject obj : current) {
            Spatial s = instances.get(obj);
            if (s == null) {
                s = createSpatialFor(obj);
                if (s != null) {
                    gameNode.attachChild(s);
                    instances.put(obj, s);
                    renderIndex.register(s, obj);
                }
            }
            if (s != null) {
                Vec3 p = obj.getPosition();
                s.setLocalTranslation(new Vector3f(p.x, p.y, p.z));
            }
        }

        // Cleanup: remove spatials for objects no longer in registry
        var it = instances.entrySet().iterator();
        while (it.hasNext()) {
            var e = it.next();
            if (!alive.contains(e.getKey())) {
                Spatial s = e.getValue();
                renderIndex.unregister(s);
                if (s.getParent() != null) s.removeFromParent();
                it.remove();
            }
        }
    }

    private Spatial createSpatialFor(GameObject obj) {
        if (obj instanceof Player) {
            Geometry g = new Geometry(obj.getName(), new Cylinder(16, 16, 0.35f, 1.4f, true));
            g.setMaterial(colored(ColorRGBA.Green));
            return g;
        } else if (obj instanceof Item) {
            Geometry g = new Geometry(obj.getName(), new Box(0.3f, 0.3f, 0.3f));
            g.setMaterial(colored(ColorRGBA.Yellow));
            return g;
        }
        // --- ADICIONA ESTE BLOCO ---
        else if (obj instanceof Ally) {
            return createHeartVisual(obj.getName());
        }
        // ---------------------------
        return null;
    }

    // 2. ADICIONAR ESTE MÉTODO NOVO NA CLASSE
    private Spatial createHeartVisual(String name) {
        Node heartNode = new Node(name);
        float size = 0.15f; // Tamanho de cada "pixel" (voxel) do coração

        // Material cor-de-rosa (#ff69b4 é HotPink)
        // Convertendo hex para RGB aproximado: R=1.0, G=0.41, B=0.71
        Material pinkMat = colored(new ColorRGBA(1.0f, 0.41f, 0.71f, 1.0f));

        // Mapa de bits simples para um coração 5x5 (1 = cubo, 0 = vazio)
        // Desenhando de cima para baixo
        int[][] shape = {
                {1, 0, 1}, // Topo (as duas bossas)
                {1, 1, 1}, // Meio superior
                {1, 1, 1}, // Meio
                {0, 1, 0}  // Ponta
        };

        // Vamos expandir um pouco para ficar mais bonito (estilo 7x6)
        int[][] pixelArt = {
                {0, 1, 1, 0, 1, 1, 0},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {0, 1, 1, 1, 1, 1, 0},
                {0, 0, 1, 1, 1, 0, 0},
                {0, 0, 0, 1, 0, 0, 0}
        };

        // Iterar sobre a matriz e criar cubos
        for (int y = 0; y < pixelArt.length; y++) {
            for (int x = 0; x < pixelArt[y].length; x++) {
                if (pixelArt[y][x] == 1) {
                    Geometry box = new Geometry("pixel", new Box(size, size, size));
                    box.setMaterial(pinkMat);

                    // Calcular posição relativa:
                    // Inverter Y para desenhar de cima para baixo corretamente
                    // Centralizar X (subtrair metade da largura)
                    float xPos = (x - 3) * (size * 2);
                    float yPos = (3 - y) * (size * 2);

                    box.setLocalTranslation(xPos, yPos + 1.0f, 0); // +1.0f para flutuar acima do chão
                    heartNode.attachChild(box);
                }
            }
        }

        // Opcional: Adicionar uma animação simples de escala ou rotação num Control,
        // mas por agora deixamos estático conforme pedido.
        return heartNode;
    }

    private Material colored(ColorRGBA color) {
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors", true);
        m.setColor("Diffuse", color.clone());
        m.setColor("Specular", ColorRGBA.White.mult(0.1f));
        m.setFloat("Shininess", 8f);
        return m;
    }

    @Override
    protected void cleanup(Application app) {
        if (gameNode != null) {
            gameNode.removeFromParent();
            gameNode = null;
        }
        instances.clear();
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}
