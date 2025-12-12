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
import jogo.gameobject.inventory.Inventory;
import jogo.gameobject.item.Item;

public class InventoryView {

    private final AssetManager assetManager;
    private final Node windowNode;
    private BitmapFont font;

    private static final int COLS = 9;
    private static final int ROWS = 3;
    private static final float SLOT_SIZE = 62f;
    private static final float PADDING = 4f;

    private BitmapText[][] slotCounts;
    private Node[][] slotIconParents;
    private Geometry craftButton;          // <- NOVO: campo para o botão

    private static final ColorRGBA BG_COLOR   = new ColorRGBA(0.847f, 0.302f, 0.471f, 0.8f);
    private static final ColorRGBA SLOT_COLOR = new ColorRGBA(0.55f, 0.55f, 0.55f, 1.0f);

    public InventoryView(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.windowNode = new Node("InventoryView");

        this.slotCounts = new BitmapText[ROWS][COLS];
        this.slotIconParents = new Node[ROWS][COLS];

        initializeResources();
        buildLayout();
    }

    private void initializeResources() {
        this.font = assetManager.loadFont("Interface/Fonts/Default.fnt");
    }

    public Node getNode() {
        return windowNode;
    }

    // NOVO: para o HudAppState detectar cliques
    public Geometry getCraftButtonGeometry() {
        return craftButton;
    }

    private void buildLayout() {
        // --- CÁLCULO DE DIMENSÕES ---
        float gridWidth = COLS * SLOT_SIZE + (COLS - 1) * PADDING;
        float gridHeight = ROWS * SLOT_SIZE + (ROWS - 1) * PADDING;

        float paddingX = 40f;
        float paddingY = 60f;

        float bgWidth = gridWidth + paddingX;
        float bgHeight = gridHeight + paddingY;

        // --- 1. FUNDO ---
        Quad bgMesh = new Quad(bgWidth, bgHeight);
        Geometry bgGeo = new Geometry("InvBackground", bgMesh);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", BG_COLOR);
        bgMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        bgGeo.setMaterial(bgMat);
        bgGeo.setLocalTranslation(-bgWidth / 2f, -bgHeight / 2f, -1);
        windowNode.attachChild(bgGeo);

        // --- 2. TÍTULO ---
        String titleText = "INVENTÁRIO";
        float titleSize = font.getCharSet().getRenderedSize() * 1.5f;
        float shadowOffset = 2f;

        BitmapText title = new BitmapText(font, false);
        title.setText(titleText);
        title.setSize(titleSize);
        float titleWidth = title.getLineWidth();

        float titleY = (bgHeight / 2f) - 10f;
        float titleX = -titleWidth / 2f;

        BitmapText titleShadow = new BitmapText(font, false);
        titleShadow.setText(titleText);
        titleShadow.setSize(titleSize);
        titleShadow.setColor(ColorRGBA.Black);

        title.setColor(ColorRGBA.White);

        title.setLocalTranslation(titleX, titleY, 1);
        titleShadow.setLocalTranslation(titleX + shadowOffset, titleY - shadowOffset, 0);

        windowNode.attachChild(titleShadow);
        windowNode.attachChild(title);

        // --- 3. BOTÃO CRAFT NO TOPO DIREITO ---
        float btnWidth = 90f;
        float btnHeight = 28f;
        float btnMarginRight = 20f;

        float btnCenterX = (bgWidth / 2f) - btnWidth / 2f - btnMarginRight;
        float btnCenterY = titleY - title.getLineHeight() / 2f;

        Quad btnMesh = new Quad(btnWidth, btnHeight);
        craftButton = new Geometry("InventoryCraftButton", btnMesh);
        Material btnMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        btnMat.setColor("Color", new ColorRGBA(0.847f, 0.302f, 0.471f, 1.0f)); // rosa escuro
        btnMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        craftButton.setMaterial(btnMat);

        craftButton.setLocalTranslation(
                btnCenterX - bgWidth / 2f,
                btnCenterY - btnHeight / 2f,
                1
        );
        windowNode.attachChild(craftButton);

        BitmapText btnText = new BitmapText(font, false);
        btnText.setText("CRAFT");
        btnText.setColor(ColorRGBA.White);
        btnText.setSize(font.getCharSet().getRenderedSize() * 0.8f);

        float textX = (btnCenterX - bgWidth / 2f) + (btnWidth - btnText.getLineWidth()) / 2f;
        float textY = (btnCenterY - btnHeight / 2f) + (btnHeight + btnText.getLineHeight()) / 2f;
        btnText.setLocalTranslation(textX, textY, 2);
        windowNode.attachChild(btnText);

        // --- 4. GRELHA DE SLOTS ---
        float startX = -gridWidth / 2f;
        float bottomMargin = 10f;
        float startY = -bgHeight / 2f + bottomMargin;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Quad slotQuad = new Quad(SLOT_SIZE, SLOT_SIZE);
                Geometry slotGeo = new Geometry("SlotBackground", slotQuad);
                Material slotMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                slotMat.setColor("Color", SLOT_COLOR);
                slotGeo.setMaterial(slotMat);

                float x = startX + col * (SLOT_SIZE + PADDING);
                float y = startY + row * (SLOT_SIZE + PADDING);
                slotGeo.setLocalTranslation(x, y, 0);
                windowNode.attachChild(slotGeo);

                Node iconParent = new Node("IconParent_" + row + "_" + col);
                iconParent.setLocalTranslation(x + SLOT_SIZE / 2f, y + SLOT_SIZE / 2f, 2);
                windowNode.attachChild(iconParent);
                slotIconParents[row][col] = iconParent;

                BitmapText count = new BitmapText(font, false);
                count.setSize(font.getCharSet().getRenderedSize() * 0.8f);
                count.setColor(ColorRGBA.White);
                count.setLocalTranslation(x + SLOT_SIZE - 10, y + 10, 3);
                windowNode.attachChild(count);
                slotCounts[row][col] = count;
            }
        }
    }
    public void updateData(Inventory inventory ) {
            // Lógica para atualizar a visualização.
            // Exemplo (pseudocódigo):
            // 1. Limpar slots antigos
            // 2. Percorrer items em playerInv
            // 3. Criar ícones/textos para cada item e adicionar ao Node

        System.out.println("Inventário atualizado na UI (Implementar lógica visual aqui)");
    }
}



