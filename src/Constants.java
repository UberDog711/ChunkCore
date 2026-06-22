public class Constants {
    public final static int RESOLUTION_X = 2*1920;
    public final static int RESOLUTION_Y = 2*1080;
    public final static float SENSITIVITY = 0.10f;
    public final static double FOV = 115;

    public final static int HORIZONTAL_CHUNK_SIZE = 128;
    public final static int VERTICAL_CHUNK_SIZE = 64;
    public final static int RENDER_DISTANCE = 2;
    public final static int WIREFRAME_LINE_WIDTH = 3;


    public final static int CHUNK_COUNT = (int) (Math.pow(RENDER_DISTANCE,2) * 4);
    public final static int CUBIC_CHUNK_BLOCK_COUNT =
            HORIZONTAL_CHUNK_SIZE *
            HORIZONTAL_CHUNK_SIZE *
            VERTICAL_CHUNK_SIZE;

    public final static double MAX_MOVEMENT_SPEED = 64;
    public final static double ACCELERATION_SPEED = 32;
    public final static double DECELERATION_SPEED = 0.99;

    public final static float R_SKY_COLOR = (float) 153 / 255;
    public final static float G_SKY_COLOR = (float) 153 / 255;
    public final static float B_SKY_COLOR = (float) 255 / 255;

    public final static boolean UTIL_VELOCITY_REPORTING = false;
    public final static boolean UTIL_FPS_REPORTING = true;

    public static class Blocks {
        public final static int TOP_BLOCK_ID = 0;
        public final static int BOTTOM_BLOCK_ID = 1;
        public final static int LEFT_BLOCK_ID = 2;
        public final static int RIGHT_BLOCK_ID = 3;
        public final static int FRONT_BLOCK_ID = 4;
        public final static int BACK_BLOCK_ID = 5;

        public final static int GRASS_BLOCK_ID = 1;
        public final static float[] GRASS_TOP_RGB = {35f / 255, 75f / 255, 60f / 255};
        public final static float[] GRASS_SIDES_RGB = {75f / 255, 65f / 255, 50f / 255};

        public final static float[] DEFAULT_ALL_RGB = {190f / 255, 190f / 255, 190f / 255};
    }
}
