import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.nio.FloatBuffer;



import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.BufferUtils;

public class Main {

    private long window;
    private CameraController camera;

    public static final int CHUNK_SIZE = 4;  // you can change this later

    private ArrayList<Chunk> chunks = new ArrayList<>();

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
        window = glfwCreateWindow(1280, 720, "Voxel Engine", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);  // enable v-sync
        glfwShowWindow(window);

        GL.createCapabilities();

        // Very basic OpenGL setup
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.5f, 0.7f, 1.0f, 1.0f); // sky color
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = 1280f / 720f;
        perspectiveGL(70.0f, aspect, 0.1f, 1000.0f);
        glMatrixMode(GL_MODELVIEW);

        camera = new CameraController();
        // Create chunks
        for (int cx = -10; cx <= 10; cx++) {
            for (int cy = -10; cy <= 10; cy++) {
                Chunk chunk = new Chunk(cx, cy);
                chunk.create_world();
                chunks.add(chunk);
            }
        }
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            glLoadIdentity();
            camera.handleKeys(window);
            camera.handleMouse(window);

            // Basic camera transform
            

            // Render all chunks
            for (Chunk chunk : chunks) {
                chunk.render();
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
