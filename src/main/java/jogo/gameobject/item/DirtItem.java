package jogo.gameobject.item;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Item Dirt - coletado ao partir blocos de terra.
 */
public class DirtItem extends Item {

    public DirtItem(int quantity) {
        super("Dirt", quantity);
    }

    @Override
    public Geometry getIconGeometry(AssetManager assetManager) {
        Box iconBox = new Box(12f, 12f, 12f);
        Geometry iconGeo = new Geometry("DirtIcon", iconBox);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.4f, 0.3f, 0.2f, 1.0f)); // Castanho escuro
        iconGeo.setMaterial(mat);

        return iconGeo;
    }

    @Override
    public void onInteract() {
        System.out.println("Dirt: bloco de construção básico");
    }
}
