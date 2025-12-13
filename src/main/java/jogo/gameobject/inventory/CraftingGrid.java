package jogo.gameobject.inventory;

import jogo.gameobject.item.Item;
import java.util.HashMap;
import java.util.Map;

/**
 * Grid 2x2 de crafting estilo Minecraft.
 * Detecta automaticamente receitas baseadas no padrão dos items.
 */
public class CraftingGrid {

    private final Item[][] grid; // 2x2
    private final CraftingSystem craftingSystem;

    public CraftingGrid(CraftingSystem craftingSystem) {
        this.grid = new Item[2][2];
        this.craftingSystem = craftingSystem;
    }

    /**
     * Coloca item numa posição da grid.
     */
    public void setItem(int row, int col, Item item) {
        if (row >= 0 && row < 2 && col >= 0 && col < 2) {
            grid[row][col] = item;
        }
    }

    /**
     * Obtém item de uma posição.
     */
    public Item getItem(int row, int col) {
        if (row >= 0 && row < 2 && col >= 0 && col < 2) {
            return grid[row][col];
        }
        return null;
    }

    /**
     * Remove item de uma posição.
     */
    public Item removeItem(int row, int col) {
        if (row >= 0 && row < 2 && col >= 0 && col < 2) {
            Item item = grid[row][col];
            grid[row][col] = null;
            return item;
        }
        return null;
    }

    /**
     * Limpa toda a grid.
     */
    public void clear() {
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                grid[row][col] = null;
            }
        }
    }

    /**
     * Verifica se uma posição está vazia.
     */
    public boolean isEmpty(int row, int col) {
        return getItem(row, col) == null;
    }

    /**
     * Detecta qual receita corresponde ao padrão atual da grid.
     * @return Recipe se padrão válido, null caso contrário
     */
    public Recipe detectRecipe() {
        // Contar items na grid
        Map<String, Integer> items = new HashMap<>();

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                Item item = grid[row][col];
                if (item != null) {
                    String name = item.getName();
                    items.put(name, items.getOrDefault(name, 0) + 1);
                }
            }
        }

        // Se grid vazia, sem receita
        if (items.isEmpty()) {
            return null;
        }

        // Verificar cada receita disponível
        for (Recipe recipe : craftingSystem.getRecipes()) {
            if (matchesRecipe(items, recipe)) {
                return recipe;
            }
        }

        return null;
    }

    /**
     * Verifica se os items na grid correspondem aos ingredientes da receita.
     */
    private boolean matchesRecipe(Map<String, Integer> gridItems, Recipe recipe) {
        Map<String, Integer> required = recipe.getIngredients();

        // Deve ter exatamente os mesmos items
        if (gridItems.size() != required.size()) {
            return false;
        }

        // Verificar cada ingrediente
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            Integer gridQty = gridItems.get(entry.getKey());
            if (gridQty == null || !gridQty.equals(entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Retorna o item craftável baseado no padrão atual.
     */
    public Item getResult() {
        Recipe recipe = detectRecipe();
        if (recipe != null) {
            try {
                return jogo.gameobject.item.ItemFactory.createFromName(
                        recipe.getResultItemName(),
                        recipe.getResultQuantity()
                );
            } catch (Exception e) {
                System.err.println("Erro ao criar resultado: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Consome os items da grid (quando jogador pega o resultado).
     */
    public void consumeIngredients() {
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                Item item = grid[row][col];
                if (item != null) {
                    item.setQuantity(item.getQuantity() - 1);
                    if (item.getQuantity() <= 0) {
                        grid[row][col] = null;
                    }
                }
            }
        }
    }

    /**
     * Retorna representação textual da grid (debug).
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Grid 2x2:\n");
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                Item item = grid[row][col];
                if (item != null) {
                    sb.append("[").append(item.getName()).append(" x").append(item.getQuantity()).append("] ");
                } else {
                    sb.append("[     ] ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
