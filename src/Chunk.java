import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.util.ArrayList;

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
    private ArrayList<Vector3> visible_blocks = new ArrayList<>();
    
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
}