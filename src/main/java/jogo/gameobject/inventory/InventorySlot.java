package jogo.gameobject.inventory;

import jogo.gameobject.item.Item;

/**
 * Representa um slot individual do inventário.
 * Encapsula lógica de empilhamento e gestão de quantidade.
 */
public class InventorySlot {

    private Item item;
    private int quantity;
    private static final int MAX_STACK = 64; // Como no Minecraft

    public InventorySlot() {
        this.item = null;
        this.quantity = 0;
    }

    /**
     * Verifica se o slot está vazio.
     */
    public boolean isEmpty() {
        return item == null || quantity == 0;
    }

    /**
     * Verifica se pode empilhar com outro item.
     */
    public boolean canStackWith(Item otherItem) {
        if (isEmpty()) return false;
        if (otherItem == null) return false;

        // Mesmo tipo e abaixo do limite
        return item.getName().equals(otherItem.getName())
                && quantity < MAX_STACK;
    }

    /**
     * Define o item deste slot (para slot vazio).
     */
    public void setItem(Item item) {
        this.item = item;
        this.quantity = item.getQuantity();
    }

    /**
     * Adiciona quantidade ao stack existente.
     */
    public void addQuantity(int amount) {
        this.quantity = Math.min(this.quantity + amount, MAX_STACK);
        if (this.item != null) {
            this.item.setQuantity(this.quantity);
        }
    }

    /**
     * Remove quantidade do stack.
     */
    public void removeQuantity(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
        if (this.item != null) {
            this.item.setQuantity(this.quantity);
        }

        if (this.quantity == 0) {
            clear();
        }
    }

    /**
     * Limpa o slot completamente.
     */
    public void clear() {
        this.item = null;
        this.quantity = 0;
    }

    // Getters
    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxStack() {
        return MAX_STACK;
    }
}
