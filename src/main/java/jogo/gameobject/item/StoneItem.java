package jogo.gameobject.item;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Item Stone - coletado ao partir blocos de pedra.
 */
public class StoneItem extends Item {

    public StoneItem(int quantity) {
        super("Stone", quantity);
    }

    @Override
    public Geometry getIconGeometry(AssetManager assetManager) {
        Box iconBox = new Box(12f, 12f, 12f);
        Geometry iconGeo = new Geometry("StoneIcon", iconBox);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f)); // Cinzento
        iconGeo.setMaterial(mat);

        return iconGeo;
    }

    @Override
    public void onInteract() {
        System.out.println("Stone: pode ser usado para crafting");
    }
}
