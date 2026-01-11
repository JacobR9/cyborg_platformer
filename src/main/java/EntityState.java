/**
 * Represents the directional and animation state of an entity.
 * <p>
 * {@code EntityState} is an immutable value object used to drive animation and
 * behavioural decisions. It stores the entity's facing direction and a logical
 * state label (for example: {@code "idle"}, {@code "running"}, {@code "aerial"}).
 *
 * @author psyjr14
 */
public class EntityState {

    /** True if the entity is facing forwards, false if facing backwards. */
    private final boolean isFacingForward;

    /** Current logical state (e.g. idle, running, aerial, hurt, shooting). */
    private final String state;

    /**
     * Constructs a new immutable entity state.
     *
     * @param isFacingForward whether the entity is facing forwards
     * @param state current logical state label
     */
    public EntityState(boolean isFacingForward, String state) {
        this.isFacingForward = isFacingForward;
        this.state = state;
    }

    /**
     * Returns the current logical state label.
     *
     * @return state label (e.g. "idle", "running")
     */
    public String getState() {
        return state;
    }

    /**
     * Returns whether the entity is facing forwards.
     *
     * @return true if facing forwards, false otherwise
     */
    public boolean isFacingForward() {
        return isFacingForward;
    }
}
