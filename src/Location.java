public class Location {
    private int x, y;

    public Location() {
        this(0, 0);
    }

    public Location(int x, int y) {
        setX(x);
        setY(y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getDistance(Location o) {
        return Math.sqrt(getDistanceSquared(o));
    }

    public int getDistanceSquared(Location o) {
        int dx = o.getX() - x;
        int dy = o.getY() - y;
        return dx * dx + dy * dy;
    }

    public void set(int x, int y) {
        setX(x);
        setY(y);
    }
}
