import java.util.ArrayList;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {


    private long window;
    private Player player;
    private WorldManager world;
    private Renderer renderer;
    public ArrayList<Chunk> chunks = new ArrayList<Chunk>();


    public void main() {
        init();
        
        loop();

        glfwFreeCallbacks(window);

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        renderer = new Renderer();
        window = renderer.getWindowID();
        player = new Player(window);
        world = new WorldManager();
        world.generateWorld();



        // Create chunks
        double tot = ((Constants.RENDER_DISTANCE * 2)+1) * ((Constants.RENDER_DISTANCE * 2) +1);
        double cur = 0;
        for (int cx = -Constants.RENDER_DISTANCE; cx <= Constants.RENDER_DISTANCE; cx++) {
            for (int cz = -Constants.RENDER_DISTANCE; cz <= Constants.RENDER_DISTANCE; cz++) {
                Chunk chunk = new Chunk(cx * Constants.CHUNK_SIZE, cz * Constants.CHUNK_SIZE);
                chunk.create_world(world.getChunkData(cx,cz));
                chunks.add(chunk);
                cur ++;
                System.out.println(cur/tot*100 + "% : Done");
            }
        }
        double end_start_time = glfwGetTime();
        System.out.println(end_start_time + " : Elapsed to Load");
    }

    private void loop() {
        double lastTime = glfwGetTime();
        int frames = 0;
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();

            player.handleInputs();
            renderer.loop(chunks);



            // FPS Counter logic
            frames++;
            double currentTime = glfwGetTime();
            if (currentTime - lastTime >= 1.0) {
                System.out.println("FPS: " + frames);
                System.out.println(1 / player.getDeltaTime());
                System.out.println("Player Vel - X : " + player.getPlayerVelocity()[0]
                        + " Y : " + player.getPlayerVelocity()[1]
                        + " Z : " + player.getPlayerVelocity()[2]
                        + " Moving : " + player.getMoving()
                );
                frames = 0;
                lastTime = currentTime;
            }
        }
    }
}
