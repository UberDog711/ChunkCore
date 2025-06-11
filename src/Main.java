import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;


import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;



public class Main {
   
    public double player_x = 0;
    public double player_y = 0;
    public double player_z = 0;
    public double player_rotation_x = 0;
    public double player_rotation_y = 0;
    public double player_rotation_z = 0;
    private long window;

    public void run() {
        
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    
    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        window = glfwCreateWindow(1280, 720, "Voxel Game", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        glfwShowWindow(window);
    }
    private void movement_rotations() {
        


    }
    private void loop() {
        GL.createCapabilities();
       
        double time = glfwGetTime();
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
            // Render logic here
            double time_between_ticks = glfwGetTime() - time;
            // Test
            /*
            starts 
            tbt = 0.01 - 000
            or 
            little bit in
            tbt = 1.41 - 1.40

        
             */



            time = glfwGetTime();
            glfwSwapBuffers(window);
            glfwPollEvents();
            System.out.println(1/time_between_ticks);
            
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}