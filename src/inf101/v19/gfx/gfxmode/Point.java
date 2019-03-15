package inf101.v19.gfx.gfxmode;

public class Point {
    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Calculate direction towards other position
     *
     * @param otherPos
     * @return
     */
    public Direction directionTo(Point otherPos) {
        return new Direction(otherPos.x - x, otherPos.y - y);
    }

    /**
     * Calculate distance to other position
     *
     * @param otherPos
     * @return
     */
    public double distanceTo(Point otherPos) {
        return Math.sqrt(Math.pow(x - otherPos.x, 2) + Math.pow(y - otherPos.y, 2));
    }

    /**
     * @return The X coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * @return The Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Relative move
     *
     * @param dir
     *            Direction
     * @param distance
     *            Distance to move
     */
    public Point move(Direction dir, double distance) {
        return new Point(x + dir.getX() * distance, y - dir.getY() * distance);
    }

    /**
     * Relative move
     *
     * @param deltaX
     * @param deltaY
     * @return A new point at x+deltaX, y+deltaY
     */
    public Point move(double deltaX, double deltaY) {
        return new Point(x + deltaX, y + deltaY);
    }

    /**
     * Relative move
     *
     * @param deltaPos
     */
    public Point move(Point deltaPos) {
        return new Point(x + deltaPos.x, y + deltaPos.y);
    }

    /**
     * Change position
     *
     * @param newX
     *            the new X coordinate
     * @param newY
     *            the new Y coordinate
     * @return A new point at newX, newY
     */
    public Point moveTo(double newX, double newY) {
        return new Point(newX, newY);
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", x, y);
    }

    /**
     * Multiply this point by a scale factor.
     *
     * @param factor A scale factor
     * @return A new Point, (getX()*factor, getY()*factor)
     */
    public Point scale(double factor) {
        return new Point(x*factor, y*factor);
    }

    /**
     * Find difference between points.
     * <p>
     * The returned value will be such that <code>this.move(deltaTo(point)).equals(point)</code>.
     * @param point Another point
     * @return A new Point, (point.getX()-getX(), point.getY()-getY())
     */
    public Point deltaTo(Point point) {
        return new Point(point.x-x, point.y-y);
    }
}
