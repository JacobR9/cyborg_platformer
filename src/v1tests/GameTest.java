import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Image;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    //T1
    @Test
    @DisplayName("Game instance can be created with default state")
    void testGameCreates() {
        Game game = new Game();
        assertNotNull(game, "Game should be constructed");
        assertFalse(game.isWon, "Game should not start as won");
        assertTrue(game.isRunning, "Game should not be running immediately");
        assertEquals(0, Player.deathCounter, "Death counter should start at 0");
    }

    /*
    //T2
    @Test
    @DisplayName("loadImages initialises map images")
    void testGameLoadsImages() {
        // pre reqs
        Game game = new Game();
        game.loadImages();

        //tests
        assertNotNull(Player.ammoBox, "Ammo box should be initialised");
        assertNotNull(Player.idleSprites, "Idle sprites should be initialised");
        assertTrue(Player.idleSprites.length > 0, "Idle sprites array should not be empty");
        assertNotNull(Player.runningSprites, "Running sprites should be initialised");
        assertTrue(Player.runningSprites.length > 0, "Running sprites array should not be empty");
        assertNotNull(Player.hurtSprites, "Hurt sprites should be initialised");
        assertNotNull(Player.shootingSprite, "Shooting sprite should be initialised");
        assertNotNull(Player.box, "Box sprite should be initialised");
        assertNotNull(Player.heart, "Heart sprite should be initialised");

        assertNotNull(Background.background, "Background should be initialised");
        assertTrue(Background.background.length > 0, "Background array should not be empty");

        assertNotNull(MapBlocks.mapImages, "Map images should be loaded");
        assertTrue(MapBlocks.mapImages.length > 0, "Map images array should not be empty");

        assertNotNull(Enemy.idleSprites, "Enemy idle sprites should be loaded");
        assertTrue(Enemy.idleSprites.length > 0, "Enemy idle sprites array should not be empty");
        assertNotNull(Enemy.walkingSprites, "Enemy walking sprites should be loaded");
        assertNotNull(Enemy.runningSprites, "Enemy running sprites should be loaded");
        assertNotNull(Enemy.hurtSprite, "Enemy hurt sprite should be loaded");
    }
    */

    //T3
    @Test
    @DisplayName("spawnEntities creates player and enemies and resets state")
    void testGameSpawnsEntities() {
        // Use the same Game instance that spawnEntities checks is won
        CyborgPlatform.game = new Game();
        BufferedImage bfr = new  BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Player.idleSprites = new Image[]{bfr};
        Enemy.idleSprites = new Image[]{bfr};

        CyborgPlatform.game.isWon = true;
        Player.deathCounter = 5;

        CyborgPlatform.game.spawnEntities();

        // Player and enemies are created
        assertNotNull(Canvas.getPlayer(), "Player should be created by spawnEntities()");
        assertNotNull(Canvas.enemies, "Enemy list should be initialised");
        assertFalse(Canvas.enemies.isEmpty(), "There should be at least one enemy");

        // State resets
        assertFalse(CyborgPlatform.game.isWon, "Game should no longer be marked as won");
        assertEquals(0, Player.deathCounter, "Death counter should be reset");

        //Bullets list resets
        assertNotNull(Canvas.activeBullets, "Active bullets list should be initialised");
        assertTrue(Canvas.activeBullets.isEmpty(), "Active bullets list should start empty");

        // Start time set
        assertTrue(Game.startTime > 0, "startTime should be set when entities spawn");
    }
}
