package jogo.voxel.blocks;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture2D;
import jogo.util.ProcTextures;
import jogo.voxel.VoxelBlockType;
import com.jme3.texture.Texture;

public class GrassBlockType extends VoxelBlockType {
    public GrassBlockType() {
        super("grass");
    }
    // isSolid() inherits true from base

    @Override
    public Material getMaterial(AssetManager assetManager) {
        // 1. Carregar a textura do ficheiro
        // O caminho é relativo à pasta 'resources'
        Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/grass.png");

        // 2. FILTRAGEM (Muito Importante para o estilo Voxel/Minecraft)
        // Isto faz com que os pixéis fiquem nítidos em vez de borrados quando te aproximas
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setTexture("DiffuseMap", tex); // Aplica a textura

        m.setBoolean("UseMaterialColors", false); // Agora usamos a cor da imagem, não cor sólida
        m.setColor("Diffuse", ColorRGBA.White);   // White aqui significa "cor original da imagem"
        m.setColor("Specular", ColorRGBA.White.mult(0.01f));
        m.setFloat("Shininess", 10f);

        return m;
    }
}
