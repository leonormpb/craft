package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import jogo.gameobject.inventory.Inventory;
import jogo.Interfaces.InventoryView; //
import jogo.Interfaces.CraftingView;

public class HudAppState extends BaseAppState {

    private final Node guiNode;
    private final AssetManager assetManager;
    private BitmapText crosshair;

    // Referência para a View
    private InventoryView inventoryView;
    private boolean isInventoryVisible = false;

    // Crafting view
    private CraftingView craftingView;
    private boolean isCraftingVisible = false;

    public HudAppState(Node guiNode, AssetManager assetManager) {
        this.guiNode = guiNode;
        this.assetManager = assetManager;
    }

    @Override
    protected void initialize(Application app) {
        // 1. Inicializar Mira
        initCrosshair(app);

        // 2. Inicializar a View do Inventário
        inventoryView = new InventoryView(assetManager);

        // 3. CraftingView
        craftingView = new CraftingView(assetManager);
    }

    public void toggleInventory(Inventory playerInv) {
        isInventoryVisible = !isInventoryVisible;

        // Obtemos a referência para a aplicação de forma correta
        SimpleApplication sapp = (SimpleApplication) getApplication();

        if (isInventoryVisible) {
            // Atualizar dados antes de mostrar
            inventoryView.updateData(playerInv);

            // Posicionar no centro do ecrã atual
            float x = sapp.getCamera().getWidth() / 2f;
            float y = sapp.getCamera().getHeight() / 2f;
            inventoryView.getNode().setLocalTranslation(x, y, 0);

            guiNode.attachChild(inventoryView.getNode());

            // CORREÇÃO: Usar getApplication() em vez de 'app'
            getApplication().getInputManager().setCursorVisible(true);

            // Opcional: Esconder mira
            guiNode.detachChild(crosshair);

        } else {
            guiNode.detachChild(inventoryView.getNode());

            // CORREÇÃO: Usar getApplication() em vez de 'app'
            getApplication().getInputManager().setCursorVisible(false);

            // Mostrar mira novamente
            guiNode.attachChild(crosshair);
        }
    }

    private void initCrosshair(Application app) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        crosshair = new BitmapText(font, false);
        crosshair.setText("+");
        crosshair.setSize(font.getCharSet().getRenderedSize() * 2f);

        SimpleApplication sapp = (SimpleApplication) app;
        float x = (sapp.getCamera().getWidth() - crosshair.getLineWidth()) / 2f;
        float y = (sapp.getCamera().getHeight() + crosshair.getLineHeight()) / 2f;
        crosshair.setLocalTranslation(x, y, 0);

        guiNode.attachChild(crosshair);
    }

    @Override public void update(float tpf) { }
    @Override
    protected void cleanup(Application app) {
        guiNode.detachChild(crosshair);
        guiNode.detachChild(inventoryView.getNode());
    }

    @Override protected void onEnable() { }
    @Override protected void onDisable() { }
}