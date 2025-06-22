import java.util.Objects;


public class Vector3b {
    public byte x, y, z;

    public Vector3b(byte x, byte y, byte z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector3i other = (Vector3i) obj;
        return Double.compare(x, other.x) == 0 &&
               Double.compare(y, other.y) == 0 &&
               Double.compare(z, other.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
