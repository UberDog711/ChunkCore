import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.util.*;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.BufferUtils;

public class Chunk {
    public Chunk(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private int x, y;
    private ArrayList<Vector3> vertex_data = new ArrayList<>();
    private ArrayList<Vector3> color_data = new ArrayList<>();
    private int vbo_id, cbo_id, vertex_count;
    private HashSet<Vector3> blocks = new HashSet<>(); // USE HASHSET HERE
    private int chunk_size = Main.CHUNK_SIZE;

    private ArrayList<Vector3> offsets = new ArrayList<>(Arrays.asList(
        new Vector3(0, 1, 0), new Vector3(0, -1, 0),
        new Vector3(-1, 0, 0), new Vector3(1, 0, 0),
        new Vector3(0, 0, -1), new Vector3(0, 0, 1)
    ));

    private Map<Integer, Vector3[]> FACE_DEFS = createFaceDefs();

    private Map<Integer, Vector3[]> createFaceDefs() {
        Map<Integer, Vector3[]> map = new HashMap<>();
        map.put(0, new Vector3[]{new Vector3(-0.5, 0.5, 0.5), new Vector3(0.5, 0.5, 0.5), new Vector3(0.5, 0.5, -0.5), new Vector3(-0.5, 0.5, -0.5)});
        map.put(1, new Vector3[]{new Vector3(-0.5, -0.5, -0.5), new Vector3(0.5, -0.5, -0.5), new Vector3(0.5, -0.5, 0.5), new Vector3(-0.5, -0.5, 0.5)});
        map.put(2, new Vector3[]{new Vector3(-0.5, -0.5, -0.5), new Vector3(-0.5, -0.5, 0.5), new Vector3(-0.5, 0.5, 0.5), new Vector3(-0.5, 0.5, -0.5)});
        map.put(3, new Vector3[]{new Vector3(0.5, -0.5, -0.5), new Vector3(0.5, 0.5, -0.5), new Vector3(0.5, 0.5, 0.5), new Vector3(0.5, -0.5, 0.5)});
        map.put(4, new Vector3[]{new Vector3(-0.5, -0.5, 0.5), new Vector3(0.5, -0.5, 0.5), new Vector3(0.5, 0.5, 0.5), new Vector3(-0.5, 0.5, 0.5)});
        map.put(5, new Vector3[]{new Vector3(-0.5, -0.5, -0.5), new Vector3(-0.5, 0.5, -0.5), new Vector3(0.5, 0.5, -0.5), new Vector3(0.5, -0.5, -0.5)});
        return map;
    }

    private ArrayList<Double> face_color(int face_num) {
        ArrayList<Double> base_color = new ArrayList<>();
        if (face_num == 0) {
            base_color.add(0.141 * 1.2);
            base_color.add(0.251 * 1.2);
            base_color.add(0.141 * 1.2);
        } else if (face_num == 1) {
            base_color.add(0.141 * 0.6);
            base_color.add(0.251 * 0.6);
            base_color.add(0.141 * 0.6);
        } else if (face_num == 2 || face_num == 3) {
            base_color.add(0.141 * 0.8);
            base_color.add(0.251 * 0.8);
            base_color.add(0.141 * 0.8);
        } else {
            base_color.add(0.141);
            base_color.add(0.251);
            base_color.add(0.141);
        }
        return base_color;
    }

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

    public void create_world() {
        // Generate blocks
        for (int x = 0; x < chunk_size; x++) {
            for (int y = 0; y < 1; y++) {
                for (int z = 0; z < chunk_size; z++) {
                    double worldX = this.x * chunk_size + x;
                    double worldY = y;
                    double worldZ = this.y * chunk_size + z;
                    blocks.add(new Vector3(worldX, worldY, worldZ));
                }
            }
        }

        for (Vector3 block : blocks) {
            double bx = block.x, by = block.y, bz = block.z;

            for (int face = 0; face < 6; face++) {
                if (face == 1) continue;  // Optional: skip bottom faces

                Vector3 offset = offsets.get(face);
                double ox = offset.x, oy = offset.y, oz = offset.z;

                if (blocks.contains(new Vector3(bx + ox, by + oy, bz + oz))) continue;

                Vector3[] face_vertices = FACE_DEFS.get(face);
                for (Vector3 v : face_vertices) {
                    vertex_data.add(new Vector3(v.x + bx, v.y + by, v.z + bz));
                }

                ArrayList<Double> colorList = face_color(face);
                Vector3 colorVector = new Vector3(colorList.get(0), colorList.get(1), colorList.get(2));
                for (int c = 0; c < 4; c++) {
                    color_data.add(colorVector);
                }
            }
        }

        float[] flat_vertices = flattenVertexData(vertex_data);
        float[] flat_colors = flattenColorData(color_data);
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

    public void render() {
        glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
        glVertexPointer(3, GL_FLOAT, 0, 0L);

        glEnableClientState(GL_COLOR_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, cbo_id);
        glColorPointer(3, GL_FLOAT, 0, 0L);

        glDrawArrays(GL_QUADS, 0, vertex_count);

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
