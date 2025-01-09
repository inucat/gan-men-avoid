package cloud.inucat.GanMenAvoid;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener, Runnable {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    private static final int ENEMY_MAX_SPEED = 5;

    private static final int FPS = 60;

    // Time per frame (nanoseconds)
    private static final long PERIOD = (long) (1.0 / FPS * 1e9);

    // FPS calculation interval (nanoseconds)
    private static long MAX_STATS_INTERVAL = (long) 1e9;

    private volatile boolean running = false;
    private long calcInterval = 0L;
    private long prevCalcTime;

    // Game timer
    private long stageIntervalNanosec = 0L;

    private long frameCount = 0;
    private double actualFPS = 0.0;

    private DecimalFormat df = new DecimalFormat("0.0");

    private Random rng = new Random();
    private int difficulty = 1;

    private Image backgroundImage, playerImage, enemyImage, playerDownImage;

    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private SoundPlayer mSoundPlayer;
    private Thread th;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        loadImage();

        setFocusable(true);
        addKeyListener(this);

        player = new Player(300, 200);
        player.setImage(playerImage);

        mSoundPlayer = new SoundPlayer();
        mSoundPlayer.load("asset/nc395043_Aquqrium.wav", "BGM");
        mSoundPlayer.load("asset/se04.wav", "SE/down");
        mSoundPlayer.loop("BGM");

        for (int i = 0; i < difficulty; i++) {
            enemies.add(new Enemy(0, 0, rng.nextInt(ENEMY_MAX_SPEED) + 1, rng.nextInt(ENEMY_MAX_SPEED) + 1));
        }

        th = new Thread(this);
        th.start();
    }

    /**
     * Loads character pictures.
     */
    public void loadImage() {
        backgroundImage = new ImageIcon(getClass().getResource("../../../asset/pipo-bg004a.jpg")).getImage();
        playerImage = new ImageIcon(getClass().getResource("../../../asset/1F603.png")).getImage();
        enemyImage = new ImageIcon(getClass().getResource("../../../asset/1F621.png")).getImage();
        playerDownImage = new ImageIcon(getClass().getResource("../../../asset/1F635.png")).getImage();
    }

    /**
     * Checks an elapsed time and raises a game difficulty by one when 8 seconds
     * have passed since the previous invocation.
     */
    private void checkAndRaiseDifficulty() {
        stageIntervalNanosec += PERIOD;

        if (stageIntervalNanosec < 8e9)
            return;

        stageIntervalNanosec = 0L;
        difficulty++;
        enemies.add(new Enemy(0, 0, rng.nextInt(ENEMY_MAX_SPEED) + 1, rng.nextInt(ENEMY_MAX_SPEED) + 1));
    }

    public void run() {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;

        beforeTime = System.nanoTime();
        prevCalcTime = beforeTime;

        running = true;
        while (running) {
            gameUpdate();
            repaint();

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (PERIOD - timeDiff) - overSleepTime;

            if (sleepTime > 0) {
                // We have time to sleep
                try {
                    Thread.sleep(sleepTime / 1000000L); // nano->ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            } else {
                // We have no time to sleep
                overSleepTime = 0L;
                if (++noDelays >= 16) {
                    Thread.yield();
                    noDelays = 0;
                }
            }

            beforeTime = System.nanoTime();

            calcFPS();

            checkAndRaiseDifficulty();
        }

        System.exit(0);
    }

    /**
     * Calculates FPS.
     */
    private void calcFPS() {
        frameCount++;
        calcInterval += PERIOD;

        if (calcInterval < MAX_STATS_INTERVAL)
            return;

        long timeNow = System.nanoTime();
        long realElapsedTime = timeNow - prevCalcTime;

        actualFPS = ((double) frameCount / realElapsedTime) * 1e9;
        System.out.println(df.format(actualFPS));

        frameCount = 0L;
        calcInterval = 0L;
        prevCalcTime = timeNow;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g.drawImage(backgroundImage, 0, 0, this);

        g.setColor(Color.WHITE);
        g.drawString("Level:" + difficulty, 1, 20);

        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.56f);
        g2.setComposite(composite);
        g2.drawImage(player.getImage(), player.x, player.y, this);
        enemies.forEach((enemy) -> {
            g2.drawImage(enemyImage, enemy.x, enemy.y, this);
        });

    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                player.setDirection(Direction.UP, true);
                break;
            case KeyEvent.VK_DOWN:
                player.setDirection(Direction.DOWN, true);
                break;
            case KeyEvent.VK_LEFT:
                player.setDirection(Direction.LEFT, true);
                break;
            case KeyEvent.VK_RIGHT:
                player.setDirection(Direction.RIGHT, true);
                break;
            case KeyEvent.VK_SPACE:
                System.exit(0);
                break;
            case KeyEvent.VK_R:
                player.setImage(playerImage);
                player.getReady();
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                player.setDirection(Direction.UP, false);
                break;
            case KeyEvent.VK_DOWN:
                player.setDirection(Direction.DOWN, false);
                break;
            case KeyEvent.VK_LEFT:
                player.setDirection(Direction.LEFT, false);
                break;
            case KeyEvent.VK_RIGHT:
                player.setDirection(Direction.RIGHT, false);
                break;
        }
    }

    private void gameUpdate() {
        player.updatePosition();
        player.x = Math.max(player.x, 0);
        player.x = Math.min(player.x, WIDTH - Face.SIZE);
        player.y = Math.max(player.y, 0);
        player.y = Math.min(player.y, HEIGHT - Face.SIZE);

        enemies.forEach((enemy) -> {
            enemy.updatePosition();
            if (enemy.x < 0 || enemy.x + Face.SIZE >= WIDTH) {
                enemy.vx = -enemy.vx;
            }
            if (enemy.y < 0 || enemy.y + Face.SIZE >= HEIGHT) {
                enemy.vy = -enemy.vy;
            }
            if (enemy.overlapsWith(player) && player.isAlive()) {
                player.getKnockedDown();
                player.setImage(playerDownImage);
                mSoundPlayer.play("SE/down");
            }
        });
    }
}
