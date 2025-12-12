package jogo.gameobject.item;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import jogo.gameobject.GameObject;

public abstract class Item extends GameObject {

    protected int quantity; // Adicionar campo para a quantidade do item

    // Construtor atualizado para aceitar o nome e a quantidade
    protected Item(String name, int quantity) {
        super(name);
        this.quantity = quantity;
    }

    /**
     * Define a representação visual (ícone) do item a ser usado no HUD.
     * Este método é obrigatório para todas as subclasses concretas.
     * @param assetManager o gerenciador de recursos para carregar materiais.
     * @return um Geometry que representa visualmente o item.
     */
    public abstract Geometry getIconGeometry(AssetManager assetManager);

    /**
     * Retorna a quantidade deste item.
     * (Método essencial para a InventoryView, resolve o erro implícito de getQuantity)
     */
    public int getQuantity() {
        return quantity;
    }

    public void onInteract() {
        // Hook for interaction logic (engine will route interactions)
    }

    // Método utilitário para mudar a quantidade, útil para o crafting/recolha
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}