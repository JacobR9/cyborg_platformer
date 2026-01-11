import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Manages the loading, storage, and rendering of map blocks.
 * <p>
 * The map layout is defined in a text file where each character
 * represents a specific tile type. Non-zero characters are converted
 * into {@link MapBlock} instances using a corresponding image.
 */
public class MapBlocks {

    /** List of all solid map blocks in the level. */
    private final List<MapBlock> blocks = new ArrayList<>();

    /** Total width of the map in pixels. */
    private int mapWidth;

    /**
     * Returns all map blocks currently loaded.
     *
     * @return list of {@link MapBlock} objects
     */
    public List<MapBlock> getBlocks() {
        return blocks;
    }

    /**
     * Returns the total width of the map in pixels.
     *
     * @return map width
     */
    public int getMapWidth() {
        return mapWidth;
    }

    /**
     * Loads the map from the {@code Maps.txt} file and generates map blocks.
     * <p>
     * Each character in the file corresponds to an image index in the
     * provided image array. The character {@code '0'} represents empty
     * space and does not generate a block.
     *
     * @param mapImages array of tile images indexed by tile ID
     * @throws RuntimeException if the map file cannot be read
     */
    public void load(Image[] mapImages) {
        blocks.clear();

        int tileX = 0;
        int tileY = 0;

        var stream = MapBlocks.class.getResourceAsStream("/Maps.txt");
        if (stream == null) throw new IllegalArgumentException("Missing resource: /Maps.txt");
        try (Scanner scanner = new Scanner(stream)) {
            Image imageTile = mapImages[9];

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] types = line.split("");

                mapWidth = types.length * 48;

                for (String value : types) {
                    switch (value) {
                        case "1" -> imageTile = mapImages[0];
                        case "2" -> imageTile = mapImages[1];
                        case "3" -> imageTile = mapImages[2];
                        case "4" -> imageTile = mapImages[3];
                        case "5" -> imageTile = mapImages[4];
                        case "6" -> imageTile = mapImages[5];
                        case "7" -> imageTile = mapImages[6];
                        case "8" -> imageTile = mapImages[7];
                        case "9" -> imageTile = mapImages[8];
                        case "A" -> imageTile = mapImages[9];
                        case "B" -> imageTile = mapImages[10];
                        case "C" -> imageTile = mapImages[11];
                        case "D" -> imageTile = mapImages[12];
                        case "E" -> imageTile = mapImages[13];
                        case "F" -> imageTile = mapImages[14];
                        case "G" -> imageTile = mapImages[15];
                        case "H" -> imageTile = mapImages[16];
                        case "I" -> imageTile = mapImages[17];
                        case "J" -> imageTile = mapImages[18];
                    }

                    if (value.equals("0")) {
                        tileX += 48;
                        continue;
                    }

                    blocks.add(new MapBlock(imageTile, tileX, tileY));
                    tileX += 48;
                }

                tileY += 48;
                tileX = 0;
            }
        }
    }

    /**
     * Draws all map blocks onto the game canvas.
     *
     * @param gc graphics context used for rendering
     */
    public void drawMap(GraphicsContext gc) {
        for (MapBlock block : blocks) {
            gc.drawImage(block.image, block.x, block.y);
        }
    }
}

