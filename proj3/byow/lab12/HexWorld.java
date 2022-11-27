package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 34253;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Draw a row of tile to the board, anchored at a given position
     */
    public static void drawRow(TETile[][] tiles, Position p, TETile t, int length) {
        for (int i = 0; i < length; i++) {
            tiles[p.x + i][p.y] = t;
        }
    }

    public static void addHexagon(TETile[][] tiles, Position p, TETile t, int size) {
        int b = size - 1;
        int tl = size;
        addHexagonHelper(tiles, p, t, size, b,tl);
    }

    public static void addHexagonHelper(TETile[][] tiles, Position p, TETile t, int size, int b, int tl) {
        if (size < 2) return;
        Position startOfRow = p.shift(b, 0);
        drawRow(tiles, startOfRow, t, tl);
        if (b > 0) {
            Position nextAnchor = p.shift(0, -1);
            addHexagonHelper(tiles, nextAnchor, t, size, b - 1, tl + 2);
        }

        Position reflectAnchoredPosition = startOfRow.shift(0, -(2*b + 1));
        drawRow(tiles, reflectAnchoredPosition, t, tl);
    }

    /**
     * Adds a column of hexagon, each of whose biomes are chosen randomly
     * to the world.
     * @param size
     */
    public static void addHexagonColumn(TETile[][] tiles, Position p, TETile t, int size, int num) {
        if (size < 2) return;
        addHexagon(tiles, p, t, size);

        if (num > 1) {
            addHexagonColumn(tiles, getBottomNeighbor(p, size), randomTile(), size, num - 1);
        }

    }
    public static Position getBottomNeighbor(Position p, int size) {
        return p.shift(0, -2*size);
    }
    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static class Position {
        int x;
        int y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public Position shift(int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.TREE;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.GRASS;
            default: return Tileset.NOTHING;
        }
    }
    public static Position getBottomRight(Position p, int size, int delta) {
        return p.shift(delta * (2 * size - 1), -size);
    }

    public static Position getTopRight(Position p, int size, int delta) {
        return p.shift(delta * (2 * size - 1), size);
    }

    /**
     * Play area for drawing the whole world.
     * @param world
     */
    public static void drawWorld(TETile[][] world) {

        Position p = new Position(5, 35);
        int size = 3;
        int num = size;
        // Draw the first column
        addHexagonColumn(world, p, randomTile(), size, num);

        for (int i = 1; i < size; i ++) {
            // Increment by 1
            p = getTopRight(p, size, 1);
            addHexagonColumn(world, p, randomTile(), size, num + i);
        }

        for (int i = size - 2; i >= 0; i--) {
            // Decrement by 1
            p = getBottomRight(p, size, 1);
            addHexagonColumn(world, p, randomTile(), size, num + i);

        }
    }


    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);
        drawWorld(world);

        ter.renderFrame(world);
    }


}
