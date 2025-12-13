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
import jogo.gameobject.inventory.InventorySlot;

public class InventoryView {

    private final AssetManager assetManager;
    private final Node windowNode;
    private BitmapFont font;

    // Grid 9x3
    private static final int GRID_COLS = 9;
    private static final int GRID_ROWS = 3;
    private static final float SLOT_SIZE = 62f;
    private static final float PADDING = 8f;

    // Cores
    private static final ColorRGBA BG_COLOR = new ColorRGBA(0.847f, 0.302f, 0.471f, 0.9f);
    private static final ColorRGBA SLOT_COLOR = new ColorRGBA(0.55f, 0.55f, 0.55f, 1.0f);

    private Node[] slotIconParents;
    private BitmapText[] tooltipTexts; // Nomes dos items

    public InventoryView(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.windowNode = new Node("InventoryView");
        this.slotIconParents = new Node[GRID_COLS * GRID_ROWS];
        this.tooltipTexts = new BitmapText[GRID_COLS * GRID_ROWS];
        initializeResources();
        buildLayout();
    }

    private void initializeResources() {
        this.font = assetManager.loadFont("Interface/Fonts/Default.fnt");
    }

    public Node getNode() {
        return windowNode;
    }

    private void buildLayout() {
        float gridWidth = GRID_COLS * SLOT_SIZE + (GRID_COLS - 1) * PADDING;
        float gridHeight = GRID_ROWS * SLOT_SIZE + (GRID_ROWS - 1) * PADDING;

        // Margem para título e espaçamento
        float bgWidth = gridWidth + 40f;
        float bgHeight = gridHeight + 80f;

        // --- 1. FUNDO ROSA (CENTRALIZADO COM OS SLOTS) ---
        Quad bgMesh = new Quad(bgWidth, bgHeight);
        Geometry bgGeo = new Geometry("InventoryBackground", bgMesh);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", BG_COLOR);
        bgMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        bgGeo.setMaterial(bgMat);
        bgGeo.setLocalTranslation(-bgWidth / 2f, -bgHeight / 2f, -1);
        windowNode.attachChild(bgGeo);

        // --- 2. TÍTULO (CENTRALIZADO NO TOPO) ---
        BitmapText title = new BitmapText(font, false);
        title.setText("INVENTARIO");
        title.setColor(ColorRGBA.White);
        title.setSize(font.getCharSet().getRenderedSize() * 1.3f);
        // Centralizar horizontalmente e posicionar no topo
        title.setLocalTranslation(-title.getLineWidth() / 2f, bgHeight / 2f - 20f, 0.5f);
        windowNode.attachChild(title);

        // --- 3. GRID 9x3 (CENTRALIZADO) ---
        float gridStartX = -gridWidth / 2f;
        float gridStartY = (bgHeight / 2f - 50f) - gridHeight / 2f + gridHeight; // Centralizar verticalmente considerando o título

        int index = 0;
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                // Fundo do slot
                Quad slotQuad = new Quad(SLOT_SIZE, SLOT_SIZE);
                Geometry slotGeo = new Geometry("Slot_" + index, slotQuad);
                Material slotMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                slotMat.setColor("Color", SLOT_COLOR);
                slotGeo.setMaterial(slotMat);

                float x = gridStartX + col * (SLOT_SIZE + PADDING);
                float y = gridStartY - row * (SLOT_SIZE + PADDING);
                slotGeo.setLocalTranslation(x, y, 1);
                windowNode.attachChild(slotGeo);

                // Node para o ícone
                Node iconParent = new Node("IconParent_" + index);
                iconParent.setLocalTranslation(x + SLOT_SIZE / 2f, y + SLOT_SIZE / 2f, 2);
                windowNode.attachChild(iconParent);
                slotIconParents[index] = iconParent;

                // Tooltip (nome do item acima do slot)
                BitmapText tooltip = new BitmapText(font, false);
                tooltip.setColor(ColorRGBA.White);
                tooltip.setSize(font.getCharSet().getRenderedSize() * 0.7f);
                tooltip.setLocalTranslation(x + SLOT_SIZE / 2f, y + SLOT_SIZE + 5f, 3);
                windowNode.attachChild(tooltip);
                tooltipTexts[index] = tooltip;

                index++;
            }
        }
    }


    /**
     * Atualiza o inventário visual.
     */
    public void updateData(Inventory inventory) {
        if (inventory == null) return;

        // Limpar
        for (int i = 0; i < slotIconParents.length; i++) {
            slotIconParents[i].detachAllChildren();
            tooltipTexts[i].setText("");
        }

        // Renderizar items
        for (int i = 0; i < Math.min(inventory.getCapacity(), slotIconParents.length); i++) {
            InventorySlot slot = inventory.getSlot(i);

            if (slot != null && !slot.isEmpty()) {
                Node parent = slotIconParents[i];

                // Ícone do item
                Geometry iconGeo = slot.getItem().getIconGeometry(assetManager);
                parent.attachChild(iconGeo);

                // Quantidade
                if (slot.getQuantity() > 1) {
                    BitmapText qtyText = new BitmapText(font, false);
                    qtyText.setText(String.valueOf(slot.getQuantity()));
                    qtyText.setColor(ColorRGBA.White);
                    qtyText.setSize(font.getCharSet().getRenderedSize() * 0.9f);
                    qtyText.setLocalTranslation(20f, -20f, 3);
                    parent.attachChild(qtyText);
                }

                // Nome do item acima do slot
                String itemName = slot.getItem().getName();
                tooltipTexts[i].setText(itemName);
                // Centralizar o texto
                float textWidth = tooltipTexts[i].getLineWidth();
                tooltipTexts[i].setLocalTranslation(
                        tooltipTexts[i].getLocalTranslation().x - textWidth / 2f,
                        tooltipTexts[i].getLocalTranslation().y,
                        tooltipTexts[i].getLocalTranslation().z
                );
            }
        }
    }
}
