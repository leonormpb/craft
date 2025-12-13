package jogo.gameobject.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jogo.gameobject.item.Item;

/**
 * Inventário com sistema de slots para empilhamento.
 * Usa ArrayList para gestão eficiente de items.
 */
public class Inventory {

    private final int capacity;
    private final List<InventorySlot> slots;

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.slots = new ArrayList<>(capacity);

        // Inicializar todos os slots vazios
        for (int i = 0; i < capacity; i++) {
            slots.add(new InventorySlot());
        }
    }

    /**
     * Adiciona item ao inventário.
     * Tenta empilhar primeiro, depois procura slot vazio.
     * @return true se adicionado com sucesso
     */
    public boolean addItem(Item item) {
        if (item == null) return false;

        // Primeiro: tentar empilhar em slot existente do mesmo tipo
        for (InventorySlot slot : slots) {
            if (slot.canStackWith(item)) {
                slot.addQuantity(item.getQuantity());
                return true;
            }
        }

        // Segundo: procurar slot vazio
        for (InventorySlot slot : slots) {
            if (slot.isEmpty()) {
                slot.setItem(item);
                return true;
            }
        }

        // Inventário cheio
        return false;
    }

    /**
     * Remove item específico do inventário.
     */
    public boolean removeItem(Item item) {
        for (InventorySlot slot : slots) {
            if (!slot.isEmpty() && slot.getItem().equals(item)) {
                slot.clear();
                return true;
            }
        }
        return false;
    }

    /**
     * Remove quantidade específica de um tipo de item.
     * Essencial para o sistema de crafting.
     */
    public boolean removeItems(String itemName, int quantity) {
        int remaining = quantity;

        // Primeira passagem: contar disponível
        int available = countItemsByName(itemName);
        if (available < quantity) {
            return false; // Recursos insuficientes
        }

        // Segunda passagem: remover
        for (InventorySlot slot : slots) {
            if (!slot.isEmpty() && slot.getItem().getName().equals(itemName)) {
                int slotQty = slot.getQuantity();

                if (slotQty <= remaining) {
                    // Remover tudo deste slot
                    remaining -= slotQty;
                    slot.clear();
                } else {
                    // Remover parcialmente
                    slot.removeQuantity(remaining);
                    remaining = 0;
                }

                if (remaining == 0) break;
            }
        }

        return true;
    }

    /**
     * Conta quantos items de um tipo específico existem no inventário.
     */
    public int countItemsByName(String itemName) {
        int total = 0;
        for (InventorySlot slot : slots) {
            if (!slot.isEmpty() && slot.getItem().getName().equals(itemName)) {
                total += slot.getQuantity();
            }
        }
        return total;
    }

    /**
     * Verifica se tem recursos suficientes para uma receita.
     */
    public boolean hasResources(Map<String, Integer> required) {
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            if (countItemsByName(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retorna slot em posição específica (para UI).
     */
    public InventorySlot getSlot(int index) {
        if (index >= 0 && index < slots.size()) {
            return slots.get(index);
        }
        return null;
    }

    /**
     * Retorna lista de todos os slots (para UI).
     */
    public List<InventorySlot> getSlots() {
        return new ArrayList<>(slots);
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDisplayString() {
        if (isEmpty()) {
            return "(vazio)";
        }

        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (InventorySlot slot : slots) {
            if (!slot.isEmpty()) {
                sb.append(i++)
                        .append(") ")
                        .append(slot.getItem().getName())
                        .append(" x")
                        .append(slot.getQuantity())
                        .append("\n");
            }
        }
        return sb.toString();
    }

    public boolean isEmpty() {
        return slots.stream().allMatch(InventorySlot::isEmpty);
    }
}
