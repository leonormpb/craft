package jogo.gameobject.character.npc.ally;

import jogo.gameobject.character.AIStrategy;
import jogo.gameobject.character.Character;
import jogo.gameobject.character.FollowStrategy;
import jogo.gameobject.character.Player;

public class Ally extends Character {

    private Player targetPlayer;
    private AIStrategy currentStrategy;

    public Ally(String name, Player targetPlayer) {
        super(name);
        this.targetPlayer = targetPlayer;
        this.speed = 2.5f; // Um pouco mais lento que o player padrão

        // Define a estratégia inicial (Strategy Pattern)
        this.currentStrategy = new FollowStrategy();
    }

    public void setStrategy(AIStrategy strategy) {
        this.currentStrategy = strategy;
    }

    @Override
    public void updateAI(float deltaTime) {
        if (isAlive() && targetPlayer != null && currentStrategy != null) {
            // Delega o comportamento para a estratégia
            currentStrategy.execute(this, targetPlayer, deltaTime);
        }
    }
}