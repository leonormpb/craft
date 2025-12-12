package jogo.gameobject.character;

import jogo.gameobject.GameObject;

public abstract class Character extends GameObject {

    protected Character(String name) {
        super(name);
    }

    // Example state hooks students can extend
    private int health = 100;
    protected int maxHealth = 100;
    protected float speed = 3.0f;
    protected boolean alive = true;

    /**
     *  Método abstrato para IA - cada NPC implementa de forma diferente.
     POLIMORFISMO principal!
     */
    public abstract void updateAI(float deltaTime);


    /**
     * Recebe dano.
     */
    public void takeDamage(int damage) {
        if (!alive) return;
        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
            onDeath();
        }
    }

    /**
     * Cura.
     */
    public void heal(int amount) {
        if (!alive) return;
        health = Math.min(health + amount, maxHealth);
    }


    /**
     * Chamado quando morre.
     */
    protected void onDeath() {
        System.out.println(name + " morreu!");
    }

    // Getters e Setters
    public int getHealth() { return health; }
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }
    public int getMaxHealth() { return maxHealth; }
    public float getSpeed() { return speed; }
    public boolean isAlive() { return alive; }

    @Override
    public String toString() {
        return name + "{vida=" + health + "/" + maxHealth + ", vivo=" + alive + "}";
    }
}
