package inf101.v19.gfx.gfxmode;

import java.util.ArrayList;
import java.util.List;

import inf101.v19.gfx.IPaintLayer;
import inf101.v19.gfx.Screen;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class TurtlePainter implements IPaintLayer, ITurtle {

    static class TurtleState {
        protected Point pos;
        protected Direction dir;
        protected Direction inDir;
        protected double penSize = 1.0;
        protected Paint ink = Color.BLACK;

        public TurtleState() {
        }

        public TurtleState(TurtleState s) {
            pos = s.pos;
            dir = s.dir;
            inDir = s.inDir;
            penSize = s.penSize;
            ink = s.ink;
        }
    }

    private final Screen screen;
    private final double width;
    private final double height;
    private final GraphicsContext context;
    private final List<TurtleState> stateStack = new ArrayList<>();

    private TurtleState state = new TurtleState();
    private final Canvas canvas;
    private boolean path = false;

    public TurtlePainter(double width, double height) {
        this.screen = null;
        this.canvas = null;
        this.context = null;
        this.width = width;
        this.height = height;
        stateStack.add(new TurtleState());
        state.dir = new Direction(1.0, 0.0);
        state.pos = new Point(width / 2, height / 2);
    }

    public TurtlePainter(Screen screen, Canvas canvas) {
        if (screen == null || canvas == null)
            throw new IllegalArgumentException();
        this.screen = screen;
        this.canvas = canvas;
        this.context = canvas.getGraphicsContext2D();
        this.width = screen.getWidth();
        this.height = screen.getHeight();
        stateStack.add(new TurtleState());
        state.dir = new Direction(1.0, 0.0);
        state.pos = new Point(screen.getWidth() / 2, screen.getHeight() / 2);
        context.setLineJoin(StrokeLineJoin.BEVEL);
        context.setLineCap(StrokeLineCap.SQUARE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T as(Class<T> clazz) {
        if (clazz == GraphicsContext.class)
            return (T) context;
        if (clazz == getClass())
            return (T) this;
        else
            return null;
    }

    @Override
    public void clear() {
        if (context != null)
            context.clearRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public ITurtle curveTo(Point to, double startControl, double endAngle, double endControl) {
        Point c1 = state.pos.move(state.dir, startControl);
        Point c2 = to.move(Direction.fromDegrees(endAngle + 180), endControl);
        if (context != null) {
            if (!path) {
                // context.save();
                context.setStroke(state.ink);
                context.setLineWidth(state.penSize);
                context.beginPath();
                context.moveTo(state.pos.getX(), state.pos.getY());
            }
            context.bezierCurveTo(c1.getX(), c1.getY(), c2.getX(), c2.getY(), to.getX(), to.getY());
        }
        state.inDir = state.dir;
        state.pos = to;
        state.dir = Direction.fromDegrees(endAngle);

        if (!path && context != null) {
            context.stroke();
            // context.restore();
        }
        return this;
    }

    @Override
    public void debugTurtle() {
        System.err.println("[" + state.pos + " " + state.dir + "]");
    }

    @Override
    public ITurtle draw(double dist) {
        Point to = state.pos.move(state.dir, dist);
        return drawTo(to);
    }

    @Override
    public ITurtle draw(Point relPos) {
        Point to = state.pos.move(relPos);
        return drawTo(to);
    }

    @Override
    public ITurtle drawTo(double x, double y) {
        Point to = new Point(x, y);
        return drawTo(to);
    }

    @Override
    public ITurtle drawTo(Point to) {
        if (path && context != null) {
            context.setStroke(state.ink);
            context.setLineWidth(state.penSize);
            context.lineTo(to.getX(), to.getY());
        } else {
            line(to);
        }
        state.inDir = state.dir;
        state.pos = to;
        return this;
    }

    @Override
    public double getAngle() {
        return state.dir.toDegrees();
    }

    @Override
    public Direction getDirection() {
        return state.dir;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public Point getPos() {
        return state.pos;
    }

    public Screen getScreen() {
        return screen;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public ITurtle jump(double dist) {
        state.inDir = state.dir;
        state.pos = state.pos.move(state.dir, dist);
        if (path && context != null)
            context.moveTo(state.pos.getX(), state.pos.getY());
        return this;
    }

    @Override
    public ITurtle jump(Point relPos) {
        // TODO: state.inDir = state.dir;
        state.pos = state.pos.move(relPos);
        if (path && context != null)
            context.moveTo(state.pos.getX(), state.pos.getY());

        return this;
    }

    @Override
    public ITurtle jumpTo(double x, double y) {
        state.inDir = state.dir;
        state.pos = new Point(x, y);
        return this;
    }

    @Override
    public ITurtle jumpTo(Point to) {
        state.inDir = state.dir;
        state.pos = to;
        return this;
    }

    @Override
    public void layerToBack() {
        if (screen != null)
            screen.moveToBack(this);
    }

    @Override
    public void layerToFront() {
        if (screen != null)
            screen.moveToFront(this);
    }

    @Override
    public ITurtle line(Point to) {
        if (context != null) {
            // context.save();
            context.setStroke(state.ink);
            context.setLineWidth(state.penSize);
            context.strokeLine(state.pos.getX(), state.pos.getY(), to.getX(), to.getY());
            // context.restore();
        }
        return this;
    }

    @Override
    public IPainter restore() {
        if (stateStack.size() > 0) {
            state = stateStack.remove(stateStack.size() - 1);
        }
        return this;
    }

    @Override
    public IPainter save() {
        stateStack.add(new TurtleState(state));
        return this;
    }

    @Override
    public IPainter setInk(Paint ink) {
        state.ink = ink;
        return this;
    }

    @Override
    public ITurtle setPenSize(double pixels) {
        if (pixels < 0)
            throw new IllegalArgumentException("Negative: " + pixels);
        state.penSize = pixels;
        return this;
    }

    @Override
    public IShape shape() {
        ShapePainter s = new ShapePainter(context);
        return s.at(getPos()).rotation(getAngle()).strokePaint(state.ink);
    }

    @Override
    public ITurtle turn(double degrees) {
        state.dir = state.dir.turn(degrees);
        return this;
    }

    @Override
    public ITurtle turnAround() {
        return turn(180);
    }

    @Override
    public ITurtle turnLeft() {
        return turn(90);
    }

    @Override
    public ITurtle turnLeft(double degrees) {
        if (degrees < 0)
            throw new IllegalArgumentException("Negative: " + degrees + " (use turn())");
        state.dir = state.dir.turn(degrees);
        return this;
    }

    @Override
    public ITurtle turnRight() {
        return turn(-90);
    }

    @Override
    public ITurtle turnRight(double degrees) {
        if (degrees < 0)
            throw new IllegalArgumentException("Negative: " + degrees + " (use turn())");
        state.dir = state.dir.turn(-degrees);
        return this;
    }

    @Override
    public ITurtle turnTo(double degrees) {
        state.dir = state.dir.turnTo(degrees);
        return this;
    }

    @Override
    public ITurtle turnTowards(double degrees, double percent) {
        state.dir = state.dir.turnTowards(new Direction(degrees), percent);
        return this;
    }

    @Override
    public ITurtle turtle() {
        TurtlePainter painter = screen != null ? new TurtlePainter(screen, canvas) : new TurtlePainter(width, height);
        painter.stateStack.set(0, new TurtleState(state));
        return painter;
    }

    public ITurtle beginPath() {
        if (path)
            throw new IllegalStateException("beginPath() after beginPath()");
        path = true;
        if (context != null) {
            context.setStroke(state.ink);
            context.beginPath();
            context.moveTo(state.pos.getX(), state.pos.getY());
        }
        return this;
    }

    public ITurtle closePath() {
        if (!path)
            throw new IllegalStateException("closePath() without beginPath()");
        if (context != null)
            context.closePath();
        return this;
    }

    public ITurtle endPath() {
        if (!path)
            throw new IllegalStateException("endPath() without beginPath()");
        path = false;
        if (context != null)
            context.stroke();
        return this;
    }

    public ITurtle fillPath() {
        if (!path)
            throw new IllegalStateException("fillPath() without beginPath()");
        path = false;
        if (context != null) {
            context.save();
            context.setFill(state.ink);
            context.fill();
            context.restore();
        }
        return this;
    }

    @Override
    public Paint getInk() {
        return state.ink;
    }
}
