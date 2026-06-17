import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Util {
    private boolean velocityReporting = Constants.UTIL_VELOCITY_REPORTING;
    private boolean fpsReporting = Constants.UTIL_FPS_REPORTING;

    private double lastTime = glfwGetTime();
    private int frames = 0;

    private final ArrayList<Double> chunkRegenTimes = new ArrayList<>();
    private final ArrayList<Double> fpsRecords = new ArrayList<>();

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
        for (double t : chunkRegenTimes) {
            totalC += t;
        }
        for (double f : fpsRecords) {
            totalF += f;
        }

        totalC /= chunkRegenTimes.size();
        totalF /= fpsRecords.size();
        System.out.println();
        System.out.println("Total Block Render Distance: " + Constants.RENDER_DISTANCE * Constants.HORIZONTAL_CHUNK_SIZE);
        System.out.println("Average FPS: " + totalF);
        System.out.println();
        System.out.println("Average Chunk Regen Time: " + totalC);
        System.out.println("Chunk Regen Time Expressed as FPS: " + 1/totalC);

    }
}
