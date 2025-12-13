package jogo.gameobject.character;

import com.jme3.math.Vector3f;
import jogo.framework.math.Vec3;
import jogo.gameobject.character.npc.ally.Ally;

public class FollowStrategy implements AIStrategy {

    @Override
    public void execute(Ally npc, Player player, float tpf) {
        // Converter posições do jogo (Vec3) para JMonkey (Vector3f) para facilitar a matemática
        Vec3 npcPosRaw = npc.getPosition();
        Vec3 playerPosRaw = player.getPosition();

        Vector3f npcPos = new Vector3f(npcPosRaw.x, npcPosRaw.y, npcPosRaw.z);
        Vector3f playerPos = new Vector3f(playerPosRaw.x, playerPosRaw.y, playerPosRaw.z);

        // Vetor direção (Player - NPC)
        Vector3f direction = playerPos.subtract(npcPos);
        float distance = direction.length();

        // Lógica: Seguir se estiver longe (> 3), parar se estiver perto
        if (distance > 3.0f) {
            direction.normalizeLocal(); // Normalizar para ter comprimento 1

            // Calcular nova posição: Posição Atual + (Direção * Velocidade * DeltaTime)
            Vector3f moveVec = direction.mult(npc.getSpeed() * tpf);
            Vector3f newPos = npcPos.add(moveVec);

            // Atualizar a posição no objeto Ally (convertendo de volta para Vec3)
            npc.setPosition(new Vec3(newPos.x, newPos.y, newPos.z));
        }
        // Se distance <= 3, ele simplesmente não atualiza a posição (fica parado)
    }
}