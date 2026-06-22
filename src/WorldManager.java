import static org.lwjgl.glfw.GLFW.glfwGetTime;


public class WorldManager {
    int horizontalChunkSize = Constants.HORIZONTAL_CHUNK_SIZE;
    int verticalChunkSize = Constants.VERTICAL_CHUNK_SIZE;
    int renderDistance = Constants.RENDER_DISTANCE;
    Chunk[] chunks = new Chunk[Constants.CHUNK_COUNT];
    Util util;



    private final int lenXZ = 2 * horizontalChunkSize * renderDistance;
    private final int halfLenXZ = lenXZ / 2;
    private final byte[][][] blocks = new byte[lenXZ][verticalChunkSize][lenXZ];

    public WorldManager (Util util) {
        this.util = util;
    }

    public byte[][][] getChunkBlockData(int chunkX, int chunkZ) {
        byte[][][] out = new byte[horizontalChunkSize][horizontalChunkSize][horizontalChunkSize];

        for (int x = 0; x < horizontalChunkSize; x++) {
            for (int z = 0; z < horizontalChunkSize; z++) {
                for (int y = 0; y < verticalChunkSize; y++) {
                    int relX = solvePos(Math.clamp(x + (horizontalChunkSize * (chunkX)), -halfLenXZ, halfLenXZ - 1));
                    int relY = y;
                    int relZ = solvePos(Math.clamp(z + (horizontalChunkSize * (chunkZ)), -halfLenXZ, halfLenXZ - 1));
                    out[x][y][z] = blocks[relX][relY][relZ];
                }
            }
        }
        return out;
    }

    public byte getBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }

    public Chunk[] getChunks() {return chunks;}

    public int getChunk(int x, int z) {
        int relX = x + renderDistance;
        int relZ = z + renderDistance;
        return (relZ * renderDistance * 2) + relX;
    }

    private int solvePos(int pos) {
        return pos + ((renderDistance * horizontalChunkSize));
    }

    public void generateRandomHeightWorld() {
        int posX;
        int posZ;
        int posY;
        // 1
        // 2
        for (int x = 0; x < (renderDistance * 2) * (horizontalChunkSize); x++) {
            for (int z = 0; z < (renderDistance * 2) * (horizontalChunkSize); z++) {
                posX = x - (renderDistance * horizontalChunkSize);
                posZ = z - (renderDistance * horizontalChunkSize);
                int maxHeightInc = (int) ((Math.random() * verticalChunkSize));
                for (posY = 0; posY <= maxHeightInc; posY++) {
                    blocks[solvePos(posX)][posY][solvePos(posZ)] = 1;
                }
            }
        }
    }

    public void generateMaxChunk() {
        int posX;
        int posZ;
        // 1
        // 2
        for (int x = 0; x < (renderDistance * 2) * (horizontalChunkSize); x++) {
            for (int z = 0; z < (renderDistance * 2) * (horizontalChunkSize); z++) {
                posX = x - (renderDistance * horizontalChunkSize);
                posZ = z - (renderDistance * horizontalChunkSize);
                for (int y = 0; y < verticalChunkSize; y++) {
                    blocks[solvePos(posX)][y][solvePos(posZ)] = 1;
                }
            }
        }
    }

    public void regenerateChunk(int x, int z) {
        chunks[getChunk(x,z)].createChunk(getChunkBlockData(x,z));
    }

    public void createWorld() {
        int tot = (int) (Math.pow(renderDistance,2) * 4);
        int cur = 0;
        util.beginChunkGen();
        for (int cx = -renderDistance; cx < renderDistance; cx++) {
            for (int cz = -renderDistance; cz < renderDistance; cz++) {
                Chunk chunk = new Chunk(cx * Constants.HORIZONTAL_CHUNK_SIZE, cz * Constants.HORIZONTAL_CHUNK_SIZE);
                chunk.createChunk(getChunkBlockData(cx,cz));
                chunks[getChunk(cx,cz)] = chunk;

                cur ++;
                System.out.println("Chunk: " + cur + " Out of: " + tot + " Overall: " + (double) cur/tot*100 + "% : Done");
            }
        }
        util.endChunkGen();
        double end_start_time = glfwGetTime();
        System.out.println(end_start_time + " : Elapsed to Load");
    }
}