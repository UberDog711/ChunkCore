import org.lwjgl.opengl.*;
import org.lwjgl.system.Configuration;

import java.util.ArrayList;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
 
public class Main {


    private long window;
    private CameraController camera;
    Player my;

    private ArrayList<Chunk> chunks = new ArrayList<>();
    public static boolean wireframe = false;
    public void run() {
        init();
        
        loop();

        glfwFreeCallbacks(window);

        glfwDestroyWindow(window);
        glfwTerminate();
    }
    public void perspectiveGL(double fovY, double aspect, double zNear, double zFar) {
    double fH = Math.tan(Math.toRadians(fovY / 2)) * zNear;
    double fW = fH * aspect;
    glFrustum(-fW, fW, -fH, fH, zNear, zFar);
    }

    private void init() {


        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        window = glfwCreateWindow(Constants.RESOLUTION_X, Constants.RESOLUTION_Y, "Voxel Engine", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);  // enable v-sync
        glfwShowWindow(window);

        GL.createCapabilities();

        // Very basic OpenGL setup
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glClearColor((float) 153 / 255, (float) 153 / 255, (float) 255 / 255, 1.0f); // sky color
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = 1280f / 720f;
        perspectiveGL(Constants.FOV, aspect, 0.1f, 4800.0f);
        glMatrixMode(GL_MODELVIEW);

        camera = new CameraController();
        my = new Player();
        // Create chunks
        double tot = ((Constants.RENDER_DISTANCE * 2)+1) * ((Constants.RENDER_DISTANCE * 2) +1);
        double cur = 0;
        for (int cx = -Constants.RENDER_DISTANCE; cx <= Constants.RENDER_DISTANCE; cx++) {
            for (int cz = -Constants.RENDER_DISTANCE; cz <= Constants.RENDER_DISTANCE; cz++) {
                Chunk chunk = new Chunk(cx * Constants.CHUNK_SIZE, cz * Constants.CHUNK_SIZE);
                chunk.create_world();
                chunks.add(chunk);
                cur ++;
                System.out.println(cur/tot*100);
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
            //camera.handleKeys(window);
            //camera.handleMouse(window);
            my.handleInputs(window);

            // Render all chunks
            for (Chunk chunk : chunks) {
                chunk.render(wireframe);
            }

            glfwSwapBuffers(window);
            glfwPollEvents();

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


    public static void main(String[] args) {
        new Main().run();
    }
}
