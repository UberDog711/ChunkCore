import org.lwjgl.opengl.*;
import org.lwjgl.system.Configuration;

import java.util.ArrayList;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
 
public class Main {


    private long window;

    private Player my = new Player();;
    private Renderer renderer;
    public ArrayList<Chunk> chunks = new ArrayList<Chunk>();


    public void run() {
        init();
        
        loop();

        glfwFreeCallbacks(window);

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        renderer = new Renderer();



        // Create chunks
        double tot = ((Constants.RENDER_DISTANCE * 2)+1) * ((Constants.RENDER_DISTANCE * 2) +1);
        double cur = 0;
        for (int cx = -Constants.RENDER_DISTANCE; cx <= Constants.RENDER_DISTANCE; cx++) {
            for (int cz = -Constants.RENDER_DISTANCE; cz <= Constants.RENDER_DISTANCE; cz++) {
                Chunk chunk = new Chunk(cx * Constants.CHUNK_SIZE, cz * Constants.CHUNK_SIZE);
                chunk.create_world();
                chunks.add(chunk);
                cur ++;
                System.out.println(cur/tot*100 + "% : Done");
            }
        }
        double end_start_time = glfwGetTime();
        System.out.println(end_start_time);
    }

    private void loop() {
        double lastTime = glfwGetTime();
        int frames = 0;

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();

            my.handleInputs(window);
            renderer.loop(chunks);



            // FPS Counter logic
            frames++;
            double currentTime = glfwGetTime();
            if (currentTime - lastTime >= 1.0) {
                System.out.println("FPS: " + frames);
                frames = 0;
                lastTime = currentTime;
                //System.out.println("Position: X=" + (camera.cameraPos[0]) + " Y=" + (camera.cameraPos[1]) + " Z=" + (camera.cameraPos[2]));
                //System.out.println("Rotation X=" + (camera.cameraRotation[0]) + " Y=" + (camera.cameraRotation[1]));
            }

        }
    }


    public static void main() {
        new Main().run();
    }
}
