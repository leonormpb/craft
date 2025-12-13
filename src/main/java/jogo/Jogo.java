package jogo;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.system.AppSettings;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;

// Imports dos AppStates
import jogo.appstate.InputAppState;
import jogo.appstate.PlayerAppState;
import jogo.appstate.WorldAppState;
import jogo.appstate.HudAppState;
import jogo.appstate.RenderAppState;
import jogo.appstate.InteractionAppState;

// Imports do Engine
import jogo.engine.GameRegistry;
import jogo.engine.RenderIndex;

// Imports dos Objetos de Jogo
import jogo.gameobject.character.npc.ally.Ally;
import jogo.gameobject.character.Player;

/**
 * Aplicação Principal (Main) do IscteCraft.
 */
public class Jogo extends SimpleApplication {

    public static void main(String[] args) {
        Jogo app = new Jogo();
        app.setShowSettings(true);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("IscteCraft - Projeto POO");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setGammaCorrection(true);
        app.setSettings(settings);
        app.start();
    }

    private BulletAppState bulletAppState;

    @Override
    public void simpleInitApp() {
        // 1. Configurações Básicas (Câmara e Cursor)
        flyCam.setEnabled(false); // Desativar câmara padrão para usarmos a nossa
        inputManager.setCursorVisible(false);
        viewPort.setBackgroundColor(new ColorRGBA(0.6f, 0.75f, 1f, 1f)); // Cor do céu

        // 2. Configurar Física (Bullet)
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false); // Mete 'true' se quiseres ver as caixas de colisão
        PhysicsSpace physicsSpace = bulletAppState.getPhysicsSpace();

        // 3. Inicializar AppStates (A ordem é importante!)

        // Input (Teclado/Rato)
        InputAppState input = new InputAppState();
        stateManager.attach(input);

        // Mundo (Voxels e Lógica de Terreno)
        WorldAppState world = new WorldAppState(rootNode, assetManager, physicsSpace, cam, input);
        stateManager.attach(world);

        // Registo de Objetos e Renderização
        GameRegistry registry = new GameRegistry();
        RenderIndex renderIndex = new RenderIndex();

        // RenderAppState (Desenha os objetos registados)
        stateManager.attach(new RenderAppState(rootNode, assetManager, registry, renderIndex));

        // Interação (Partir blocos, usar itens)
        stateManager.attach(new InteractionAppState(rootNode, cam, input, renderIndex, world));

        // Player (Controlador do Jogador)
        PlayerAppState playerState = new PlayerAppState(rootNode, assetManager, cam, input, physicsSpace, world);
        stateManager.attach(playerState);

        // ==================================================================================
        // INÍCIO DA LÓGICA DO ALIADO (NPC)
        // ==================================================================================

        // Passo A: Obter a referência do Jogador (necessário para o Aliado o seguir)
        // Nota: Garante que tens o método "getPlayer()" na classe PlayerAppState!
        Player playerObject = playerState.getPlayer();

        if (playerObject != null) {
            // Passo B: Criar o Aliado e calcular a posição de spawn segura
            // (O método spawnAlly no WorldAppState verifica a altura do terreno)
            Ally meuAliado = world.spawnAlly(playerObject);

            // Passo C: REGISTAR O ALIADO (CRUCIAL)
            // Sem isto, o RenderAppState não sabe que o Aliado existe e não desenha o coração.
            if (meuAliado != null) {
                registry.add(meuAliado);
                System.out.println("Aliado [Coração] criado e registado com sucesso!");
            }
        } else {
            System.err.println("AVISO: Player é null. O Aliado não foi criado.");
        }

        // ==================================================================================
        // FIM DA LÓGICA DO ALIADO
        // ==================================================================================


        // 4. Pós-Processamento (SSAO - Sombras de oclusão ambiental)
        try {
            FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
            // Carregar SSAO via Reflection para evitar erros se a lib de efeitos faltar
            Class<?> ssaoCls = Class.forName("com.jme3.post.ssao.SSAOFilter");
            Object ssao = ssaoCls.getConstructor(float.class, float.class, float.class, float.class)
                    .newInstance(2.1f, 0.6f, 0.5f, 0.02f); // radius, intensity, scale, bias

            java.lang.reflect.Method addFilter = FilterPostProcessor.class.getMethod("addFilter", Class.forName("com.jme3.post.Filter"));
            addFilter.invoke(fpp, ssao);
            viewPort.addProcessor(fpp);
        } catch (Exception e) {
            System.out.println("SSAO não disponível (falta jme3-effects?): " + e.getMessage());
        }

        // 5. Interface Gráfica (HUD, Inventário, Mira)
        stateManager.attach(new HudAppState());

    }
}