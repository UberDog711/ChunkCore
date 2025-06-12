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
                
            }


            

        }
    }
}