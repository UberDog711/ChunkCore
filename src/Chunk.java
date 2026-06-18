import java.util.*;
import java.nio.FloatBuffer;

//import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.BufferUtils;

public class Chunk {

// VARIABLES
    private final int x;
    private final int z;
    private final ArrayList<Vector3> vertex_data;
    private final ArrayList<float[]> color_data;
    private int vbo_id;
    private int cbo_id;
    private int vertex_count;

    private final int horizontalChunkSize;
    private final int verticalChunkSize;
    private final int wireframeLineWidth;

    private final int[][] offsets;
    private final int[][][] verticesOffsets;


    public Chunk(int x, int z) {
        this.x = x;
        this.z = z;

        offsets = new int[][] {
                {0,1,0},
                {0,-1,0},
                {-1,0,0},
                {1,0,0},
                {0,0,1},
                {0,0,-1},
        };

        verticesOffsets = new int[][][]{
                // Top
                {{0,0,0}, {1,0,0}, {1,0,1}, {0,0,1}},
                // Bottom
                {{0,-1,0}, {0,-1,1}, {1,-1,1}, {1,-1,0}},
                // Left
                {{0,0,0}, {0,0,1}, {0,-1,1}, {0,-1,0}},
                // Right
                {{1,0,0}, {1,-1,0}, {1,-1,1}, {1,0,1}},
                // Front
                {{0,0,1}, {1,0,1}, {1,-1,1}, {0,-1,1}},
                // Back
                {{0,0,0}, {0,-1,0}, {1,-1,0}, {1,0,0}}
        };
        verticalChunkSize = Constants.VERTICAL_CHUNK_SIZE;
        horizontalChunkSize = Constants.HORIZONTAL_CHUNK_SIZE;
        wireframeLineWidth = Constants.WIREFRAME_LINE_WIDTH;
        vertex_data = new ArrayList<>();
        color_data = new ArrayList<>();
    }
// CREATES FACE COLORS
    private float[] face_color(int face_num, int block_type) {
        float[] out = new float[3];
        float baseR;
        float baseG;
        float baseB;
        if (block_type == 1) { // Grass
            baseR = 35f/255;
            baseG = 74f/255f;
            baseB = 57f/255f;
        } else { // Example
            baseR = 30f/255;
            baseG = 70f/255f;
            baseB = 40f/255f;
        }

        // Brightness multipliers per face
        float multiplier;
        if (face_num == 0) {
            multiplier = 1.2f;
        } else if (face_num == 1) {
            multiplier = 0.6f;
        } else if (face_num == 2 || face_num == 3) {
            multiplier = 0.8f;
        } else {
            multiplier = 1.0f;
        }

        // Apply brightness to base color
        out[0] = (baseR * multiplier);
        out[1] = (baseG * multiplier);
        out[2] = (baseB * multiplier);

        return out;
    }
// FLATTENS VERTEX DATA
    private float[] flattenVertexData(ArrayList<Vector3> data) {
        float[] flat = new float[data.size() * 3];
        for (int i = 0; i < data.size(); i++) {
            Vector3 v = data.get(i);
            flat[i * 3] = (float) v.x;
            flat[i * 3 + 1] = (float) v.y;
            flat[i * 3 + 2] = (float) v.z;
        }
        return flat;
    }
// FLATTENS COLOR DATA
    private float[] flattenColorData(ArrayList<float[]> data) {
        float[] out = new float[data.size() * 3];
        for (int i = 0; i < data.size(); i++) {
            out[i * 3] = data.get(i)[0];
            out[i * 3 + 1] = data.get(i)[1];
            out[i * 3 + 2] = data.get(i)[2];
        }
        return out;
    }
// CREATES CHUNK
    public void create_world(byte[][][] blocks) {
        byte val;



        for (int bx = 0; bx < horizontalChunkSize; bx ++) {
            for (int bz = 0; bz < horizontalChunkSize; bz ++) {
                for (int by = 0; by < verticalChunkSize; by++) {
                    val = blocks[bx][by][bz];
                    if (val == 0) continue;

                    for (int face = 0; face < 6; face++) {


                        int ox = offsets[face][0];
                        int oy = offsets[face][1];
                        int oz = offsets[face][2];



                        int nx = bx + ox;
                        int ny = by + oy;
                        int nz = bz + oz;


                        if (nx >= 0 && nx < horizontalChunkSize &&
                            ny >= 0 && ny < verticalChunkSize &&
                            nz >= 0 && nz < horizontalChunkSize) {
                                if (blocks[nx][ny][nz] == 1) {
                                    continue;
                            }
                        }

                        // If we reach here, we draw the face — either empty neighbor OR outside chunk


                        int[][] faceVertices = verticesOffsets[face];
                        for (int[] cords : faceVertices) {
                            vertex_data.add(new Vector3(
                                    cords[0] + bx + this.x,
                                    cords[1] + by,
                                    cords[2] + bz + this.z
                            ));
                        }

                        float[] colorArray = face_color(face, val);

                        for (int c = 0; c < 4; c++) {
                            color_data.add(colorArray);
                        }
                    }
                }
            }
        }


        float[] flat_vertices = flattenVertexData(vertex_data);
        float[] flat_colors = flattenColorData(color_data);

        vertex_data.clear();
        color_data.clear();
        
        vertex_count = flat_vertices.length / 3;

        vbo_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(flat_vertices.length);
        vertexBuffer.put(flat_vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        cbo_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, cbo_id);
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(flat_colors.length);
        colorBuffer.put(flat_colors).flip();
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
// REMDERS CHUNK
   public void render(boolean wireframe) {
    glEnableClientState(GL_VERTEX_ARRAY);
    glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
    glVertexPointer(3, GL_FLOAT, 0, 0L);

    glEnableClientState(GL_COLOR_ARRAY);
    glBindBuffer(GL_ARRAY_BUFFER, cbo_id);
    glColorPointer(3, GL_FLOAT, 0, 0L);

    if (wireframe) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glLineWidth(wireframeLineWidth);
    } else {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    glDrawArrays(GL_QUADS, 0, vertex_count);

    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_COLOR_ARRAY);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
}
}

 