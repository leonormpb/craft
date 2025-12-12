package jogo.gameobject;

import jogo.gameobject.interaction.Interactable;
import jogo.gameobject.item.FlowerItem;
import jogo.gameobject.inventory.Inventory;
import jogo.gameobject.character.Player;
/**
 * Flor Rosa - um objeto decorativo simples do mundo.
 * Pode ser interagido via raycast/E.
 */
public class Flower extends GameObject implements Interactable<Player> {

    private String color;      // Cor da flor (sempre "rosa")
    private boolean wilted;    // Se está murchada

    public Flower(String name, float x, float y, float z) {
        super(name);
        this.position.set(x, y, z);
        this.color = "rosa";
        this.wilted = false;
    }

    /**
     * Interação via E: colhe a flor e adiciona um item ao inventário do jogador.
     */
    @Override
    public void interact(Player player) {
        System.out.println("🌸 Colheste uma flor rosa!");
        this.wilted = true;

        Inventory inv = player.getInventory();
        boolean added = inv.addItem(new FlowerItem(color));
        if (!added) {
            System.out.println("Inventário cheio, não foi possível guardar a flor.");
        }

        // Aqui depois sinalizas ao motor que esta Flower deve ser removida do mundo.
    }

    public String getColor() {
        return color;
    }

    public boolean isWilted() {
        return wilted;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getZ() {
        return position.z;
    }

    @Override
    public String toString() {
        return String.format(
                "Flor Rosa{nome=%s, murchada=%b, pos=(%.1f, %.1f, %.1f)}",
                name, wilted, position.x, position.y, position.z
        );
    }
}
