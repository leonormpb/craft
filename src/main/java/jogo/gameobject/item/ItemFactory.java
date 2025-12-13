package jogo.gameobject.item;

/**
 * Factory para criar items baseados no tipo de bloco.
 * Facilita a criação de drops quando blocos são partidos.
 */
public class ItemFactory {

    /**
     * Cria um item baseado no ID do bloco voxel.
     * @param blockId ID do bloco (0=Air, 1=Stone, etc.)
     * @param quantity quantidade do item
     * @return Item correspondente ou null se não dropável
     */
    public static Item createFromBlockId(int blockId, int quantity) {
        switch (blockId) {
            case 0: // Air - não dropa nada
                return null;

            case 1: // Stone
                return new StoneItem(quantity);

            case 2: // Wood (assumindo que 2 é wood)
                return new WoodItem(quantity);

            case 3: // Dirt (assumindo que 3 é dirt)
                return new DirtItem(quantity);

            // Adiciona mais casos conforme necessário
            // case 4: return new GrassItem(quantity);
            // case 5: return new SandItem(quantity);

            default:
                System.out.println("⚠️ Bloco ID " + blockId + " não tem item associado");
                return null;
        }
    }

    /**
     * Cria um item pelo nome.
     * Útil para crafting system.
     */
    public static Item createFromName(String itemName, int quantity) {
        switch (itemName) {
            case "Stone":
                return new StoneItem(quantity);
            case "Wood":
                return new WoodItem(quantity);
            case "Dirt":
                return new DirtItem(quantity);
            case "Stick":
                return new StickItem(quantity);
            case "Wood Plank":
                return new WoodPlankItem(quantity);
            default:
                throw new IllegalArgumentException("Item desconhecido: " + itemName);
        }
    }
}
