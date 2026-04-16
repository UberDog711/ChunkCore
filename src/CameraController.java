import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

public class CameraController {
    public float[] cameraPos = new float[] {0f, 0f, 0f};    // x, y, z
    public float[] cameraRotation = new float[] {0f, 0f};  // yaw (around y), pitch (around x)

    private double lastFrameTime;
    private float movementSpeed = Constants.MAX_MOVEMENT_SPEED;
    private boolean prevQ = false;

    public void handleKeys(long window) {
        double currentTime = glfwGetTime();
        float delta = (float)(currentTime - lastFrameTime);
        lastFrameTime = currentTime;

        float speed = movementSpeed * delta;

        // Get key states
        boolean w = glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS;
        boolean s = glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS;
        boolean a = glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS;
        boolean d = glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS;
        boolean space = glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS;
        boolean ctrl = glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS;
        boolean q = glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS;
        

        // Movement calculations
        double yawRad = Math.toRadians(cameraRotation[0]);

        if (w) {
            cameraPos[0] += Math.sin(yawRad) * speed;
            cameraPos[2] -= Math.cos(yawRad) * speed;
        }
        if (s) {
            cameraPos[0] -= Math.sin(yawRad) * speed;
            cameraPos[2] += Math.cos(yawRad) * speed;
        }
        if (a) {
            double leftYaw = yawRad - Math.PI / 2;
            cameraPos[0] += Math.sin(leftYaw) * speed;
            cameraPos[2] -= Math.cos(leftYaw) * speed;
        }
        if (d) {
            double rightYaw = yawRad + Math.PI / 2;
            cameraPos[0] += Math.sin(rightYaw) * speed;
            cameraPos[2] -= Math.cos(rightYaw) * speed;
        }
        if (space) {
            cameraPos[1] += speed;
        }
        if (ctrl) {
            cameraPos[1] -= speed;
        }

    }
            

    public void handleMouse(long window) {
        double[] xpos = new double[1];
        double[] ypos = new double[1];
        glfwGetCursorPos(window, xpos, ypos);


        double centerX = Constants.RESOLUTION_X / 2;
        double centerY = Constants.RESOLUTION_Y / 2;

        double deltaX = xpos[0] - centerX;
        double deltaY = ypos[0] - centerY;

        float sensitivity = Constants.SENSITIVITY;

        cameraRotation[0] += (float)(deltaX * sensitivity);
        cameraRotation[1] -= (float)(-deltaY * sensitivity);  // usually invert Y

        // Clamp pitch to avoid flipping
        if (cameraRotation[1] > 90f) cameraRotation[1] = 90f;
        if (cameraRotation[1] < -90f) cameraRotation[1] = -90f;

        // Reset mouse to center for next frame
        glfwSetCursorPos(window, centerX, centerY);

        // Apply camera transforms
        glLoadIdentity();
        glRotatef(cameraRotation[1], 1f, 0f, 0f); // pitch
        glRotatef(cameraRotation[0], 0f, 1f, 0f); // yaw
        glTranslatef(-cameraPos[0], -cameraPos[1], -cameraPos[2]);
        

    }
}