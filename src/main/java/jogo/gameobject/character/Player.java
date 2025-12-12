package jogo.gameobject.character;

import jogo.gameobject.inventory.Inventory;

public class Player extends Character {

    private final Inventory inventory;

    public Player(String name, float x, float y, float z) {
        super(name);                 // só passa o nome para Character
        this.position.set(x, y, z);  // 'position' vem de GameObject
        this.inventory = new Inventory(20);
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void updateAI(float tpf) {
        // Vazio porque o jogador não é AI
    }
}
