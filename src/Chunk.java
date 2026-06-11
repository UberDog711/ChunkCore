import java.util.*;
import java.nio.FloatBuffer;

//import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.BufferUtils;

public class Chunk {

// VARIABLES
    private int x, z;
    private ArrayList<Vector3> vertex_data = new ArrayList<>();
    private ArrayList<Vector3> color_data = new ArrayList<>();
    private int vbo_id, cbo_id, vertex_count;


    private int chunk_size = Constants.CHUNK_SIZE;
    private int[][] offsets;
    // Origin - All except Y

    private int[][][] verticesOffsets = new int[6][4][3];






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
    }
// CREATES FACE COLORS
    private ArrayList<Double> face_color(int face_num,int block_type) {
        ArrayList<Double> base_color = new ArrayList<>();
        double BASE_R;
        double BASE_G;
        double BASE_B;
        if (block_type == 0) { // Grass
            BASE_R = 35f/255;
            BASE_G = 74f/255f;
            BASE_B = 57f/255f;
        } else { // Example
            BASE_R = 30f/255;
            BASE_G = 70f/255f;
            BASE_B = 40f/255f;
        }

        // Brightness multipliers per face
        double multiplier;
        if (face_num == 0) {
            multiplier = 1.2;
        } else if (face_num == 1) {
            multiplier = 0.6;
        } else if (face_num == 2 || face_num == 3) {
            multiplier = 0.8;
        } else {
            multiplier = 1.0;
        }

        // Apply brightness to base color
        base_color.add(BASE_R * multiplier);
        base_color.add(BASE_G * multiplier);
        base_color.add(BASE_B * multiplier);

        return base_color;
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
    private float[] flattenColorData(ArrayList<Vector3> data) {
        float[] flat = new float[data.size() * 3];
        for (int i = 0; i < data.size(); i++) {
            Vector3 v = data.get(i);
            flat[i * 3] = (float) v.x;
            flat[i * 3 + 1] = (float) v.y;
            flat[i * 3 + 2] = (float) v.z;
        }
        return flat;
    }
//FLATTENS BLOCK DATA
    public int packPos(int x, int y, int z) {
        return (x & 0x7F)       // bits 0-5
            | ((y & 0x7F) << 7)  // bits 6-11
            | ((z & 0x7F) << 14); // bits 12-17

    }
// UNFLATTENS BLOCK DATA
    public int getX(int packed) {
        return packed & 0x7F;
    }
    public int getY(int packed) {
        return (packed >> 7) & 0x7F;
    }
    public int getZ(int packed) {
        return (packed >> 14) & 0x7F;
    }
// CREATES CHUNK
    public void create_world(byte[][][] blocks) {
        byte val;


        for (int bx = 0; bx < chunk_size; bx ++) {
            for (int bz = 0; bz < chunk_size; bz ++) {
                for (int by = 0; by < chunk_size; by++) {
                    val = blocks[bx][by][bz];
                    if (val == 0) continue;

                    for (int face = 0; face < 6; face++) {


                        int ox = offsets[face][0];
                        int oy = offsets[face][1];
                        int oz = offsets[face][2];



                        int nx = bx + ox;
                        int ny = by + oy;
                        int nz = bz + oz;


                        if (nx >= 0 && nx < chunk_size &&
                            ny >= 0 && ny < chunk_size &&
                            nz >= 0 && nz < chunk_size) {
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

                        ArrayList<Double> colorList = face_color(face, 0);
                        Vector3 colorVector = new Vector3(colorList.get(0), colorList.get(1), colorList.get(2));
                        for (int c = 0; c < 4; c++) {
                            color_data.add(colorVector);
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
        glLineWidth(1);
    } else {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    glDrawArrays(GL_QUADS, 0, vertex_count);

    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_COLOR_ARRAY);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
}
}

 