import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

public class Main {
    private GameManager gameManager;
    private InputHandler inputHandler;
    private WorldManager world;
    private Renderer renderer;
    private Util util;
    private Player player;


    public void main() {
        init();
        
        loop();

        glfwFreeCallbacks(GameManager.windowID);
        glfwDestroyWindow(GameManager.windowID);
        glfwTerminate();
        util.provideReport();
    }

    private void init() {
        // Needs Nothing
        gameManager = new GameManager();
        renderer = new Renderer();
        util = new Util();

        gameManager.init(util,
                renderer.getWindowID(),
                world,
                player,
                inputHandler
        );


        world = new WorldManager();
        inputHandler = new InputHandler();




    }


    private void loop() {
        while (renderer.shouldWindowClose()) {
            renderer.prepRender();
            inputHandler.handleInputs();
            renderer.render3d(
                    world.getChunks(),
                    inputHandler.getWireframeStatus());
            util.performanceCheck(inputHandler);
        }
    }
}
