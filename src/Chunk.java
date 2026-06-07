import java.util.*;
import java.nio.FloatBuffer;

//import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.BufferUtils;

public class Chunk {
    public Chunk(int x, int z) {
        this.x = x;
        this.z = z;
        
    }
// VARIABLES
    private int x, z;
    private ArrayList<Vector3> vertex_data = new ArrayList<>();
    private ArrayList<Vector3> color_data = new ArrayList<>();
    private int vbo_id, cbo_id, vertex_count;
    //private Set<Integer> grass_blocks = new HashSet<>();
    private Map<Integer, Byte> blocks = new HashMap<>();

    private int chunk_size = Constants.CHUNK_SIZE;

    private ArrayList<Vector3i> offsets = new ArrayList<>(Arrays.asList(
        new Vector3i(0, 1, 0), new Vector3i(0, -1, 0),
        new Vector3i(-1, 0, 0), new Vector3i(1, 0, 0),
        new Vector3i(0, 0, 1), new Vector3i(0, 0, -1)
    ));
    private Map<Integer, Vector3[]> FACE_DEFS = createFaceDefs();


// METHOD CREATES LIST
    private Map<Integer, Vector3[]> createFaceDefs() {
        Map<Integer, Vector3[]> map = new HashMap<>();

        // Top face (+Y), looking from above
        map.put(0, new Vector3[]{
            new Vector3(0, 1, 1),
            new Vector3(1, 1, 1),
            new Vector3(1, 1, 0),
            new Vector3(0, 1, 0)
        });

        // Bottom face (-Y), looking from below
        map.put(1, new Vector3[]{
            new Vector3(0, 0, 0),
            new Vector3(1, 0, 0),
            new Vector3(1, 0, 1),
            new Vector3(0, 0, 1)
        });

        // Left face (-X), looking from left
        map.put(2, new Vector3[]{
            new Vector3(0, 0, 1),
            new Vector3(0, 1, 1),
            new Vector3(0, 1, 0),
            new Vector3(0, 0, 0)
        });

        // Right face (+X), looking from right
        map.put(3, new Vector3[]{
            new Vector3(1, 0, 0),
            new Vector3(1, 1, 0),
            new Vector3(1, 1, 1),
            new Vector3(1, 0, 1)
        });

        // Front face (+Z), looking from front
        map.put(4, new Vector3[]{
            new Vector3(0, 0, 1),
            new Vector3(1, 0, 1),
            new Vector3(1, 1, 1),
            new Vector3(0, 1, 1)
        });

        // Back face (-Z), looking from back
        map.put(5, new Vector3[]{
            new Vector3(0, 0, 0),
            new Vector3(0, 1, 0),
            new Vector3(1, 1, 0),
            new Vector3(1, 0, 0)
        });

        return map;
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
    public void create_world() {

       
        // Noise settings matching your Python code
        float scale = 150f;
        int seed = 0; // base in python pnoise2 base=0
        int octaves = 2;
        float persistence = 0.5f;
        float lacunarity = 0.5f;

        int baseX = this.x;
        int baseZ = this.z;

        for (int localX = 0; localX < chunk_size; localX++) {
            for (int localZ = 0; localZ < chunk_size; localZ++) {
                int worldX = baseX + localX;
                int worldZ = baseZ + localZ;

                float baseNoise = 0;

                // Calculate combined octave noise with offsets like Python's pnoise2 does internally
                float amplitude = 1f;
                float frequency = 1f;
                float noiseHeight = 0f;

                // You can generate offsets per octave to mimic Python's base param better if you want:
                Random rand = new Random(seed);
                float[] octaveOffsetsX = new float[octaves];
                float[] octaveOffsetsY = new float[octaves];
                for (int i = 0; i < octaves; i++) {
                    octaveOffsetsX[i] = rand.nextFloat() * 20000 - 10000;
                    octaveOffsetsY[i] = rand.nextFloat() * 20000 - 10000;
                }

                for (int o = 0; o < octaves; o++) {
                    float sampleX = (worldX / scale) * frequency + octaveOffsetsX[o];
                    float sampleZ = (worldZ / scale) * frequency + octaveOffsetsY[o];
                    float perlinValue = PerlinNoise.perlin(sampleX, sampleZ) * 2f - 1f;
                    noiseHeight += perlinValue * amplitude;

                    amplitude *= persistence;
                    frequency *= lacunarity;
                }
                baseNoise = noiseHeight;

                // Apply the Python equivalent: val = 1 + base_noise
                float val = 1f + baseNoise;

                // height = int(val^2 * 64)
                int height = (int)(val * val * 32f);

                // Clamp height if necessary
                if (height < 0) height = 0;
                if (height > 127) height = 127;

                // Add blocks for the column, you can adjust the fill depth here
//                for (int y = height - 2; y <= height; y++) {
                if (height < 0) continue;
                int packed = packPos(localX, height, localZ);
                blocks.put(packed, (byte) 0);

            }
        }
    

   


        
        


        for (int block : blocks.keySet()) {
            int bx = getX(block), by = getY(block), bz = getZ(block);
            for (int face = 0; face < 6; face++) {
                if (face == 1) continue;  // Optional: skip bottom faces

                Vector3i offset = offsets.get(face);
                int ox = offset.x, oy = offset.y, oz = offset.z;

                // Get neighbor position
                int nx = bx + ox;
                int ny = by + oy;
                int nz = bz + oz;

                // Check boundaries before checking grass_grass_blocks list
                if (nx >= 0 && nx < chunk_size &&
                    ny >= 0 && ny < chunk_size &&
                    nz >= 0 && nz < chunk_size) {
                    // Neighbor is inside the chunk
                           
                    if (blocks.containsKey(packPos(nx, ny, nz))) {
                        continue; // Skip face if neighbor is filled
                    }
                }

                // If we reach here, we draw the face — either empty neighbor OR outside chunk



                Vector3[] face_vertices = FACE_DEFS.get(face);
                for (Vector3 v : face_vertices) {
                    vertex_data.add(new Vector3(
                        v.x + bx + this.x,
                        v.y + by,
                        v.z + bz + this.z
                    ));
                }


                ArrayList<Double> colorList = face_color(face,0);
                Vector3 colorVector = new Vector3(colorList.get(0), colorList.get(1), colorList.get(2));
                for (int c = 0; c < 4; c++) {
                    color_data.add(colorVector);
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

 