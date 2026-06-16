import java.util.Random;

import static org.lwjgl.glfw.GLFW.glfwGetTime;


public class WorldManager {


    float scale = 150f;
    int seed = 0; // base in python pnoise2 base=0
    int octaves = 2;
    float persistence = 0.5f;
    float lacunarity = 0.5f;
    float amplitude = 1f;
    float frequency = 1f;
    float noiseHeight = 0f;


    int chunkSize = Constants.CHUNK_SIZE;
    int renderDistance = Constants.RENDER_DISTANCE;



    private final int lenXZ = 2 * chunkSize * renderDistance;
    private final int halfLenXZ = lenXZ / 2;
    private final byte[][][] blocks = new byte[lenXZ][chunkSize][lenXZ];


    public byte[][][] getChunkBlockData(int chunkX, int chunkZ) {
        byte[][][] out = new byte[chunkSize][chunkSize][chunkSize];

        for (int x = 0; x < chunkSize; x++) {
            for (int z = 0; z < chunkSize; z++) {
                for (int y = 0; y < chunkSize; y++) {
                    int relX = solvePos(Math.clamp(x + (chunkSize * (chunkX)), -halfLenXZ, halfLenXZ - 1));
                    int relY = y;
                    int relZ = solvePos(Math.clamp(z + (chunkSize * (chunkZ)), -halfLenXZ, halfLenXZ - 1));
                    out[x][y][z] = blocks[relX][relY][relZ];
                }
            }
        }

        return out;
    }

    public byte getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    private int solvePos(int pos) {
        int out = pos + ((renderDistance * chunkSize));

        return out;
    }

    public void generateWorld() {
        int posX;
        int posZ;
        // 1
        // 2
        for (int x = 0; x < (renderDistance * 2) * (chunkSize); x++) {
            for (int z = 0; z < (renderDistance * 2) * (chunkSize); z++) {
                posX = x - (renderDistance * chunkSize);
                posZ = z - (renderDistance * chunkSize);
                float baseNoise = 0;


                Random rand = new Random(seed);
                float[] octaveOffsetsX = new float[octaves];
                float[] octaveOffsetsY = new float[octaves];
                for (int i = 0; i < octaves; i++) {
                    octaveOffsetsX[i] = rand.nextFloat() * 20000 - 10000;
                    octaveOffsetsY[i] = rand.nextFloat() * 20000 - 10000;
                }

                for (int o = 0; o < octaves; o++) {
                    float sampleX = (posX / scale) * frequency + octaveOffsetsX[o];
                    float sampleZ = (posZ / scale) * frequency + octaveOffsetsY[o];
                    float perlinValue = PerlinNoise.perlin(sampleX, sampleZ) * 2f - 1f;
                    noiseHeight += perlinValue * amplitude;

                    amplitude *= persistence;
                    frequency *= lacunarity;
                }
                baseNoise = noiseHeight;


                float val = 1f + baseNoise;


                int height = (int) (val * val * 127f);

                // Clamp height if necessary
                 height = (int) (Math.random() * 127);
                //height = Math.clamp(height, 0, chunkSize - 1);
                //System.out.println(x + "-" + z);
                blocks[solvePos(posX)][height][solvePos(posZ)] = 1;

            }
        }
    }

    public void generateMaxChunk() {
        int posX;
        int posZ;
        // 1
        // 2
        for (int x = 0; x < (renderDistance * 2) * (chunkSize); x++) {
            for (int z = 0; z < (renderDistance * 2) * (chunkSize); z++) {
                posX = x - (renderDistance * chunkSize);
                posZ = z - (renderDistance * chunkSize);
                for (int y = 0; y < chunkSize; y++) {
                    blocks[solvePos(posX)][y][solvePos(posZ)] = 1;
                }
            }
        }
    }

    public void createWorld() {

    }


}