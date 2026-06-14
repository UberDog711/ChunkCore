import org.lwjgl.opengl.GL;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Renderer {
    private long window;
    private int renderDistance = Constants.RENDER_DISTANCE;

    public Renderer() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        window = glfwCreateWindow(Constants.RESOLUTION_X, Constants.RESOLUTION_Y, "Voxel Engine", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create window");
        }

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);

        GL.createCapabilities();

        // Very basic OpenGL setup
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        glCullFace(GL_FRONT);
        glClearColor(Constants.R_SKY_COLOR, Constants.G_SKY_COLOR, Constants.B_SKY_COLOR, 1.0f); // sky color
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = Constants.RESOLUTION_X / Constants.RESOLUTION_Y;
        perspectiveGL(Constants.FOV, aspect, 0.1f, 4800.0f);
        glMatrixMode(GL_MODELVIEW);
        glfwSwapInterval(0);
    }

    public long getWindowID() {
        return window;
    }

    private void perspectiveGL(double fovY, double aspect, double zNear, double zFar) {
        double fH = Math.tan(Math.toRadians(fovY / 2)) * zNear;
        double fW = fH * aspect;
        glFrustum(-fW, fW, -fH, fH, zNear, zFar);
    }
    public void loop(ArrayList<Chunk> chunks) {
        for (Chunk chunk : chunks) {
            chunk.render(false);
        }

            glfwSwapBuffers(window);
            glfwPollEvents();
    }
}

