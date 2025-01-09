package cloud.inucat.GanMenAvoid;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Entrypoint extends JFrame {
    public Entrypoint() {
        GamePanel panel = new GamePanel();
        Container contentPane = getContentPane();
        contentPane.add(panel);

        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);

        // initFullScreen();
    }

    public static void main(String[] args) {
        new Entrypoint();
    }

    private void initFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice device = ge.getDefaultScreenDevice();

        // Hide cursor when needed
        Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                new Point(), "");
        setCursor(cursor);

        if (!device.isFullScreenSupported()) {
            System.out.println("Full screen mode is not supported!");
            System.exit(0);
        }

        // Full screen mode!
        device.setFullScreenWindow(this);
        showDisplayModes(device);
        // Display mode can be changed after entering full screen mode
        // Choices are limited to the available ones
        // 640x480, 800x600, 1024x768 32bit seem available everywhere
        setDisplayMode(device, 640, 480, 32);
        // Hide the title bar and border
        setUndecorated(true);
    }

    /**
     * Shows all available display modes for the device.
     *
     * @param device - the device in question
     */
    private void showDisplayModes(GraphicsDevice device) {
        System.out.println("Available display modes:");
        DisplayMode[] modes = device.getDisplayModes();
        for (int i = 0; i < modes.length; i++) {
            System.out.print("(" + modes[i].getWidth() + ","
                    + modes[i].getHeight() + "," + modes[i].getBitDepth() + ","
                    + modes[i].getRefreshRate() + ") ");
            if ((i + 1) % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    /**
     * Sets display mode of the device to the specified values.
     *
     * @param device   - the device in question
     * @param width    - screen width
     * @param height   - screen height
     * @param bitDepth - color depth
     */
    private void setDisplayMode(GraphicsDevice device, int width, int height, int bitDepth) {
        if (!device.isDisplayChangeSupported()) {
            System.out.println("Display mode changing is not supported!");
            return;
        }

        DisplayMode dm = new DisplayMode(width, height, bitDepth,
                DisplayMode.REFRESH_RATE_UNKNOWN);
        device.setDisplayMode(dm);
    }
}
