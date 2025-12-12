package jogo.gameobject.npc;

import jogo.framework.math.Vec3;
import jogo.gameobject.character.Ally;
import jogo.gameobject.character.Player;
import com.jme3.math.Vector3f;

/**
 * Estratégia de comportamento "Seguir o Jogador".
 *
 * O aliado caminha na direção do jogador se estiver a mais de 3 unidades
 * de distância, e para quando chega perto.
 *
 * Responsabilidades:
 * - Calcular a distância entre aliado e jogador
 * - Determinar a direção de movimento
 * - Mover o aliado suavemente usando a velocidade dele
 * - Respeitar a distância mínima (3 unidades)
 */
public class FollowStrategy implements AIStrategy {

    // Constante: distância mínima antes de parar
    private static final float STOP_DISTANCE = 3.0f;

    /**
     * Executa o comportamento de seguir o jogador.
     *
     * Algoritmo:
     * 1. Calcular a distância entre aliado e jogador
     * 2. Se distância > STOP_DISTANCE:
     *    a. Calcular direção (vetor normalizado)
     *    b. Mover na direção do jogador
     * 3. Se distância <= STOP_DISTANCE:
     *    a. Parar (não mover)
     *
     * @param npc O aliado que está a usar esta estratégia
     * @param player O jogador que o aliado deve seguir
     * @param tpf Tempo por frame para movimento suave
     */
    @Override
    public void execute(Ally npc, Player player, float tpf) {
        // ✅ CORRIGIDO: Converter Vec3 para Vector3f
        // getPosition() retorna Vec3 (classe interna do seu GameObject)
        // Precisamos de converter para Vector3f para usar distância, etc.

        // Opção 1: Criar novo Vector3f a partir dos valores
        Vec3 allyVec = npc.getPosition();
        Vector3f allyPos = new Vector3f(allyVec.x, allyVec.y, allyVec.z);

        Vec3 playerVec = player.getPosition();
        Vector3f playerPos = new Vector3f(playerVec.x, playerVec.y, playerVec.z);

        // PASSO 1: Calcular a distância entre aliado e jogador
        // distance(a, b) = sqrt((a.x - b.x)² + (a.y - b.y)² + (a.z - b.z)²)
        float distance = allyPos.distance(playerPos);

        // PASSO 2: Determinar se o aliado deve mover-se
        if (distance > STOP_DISTANCE) {
            // O jogador está longe, mover em sua direção

            // Calcular o vetor de direção (do aliado para o jogador)
            Vector3f direction = playerPos.subtract(allyPos);

            // Normalizar o vetor (comprimento = 1, só indica direção)
            direction.normalizeLocal();

            // Calcular o movimento a fazer neste frame
            // movimento = direção * velocidade * deltaTime
            float distance_to_move = npc.getSpeed() * tpf;
            Vector3f movement = direction.mult(distance_to_move);

            // ✅ CORRIGIDO: Converter Vector3f de volta para Vec3
            Vector3f newPosVec3f = allyPos.add(movement);
            Vec3 newPosition = new Vec3(newPosVec3f.x, newPosVec3f.y, newPosVec3f.z);
            npc.setPosition(newPosition);

            // Log (opcional, comentado para não poluir a consola)
            // System.out.println("[" + npc.getName() + "] Seguindo jogador. Distância: " + distance);
        } else {
            // O jogador está perto, parar
            // (Não fazer nada, o aliado mantém a posição)
            // System.out.println("[" + npc.getName() + "] Próximo do jogador. Parando.");
        }
    }

}
