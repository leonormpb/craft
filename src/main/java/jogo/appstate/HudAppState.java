package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import jogo.Interfaces.InventoryView;
import jogo.Interfaces.CraftingView;
import jogo.gameobject.character.Player;
import jogo.gameobject.item.Item;

public class HudAppState extends BaseAppState {

    private Node guiNode;
    private Camera guiCam;
    private InventoryView inventoryView;
    private CraftingView craftingView;

    private boolean inventoryVisible = false;

    @Override
    protected void initialize(Application app) {
        this.guiNode = ((SimpleApplication) app).getGuiNode();
        this.guiCam = app.getGuiViewPort().getCamera();

        int width = app.getCamera().getWidth();
        int height = app.getCamera().getHeight();

        float baseY = height / 2f;

        // === INVENTÁRIO À ESQUERDA ===
        this.inventoryView = new InventoryView(app.getAssetManager());
        inventoryView.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Always);
        guiNode.attachChild(inventoryView.getNode());

        // Posicionar à esquerda com margem
        float invX = width * 0.25f;
        inventoryView.getNode().setLocalTranslation(invX, baseY, 0);

        // === CRAFTING À DIREITA ===
        this.craftingView = new CraftingView(app.getAssetManager());
        craftingView.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Always);
        guiNode.attachChild(craftingView.getNode());

        // Posicionar à direita (alinhado na mesma altura)
        float craftX = width * 0.75f;
        craftingView.getNode().setLocalTranslation(craftX, baseY, 0);

        System.out.println("✓ HUD inicializado (Inventário @ " + invX + ", Crafting @ " + craftX + ", Y = " + baseY + ")");
    }


    public void toggleInventory() {
        inventoryVisible = !inventoryVisible;

        if (inventoryVisible) {
            showUI();
        } else {
            hideUI();
        }
    }

    public void toggleCrafting() {
        toggleInventory();
    }

    private void showUI() {
        inventoryView.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Never);
        craftingView.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Never);

        updateInventoryView();
        updateCraftingView();

        getApplication().getInputManager().setCursorVisible(true);

        System.out.println("📦 UI aberta (Inventário + Crafting lado a lado)");
    }

    private void hideUI() {
        inventoryView.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Always);
        craftingView.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Always);

        getApplication().getInputManager().setCursorVisible(false);

        System.out.println("📦 UI fechada");
    }

    public void updateInventoryView() {
        Player player = getPlayer();
        if (player != null) {
            inventoryView.updateData(player.getInventory());
        }
    }

    public void updateCraftingView() {
        Player player = getPlayer();
        if (player != null) {
            Item result = player.getCraftingGrid().getResult();
            craftingView.updateResult(result);
        }
    }

    /**
     * Processa clique no botão CRAFT.
     */
    public void handleLeftClick(Vector2f mousePos) {
        if (!inventoryVisible) return;

        // Fazer raycast na GUI
        Ray ray = new Ray();
        Vector3f origin = guiCam.getWorldCoordinates(mousePos, 0f);
        Vector3f direction = guiCam.getWorldCoordinates(mousePos, 1f).subtractLocal(origin).normalizeLocal();
        ray.setOrigin(origin);
        ray.setDirection(direction);

        CollisionResults results = new CollisionResults();
        craftingView.getNode().collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();

            // Verificar se clicou no botão
            if (closest.getGeometry().equals(craftingView.getCraftButtonGeometry())) {
                System.out.println("🖱️ Botão CRAFT clicado!");
                executeCraft();
            }
        }
    }

    /**
     * Executa o craft.
     */
    private void executeCraft() {
        Player player = getPlayer();
        if (player == null) return;

        Item result = player.getCraftingGrid().getResult();
        if (result != null) {
            boolean added = player.getInventory().addItem(result);

            if (added) {
                player.getCraftingGrid().consumeIngredients();

                updateInventoryView();
                updateCraftingView();

                System.out.println("✅ Craftado: " + result.getName() + " x" + result.getQuantity());
            } else {
                System.out.println("❌ Inventário cheio!");
            }
        } else {
            System.out.println("❌ Receita inválida!");
        }
    }

    private Player getPlayer() {
        PlayerAppState playerState = getState(PlayerAppState.class);
        return playerState != null ? playerState.getPlayer() : null;
    }

    public boolean isInventoryVisible() {
        return inventoryVisible;
    }

    public boolean isCraftingVisible() {
        return inventoryVisible;
    }

    @Override
    protected void cleanup(Application app) {
        if (inventoryView != null && inventoryView.getNode() != null) {
            guiNode.detachChild(inventoryView.getNode());
        }
        if (craftingView != null && craftingView.getNode() != null) {
            guiNode.detachChild(craftingView.getNode());
        }
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {
        if (inventoryVisible) {
            hideUI();
        }
    }

    @Override
    public void update(float tpf) {}
}
