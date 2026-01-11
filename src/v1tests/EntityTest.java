import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.text.html.parser.Entity;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

//tests generic logic for entities which by inheritance checks for Player and Enemy objects
class EntityTest {
    //T13
    @Test
    @DisplayName("Checks that a new entity (base class) initialises correctly.")
    void testEntityCreates(){
        BufferedImage bfr = new  BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Image tst = new ImageIcon(bfr).getImage();
        Entity ent = new Entity(tst,0, 0, 10, 10, 30);
        assertNotNull(ent, "entity should not be null");
        assertEquals(0, ent.x, "Entity x position should be initialised correctly");
        assertEquals(0, ent.y, "Entity y position should be initialised correctly");
        assertEquals(30, ent.hitBox, "Entity hitBox should be initialised correctly");
    }

    //T14
    @Test
    @DisplayName("Places entity in a MapBlocks tile and checks intersection detection.")
    void testEntityIntersects() {
        BufferedImage bfr = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Image tst = new ImageIcon(bfr).getImage();
        Entity ent = new Entity(tst, 0, 0, 10, 10, 30);
        MapBlocks.map.clear();
        //create MapBlock not read to test intersection
        MapBlocks block = new MapBlocks(tst, 50, 50);
        MapBlocks.map.add(block);
        assertFalse(ent.intersect(), "entity should not intersect");
    }

    //T15
    @Test
    @DisplayName("Verifies jump logic from grounded state - should decrease velocity")
    void testJump() {
        BufferedImage bfr = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Image tst = new ImageIcon(bfr).getImage();
        Entity ent = new Entity(tst, 0, 0, 10, 10, 30);
        ent.isGrounded = true;
        ent.jumpCounter = 0;
        ent.jump();
        assertTrue(ent.velocity < 0, "velocity should be not positive");
    }

    // T16
    @Test
    @DisplayName("Checks that gravity moves an airborne entity downwards.")
    void testGravity() {
        BufferedImage bfr = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Image img = new ImageIcon(bfr).getImage();

        // Ensure no blocks exist so intersect() stays false
        MapBlocks.map.clear();
        Entity ent = new Entity(img, 0, 0, 10, 10, 30);

        ent.isGrounded = false;
        ent.velocity = 1.0;
        int startY = ent.y;
        double startVel = ent.velocity;
        ent.gravity();

        assertTrue(ent.y > startY, "Entity y position should increase when falling");
        assertEquals(startVel + ent.acceleration, ent.velocity, 0.0001, "Velocity should increase by acceleration after gravity()");
        assertFalse(ent.isGrounded, "Entity should remain airborne when no collision occurs");
    }


}