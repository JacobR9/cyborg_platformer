import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.geometry.Point2D;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main game model for the platformer.
 * <p>
 * Responsible for holding game state (player, enemies, bullets, map, camera),
 * loading assets, and advancing the simulation each tick via {@link #update()}.
 * <p>
 * In the MVC refactor this class represents the "Model": it contains no rendering
 * code and exposes getters for the View/Renderer to display the current state.
 *
 * @author psyjr14
 */
public class Game {

    //default constructor
    public Game(){}

    //map and image assets
    private Font font;
    private Image cloud;
    private int cameraOffset;
    private MapBlocks map;
    private Image[] mapImages;
    private Background background;

    //game status
    /** True when the player reaches the win condition. */
    public boolean isWon = false;
    /** Timestamp (ms since epoch) of when the current run started. */
    public static double startTime;
    /** Controls whether the game loop should keep running (legacy field). */
    protected boolean isRunning = true;

    //player and related HUD assets and fields
    private Player player;
    private int deathCounter = 0;
    private int killCounter = 0;
    private Image ammoBox, heart, playerShootingSprite, box;
    private Image[] playerIdleSprites, playerRunningSprites, playerHurtSprites;

    //bullet assets
    private ArrayList<Bullet> activeBullets = new ArrayList<>();
    private Image bulletImage;

    //enemy assets
    private Image[] enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites;
    private Image enemyHurtSprite;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private final InputHandler inputHandler = new InputHandler();

    private GameState gameState =  GameState.RUNNING;
    /**
     * @return the current player instance for the active run.
     */
    public Player getPlayer() { return player; }

    /**
     * @return mutable list of enemies currently active in the level.
     */
    public ArrayList<Enemy> getEnemies() { return enemies; }

    /**
     * @return mutable list of bullets currently active in the level.
     */
    public ArrayList<Bullet> getActiveBullets() { return activeBullets; }

    /**
     * @return input handler used to record and consume player input.
     */
    public InputHandler getInputHandler() { return inputHandler; }

    /**
     * @return HUD image used to draw an ammo icon.
     */
    public Image getAmmoBox() { return ammoBox; }

    /**
     * @return HUD image used to draw a heart icon.
     */
    public Image getHeart() { return heart; }

    /**
     * Convenience accessor for movement keys recorded by {@link InputHandler}.
     *
     * @return boolean array representing current movement input state.
     */
    public boolean[] getKeysPressed() {
        return inputHandler.getKeysPressed();
    }

    /**
     * Convenience accessor for the last horizontal direction pressed.
     *
     * @return true if last direction was right/forwards, false if left/backwards.
     */
    public boolean isLastDirectionForwards() {
        return inputHandler.isLastDirectionForwards();
    }

    /**
     * @return true if the win condition has been reached for the current run.
     */
    public boolean isWon() { return isWon; }

    /**
     * @return the loaded font used by the renderer/UI.
     */
    public Font getFont() { return font; }

    /**
     * @return the cloud image used for jump effects.
     */
    public Image getCloud() { return cloud; }

    /**
     * Camera x-offset (in world pixels) used by the renderer to convert world
     * coordinates into screen coordinates.
     *
     * @return current camera offset in pixels.
     */
    public int getCameraOffset() { return cameraOffset; }

    /**
     * @return the current map model containing tile blocks and map width.
     */
    public MapBlocks getMap() { return map; }

    /**
     * @return number of player deaths/respawns in the current run.
     */
    public int getDeathCounter() { return deathCounter; }

    /**
     * @return number of player kills in the current run.
     */

    public int getKillCounter() { return killCounter; }
    /**
     * Increments the recorded death counter for the current run.
     */
    public void incrementDeaths() { deathCounter++; }

    /**
     * @return background model used for parallax rendering.
     */
    public Background getBackground() { return background; }

    /**
     * Loads an image from a file path as a JavaFX {@link Image}.
     *
     * @param resourcePath file path to load (relative to project root in this codebase).
     * @return loaded JavaFX image instance.
     */
    private static Image fxImage(String resourcePath) {
        var url = Game.class.getResource(resourcePath);
        if (url == null) {
            throw new IllegalArgumentException("Missing resource: " + resourcePath);
        }
        return new Image(url.toExternalForm());
    }

    private static Font fxFont(String resourcePath, double size) {
        var stream = Game.class.getResourceAsStream(resourcePath);
        if (stream == null) throw new IllegalArgumentException("Missing font: " + resourcePath);
        return Font.loadFont(stream, size);
    }

    public void loadImages() {
        font = fxFont("/Font/font.TTF", 60);

        cloud = fxImage("/Sprites/cloud.png");

        playerIdleSprites = new Image[] {
                fxImage("/Sprites/Player/idle/Cyborg_idle_1.png"),
                fxImage("/Sprites/Player/idle/Cyborg_idle_2.png"),
                fxImage("/Sprites/Player/idle/Cyborg_idle_3.png"),
                fxImage("/Sprites/Player/idle/Cyborg_idle_4.png")
        };

        playerRunningSprites = new Image[] {
                fxImage("/Sprites/Player/run/Cyborg_run_1.png"),
                fxImage("/Sprites/Player/run/Cyborg_run_2.png"),
                fxImage("/Sprites/Player/run/Cyborg_run_3.png"),
                fxImage("/Sprites/Player/run/Cyborg_run_4.png"),
                fxImage("/Sprites/Player/run/Cyborg_run_5.png"),
                fxImage("/Sprites/Player/run/Cyborg_run_6.png")
        };

        playerHurtSprites = new Image[] {
                fxImage("/Sprites/Player/hurt/Cyborg_hurt_1.png"),
                fxImage("/Sprites/Player/hurt/Cyborg_hurt_2.png")
        };

        heart = fxImage("/Sprites/heart.png");
        box = fxImage("/Sprites/box.png");
        ammoBox = fxImage("/Sprites/ammo.png");
        playerShootingSprite = fxImage("/Sprites/Player/shoot/shootingSprite.png");
        bulletImage = fxImage("/Sprites/Player/shoot/bullet.png");

        mapImages = new Image[] {
                fxImage("/Tiles/1_FrameTopLeftCorner.png"),
                fxImage("/Tiles/2_FrameTopRightCorner.png"),
                fxImage("/Tiles/3_FrameBottomLeftCorner.png"),
                fxImage("/Tiles/4_FrameBottomRightCorner.png"),
                fxImage("/Tiles/5_FrameTopMid.png"),
                fxImage("/Tiles/6_FrameLeftMid.png"),
                fxImage("/Tiles/7_FrameRightMid.png"),
                fxImage("/Tiles/8_FrameBottomMod.png"),
                fxImage("/Tiles/9_FrameMid.png"),
                fxImage("/Tiles/A_Box.png"),
                fxImage("/Tiles/B_HalfSlab.png"),
                fxImage("/Tiles/C_IndustrialTabLeft.png"),
                fxImage("/Tiles/D_IndustrialSlabMid.png"),
                fxImage("/Tiles/E_IndustrialSlabRight.png"),
                fxImage("/Tiles/F_LightPole.png"),
                fxImage("/Tiles/G_LightTop.png"),
                fxImage("/Tiles/H_TreadLeft.png"),
                fxImage("/Tiles/I_TreadMid.png"),
                fxImage("/Tiles/J_TreadRight.png")
        };

        Image[] bg = new Image[] {
                fxImage("/Background/1_Background.png"),
                fxImage("/Background/2_Background.png"),
                fxImage("/Background/3_Background.png"),
                fxImage("/Background/4_Background.png")
        };
        background = new Background(bg);

        enemyIdleSprites = new Image[] {
                fxImage("/Sprites/Enemy/Idle/Idle_1.png"),
                fxImage("/Sprites/Enemy/Idle/Idle_2.png"),
                fxImage("/Sprites/Enemy/Idle/Idle_3.png"),
                fxImage("/Sprites/Enemy/Idle/Idle_4.png"),
                fxImage("/Sprites/Enemy/Idle/Idle_5.png"),
                fxImage("/Sprites/Enemy/Idle/Idle_6.png"),
                fxImage("/Sprites/Enemy/Idle/Idle_7.png"),
                fxImage("/Sprites/Enemy/Idle/Idle_8.png")
        };

        enemyWalkingSprites = new Image[] {
                fxImage("/Sprites/Enemy/Walking/Walk_1.png"),
                fxImage("/Sprites/Enemy/Walking/Walk_2.png"),
                fxImage("/Sprites/Enemy/Walking/Walk_3.png"),
                fxImage("/Sprites/Enemy/Walking/Walk_4.png"),
                fxImage("/Sprites/Enemy/Walking/Walk_5.png"),
                fxImage("/Sprites/Enemy/Walking/Walk_6.png"),
                fxImage("/Sprites/Enemy/Walking/Walk_7.png"),
                fxImage("/Sprites/Enemy/Walking/Walk_8.png")
        };

        enemyRunningSprites = new Image[] {
                fxImage("/Sprites/Enemy/Running/Run_1.png"),
                fxImage("/Sprites/Enemy/Running/Run_2.png"),
                fxImage("/Sprites/Enemy/Running/Run_3.png"),
                fxImage("/Sprites/Enemy/Running/Run_4.png"),
                fxImage("/Sprites/Enemy/Running/Run_5.png"),
                fxImage("/Sprites/Enemy/Running/Run_6.png"),
                fxImage("/Sprites/Enemy/Running/Run_7.png")
        };

        enemyHurtSprite = fxImage("/Sprites/Enemy/Hurt.png");
    }


    /**
     * Advances the game simulation by one tick.
     * <p>
     * This method consumes one-shot inputs (jump/shoot/restart), updates the camera,
     * updates the player and enemies, and steps bullets. It also handles win/death
     * state transitions and respawning.
     */
    public void update() {
        if (handleRestart()) {
            return;
        }

        boolean[] keys = inputHandler.getKeysPressed();
        boolean lastDir = inputHandler.isLastDirectionForwards();
        boolean jumpPressed = inputHandler.processJump();
        boolean shootPressed = inputHandler.processShoot();
        List<MapBlock> blocks = map.getBlocks();

        updateCamera(1280);

        updatePlayer(keys, lastDir, jumpPressed, blocks);
        if (handleWinOrDeath()) {
            return;
        }

        handleShooting(keys, lastDir, shootPressed);

        updateEnemies(blocks);
        updateBullets(blocks);
    }

    /**
     * Processes a restart request and respawns entities when requested.
     *
     * @return true if a restart was processed and the caller should return immediately
     */
    private boolean handleRestart() {
        if (inputHandler.processRestart()) {
            spawnEntities();
            return true;
        }
        return false;
    }

    /**
     * Updates the player entity for this tick.
     *
     * @param keys current movement input array
     * @param lastDir last horizontal direction pressed
     * @param jumpPressed true if jump was requested this tick
     * @param blocks map blocks used for collision detection
     */
    private void updatePlayer(boolean[] keys, boolean lastDir, boolean jumpPressed, List<MapBlock> blocks) {
        player.update(keys, lastDir, jumpPressed, blocks);
    }

    /**
     * Handles win and death transitions after the player update.
     * <p>
     * If the player has won, sets {@link #isWon} and stops further updates.
     * If the player is dead, increments deaths and respawns entities.
     *
     * @return true if a state transition occurred and the caller should return immediately
     */
    private boolean handleWinOrDeath() {
        if (player.hasWon()) {
            isWon = true;
            return true;
        }
        if (player.isDead()) {
            incrementDeaths();
            spawnEntities();
            return true;
        }
        return false;
    }

    /**
     * Handles player shooting input and spawns a bullet if allowed.
     * <p>
     * Shooting is limited by a cooldown ({@code justShot}) and ammunition count.
     *
     * @param keys current movement input array (used for facing computation)
     * @param lastDir last horizontal direction pressed
     * @param shootPressed true if shooting was requested this tick
     */
    private void handleShooting(boolean[] keys, boolean lastDir, boolean shootPressed) {
        if (!shootPressed) return;

        boolean facing = player.isFacingForwards(keys, lastDir);
        Bullet b = player.tryShoot(facing, bulletImage);
        if (b != null) {
            activeBullets.add(b);
        }
    }

    /**
     * Updates all active enemies and removes dead enemies.
     * <p>
     * When an enemy dies, the player is rewarded with ammunition.
     *
     * @param blocks map blocks used for collision detection
     */
    private void updateEnemies(List<MapBlock> blocks) {
        var enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy e = enemyIt.next();
            e.update(player, enemies, blocks);
            if (e.isDead()) {
                killCounter++;
                player.ammo += 2;
                enemyIt.remove();
            }
        }
    }

    /**
     * Updates all active bullets and removes bullets that are no longer active.
     * <p>
     * A bullet is removed once it collides with a map block, hits an enemy, or
     * exceeds its maximum travel distance.
     *
     * @param blocks map blocks used for collision detection
     */
    private void updateBullets(List<MapBlock> blocks) {
        var bulletIt = activeBullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            boolean shouldRemove = b.update(enemies, blocks);
            if (shouldRemove) {
                bulletIt.remove();
            }
        }
    }

    /**
     * Updates the horizontal camera offset so the player stays centred where possible.
     * <p>
     * The camera is clamped to the map bounds so it never scrolls past the level.
     *
     * @param viewWidth width of the visible viewport in pixels.
     */
    public void updateCamera(int viewWidth) {
        int desired = player.desiredCameraX(viewWidth);
        int max = map.getMapWidth() - viewWidth;
        cameraOffset = Math.max(0, Math.min(desired, max));
    }

    /**
     * Resets and spawns the player, enemies, and bullet list for a new run.
     * <p>
     * This also resets the run start time and clears win state. Enemy spawn
     * positions are currently hard-coded.
     */
    public void spawnEntities() {
        isWon = false;

        player = new Player(20, 300, 3, playerIdleSprites, playerRunningSprites, playerHurtSprites, playerShootingSprite);

        enemies = new ArrayList<>();
        enemies.add(new Enemy(1475, 230, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(2570, 196, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(2750, 320, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(3060, 470, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(4219, 100, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(4900, 530, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(4970, 530, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(5040, 539, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(6397, 196, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(6540, 520, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(6600, 520, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(6660, 520, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));
        enemies.add(new Enemy(6720, 520, 2, enemyIdleSprites, enemyWalkingSprites, enemyRunningSprites, enemyHurtSprite));

        activeBullets = new ArrayList<>();
    }

    /**
     * Resets all runtime statistics for the current game run.
     * <p>
     * Intended to be called when restarting a level or starting a new run.
     * </p>
     */
    public void resetRunStats() {
        deathCounter = 0;
        killCounter = 0;
        isWon = false;
        activeBullets.clear();
    }
    /**
     * Initialises the map and spawns entities for the first run.
     * <p>
     * This should be called after {@link #loadImages()} so that map tiles and
     * sprites are already available.
     */
    public void init() {
        map = new MapBlocks();
        map.load(mapImages);
        spawnEntities();
    }
}
