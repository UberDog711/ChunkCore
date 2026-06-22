import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Util {
    private boolean velocityReporting = Constants.UTIL_VELOCITY_REPORTING;
    private boolean fpsReporting = Constants.UTIL_FPS_REPORTING;

    private double lastTime = glfwGetTime();
    private int frames = 0;
    private final ArrayList<Double> chunkRegenTimes = new ArrayList<>();
    private final ArrayList<Double> fpsRecords = new ArrayList<>();

    private double initChunkCreation = 0;
    private double endChunkCreation = 0;

    public void beginChunkGen() {
        initChunkCreation = glfwGetTime();
    }

    public void endChunkGen() {
        endChunkCreation = glfwGetTime();
    }


    public void getRegenTime (WorldManager world, int x, int z) {
        double startTime = glfwGetTime();
        world.regenerateChunk(x,z);
        chunkRegenTimes.add(glfwGetTime() - startTime);
    }

    public void performanceCheck(Player player) {
        frames++;
        double currentTime = glfwGetTime();
        if (currentTime - lastTime >= 1.0) {
            if (fpsReporting) {
                double fpsDelta = (1 / player.getDeltaTime());
                double fpsAVG = (frames + fpsDelta)/2;
                System.out.println("FPS AVG: " + fpsAVG +
                        "FPS Count: " + frames +
                        " FPS Delta: " + fpsDelta);
                fpsRecords.add(fpsAVG);
            }

            if (velocityReporting) {
                System.out.println("Player Vel - X : " + player.getPlayerVelocity()[0]
                        + " Y : " + player.getPlayerVelocity()[1]
                        + " Z : " + player.getPlayerVelocity()[2]
                        + " Moving Activated: " + player.getMovingKeyActivated()
                );
            }
            frames = 0;
            lastTime = currentTime;

        }
    }

    public void provideReport() {
        double totalC = 0;
        double totalF = 0;
        String totalBlocks;
        String fps;
        String timeToLoad;
        int horizontalRenderDistance;
        int totalChunks;


        for (double t : chunkRegenTimes) {
            totalC += t;
        }
        for (double f : fpsRecords) {
            totalF += f;
        }

        totalC /= chunkRegenTimes.size();
        totalF /= fpsRecords.size();
        totalChunks = Constants.CHUNK_COUNT;
        fps = String.format("%,.2f", totalF);
        timeToLoad = String.format("%,.2f", endChunkCreation - initChunkCreation);
        totalBlocks = String.format("%,.0f", (double) Constants.CHUNK_COUNT * Constants.CUBIC_CHUNK_BLOCK_COUNT);
        horizontalRenderDistance = Constants.RENDER_DISTANCE * Constants.HORIZONTAL_CHUNK_SIZE;


        System.out.println();
        System.out.println("---- Scene Information ----");
        System.out.println("Horizontal Render Distance: " + horizontalRenderDistance);
        System.out.println("Total Blocks In Scene: " + totalBlocks);
        System.out.println("Draw Calls: " + totalChunks);
        System.out.println();
        System.out.println("---- Performance ----");
        System.out.println("Loading Time: " + timeToLoad);
        System.out.println("Average FPS: " + fps);
        System.out.println("Average Chunk Regen Time: " + totalC);
        System.out.println("Chunk Regen Time Expressed as FPS: " + 1/totalC);

    }
}
