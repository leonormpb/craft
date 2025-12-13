package jogo.gameobject.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jogo.gameobject.item.Item;
import jogo.gameobject.item.ItemFactory;
import jogo.gameobject.item.StickItem;
import jogo.gameobject.item.WoodPlankItem;

/**
 * Sistema central de crafting.
 * Gere receitas e processa crafting com validação.
 */
public class CraftingSystem {

    private final List<Recipe> recipes;
    private final Inventory inventory;

    public CraftingSystem(Inventory inventory) {
        this.inventory = inventory;
        this.recipes = new ArrayList<>();
        initializeRecipes();
    }

    /**
     * Inicializa receitas base do jogo.
     * Segue o enunciado: mínimo 4 receitas.
     */
    private void initializeRecipes() {
        // Receita 1: 2 Wood Plank → 4 Sticks
        recipes.add(new Recipe("stick", "Stick", 4)
                .addIngredient("Wood Plank", 2));

        // Receita 2: 1 Wood → 4 Wood Planks
        recipes.add(new Recipe("wood_plank", "Wood Plank", 4)
                .addIngredient("Wood", 1));

        // Receita 3: 3 Stone + 2 Sticks → Stone Pickaxe
        recipes.add(new Recipe("stone_pickaxe", "Stone Pickaxe", 1)
                .addIngredient("Stone", 3)
                .addIngredient("Stick", 2));

        // Receita 4: 4 Wood Planks → Crafting Table
        recipes.add(new Recipe("crafting_table", "Crafting Table", 1)
                .addIngredient("Wood Plank", 4));
    }

    /**
     * Tenta craftar um item pela receita.
     * @param recipeId identificador da receita
     * @return item craftado ou null se falhar
     * @throws InsufficientResourcesException se faltar recursos
     * @throws InvalidRecipeException se receita não existir
     */
    public Item craft(String recipeId) throws InsufficientResourcesException, InvalidRecipeException {
        // 1. Encontrar receita
        Recipe recipe = findRecipe(recipeId);
        if (recipe == null) {
            throw new InvalidRecipeException("Receita não encontrada: " + recipeId);
        }

        // 2. Verificar recursos
        if (!recipe.canCraft(inventory)) {
            throw new InsufficientResourcesException(
                    "Recursos insuficientes para: " + recipe.getResultItemName()
            );
        }

        // 3. Remover ingredientes
        for (Map.Entry<String, Integer> ingredient : recipe.getIngredients().entrySet()) {
            boolean removed = inventory.removeItems(ingredient.getKey(), ingredient.getValue());
            if (!removed) {
                // Rollback (não devia acontecer devido à validação)
                throw new InsufficientResourcesException("Erro ao remover: " + ingredient.getKey());
            }
        }

        // 4. Criar item resultante
        Item craftedItem = createItem(recipe.getResultItemName(), recipe.getResultQuantity());

        // 5. Adicionar ao inventário
        boolean added = inventory.addItem(craftedItem);
        if (!added) {
            // Inventário cheio - devolve recursos (rollback)
            for (Map.Entry<String, Integer> ingredient : recipe.getIngredients().entrySet()) {
                Item refund = createItem(ingredient.getKey(), ingredient.getValue());
                inventory.addItem(refund);
            }
            throw new InsufficientResourcesException("Inventário cheio! Craft cancelado.");
        }

        System.out.println("✓ Craftado: " + craftedItem.getName() + " x" + recipe.getResultQuantity());
        return craftedItem;
    }

    /**
     * Procura receita por ID.
     */
    private Recipe findRecipe(String recipeId) {
        for (Recipe recipe : recipes) {
            if (recipe.getId().equals(recipeId)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Factory method para criar items por nome.
     * TODO: Expandir conforme novos items forem criados.
     */
    private Item createItem(String itemName, int quantity) {
        return ItemFactory.createFromName(itemName, quantity);
    }


    /**
     * Retorna lista de todas as receitas disponíveis.
     */
    public List<Recipe> getRecipes() {
        return new ArrayList<>(recipes);
    }

    /**
     * Retorna receitas que podem ser craftadas atualmente.
     */
    public List<Recipe> getAvailableRecipes() {
        List<Recipe> available = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.canCraft(inventory)) {
                available.add(recipe);
            }
        }
        return available;
    }
}
