package jogo.gameobject.npc;

import jogo.gameobject.character.Character;
import jogo.gameobject.character.Player;
import jogo.gameobject.character.Ally;

/**
 * Interface que define o contrato para estratégias de comportamento de IA.
 * Implementa o Padrão Strategy, permitindo diferentes comportamentos
 * para NPCs sem alterar a classe base.
 *
 * Padrão Strategy: Encapsula uma família de algoritmos (comportamentos)
 * permitindo que sejam trocáveis em tempo de execução.
 */
public interface AIStrategy {

    /**
     * Executa o comportamento específico da estratégia de IA.
     *
     * @param npc O personagem NPC que está a usar esta estratégia
     * @param player O jogador (alvo da estratégia)
     * @param tpf Tempo por frame (delta time) para movimento suave
     */
    void execute(Ally npc, Player player, float tpf);

}