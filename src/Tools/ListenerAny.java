package Tools;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class ListenerAny {
    private final long window;
    private final int key;


    private boolean toggle = false;


    public ListenerAny (long window, int key) {
        this.window = window;
        this.key = key;
    }

    public boolean getStatus () {
        if (glfwGetKey(window, key) == GLFW_PRESS) {
            if (!toggle) {
                toggle = true;
                return true;
            }
            return false;
        }
        toggle = false;
        return false;
    }
}
