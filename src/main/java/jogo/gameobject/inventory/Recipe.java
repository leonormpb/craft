package jogo.gameobject.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa uma receita de crafting.
 * Usa Map para armazenar ingredientes necessários.
 */
public class Recipe {

    private final String id;
    private final String resultItemName;
    private final int resultQuantity;
    private final Map<String, Integer> ingredients;

    /**
     * Construtor de receita.
     * @param id identificador único
     * @param resultItemName nome do item resultante
     * @param resultQuantity quantidade produzida
     */
    public Recipe(String id, String resultItemName, int resultQuantity) {
        this.id = id;
        this.resultItemName = resultItemName;
        this.resultQuantity = resultQuantity;
        this.ingredients = new HashMap<>();
    }

    /**
     * Adiciona ingrediente à receita (builder pattern).
     */
    public Recipe addIngredient(String itemName, int quantity) {
        ingredients.put(itemName, quantity);
        return this;
    }

    /**
     * Verifica se o inventário tem recursos para craftar.
     */
    public boolean canCraft(Inventory inventory) {
        return inventory.hasResources(ingredients);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getResultItemName() {
        return resultItemName;
    }

    public int getResultQuantity() {
        return resultQuantity;
    }

    public Map<String, Integer> getIngredients() {
        return new HashMap<>(ingredients);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Receita [").append(id).append("]\n");
        sb.append("Resultado: ").append(resultItemName)
                .append(" x").append(resultQuantity).append("\n");
        sb.append("Ingredientes:\n");
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            sb.append("  - ").append(entry.getKey())
                    .append(" x").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
