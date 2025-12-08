package jogo.gameobject;

import jogo.framework.math.Vec3;

/**
 * Flor Rosa - um objeto decorativo simples do mundo.
 * É neutro: apenas existe, pode ser interagido via raycast/E.
 */
public class Flower extends GameObject {

    private String color;      // Cor da flor (sempre "rosa")
    private boolean wilted;    // Se está murchada

    /**
     * Construtor de uma flor rosa.
     *
     * @param name Nome da flor
     * @param x Posição X
     * @param y Posição Y
     * @param z Posição Z
     */
    public Flower(String name, float x, float y, float z) {
        super(name);
        this.position.set(x, y, z);
        this.color = "rosa";
        this.wilted = false;
    }

    /**
     * Colhe a flor (interação via E).
     */
    public void onInteract() {
        System.out.println("🌸 Colheste uma flor rosa!");
        this.wilted = true;
    }

    /**
     * Retorna a cor da flor.
     */
    public String getColor() {
        return color;
    }

    /**
     * Verifica se a flor está murchada.
     */
    public boolean isWilted() {
        return wilted;
    }

    /**
     * Retorna X da posição.
     */
    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    /**
     * Retorna Z da posição.
     */
    public float getZ() {
        return position.z;
    }

    /**
     * Descrição da flor.
     */
    @Override
    public String toString() {
        return String.format("Flor Rosa{nome=%s, murchada=%b, pos=(%.1f, %.1f, %.1f)}",
                name, wilted, position.x, position.y, position.z);
    }
}
