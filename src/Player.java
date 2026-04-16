import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.opengl.GL11.*;

public class Player {
    private float[] playerPos = new float[] {0.0f, 0.0f, 0.0f};
    private float[] playerVel_Per_Second = new float[] {0.0f, 0.0f, 0.0f};
    private float[] playerRot = new float[] {0.0f, 0.0f};
    private double yawRad;
    private double pitchRad;


    private double deltaTime;
    private double currentTime;
    private double lastFrameTime;
    private float deltaSpeed;


    public float[] getPlayerPos () {return playerPos;}
    public float[] getPlayerRot () {return playerRot;}

    public void handleInputs(long window) {
        currentTime = glfwGetTime();
        deltaTime = currentTime - lastFrameTime;
        lastFrameTime = currentTime;
        deltaSpeed = (float) (deltaTime * Constants.ACCELERATION_SPEED);
        yawRad = Math.toRadians(playerRot[0]);
        pitchRad = Math.toRadians(playerRot[1]);


        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            moveForward();
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            moveBack();
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            moveLeft();
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            moveRight();
        }

        handleMouse(window);

        for (int i = 0; i < 3; i++) {
            playerVel_Per_Second[i] = Math.clamp(playerVel_Per_Second[i],-Constants.MAX_MOVEMENT_SPEED,Constants.MAX_MOVEMENT_SPEED);
            playerVel_Per_Second[i] *= (float) (1f - Constants.DECELERATION_SPEED * deltaTime);
            playerPos[i] += ((playerVel_Per_Second[i]) * deltaTime);
        }

        glLoadIdentity();
        glRotatef(playerRot[1], 1f, 0f, 0f); // pitch
        glRotatef(playerRot[0], 0f, 1f, 0f); // yaw
        glTranslatef(-playerPos[0], -playerPos[1], -playerPos[2]);
    }


    private void moveForward () {
        playerVel_Per_Second[0] += (float) (Math.sin(yawRad) * deltaSpeed);
        playerVel_Per_Second[1] -= (float) (Math.tan(pitchRad) * deltaSpeed);
        playerVel_Per_Second[2] -= (float) (Math.cos(yawRad) * deltaSpeed);
    }
    private void moveBack () {
        playerVel_Per_Second[0] -= (float) (Math.sin(yawRad) * deltaSpeed);
        playerVel_Per_Second[1] += (float) (Math.tan(pitchRad) * deltaSpeed);
        playerVel_Per_Second[2] += (float) (Math.cos(yawRad) * deltaSpeed);
    }
    private void moveLeft () {
        double leftYaw = yawRad - Math.PI / 2;
        playerVel_Per_Second[0] += (float) (Math.sin(leftYaw) * deltaSpeed);
        playerVel_Per_Second[2] -= (float) (Math.cos(leftYaw) * deltaSpeed);
    }
    private void moveRight () {
        double rightYaw = yawRad + Math.PI / 2;
        playerVel_Per_Second[0] += (float) (Math.sin(rightYaw) * deltaSpeed);
        playerVel_Per_Second[2] -= (float) (Math.cos(rightYaw) * deltaSpeed);
    }

    private void handleMouse (long window) {
        double[] xpos = new double[1];
        double[] ypos = new double[1];

        glfwGetCursorPos(window, xpos, ypos);

        double centerX = Constants.RESOLUTION_X / 2;
        double centerY = Constants.RESOLUTION_Y / 2;

        double deltaX = xpos[0] - centerX;
        double deltaY = ypos[0] - centerY;

        float sensitivity = Constants.SENSITIVITY;

        playerRot[0] += (float)(deltaX * sensitivity);
        playerRot[1] -= (float)(-deltaY * sensitivity);  // usually invert Y

        // Clamp pitch to avoid flipping
        playerRot[1] = Math.clamp(playerRot[1],-90,90);

        // Reset mouse to center for next frame
        glfwSetCursorPos(window, centerX, centerY);

    }
}
