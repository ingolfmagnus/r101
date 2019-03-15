package inf101.v19.gfx.gfxmode;

/**
 * @author anya
 *
 */
public class Direction {
    /**
     * Construct direction from an angle
     *
     * @param degrees
     *            Angle in degrees, where 0 is (1,0)
     */
    public static Direction fromDegrees(double degrees) {
        return new Direction(degrees);
    }

    /**
     * Construct direction from a vector
     *
     * @param x
     *            X direction
     * @param y
     *            Y direction
     */
    public static Direction fromVector(double x, double y) {
        return new Direction(x, y);
    }

    private double xDir;

    private double yDir;

    /**
     * Create a new direction.
     *
     * The direction vector will be normalised to a vector of length 1.
     *
     * @param degrees
     *            Angle of direction in degrees
     */
    public Direction(double degrees) {
        double radians = Math.toRadians(degrees);
        this.xDir = Math.cos(radians);
        this.yDir = Math.sin(radians);
        normalize();
    }

    /**
     * Create a new direction.
     *
     * The direction vector will be normalised to a vector of length 1.
     *
     * @param xDir
     *            X-component of direction vector
     * @param yDir
     *            Y-component of direction vector
     */
    public Direction(double xDir, double yDir) {
        this.xDir = xDir;
        this.yDir = yDir;
        normalize();
    }

    /**
     * Multiply direction by distance
     *
     * @param distance
     * @return Position delta
     */
    public Point getMovement(double distance) {
        return new Point(xDir * distance, -yDir * distance);
    }

    /**
     * @return X-component of direction vector
     *
     *         Same as the Math.cos(toRadians())
     */
    public double getX() {
        return xDir;
    }

    /**
     * @return Y-component of direction vector
     *
     *         Same as the Math.sin(toRadians())
     */
    public double getY() {
        return yDir;
    }

    private void normalize() {
        double l = Math.sqrt(xDir * xDir + yDir * yDir);
        if (l >= 0.00001) {
            xDir = xDir / l;
            yDir = yDir / l;
        } else if (xDir > 0) {
            xDir = 1;
            yDir = 0;
        } else if (xDir < 0) {
            xDir = -1;
            yDir = 0;
        } else if (yDir > 0) {
            xDir = 0;
            yDir = 1;
        } else if (yDir < 0) {
            xDir = 0;
            yDir = -1;
        } else {
            xDir = 1;
            yDir = 0;
        }

    }

    /**
     * Translate to angle (in degrees)
     *
     * @return Angle in degrees, -180 .. 180
     */
    public double toDegrees() {
        return Math.toDegrees(Math.atan2(yDir, xDir));
    }

    /**
     * Translate to angle (in radians)
     *
     * @return Angle in radians, -2π .. 2π
     */
    public double toRadians() {
        return Math.atan2(yDir, xDir);
    }

    @Override
    public String toString() {
        return String.format("%.2f", toDegrees());
    }

    /**
     * Turn (relative)
     *
     * @param deltaDir
     */
    public Direction turn(Direction deltaDir) {
        return new Direction(xDir + deltaDir.xDir, yDir + deltaDir.yDir);
    }

    /**
     * Turn angle degrees
     *
     * @param angle
     */
    public Direction turn(double angle) {
        return turnTo(toDegrees() + angle);
    }

    /**
     * Turn around 180 degrees
     */
    public Direction turnBack() {
        return turn(180.0);
    }

    /**
     * Turn left 90 degrees
     */
    public Direction turnLeft() {
        return turn(90.0);
    }

    /**
     * Turn right 90 degrees
     */
    public Direction turnRight() {
        return turn(-90.0);
    }

    /**
     * Absolute turn
     *
     * @param degrees
     *            Angle in degrees, where 0 is (1,0)
     */
    public Direction turnTo(double degrees) {
        return new Direction(degrees);
    }

    /**
     * Turn slightly towards a directions
     *
     * @param dir
     *            A direction
     * @param percent
     *            How much to turn (100.0 is the same as turnTo())
     */
    public Direction turnTowards(Direction dir, double percent) {
        return new Direction(xDir * (1.00 - percent / 100.0) + dir.xDir * (percent / 100.0),
                             yDir * (1.00 - percent / 100.0) + dir.yDir * (percent / 100.0));
        // double thisAngle = toAngle();
        // double otherAngle = dir.toAngle();
        // turnTo(thisAngle*(1.00 - percent/100.0) +
        // otherAngle*(percent/100.0));
    }
}
