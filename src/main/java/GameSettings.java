/**
 * Stores user-configurable game settings.
 *
 * <p>Settings persist across menus and gameplay and are shared
 * between UI and game components.</p>
 */
public class GameSettings {

    /** Whether background music is enabled. */
    private boolean musicEnabled;

    /** UI text scale factor. */
    private double textScale = 1.0;

    /**
     * @return {@code true} if music is enabled
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Enables or disables background music.
     *
     * @param enabled {@code true} to enable music
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
    }

    /**
     * @return current text scale factor
     */
    public double getTextScale() {
        return textScale;
    }

    /**
     * Sets the UI text scale factor.
     *
     * @param scale scale factor to apply
     */
    public void setTextScale(double scale) {
        this.textScale = scale;
    }
}
