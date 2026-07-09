public class GameManager {
    public static Util utilToolkit;
    public static long windowID;
    public static WorldManager worldManager;
    public static Player player;
    public static InputHandler inputHandler;

    public GameManager() {
    }

    public void init(Util util,
                     long window,
                     WorldManager world,
                     Player player,
                     InputHandler inputs) {
        utilToolkit = util;
        windowID = window;
        worldManager = world;
        GameManager.player = player;
        inputHandler = inputs;
    }
}
