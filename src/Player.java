public class Player {
    private final double[] playerPos = new double[] {0.0f, 10.0f, 0.0f};
    private final double[] playerVel_Per_Second = new double[] {0.0f, 0.0f, 0.0f};
    private final double[] playerRot = new double[] {0.0f, 0.0f};

    private final long window;


    public Player(long window) {
        this.window = window;
    }


}
