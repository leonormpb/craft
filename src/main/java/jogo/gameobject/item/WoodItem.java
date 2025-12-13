package jogo.gameobject.item;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class WoodItem extends Item {

    public WoodItem(int quantity) {
        super("Wood", quantity);
    }

    @Override
    public Geometry getIconGeometry(AssetManager assetManager) {
        Box iconBox = new Box(12f, 12f, 12f);
        Geometry iconGeo = new Geometry("WoodIcon", iconBox);

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        try {
            // Carregar textura do bloco de madeira
            Texture texture = assetManager.loadTexture("Textures/wood1.png");
            mat.setTexture("DiffuseMap", texture);
        } catch (Exception e) {
            // Fallback: cor castanha se textura não encontrada
            System.err.println("⚠️ Textura wood1.png não encontrada, usando cor");
            mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", new ColorRGBA(0.4f, 0.25f, 0.1f, 1.0f));
        }

        iconGeo.setMaterial(mat);

        return iconGeo;
    }

    @Override
    public void onInteract() {
        System.out.println("Wood: pode ser transformado em Wood Planks");
    }
}
