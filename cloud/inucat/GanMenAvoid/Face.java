package cloud.inucat.GanMenAvoid;

import java.awt.Image;
import java.awt.Point;

abstract class Face {
    // Face width and height
    public static final int SIZE = 32;

    private Image image;

    // Position
    int x;
    int y;

    // Current velocity
    int vx = 0;
    int vy = 0;

    public Face(int x, int y, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.vx = xSpeed;
        this.vy = ySpeed;
    }

    abstract void updatePosition();

    void setImage(Image image) {
        this.image = image;
    }

    Image getImage() {
        return this.image;
    }

    boolean overlapsWith(Face other) {
        Point meCenter = new Point(x, y);
        Point otherCenter = new Point(other.x, other.y);
        return meCenter.distance(otherCenter) < Face.SIZE;
    }
}
