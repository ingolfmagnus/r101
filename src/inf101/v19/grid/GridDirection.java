package inf101.v19.grid;

import java.util.Arrays;
import java.util.List;

public enum GridDirection {
    EAST(0, 1, 0, 4), NORTH(90, 0, -1, 1), WEST(180, -1, 0, 8), SOUTH(270, 0, 1, 2), //
    NORTHEAST(45, 1, -1, 5), NORTHWEST(135, -1, -1, 9), SOUTHWEST(225, -1, 1, 10), SOUTHEAST(315, 1, 1, 6), //
    CENTER(0, 0, 0, 0);

    /**
     * The four cardinal directions: {@link #NORTH}, {@link #SOUTH}, {@link #EAST},
     * {@link #WEST}.
     */
    public static final List<GridDirection> FOUR_DIRECTIONS = Arrays.asList(EAST, NORTH, WEST, SOUTH);
    /**
     * The eight cardinal and intercardinal directions: {@link #NORTH},
     * {@link #SOUTH}, {@link #EAST}, {@link #WEST}, {@link #NORTHWEST},
     * {@link #NORTHEAST}, {@link #SOUTHWEST}, {@link #SOUTHEAST}.
     */
    public static final List<GridDirection> EIGHT_DIRECTIONS = Arrays.asList(EAST, NORTHEAST, NORTH, NORTHWEST, WEST,
                                                                             SOUTHWEST, SOUTH, SOUTHEAST);
    /**
     * The eight cardinal and intercardinal directions ({@link #EIGHT_DIRECTIONS}),
     * plus {@link #CENTER}.
     */
    public static final List<GridDirection> NINE_DIRECTIONS = Arrays.asList(EAST, NORTHEAST, NORTH, NORTHWEST, WEST,
                                                                            SOUTHWEST, SOUTH, SOUTHEAST, CENTER);

    private final double degrees;
    private final int dx;
    private final int dy;
    private final int mask;

    private GridDirection(double degrees, int dx, int dy, int mask) {
        this.degrees = degrees;
        this.dx = dx;
        this.dy = dy;
        this.mask = mask;
    }

    /**
     * @return The angle of this direction, with 0° facing due {@link #EAST} and 90°
     *         being {@link #NORTH}.
     */
    public double getDegrees() {
        return degrees;
    }

    /**
     * @return The change to your X-coordinate if you were to move one step in this
     *         direction
     */
    public int getDx() {
        return dx;
    }

    /**
     * @return The change to your Y-coordinate if you were to move one step in this
     *         direction
     */
    public int getDy() {
        return dy;
    }

    public int getMask() {
        return mask;
    }
}
