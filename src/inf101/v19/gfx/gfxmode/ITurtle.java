package inf101.v19.gfx.gfxmode;

public interface ITurtle extends IPainter {

    /**
     * This method is used to convert the turtle to other type, determined by the
     * class object given as an argument.
     * <p>
     * This can be used to access extra functionality not provided by this
     * interface, such as direct access to the underlying graphics context.
     *
     * @param clazz
     * @return This object or an appropriate closely related object of the given
     *         time; or <code>null</code> if no appropriate object can be found
     */
    <T> T as(Class<T> clazz);

    /**
     * Move to the given position while drawing a curve
     *
     * <p>
     * The resulting curve is a cubic Bézier curve with the control points located
     * at <code>getPos().move(getDirection, startControl)</code> and
     * <code>to.move(Direction.fromDegrees(endAngle+180), endControl)</code>.
     * <p>
     * The turtle is left at point <code>to</code>, facing <code>endAngle</code>.
     * <p>
     * The turtle will start out moving in its current direction, aiming for a point
     * <code>startControl</code> pixels away, then smoothly turning towards its
     * goal. It will approach the <code>to</code> point moving in the direction
     * <code>endAngle</code> (an absolute bearing, with 0° pointing right and 90°
     * pointing up).
     *
     * @param to
     *            Position to move to
     * @param startControl
     *            Distance to the starting control point.
     * @return {@code this}, for sending more draw commands
     */
    ITurtle curveTo(Point to, double startControl, double endAngle, double endControl);

    void debugTurtle();

    /**
     * Move forward the given distance while drawing a line
     *
     * @param dist
     *            Distance to move
     * @return {@code this}, for sending more draw commands
     */
    ITurtle draw(double dist);

    /**
     * Move to the given position while drawing a line
     *
     * @param x
     *            X-position to move to
     * @param y
     *            Y-position to move to
     * @return {@code this}, for sending more draw commands
     */
    ITurtle drawTo(double x, double y);

    /**
     * Move to the given position while drawing a line
     *
     * @param to
     *            Position to move to
     * @return {@code this}, for sending more draw commands
     */
    ITurtle drawTo(Point to);

    /**
     * @return The current angle of the turtle, with 0° pointing to the right and
     *         90° pointing up. Same as {@link #getDirection()}.getAngle()
     */
    double getAngle();

    /**
     * @return The current direction of the turtle. Same as calling
     *         <code>new Direction(getAngle())</code>
     */
    Direction getDirection();

    /**
     * @return The current position of the turtle.
     */
    Point getPos();

    /**
     * Move a distance without drawing.
     *
     * @param dist
     *            Distance to move
     * @return {@code this}, for sending more draw commands
     */
    ITurtle jump(double dist);

    /**
     * Move a position without drawing.
     *
     * @param x
     *            X position to move to
     * @param y
     *            Y position to move to
     * @return {@code this}, for sending more draw commands
     */
    ITurtle jumpTo(double x, double y);

    /**
     * Move a position without drawing.
     *
     * @param to
     *            X,Y position to move to
     * @return {@code this}, for sending more draw commands
     */
    ITurtle jumpTo(Point to);

    /**
     * Draw a line from the current position to the given position.
     *
     * <p>
     * This method does not change the turtle position.
     *
     * @param to
     *            Other end-point of the line
     * @return {@code this}, for sending more draw commands
     */
    ITurtle line(Point to);

    /**
     * Set the size of the turtle's pen
     *
     * @param pixels
     *            Line width, in pixels
     * @return {@code this}, for sending more draw commands
     * @requires pixels >= 0
     */
    ITurtle setPenSize(double pixels);

    /**
     * Start drawing a shape at the current turtle position.
     *
     * <p>
     * The shape's default origin and rotation will be set to the turtle's current
     * position and direction, but can be modified with {@link IShape#at(Point)} and
     * {@link IShape#rotation(double)}.
     * <p>
     * The turtle's position and attributes are unaffected by drawing the shape.
     *
     * @return An IDrawParams object for setting up and drawing the shape
     */
    @Override
    IShape shape();

    /**
     * Change direction the given number of degrees (relative to the current
     * direction).
     *
     * <p>
     * Positive degrees turn <em>left</em> while negative degrees turn
     * <em>right</em>.
     *
     * @param degrees
     * @return {@code this}, for sending more draw commands
     */
    ITurtle turn(double degrees);

    /**
     * Turn 180°.
     *
     * <p>
     * Same as <code>turn(180)</code> and <code>turn(-180)</code>.
     *
     * @return {@code this}, for sending more draw commands
     */
    ITurtle turnAround();

    /**
     * Turn left 90°.
     *
     * <p>
     * Same as <code>turn(90)</code>.
     *
     * @return {@code this}, for sending more draw commands
     */
    ITurtle turnLeft();

    /**
     * Turn left.
     *
     * <p>
     * Same as <code>turn(degrees)</code>.
     *
     * @return {@code this}, for sending more draw commands
     * @requires degrees >= 0
     */
    ITurtle turnLeft(double degrees);

    /**
     * Turn right 90°.
     *
     * <p>
     * Same as <code>turn(-90)</code>.
     *
     * @return {@code this}, for sending more draw commands
     */
    ITurtle turnRight();

    /**
     * Turn left.
     *
     * <p>
     * Same as <code>turn(-degrees)</code>.
     *
     * @return {@code this}, for sending more draw commands
     * @requires degrees >= 0
     */
    ITurtle turnRight(double degrees);

    /**
     * Turn to the given bearing.
     *
     * <p>
     * 0° is due right, 90° is up.
     *
     * @param degrees
     *            Bearing, in degrees
     * @return {@code this}, for sending more draw commands
     */
    ITurtle turnTo(double degrees);

    /**
     * Turn towards the given bearing.
     *
     * <p>
     * Use this method to turn slightly towards something.
     *
     * <p>
     * 0° is due right, 90° is up.
     *
     * @param degrees
     *            Bearing, in degrees
     * @param percent
     *            How far to turn, in degrees.
     * @return {@code this}, for sending more draw commands
     */
    ITurtle turnTowards(double degrees, double percent);

    /**
     * Jump (without drawing) to the given relative position.
     * <p>
     * The new position will be equal to getPos().move(relPos).
     *
     * @param relPos
     *            A position, interpreted relative to current position
     * @return {@code this}, for sending more draw commands
     */
    ITurtle jump(Point relPos);

    /**
     * Move to the given relative position while drawing a line
     * <p>
     * The new position will be equal to getPos().move(relPos).
     *
     * @return {@code this}, for sending more draw commands
     */
    ITurtle draw(Point relPos);

}
