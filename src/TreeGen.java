
import java.util.ArrayList;
import java.util.Random;

public class TreeGen {
    public TreeGen(int x , int y , int z) {
        this.origin_x = x;
        this.origin_y = y;
        this.origin_z = z;
    }
    public int origin_x , origin_y , origin_z;
    public ArrayList<Vector3i> trunk_blocks = new ArrayList<>();
    public ArrayList<Vector3i> leef_blocks = new ArrayList<>();

    public void GenerateTree() {
        Random rand = new Random();
        int tree_height = rand.nextInt(10) + 10;
        Vector3i top_trunk;

        for ( int tree_y = 0 ; tree_y <= tree_height; tree_y++ ) {
            trunk_blocks.add(new Vector3i(origin_x , origin_y + tree_y , origin_z));
            if (tree_y == tree_height) {
                top_trunk = new Vector3i(origin_x , origin_y + tree_y , origin_z);
            }  
        }


    }

}
