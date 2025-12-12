package jogo.gameobject.item;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import jogo.gameobject.GameObject; // Assumindo que GameObject já está importado ou não é necessário.

/**
 * Item correspondente a uma flor colhida.
 */
public class FlowerItem extends Item {

    private final String color;

    // Construtor corrigido: passa nome e quantidade inicial (assumimos 1)
    public FlowerItem(String color) {
        super("FlowerItem - " + color, 1); // CORREÇÃO 1: Adicionar a quantidade inicial (ex: 1)
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    // CORREÇÃO 2: Implementar o método abstrato obrigatório (Polimorfismo)
    @Override
    public Geometry getIconGeometry(AssetManager assetManager) {
        // Criar uma representação visual simples para a flor (ex: um pequeno cilindro)

        // 1. Criar forma
        Cylinder flowerMesh = new Cylinder(3, 16, 0.1f, 0.4f, true); // (raio, altura)
        Geometry iconGeo = new Geometry("FlowerIcon", flowerMesh);

        // 2. Definir material baseado na cor
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        ColorRGBA itemColor = ColorRGBA.White;

        switch (color.toLowerCase()) {
            case "red":
                itemColor = ColorRGBA.Red;
                break;
            case "yellow":
                itemColor = ColorRGBA.Yellow;
                break;
            // Adicionar mais cores conforme necessário
            case "blue":
                itemColor = ColorRGBA.Blue;
                break;
        }

        mat.setColor("Color", itemColor);
        iconGeo.setMaterial(mat);

        // Ajustar a escala/rotação se o ícone for muito grande
        iconGeo.setLocalScale(0.5f);

        return iconGeo;
    }

    // ... (onInteract e outros métodos)
}