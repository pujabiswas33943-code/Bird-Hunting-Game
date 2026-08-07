package engine;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static Clip bgMusicClip;
    private static Map<String, Clip> soundCache = new HashMap<>();
    private static long pausedPosition = 0;
    private static boolean muted = false;   // ★ global mute state

    public static void playBackgroundMusic(String path) {
        if (muted) return;
        try {
            stopBackgroundMusic();
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("Sound file not found: " + path);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            bgMusicClip = AudioSystem.getClip();
            bgMusicClip.open(ais);
            bgMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgMusicClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopBackgroundMusic() {
        if (bgMusicClip != null && bgMusicClip.isRunning()) {
            bgMusicClip.stop();
            bgMusicClip.close();
        }
    }

    public static void play(String path) {
        if (muted) return;
        try {
            Clip clip = soundCache.get(path);
            if (clip == null) {
                File file = new File(path);
                if (!file.exists()) {
                    System.out.println("Sound file not found: " + path);
                    return;
                }
                clip = AudioSystem.getClip();
                AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                clip.open(ais);
                soundCache.put(path, clip);
            }
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pauseBackgroundMusic() {
        if (bgMusicClip != null && bgMusicClip.isRunning()) {
            pausedPosition = bgMusicClip.getMicrosecondPosition();
            bgMusicClip.stop();
        }
    }

    public static void resumeBackgroundMusic() {
        if (muted) return;
        if (bgMusicClip != null) {
            bgMusicClip.setMicrosecondPosition(pausedPosition);
            bgMusicClip.start();
        }
    }

    // ★ mute toggle করার method
    public static void setMuted(boolean m) {
        muted = m;
        if (muted) {
            stopBackgroundMusic();
        }
    }

    public static boolean isMuted() {
        return muted;
    }
}