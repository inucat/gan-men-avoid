package cloud.inucat.GanMenAvoid;

import java.io.File;
import java.util.HashMap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer {
    private HashMap<String, Clip> mSongStore = new HashMap<>();

    void load(String filename, String label) {
        Clip clip;
        try {
            clip = AudioSystem.getClip();
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filename));
            clip.open(stream);
            mSongStore.put(label, clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loop(String label) {
        play(label, Clip.LOOP_CONTINUOUSLY);
    }

    void play(String label, int count) {
        Clip clip = mSongStore.get(label);
        if (clip == null) {
            System.err.println("Song not loaded for label `" + label + "`");
            return;
        }
        clip.setFramePosition(0);
        clip.loop(count);
    }

    void play(String label) {
        play(label, 0);
    }
}
