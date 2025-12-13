package jogo.gameobject.character;

import jogo.gameobject.inventory.Inventory;
import jogo.gameobject.inventory.CraftingSystem;
import jogo.gameobject.inventory.CraftingGrid;

public class Player extends Character {

    private final Inventory inventory;
    private final CraftingSystem craftingSystem;
    private final CraftingGrid craftingGrid; // ⬅️ ADICIONAR

    public Player(String name, float x, float y, float z) {
        super(name);
        this.position.set(x, y, z);
        this.inventory = new Inventory(27);
        this.craftingSystem = new CraftingSystem(inventory);
        this.craftingGrid = new CraftingGrid(craftingSystem); // ⬅️ ADICIONAR
    }

    public Inventory getInventory() {
        return inventory;
    }

    public CraftingSystem getCraftingSystem() {
        return craftingSystem;
    }

    public CraftingGrid getCraftingGrid() { // ⬅️ ADICIONAR
        return craftingGrid;
    }

    @Override
    public void updateAI(float tpf) {
        // Vazio porque o jogador não é AI
    }
}
