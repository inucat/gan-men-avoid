package cloud.inucat.GanMenAvoid;

class Player extends Face {
    // Player speed
    private static final int PLAYER_SPEED = 3;

    private boolean isAlive = true;

    private boolean isMovingUp;
    private boolean isMovingDown;
    private boolean isMovingLeft;
    private boolean isMovingRight;

    public Player(int x, int y) {
        super(x, y, 0, 0);
        vx = vy = 0;
    }

    void setDirection(Direction dir, boolean isMoving) {
        switch (dir) {
            case UP:
                isMovingUp = isMoving;
                break;

            case DOWN:
                isMovingDown = isMoving;
                break;

            case LEFT:
                isMovingLeft = isMoving;
                break;

            case RIGHT:
                isMovingRight = isMoving;
                break;
        }
    }

    void updatePosition() {
        if (!isAlive)
            return;
        vx = (isMovingLeft ? -PLAYER_SPEED : 0) + (isMovingRight ? PLAYER_SPEED : 0);
        vy = (isMovingUp ? -PLAYER_SPEED : 0) + (isMovingDown ? PLAYER_SPEED : 0);
        x += vx;
        y += vy;
    }

    void getReady() {
        isAlive = true;
    }

    void getKnockedDown() {
        isAlive = false;
    }

    boolean isAlive() {
        return isAlive;
    }
}
