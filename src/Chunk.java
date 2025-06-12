import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
 
public class Chunk {
    private int x;
    private int y;
    private ArrayList<Vector3> vertex_data = new ArrayList<>();
    private ArrayList<Vector3> color_data = new ArrayList<>();
    private int vbo_id;
    private int cbo_id;
    private ArrayList<Vector3> blocks = new ArrayList<>();
    
// Needed Variabhles
    private int chunk_size = Main.CHUNK_SIZE;
    private ArrayList<Vector3> offsets = new ArrayList<>(Arrays.asList(new Vector3(0, 1, 0), new Vector3(0,-1,0), new Vector3(-1,0,0), new Vector3(1,0,0), new Vector3(0,0,-1), new Vector3(0,0,1)));
    private Map<Integer, Vector3[]> FACE_DEFS = createFaceDefs();
// DICTIONARY MAKER
    private Map<Integer, Vector3[]> createFaceDefs() {
        Map<Integer, Vector3[]> map = new HashMap<>();

        map.put(0, new Vector3[]{
            new Vector3(-0.5f, 0.5f,  0.5f),
            new Vector3( 0.5f, 0.5f,  0.5f),
            new Vector3( 0.5f, 0.5f, -0.5f),
            new Vector3(-0.5f, 0.5f, -0.5f)
        });

        map.put(1, new Vector3[]{
            new Vector3(-0.5f, -0.5f, -0.5f),
            new Vector3( 0.5f, -0.5f, -0.5f),
            new Vector3( 0.5f, -0.5f,  0.5f),
            new Vector3(-0.5f, -0.5f,  0.5f)
        });

        map.put(2, new Vector3[]{
            new Vector3(-0.5f, -0.5f, -0.5f),
            new Vector3(-0.5f, -0.5f,  0.5f),
            new Vector3(-0.5f,  0.5f,  0.5f),
            new Vector3(-0.5f,  0.5f, -0.5f)
        });

        map.put(3, new Vector3[]{
            new Vector3( 0.5f, -0.5f, -0.5f),
            new Vector3( 0.5f,  0.5f, -0.5f),
            new Vector3( 0.5f,  0.5f,  0.5f),
            new Vector3( 0.5f, -0.5f,  0.5f)
        });

        map.put(4, new Vector3[]{
            new Vector3(-0.5f, -0.5f,  0.5f),
            new Vector3( 0.5f, -0.5f,  0.5f),
            new Vector3( 0.5f,  0.5f,  0.5f),
            new Vector3(-0.5f,  0.5f,  0.5f)
        });

        map.put(5, new Vector3[]{
            new Vector3(-0.5f, -0.5f, -0.5f),
            new Vector3(-0.5f,  0.5f, -0.5f),
            new Vector3( 0.5f,  0.5f, -0.5f),
            new Vector3( 0.5f, -0.5f, -0.5f)
        });

        return map;
    }
// FACE COLOR PICKER
    private ArrayList<Double> face_color(int face_num) {
        ArrayList<Double> base_color = new ArrayList<>();
        if (face_num == 0) {
            base_color.add(0.141*1.2);
            base_color.add(0.251*1.2);
            base_color.add(0.141*1.2);
        }
        if (face_num == 1) {
            base_color.add(0.141*0.6);
            base_color.add(0.251*0.6);
            base_color.add(0.141*0.6);
        }
        if (face_num == 2 || face_num == 3 ) {
            base_color.add(0.141*0.8);
            base_color.add(0.251*0.8);
            base_color.add(0.141*0.8);
        }
        if (face_num == 4 || face_num == 5) {
            base_color.add(0.141);
            base_color.add(0.251);
            base_color.add(0.141);
        }
        return base_color;
        
    }
// FLAT VERTEX DATA
    private float[] flattenVertexData(ArrayList<Vector3> vertexData) {
        // Each Vector3 has 3 components: x, y, z
        float[] flatData = new float[vertexData.size() * 3];
        
        for (int i = 0; i < vertexData.size(); i++) {
            Vector3 v = vertexData.get(i);
            flatData[i * 3] = (float)v.x;
            flatData[i * 3 + 1] = (float)v.y;
            flatData[i * 3 + 2] = (float)v.z;
        }
        
        return flatData;
    }
// FLATTEN COLOR DATA
    private float[] flattenColorData(ArrayList<Vector3> colorData) {
        float[] flatData = new float[colorData.size() * 3];
        
        for (int i = 0; i < colorData.size(); i++) {
            Vector3 v = colorData.get(i);
            flatData[i * 3] = (float)v.x;
            flatData[i * 3 + 1] = (float)v.y;
            flatData[i * 3 + 2] = (float)v.z;
        }
        
        return flatData;
    }

// CHUNK MAKER 
    private void create_world() {
        for (int x = 0; x < chunk_size; x++) {
            for (int y = 0; y < chunk_size; y++) {
                for (int z = 0; z < chunk_size; z++) {
                    blocks.add(new Vector3(x,y,z));
                }
            }
        }
        int i = -1;
        for (Vector3 temp_block : blocks) {
            i++;
            if (i == 1) {
                continue;
            }
            double block_x = temp_block.x; 
            double block_y = temp_block.y;
            double block_z = temp_block.z;
            for (Vector3 temp_offset : offsets) {
                double off_x = temp_offset.x;
                double off_y = temp_offset.y;
                double off_z = temp_offset.z;
                if (blocks.contains( new Vector3(block_x-off_x, block_y-off_y, block_z-off_z))) {
                    continue;
                }
                Vector3[] face_vertices = FACE_DEFS.get(0);
                
                for (Vector3 temp_vertex: face_vertices) {
                    double vertex_x = temp_vertex.x;
                    double vertex_y = temp_vertex.y;
                    double vertex_z = temp_vertex.z;
                    vertex_data.add(new Vector3(vertex_x+block_x, vertex_y+block_y, vertex_z+block_z));
                }
                ArrayList<Double> colorList = face_color(i);
                Vector3 colorVector = new Vector3(colorList.get(0), colorList.get(1), colorList.get(2));
                color_data.add(colorVector);
            }
                // Assume flat_vertex_data and flat_color_data are float[] arrays (flattened vertex/color data)
        float[] flat_vertex_data = flattenVertexData(vertex_data);
        float[] flat_color_data = flattenColorData(color_data);

        // Generate VBO ID for vertex data
        vbo_id = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_id);

        // Create FloatBuffer from flat_vertex_data
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(flat_vertex_data.length);
        vertexBuffer.put(flat_vertex_data);
        vertexBuffer.flip();  // Prepare buffer for reading by OpenGL

        // Upload vertex data to GPU
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Generate VBO ID for color data
        cbo_id = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cbo_id);

        // Create FloatBuffer from flat_color_data
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(flat_color_data.length);
        colorBuffer.put(flat_color_data);
        colorBuffer.flip();  // Prepare buffer

        // Upload color data to GPU
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);

        // Unbind buffer (bind to 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);



            

        }
    }
}