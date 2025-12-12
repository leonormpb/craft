package jogo.gameobject.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jogo.gameobject.item.Item;

/**
 * Inventário simples com capacidade fixa.
 */
public class Inventory {

    private final int capacity;
    private final List<Item> items = new ArrayList<>();

    public Inventory(int capacity) {
        this.capacity = capacity;
    }

    public boolean addItem(Item item) {
        if (items.size() >= capacity) return false;
        items.add(item);
        return true;
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public long countByType(Class<? extends Item> type) {
        return items.stream().filter(type::isInstance).count();
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDisplayString() {
        if (items.isEmpty()) {
            return "(vazio)";
        }

        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (Item item : items) {
            sb.append(i++)
                    .append(") ")
                    .append(item.getClass().getSimpleName())
                    .append("\n");
        }
        return sb.toString();
    }
}
