package jogo.voxel.blocks;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture2D;
import jogo.util.ProcTextures;
import jogo.voxel.VoxelBlockType;

public class SandBlockType extends VoxelBlockType {
    public SandBlockType() {
        super("leaves");
    }
    // isSolid() inherits true from base

    @Override
    public Material getMaterial(AssetManager assetManager) {
        Texture2D tex = ProcTextures.checker(128, 4, ColorRGBA.Yellow, ColorRGBA.LightGray);
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setTexture("DiffuseMap", tex);
        m.setBoolean("UseMaterialColors", true);
        m.setColor("Diffuse", ColorRGBA.White);
        m.setColor("Specular", ColorRGBA.White.mult(0.01f)); // A areia tem pouco brilho
        m.setFloat("Shininess", 10f);
        return m;
    }

    // Efeito da areia
    @Override
    public float getSpeedModifier() {
        return 0.4f; // O jogador anda a 40% da velocidade normal
    }
}
