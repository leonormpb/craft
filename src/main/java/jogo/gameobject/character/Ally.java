package jogo.gameobject.character;

import jogo.gameobject.npc.AIStrategy;
import com.jme3.math.Vector3f;

/**
 * Classe que representa um NPC Aliado no jogo IscteCraft.
 * Um Ally é um personagem controlado pela IA que ajuda o jogador.
 *
 * Estende Character (superclasse abstrata) e implementa o padrão Strategy
 * para permitir diferentes comportamentos de IA de forma flexível.
 *
 * Responsabilidades:
 * - Gerir o estado do aliado (health, speed, position)
 * - Executar a estratégia de IA atual via updateAI()
 * - Permitir mudança de estratégia em tempo de execução
 */
public class Ally extends Character {

    // Atributos específicos do Ally
    private AIStrategy currentStrategy;

    /**
     * Construtor do Ally.
     *
     * @param name O nome do aliado (ex: "Companheiro")
     * @param health A saúde inicial do aliado (ex: 100)
     * @param speed A velocidade de movimento do aliado (ex: 5.0f)
     * @param initialStrategy A estratégia de IA inicial (ex: new FollowStrategy())
     */
    public Ally(String name, float health, float speed, AIStrategy initialStrategy) {
        // Chama o construtor da superclasse Character
        // Adapte os parâmetros conforme o construtor real de Character
        super(name);  // Ou: super(name, health, speed) se Character aceitar
        this.currentStrategy = initialStrategy;
    }

    /**
     * Obtém a estratégia de IA atual.
     * @return A estratégia atual
     */
    public AIStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    /**
     * Define uma nova estratégia de IA.
     * Isto permite trocar de comportamento durante o jogo.
     *
     * @param strategy A nova estratégia a executar
     */
    public void setStrategy(AIStrategy strategy) {
        this.currentStrategy = strategy;
    }

    /**
     * Atualiza o comportamento do aliado a cada frame.
     * É chamado pelo engine a cada atualização.
     *
     * Implementação do método abstrato de Character.
     * Aqui delegamos o comportamento à estratégia atual via padrão Strategy.
     *
     * @param tpf Tempo por frame (para movimento suave independente de FPS)
     */
    @Override
    public void updateAI(float tpf) {
        // Nota: Este método precisaria de acesso ao Player.
        // Você pode:
        // 1. Passá-lo via um método separado
        // 2. Guardá-lo como atributo do Ally
        // 3. Usar um callback/listener

        // Por enquanto, deixamos vazio - será implementado conforme sua estrutura
    }

    /**
     * Executa a estratégia de IA com o jogador como referência.
     * Chame este método a partir do WorldAppState passando o Player.
     *
     * @param player O jogador (alvo da estratégia)
     * @param tpf Tempo por frame
     */
    public void executeStrategy(Player player, float tpf) {
        if (currentStrategy != null) {
            currentStrategy.execute(this, player, tpf);
        }
    }

    /**
     * Devolve informações sobre o aliado como String.
     * @return String com dados do aliado
     */
    @Override
    public String toString() {
        return "Ally{" +
                "name='" + getName() + '\'' +
                ", strategy=" + (currentStrategy != null ? currentStrategy.getClass().getSimpleName() : "None") +
                '}';
    }

}
