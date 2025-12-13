package jogo.gameobject.item;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Item Stick - craftável a partir de Wood Planks.
 * Seguindo hierarquia Item → concrete items.
 */
public class StickItem extends Item {

    public StickItem(int quantity) {
        super("Stick", quantity);
    }

    @Override
    public Geometry getIconGeometry(AssetManager assetManager) {
        // Criar representação visual simples (pau castanho)
        Box iconBox = new Box(8f, 20f, 2f); // Forma de pau
        Geometry iconGeo = new Geometry("StickIcon", iconBox);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.6f, 0.4f, 0.2f, 1.0f)); // Castanho
        iconGeo.setMaterial(mat);

        return iconGeo;
    }

    @Override
    public void onInteract() {
        System.out.println("Stick usado - implementar lógica específica");
    }
}
