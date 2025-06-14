public class Vector3 {
    public double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector3)) return false;
        Vector3 other = (Vector3) o;
        return Double.compare(x, other.x) == 0 &&
               Double.compare(y, other.y) == 0 &&
               Double.compare(z, other.z) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y, z);
    }
}
