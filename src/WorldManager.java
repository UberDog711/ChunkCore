import java.util.Random;


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

    private byte[][][] blocks = new byte[2 * chunkSize * renderDistance][chunkSize][2 * chunkSize*renderDistance];


    public byte[][][] getChunkData(int chunkX, int chunkZ) {
        byte[][][] out = new byte[chunkSize][chunkSize][chunkSize];

        for (int x = 0; x < chunkSize; x++) {
            for (int y = 0; y < chunkSize; y++) {
                for (int z = 0; z < chunkSize; z++) {
                    int relX = x + (chunkSize * chunkX);
                    int relY = y;
                    int relZ = z + (chunkSize * chunkZ);
                    out[x][y][z] = blocks[solvePos(relX,-1)][relY][solvePos(relZ,-2)];
                }
            }
        }

        return out;
    }

    public byte getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    private int solvePos(int pos, int maxLen) {
        int out = pos + ((renderDistance * chunkSize));
        if (out < 0 || out >= maxLen ) {
            System.out.println("Input: " + pos + " Output: " + out + " Max: " + maxLen);
        }
        return out;
    }

    public void generateWorld() {
        int posX;
        int posZ;
        // 1
        // 2
        for (int x = 0; x < (renderDistance * 2)*(chunkSize); x++) {
            for (int z = 0; z < (renderDistance * 2)*(chunkSize); z++) {
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


                int height = (int) (val * val * 32f);

                // Clamp height if necessary
                height = Math.clamp(height,0,127);
                System.out.println(x + "-" + z);
                blocks[solvePos(posX,blocks.length)][height][solvePos(posZ,blocks.length)] = 1;

            }
        }
    }
}
