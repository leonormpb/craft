package jogo.gameobject.inventory;

/**
 * Exceção lançada quando uma receita inválida é solicitada.
 * Tratamento de erros conforme enunciado.
 */
public class InvalidRecipeException extends Exception {

    public InvalidRecipeException(String message) {
        super(message);
    }

    public InvalidRecipeException(String message, Throwable cause) {
        super(message, cause);
    }
}
