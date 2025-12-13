package jogo.gameobject.inventory;

/**
 * Exceção lançada quando não há recursos suficientes para crafting.
 * Tratamento de erros conforme enunciado.
 */
public class InsufficientResourcesException extends Exception {

    public InsufficientResourcesException(String message) {
        super(message);
    }

    public InsufficientResourcesException(String message, Throwable cause) {
        super(message, cause);
    }
}
