package jogo.Interfaces;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import jogo.gameobject.item.Item;

public class CraftingView {

    private final AssetManager assetManager;
    private final Node windowNode;
    private BitmapFont font;

    // Configurações 2x2 + Resultado
    private static final int GRID_COLS = 2;
    private static final int GRID_ROWS = 2;
    private static final float SLOT_SIZE = 62f;
    private static final float PADDING = 10f; // Aumentar padding para espaço de resultado

    // Referências para manipulação (Modelo: Arrays/Coleções)
    private Node[][] inputSlotParents; // 2x2
    private Node resultSlotParent;     // 1 slot
    private Geometry craftButton;      // Botão para o HudAppState interagir

    // Cores (Reutilizar as da InventoryView para consistência visual)
    private static final ColorRGBA BG_COLOR = new ColorRGBA(0.847f, 0.302f, 0.471f, 0.8f);
    private static final ColorRGBA SLOT_COLOR = new ColorRGBA(0.55f, 0.55f, 0.55f, 1.0f);
    private static final ColorRGBA RESULT_SLOT_COLOR = new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f); // Cinzento claro para resultado

    public CraftingView(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.windowNode = new Node("CraftingView");
        this.inputSlotParents = new Node[GRID_ROWS][GRID_COLS];

        initializeResources();
        buildLayout();
    }

    private void initializeResources() {
        this.font = assetManager.loadFont("Interface/Fonts/Default.fnt");
    }

    public Node getNode() {
        return windowNode;
    }

    // Permite ao HudAppState saber onde o botão de craft está
    public Geometry getCraftButtonGeometry() {
        return craftButton;
    }

    private void buildLayout() {

        // --- CÁLCULO DE DIMENSÕES ---
        float gridWidth = GRID_COLS * SLOT_SIZE + (GRID_COLS - 1) * PADDING;
        float resultOffset = SLOT_SIZE + 4 * PADDING; // Espaço extra para o slot de resultado

        // Largura total: Grelha + Espaço + Slot Resultado
        float bgWidth = gridWidth + resultOffset;
        float bgHeight = GRID_ROWS * SLOT_SIZE + (GRID_ROWS - 1) * PADDING + 40f; // Altura base

        // --- 1. FUNDO (Layout da InventoryView) ---
        Quad bgMesh = new Quad(bgWidth, bgHeight);
        Geometry bgGeo = new Geometry("CraftingBackground", bgMesh);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", BG_COLOR);
        bgMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        bgGeo.setMaterial(bgMat);
        bgGeo.setLocalTranslation(-bgWidth / 2f, -bgHeight / 2f, -1);
        windowNode.attachChild(bgGeo);

        // --- 2. GRELHA 2x2 (Input) ---
        float gridStartX = -bgWidth / 2f + PADDING;
        float gridStartY = -bgHeight / 2f + 10f; // 10px margem inferior

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                // Fundo do Slot
                Quad slotQuad = new Quad(SLOT_SIZE, SLOT_SIZE);
                Geometry slotGeo = new Geometry("InputSlot", slotQuad);
                Material slotMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                slotMat.setColor("Color", SLOT_COLOR);
                slotGeo.setMaterial(slotMat);

                float x = gridStartX + col * (SLOT_SIZE + PADDING);
                float y = gridStartY + row * (SLOT_SIZE + PADDING);
                slotGeo.setLocalTranslation(x, y, 0);
                windowNode.attachChild(slotGeo);

                // Node contentor para o ícone
                Node iconParent = new Node("InputIconParent");
                iconParent.setLocalTranslation(x + SLOT_SIZE / 2f, y + SLOT_SIZE / 2f, 2);
                windowNode.attachChild(iconParent);
                inputSlotParents[row][col] = iconParent;
            }
        }

        // --- 3. SLOT DE RESULTADO (Único, maior) ---
        float resultX = gridStartX + gridWidth + PADDING * 2;
        float resultY = gridStartY + (bgHeight - 40f) / 2f - SLOT_SIZE / 2f; // Centrado verticalmente

        Quad resultQuad = new Quad(SLOT_SIZE, SLOT_SIZE);
        Geometry resultGeo = new Geometry("ResultSlot", resultQuad);
        Material resultMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        resultMat.setColor("Color", RESULT_SLOT_COLOR);
        resultGeo.setMaterial(resultMat);
        resultGeo.setLocalTranslation(resultX, resultY, 0);
        windowNode.attachChild(resultGeo);

        // Node contentor para o ícone do resultado
        resultSlotParent = new Node("ResultIconParent");
        resultSlotParent.setLocalTranslation(resultX + SLOT_SIZE / 2f, resultY + SLOT_SIZE / 2f, 2);
        windowNode.attachChild(resultSlotParent);

        // Adicionar uma seta (opcional)
        // ...

        // --- 4. BOTÃO "CRAFT" (DO SLOT DE RESULTADO) ---
        // Este botão é para efetivar o craft (separado do botão da InventoryView)
        float btnWidth = SLOT_SIZE + 10f;
        float btnHeight = 30f;
        float btnX = resultX - 5f;
        float btnY = gridStartY + 5f; // Perto do canto inferior do grid

        Quad craftBtnMesh = new Quad(btnWidth, btnHeight);
        craftButton = new Geometry("CraftAction", craftBtnMesh); // Salvamos a referência
        Material craftBtnMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        craftBtnMat.setColor("Color", new ColorRGBA(0f, 0.7f, 0f, 1f)); // Verde
        craftButton.setMaterial(craftBtnMat);
        craftButton.setLocalTranslation(btnX, btnY, 1);
        windowNode.attachChild(craftButton);

        BitmapText btnText = new BitmapText(font, false);
        btnText.setText("CRAFT");
        btnText.setColor(ColorRGBA.White);
        btnText.setSize(font.getCharSet().getRenderedSize() * 0.8f);
        btnText.setLocalTranslation(btnX + (btnWidth - btnText.getLineWidth()) / 2f, btnY + 20f, 2);
        windowNode.attachChild(btnText);
    }

    // NOVO MÉTODO (Será chamado pelo HudAppState para atualizar a View)
    public void updateResult(Item resultItem) {
        // Lógica de polimorfismo: o item sabe como se desenhar
        resultSlotParent.detachAllChildren();
        if (resultItem != null) {
            Geometry iconGeo = resultItem.getIconGeometry(assetManager);
            resultSlotParent.attachChild(iconGeo);
            // Aqui adicionarias a contagem, se fosse > 1
        }
    }
}