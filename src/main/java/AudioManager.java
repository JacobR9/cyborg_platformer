import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Simple wrapper for JavaFX audio playback.
 *
 * <p>Handles looping background audio and basic playback controls.</p>
 */
public class AudioManager {

    /** Media player used for audio playback. */
    private final MediaPlayer player;

    /**
     * Creates a new audio manager for the given resource.
     *
     * @param resourcePath classpath path to the audio file
     */
    public AudioManager(String resourcePath) {
        Media media = new Media(
                getClass().getResource(resourcePath).toExternalForm()
        );
        player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE);
    }

    /** Starts or resumes playback. */
    public void play() {
        if (player.getStatus() != MediaPlayer.Status.PLAYING) {
            player.play();
        }
    }

    /** Pauses playback. */
    public void pause() { player.pause(); }

    /** Stops playback and resets position. */
    public void stop() { player.stop(); }

    /**
     * Sets playback volume.
     *
     * @param v01 volume in range {@code 0.0–1.0}
     */
    public void setVolume(double v01) {
        player.setVolume(clamp01(v01));
    }

    /**
     * Mutes or unmutes audio.
     *
     * @param muted {@code true} to mute audio
     */
    public void setMuted(boolean muted) {
        player.setMute(muted);
    }

    /**
     * Clamps a value to the range 0–1.
     *
     * @param v input value
     * @return clamped value
     */
    private double clamp01(double v) {
        return Math.max(0, Math.min(1, v));
    }
}
