import java.security.PublicKey;

public class Constants {
    public final static int RESOLUTION_X = 1920*2;
    public final static int RESOLUTION_Y = 1080*2;
    public final static float SENSITIVITY = 0.10f;
    public final static double FOV = 115;

    public final static int HORIZONTAL_CHUNK_SIZE = 256;
    public final static int VERTICAL_CHUNK_SIZE = 3;
    public final static int RENDER_DISTANCE = 1;

    public final static int TOTAL_CHUNKS = (int) (Math.pow(RENDER_DISTANCE,2) * 4);

    public final static double MAX_MOVEMENT_SPEED = 256;
    public final static double ACCELERATION_SPEED = 128f;
    public final static double DECELERATION_SPEED = 0.99;

    public final static float R_SKY_COLOR = (float) 153 / 255;
    public final static float G_SKY_COLOR = (float) 153 / 255;
    public final static float B_SKY_COLOR = (float) 255 / 255;

    public final static boolean UTIL_VELOCITY_REPORTING = false;
    public final static boolean UTIL_FPS_REPORTING = true;
    public final static boolean TESTING = true;
}
