package cloud.inucat.GanMenAvoid;

class Enemy extends Face {
    public Enemy(int x, int y, int xSpeed, int ySpeed) {
        super(x, y, xSpeed, ySpeed);
    }

    void updatePosition() {
        x += vx;
        y += vy;
    }
}
