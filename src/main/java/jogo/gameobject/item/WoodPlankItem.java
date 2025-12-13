package jogo.gameobject.item;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Item Wood Plank - craftável a partir de Wood.
 */
public class WoodPlankItem extends Item {

    public WoodPlankItem(int quantity) {
        super("Wood Plank", quantity);
    }

    @Override
    public Geometry getIconGeometry(AssetManager assetManager) {
        // Tábua de madeira (retângulo marrom claro)
        Box iconBox = new Box(16f, 16f, 3f);
        Geometry iconGeo = new Geometry("WoodPlankIcon", iconBox);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.7f, 0.5f, 0.3f, 1.0f));
        iconGeo.setMaterial(mat);

        return iconGeo;
    }

    @Override
    public void onInteract() {
        System.out.println("Wood Plank usado");
    }
}
