import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    private long window;
    private Player player;
    private WorldManager world;
    private Renderer renderer;
    private Util util;



    public void main() {
        init();
        
        loop();
        System.out.println("t");
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        renderer = new Renderer();
        util = new Util();
        window = renderer.getWindowID();
        world = new WorldManager();
        world.generateRandomHeightWorld();
        world.createWorld();

        player = new Player(window, util, world);



    }


    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            renderer.prepRender();
            player.handleInputs();
            renderer.render3d(
                    world.getChunks(),
                    player.getWireframeStatus());
            util.performanceCheck(player);
        }
        util.provideReport();
    }
}
