package inf101.v19.gfx.gfxmode;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

public class ShapePainter implements IShape {
    private abstract static class DrawCommand {
        protected double calcX(Gravity g, double w) {
            switch (g) {
            default:
            case CENTER:
                return w / 2;
            case EAST:
                return w;
            case NORTH:
                return w / 2;
            case NORTHEAST:
                return w;
            case NORTHWEST:
                return 0;
            case SOUTH:
                return w / 2;
            case SOUTHEAST:
                return w;
            case SOUTHWEST:
                return 0;
            case WEST:
                return 0;
            }
        }

        protected double calcY(Gravity g, double h) {
            switch (g) {
            default:
            case CENTER:
                return h / 2;
            case EAST:
                return h / 2;
            case NORTH:
                return 0;
            case NORTHEAST:
                return 0;
            case NORTHWEST:
                return 0;
            case SOUTH:
                return h;
            case SOUTHEAST:
                return h;
            case SOUTHWEST:
                return h;
            case WEST:
                return h / 2;
            }
        }

        public void fill(GraphicsContext ctx, ShapePainter p) {
            ctx.save();
            ctx.setFill(p.fill);
            ctx.translate(p.x, p.y);
            if (p.rot != 0)
                ctx.rotate(-p.rot);
            fillIt(ctx, p);
            ctx.restore();
        }

        protected abstract void fillIt(GraphicsContext ctx, ShapePainter p);

        // public abstract Shape toFXShape(DrawParams p);
        //
        // public abstract String toSvg(DrawParams p);

        public void stroke(GraphicsContext ctx, ShapePainter p) {
            ctx.save();
            ctx.setStroke(p.stroke);
            if (p.strokeWidth != 0)
                ctx.setLineWidth(p.strokeWidth);
            ctx.translate(p.x, p.y);
            if (p.rot != 0)
                ctx.rotate(-p.rot);
            strokeIt(ctx, p);
            ctx.restore();
        }

        protected abstract void strokeIt(GraphicsContext ctx, ShapePainter p);
    }

    private static class DrawEllipse extends DrawCommand {

        @Override
        public void fillIt(GraphicsContext ctx, ShapePainter p) {
            ctx.fillOval(-calcX(p.gravity, p.w), -calcY(p.gravity, p.h), p.w, p.h);
        }

        @Override
        public void strokeIt(GraphicsContext ctx, ShapePainter p) {
            ctx.strokeOval(-calcX(p.gravity, p.w), -calcY(p.gravity, p.h), p.w, p.h);
        }
    }

    private static class DrawLine extends DrawCommand {

        @Override
        public void fillIt(GraphicsContext ctx, ShapePainter p) {
            if (p.lineSegments != null) {
                int nPoints = (p.lineSegments.size() / 2) + 1;
                double xs[] = new double[nPoints];
                double ys[] = new double[nPoints];
                xs[0] = -calcX(p.gravity, p.w);
                ys[0] = -calcY(p.gravity, p.h);
                for (int i = 0; i < p.lineSegments.size(); i++) {
                    xs[i] = p.lineSegments.get(i * 2) - p.x;
                    ys[i] = p.lineSegments.get(i * 2 + 1) - p.y;
                }
                ctx.fillPolygon(xs, ys, nPoints);
            }
        }

        @Override
        public void strokeIt(GraphicsContext ctx, ShapePainter p) {
            if (p.lineSegments == null) {
                double x = -calcX(p.gravity, p.w);
                double y = -calcY(p.gravity, p.h);
                ctx.strokeLine(x, y, x + p.w, y + p.h);
            } else {
                int nPoints = (p.lineSegments.size() / 2) + 1;
                double xs[] = new double[nPoints];
                double ys[] = new double[nPoints];
                xs[0] = -calcX(p.gravity, p.w);
                ys[0] = -calcY(p.gravity, p.h);
                for (int i = 0; i < p.lineSegments.size(); i++) {
                    xs[i] = p.lineSegments.get(i * 2) - p.x;
                    ys[i] = p.lineSegments.get(i * 2 + 1) - p.y;
                }
                if (p.closed)
                    ctx.strokePolygon(xs, ys, nPoints);
                else
                    ctx.strokePolyline(xs, ys, nPoints);
            }
        }
    }

    private static class DrawRectangle extends DrawCommand {

        @Override
        public void fillIt(GraphicsContext ctx, ShapePainter p) {
            ctx.fillRect(-calcX(p.gravity, p.w), -calcY(p.gravity, p.h), p.w, p.h);
        }

        @Override
        public void strokeIt(GraphicsContext ctx, ShapePainter p) {
            ctx.strokeRect(-calcX(p.gravity, p.w), -calcY(p.gravity, p.h), p.w, p.h);
        }
    }

    private double x = 0, y = 0, w = 0, h = 0, rot = 0, strokeWidth = 0;
    private List<Double> lineSegments = null;
    private Paint fill = null;
    private Paint stroke = null;

    private Gravity gravity = Gravity.CENTER;

    private DrawCommand cmd = null;

    private boolean closed = false;

    private final GraphicsContext context;

    public ShapePainter(GraphicsContext context) {
        super();
        this.context = context;
    }

    @Override
    public IShape addPoint(double x, double y) {
        lineSegments.add(x);
        lineSegments.add(y);
        return this;
    }

    @Override
    public IShape addPoint(Point xy) {
        lineSegments.add(xy.getX());
        lineSegments.add(xy.getY());
        return this;
    }

    @Override
    public ShapePainter angle(double a) {
        return this;
    }

    @Override
    public IShape arc() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ShapePainter at(Point p) {
        if (p != null) {
            this.x = p.getX();
            this.y = p.getY();
        } else {
            this.x = 0;
            this.y = 0;
        }
        return this;
    }

    @Override
    public IShape close() {
        closed = true;
        return this;
    }

    @Override
    public void draw() {
        draw(context);
    }

    @Override
    public void draw(GraphicsContext context) {
        if (cmd != null && context != null) {
            if (fill != null)
                cmd.fill(context, this);
            if (stroke != null)
                cmd.stroke(context, this);
        }
    }

    @Override
    public IShape ellipse() {
        cmd = new DrawEllipse();
        return this;
    }

    @Override
    public ShapePainter fill() {
        if (cmd != null && context != null)
            cmd.fill(context, this);
        return this;
    }

    @Override
    public ShapePainter fillPaint(Paint p) {
        fill = p;
        return this;
    }

    @Override
    public ShapePainter gravity(Gravity g) {
        gravity = g;
        return this;
    }

    @Override
    public ShapePainter height(double h) {
        this.h = h;
        return this;
    }

    @Override
    public ShapePainter length(double l) {
        w = l;
        h = l;
        return this;
    }

    @Override
    public IShape line() {
        cmd = new DrawLine();
        return this;
    }

    @Override
    public IShape rectangle() {
        cmd = new DrawRectangle();
        return this;
    }

    @Override
    public ShapePainter rotation(double angle) {
        rot = angle;
        return this;
    }

    @Override
    public ShapePainter stroke() {
        if (cmd != null && context != null)
            cmd.stroke(context, this);
        return this;
    }

    @Override
    public ShapePainter strokePaint(Paint p) {
        stroke = p;
        return this;
    }

    @Override
    public Shape toFXShape() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toSvg() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ShapePainter width(double w) {
        this.w = w;
        return this;
    }

    @Override
    public ShapePainter x(double x) {
        this.x = x;
        return this;
    }

    @Override
    public ShapePainter y(double y) {
        this.y = y;
        return this;
    }
}
