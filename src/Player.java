import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.opengl.GL11.*;

public class Player {
    private final double[] playerPos = new double[] {0.0f, 0.0f, 0.0f};
    private final double[] playerVel_Per_Second = new double[] {0.0f, 0.0f, 0.0f};
    private final double[] playerRot = new double[] {0.0f, 0.0f};
    private double yawRad;
    private double pitchRad;
    private final long window;

    private double deltaTime;
    private double currentTime;
    private double lastFrameTime;
    private double deltaSpeed;
    private boolean moving;

    public double[] getPlayerPos () {return playerPos;}
    public double[] getPlayerRot () {return playerRot;}
    public double[] getPlayerVelocity () {return playerVel_Per_Second;}
    public double getDeltaTime () {return deltaTime;}
    public boolean getMoving () {return moving;}

    public Player (long window) {
        this.window = window;
    }

    public void handleInputs() {
        currentTime = glfwGetTime();
        deltaTime = currentTime - lastFrameTime;
        lastFrameTime = currentTime;
        deltaSpeed = (deltaTime * Constants.ACCELERATION_SPEED);
        yawRad = Math.toRadians(playerRot[0]);
        pitchRad = Math.toRadians(playerRot[1]);
        moving = false;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            moveForward();
            moving = true;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            moveBack();
            moving = true;
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            moveLeft();
            moving = true;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            moveRight();
            moving = true;
        }

        handleMouse(window);

        for (int i = 0; i < 3; i++) {
            playerVel_Per_Second[i] = Math.clamp(playerVel_Per_Second[i],-Constants.MAX_MOVEMENT_SPEED,Constants.MAX_MOVEMENT_SPEED);
            if (!moving) {
                playerVel_Per_Second[i] *= (1-(Constants.DECELERATION_SPEED * deltaTime));
                if (Math.abs(playerVel_Per_Second[i]) < 0.25) playerVel_Per_Second[i] = 0;
            }
            playerPos[i] += (float) ((playerVel_Per_Second[i]) * deltaTime);
        }

        glLoadIdentity();
        glRotated(playerRot[1], 1f, 0f, 0f); // pitch
        glRotated(playerRot[0], 0f, 1f, 0f); // yaw
        glTranslated(-playerPos[0], -playerPos[1], -playerPos[2]);
    }

    private void handleMouse (long window) {
        double[] xpos = new double[1];
        double[] ypos = new double[1];

        glfwGetCursorPos(window, xpos, ypos);

        double centerX = (double) Constants.RESOLUTION_X / 2;
        double centerY = (double) Constants.RESOLUTION_Y / 2;

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

    private void moveForward () {
        playerVel_Per_Second[0] +=  (Math.sin(yawRad) * deltaSpeed);
        playerVel_Per_Second[1] -=  (Math.tan(pitchRad) * deltaSpeed);
        playerVel_Per_Second[2] -=  (Math.cos(yawRad) * deltaSpeed);
    }
    private void moveBack () {
        playerVel_Per_Second[0] -=  (Math.sin(yawRad) * deltaSpeed);
        playerVel_Per_Second[1] +=  (Math.tan(pitchRad) * deltaSpeed);
        playerVel_Per_Second[2] +=  (Math.cos(yawRad) * deltaSpeed);
    }
    private void moveLeft () {
        double leftYaw = yawRad - Math.PI / 2;
        playerVel_Per_Second[0] +=  (Math.sin(leftYaw) * deltaSpeed);
        playerVel_Per_Second[2] -=  (Math.cos(leftYaw) * deltaSpeed);
    }
    private void moveRight () {
        double rightYaw = yawRad + Math.PI / 2;
        playerVel_Per_Second[0] +=  (Math.sin(rightYaw) * deltaSpeed);
        playerVel_Per_Second[2] -=  (Math.cos(rightYaw) * deltaSpeed);
    }


}
