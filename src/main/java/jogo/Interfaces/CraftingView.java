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

    private static final float SLOT_SIZE = 62f;
    private static final float PADDING = 10f;

    private Node[][] inputSlotParents; // 2x2
    private Node resultSlotParent;
    private Geometry craftButton;
    private BitmapText resultTooltip; // Nome do item resultado

    // Cores
    private static final ColorRGBA BG_COLOR = new ColorRGBA(0.847f, 0.302f, 0.471f, 0.9f);
    private static final ColorRGBA SLOT_COLOR = new ColorRGBA(0.55f, 0.55f, 0.55f, 1.0f);
    private static final ColorRGBA RESULT_SLOT_COLOR = new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f); // CINZENTO ESCURO
    private static final ColorRGBA BUTTON_COLOR = new ColorRGBA(0.75f, 0.25f, 0.45f, 1.0f);

    public CraftingView(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.windowNode = new Node("CraftingView");
        this.inputSlotParents = new Node[2][2];
        initializeResources();
        buildLayout();
    }

    private void initializeResources() {
        this.font = assetManager.loadFont("Interface/Fonts/Default.fnt");
    }

    public Node getNode() {
        return windowNode;
    }

    public Geometry getCraftButtonGeometry() {
        return craftButton;
    }

    private void buildLayout() {
        float gridWidth = 2 * SLOT_SIZE + PADDING;
        float resultOffset = SLOT_SIZE + 4 * PADDING;
        float totalContentWidth = gridWidth + resultOffset + SLOT_SIZE; // Largura total do conteúdo (grid + seta + resultado)

        float bgWidth = totalContentWidth + 40f; // Adicionar margem
        float bgHeight = 2 * SLOT_SIZE + PADDING + 100f; // Mais espaço para o botão

        // --- 1. FUNDO CENTRALIZADO ---
        Quad bgMesh = new Quad(bgWidth, bgHeight);
        Geometry bgGeo = new Geometry("CraftingBackground", bgMesh);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", BG_COLOR);
        bgMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        bgGeo.setMaterial(bgMat);
        bgGeo.setLocalTranslation(-bgWidth / 2f, -bgHeight / 2f, -1);
        windowNode.attachChild(bgGeo);

        // --- 2. GRELHA 2x2 (CENTRALIZADA) ---
        float gridStartX = -totalContentWidth / 2f;
        float gridStartY = bgHeight / 2f - 50f; // Espaço do topo

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                Quad slotQuad = new Quad(SLOT_SIZE, SLOT_SIZE);
                Geometry slotGeo = new Geometry("InputSlot", slotQuad);
                Material slotMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                slotMat.setColor("Color", SLOT_COLOR);
                slotGeo.setMaterial(slotMat);

                float x = gridStartX + col * (SLOT_SIZE + PADDING);
                float y = gridStartY - row * (SLOT_SIZE + PADDING);
                slotGeo.setLocalTranslation(x, y, 1);
                windowNode.attachChild(slotGeo);

                Node iconParent = new Node("InputIconParent");
                iconParent.setLocalTranslation(x + SLOT_SIZE / 2f, y + SLOT_SIZE / 2f, 2);
                windowNode.attachChild(iconParent);
                inputSlotParents[row][col] = iconParent;
            }
        }

        // --- 3. SETA → ---
        BitmapText arrow = new BitmapText(font, false);
        arrow.setText("→");
        arrow.setColor(ColorRGBA.White);
        arrow.setSize(font.getCharSet().getRenderedSize() * 1.5f);
        float arrowX = gridStartX + 2 * SLOT_SIZE + 2 * PADDING + 5f;
        arrow.setLocalTranslation(arrowX, gridStartY - SLOT_SIZE / 2f, 1);
        windowNode.attachChild(arrow);

        // --- 4. SLOT RESULTADO ---
        float resultX = arrowX + 35f;
        float resultY = gridStartY - SLOT_SIZE / 2f;

        Quad resultQuad = new Quad(SLOT_SIZE, SLOT_SIZE);
        Geometry resultGeo = new Geometry("ResultSlot", resultQuad);
        Material resultMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        resultMat.setColor("Color", RESULT_SLOT_COLOR);
        resultGeo.setMaterial(resultMat);
        resultGeo.setLocalTranslation(resultX, resultY, 1);
        windowNode.attachChild(resultGeo);

        resultSlotParent = new Node("ResultIconParent");
        resultSlotParent.setLocalTranslation(resultX + SLOT_SIZE / 2f, resultY + SLOT_SIZE / 2f, 2);
        windowNode.attachChild(resultSlotParent);

        // Tooltip do resultado
        resultTooltip = new BitmapText(font, false);
        resultTooltip.setColor(ColorRGBA.White);
        resultTooltip.setSize(font.getCharSet().getRenderedSize() * 0.7f);
        resultTooltip.setLocalTranslation(resultX + SLOT_SIZE / 2f, resultY + SLOT_SIZE + 5f, 3);
        windowNode.attachChild(resultTooltip);

        // --- 5. BOTÃO CRAFT (ABAIXO DO SLOT DE RESULTADO) ---
        float btnWidth = SLOT_SIZE;
        float btnHeight = 32f;
        float btnX = resultX; // Alinhado com o slot de resultado
        float btnY = resultY - btnHeight - 15f; // Abaixo do slot de resultado com espaçamento

        Quad craftBtnMesh = new Quad(btnWidth, btnHeight);
        craftButton = new Geometry("CraftAction", craftBtnMesh);
        Material craftBtnMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        craftBtnMat.setColor("Color", BUTTON_COLOR);
        craftButton.setMaterial(craftBtnMat);
        craftButton.setLocalTranslation(btnX, btnY, 1);
        craftButton.setUserData("clickable", true);
        windowNode.attachChild(craftButton);

        BitmapText btnText = new BitmapText(font, false);
        btnText.setText("CRAFT");
        btnText.setColor(ColorRGBA.White);
        btnText.setSize(font.getCharSet().getRenderedSize() * 0.85f);
        btnText.setLocalTranslation(btnX + (btnWidth - btnText.getLineWidth()) / 2f, btnY + 21f, 2);
        windowNode.attachChild(btnText);
    }


    /**
     * Atualiza resultado.
     */
    public void updateResult(Item resultItem) {
        resultSlotParent.detachAllChildren();
        resultTooltip.setText("");

        if (resultItem != null) {
            Geometry iconGeo = resultItem.getIconGeometry(assetManager);
            resultSlotParent.attachChild(iconGeo);

            // Quantidade
            if (resultItem.getQuantity() > 1) {
                BitmapText qtyText = new BitmapText(font, false);
                qtyText.setText("x" + resultItem.getQuantity());
                qtyText.setColor(ColorRGBA.White);
                qtyText.setSize(font.getCharSet().getRenderedSize() * 0.8f);
                qtyText.setLocalTranslation(20f, -20f, 3);
                resultSlotParent.attachChild(qtyText);
            }

            // Nome do item
            resultTooltip.setText(resultItem.getName());
            float textWidth = resultTooltip.getLineWidth();
            resultTooltip.setLocalTranslation(
                    resultTooltip.getLocalTranslation().x - textWidth / 2f,
                    resultTooltip.getLocalTranslation().y,
                    resultTooltip.getLocalTranslation().z
            );
        }
    }

    /**
     * Retorna a largura total da janela de crafting.
     */
    public float getWidth() {
        float gridWidth = 2 * SLOT_SIZE + PADDING;
        float resultOffset = SLOT_SIZE + 4 * PADDING;
        return gridWidth + resultOffset + 20f;
    }

    /**
     * Retorna a altura total da janela de crafting.
     */
    public float getHeight() {
        return 2 * SLOT_SIZE + PADDING + 80f;
    }
}
