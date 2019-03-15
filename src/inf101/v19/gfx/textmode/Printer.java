package inf101.v19.gfx.textmode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import inf101.v19.gfx.IPaintLayer;
import inf101.v19.gfx.Screen;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Printer implements IPaintLayer {
    private static class Char {
        public int mode;
        public String s;
        public Color fill;
        public Color stroke;
        public Paint bg;

        public Char(String s, Color fill, Color stroke, Paint bg, int mode) {
            this.s = s;
            this.fill = fill;
            this.stroke = stroke;
            this.bg = bg;
            this.mode = mode;
        }
    }

    public static final TextFont FONT_MONOSPACED = new TextFont("Monospaced", 27.00, TextMode.CHAR_BOX_SIZE, 3.4000,
                                                                -6.7000, 1.5000, 1.0000, true);
    public static final TextFont FONT_LMMONO = new TextFont("lmmono10-regular.otf", 30.00, TextMode.CHAR_BOX_SIZE,
                                                            4.0000, -8.5000, 1.5000, 1.0000, true);
    public static final TextFont FONT_ZXSPECTRUM7 = new TextFont("ZXSpectrum-7.otf", 22.00, TextMode.CHAR_BOX_SIZE,
                                                                 3.1000, -3.8000, 1.0000, 1.0000, true);

    /**
     * TTF file can be found here: http://users.teilar.gr/~g1951d/ in this ZIP file:
     * http://users.teilar.gr/~g1951d/Symbola.zip
     * <p>
     * (Put the extracted Symbola.ttf in src/inf101/v19/gfx/fonts/)
     */
    public static final TextFont FONT_SYMBOLA = new TextFont("Symbola.ttf", 26.70, TextMode.CHAR_BOX_SIZE, -0.4000,
                                                             -7.6000, 1.35000, 1.0000, true);

    /**
     * TTF file can be found here:
     * http://www.kreativekorp.com/software/fonts/c64.shtml
     */
    public static final TextFont FONT_GIANA = new TextFont("Giana.ttf", 25.00, TextMode.CHAR_BOX_SIZE, 4.6000, -5.0000,
                                                           1.0000, 1.0000, true);

    /**
     * TTF file can be found here:
     * http://www.kreativekorp.com/software/fonts/c64.shtml
     */
    public static final TextFont FONT_C64 = new TextFont("PetMe64.ttf", 31.50, TextMode.CHAR_BOX_SIZE, 0.0000, -4.000,
                                                         1.0000, 1.0000, true);
    private static final Paint DEFAULT_BACKGROUND = Color.TRANSPARENT;
    private static final TextMode DEFAULT_MODE = TextMode.MODE_40X22;
    private static final boolean DEBUG_REDRAW = false;

    public static String center(String s, int width) {
        for (; s.length() < width; s = " " + s + " ")
            ;
        return s;
    }

    public static String repeat(String s, int width) {
        String r = s;
        for (; r.length() < width; r += s)
            ;
        return r;
    }

    Color DEFAULT_FILL = Color.BLACK;

    Color DEFAULT_STROKE = Color.TRANSPARENT;

    private TextMode textMode;
    private Color fill;
    private Color stroke;
    private Paint background;
    private Screen screen;
    private List<Char[]> lineBuffer = new ArrayList<>();
    private boolean autoscroll = true;
    private final Canvas textPage;
    private int x = 1, y = 1, savedX = 1, savedY = 1;
    // private int pageWidth = LINE_WIDTHS[resMode], pageHeight =
    // PAGE_HEIGHTS[resMode];
    private int leftMargin = 1, topMargin = 1;
    private TextFont font = FONT_LMMONO;
    private int videoAttrs = 0;

    private String csiSeq = null;

    private boolean csiEnabled = true;

    private int csiMode = 0;

    private final double width;
    private final double height;

    private int dirtyX0 = Integer.MAX_VALUE;
    private int dirtyX1 = Integer.MIN_VALUE;
    private int dirtyY0 = Integer.MAX_VALUE;
    private int dirtyY1 = Integer.MIN_VALUE;
    private boolean useBuffer = true;

    public Printer(double width, double height) {
        this.screen = null;
        this.textPage = null;
        this.width = width;
        this.height = height;
        for (int i = 0; i < TextMode.PAGE_HEIGHT_MAX; i++) {
            lineBuffer.add(new Char[TextMode.LINE_WIDTH_MAX]);
        }
        resetFull();
    }

    public Printer(Screen screen, Canvas page) {
        this.screen = screen;
        this.textPage = page;
        this.width = page.getWidth();
        this.height = page.getHeight();
        for (int i = 0; i < TextMode.PAGE_HEIGHT_MAX; i++) {
            lineBuffer.add(new Char[TextMode.LINE_WIDTH_MAX]);
        }
        resetFull();
    }

    public void addToCharBuffer(String string) {
        string.codePoints().mapToObj((int i) -> String.valueOf(Character.toChars(i))).forEach((String s) -> {
                if (csiMode != 0) {
                    s = addToCsiBuffer(s);
                }
                switch (s) {
                case "\r":
                    moveTo(leftMargin, y);
                    break;
                case "\n":
                    moveTo(leftMargin, y + 1);
                    break;
                case "\f":
                    moveTo(leftMargin, topMargin);
                    for (Char[] line : lineBuffer)
                        Arrays.fill(line, null);
                    if (textPage != null) {
                        GraphicsContext context = textPage.getGraphicsContext2D();
                        if (background != null && background != Color.TRANSPARENT) {
                            context.setFill(background);
                            context.fillRect(0.0, 0.0, textPage.getWidth(), textPage.getHeight());
                        } else
                            context.clearRect(0.0, 0.0, textPage.getWidth(), textPage.getHeight());
                    }
                    break;
                case "\b":
                    moveHoriz(-1);
                    break;
                case "\t":
                    moveTo((x + 8) % 8, y);
                    break;
                case "\u001b":
                    if (csiEnabled) {
                        csiSeq = s;
                        csiMode = 1;
                    }
                    break;
                default:
                    if (s.length() > 0 && s.codePointAt(0) >= 0x20) {
                        drawChar(x, y, setChar(x, y, s));
                        moveHoriz(1);
                    }
                    break;
                }
            });
    }

    private String addToCsiBuffer(String s) {
        if (csiMode == 1) {
            switch (s) {
            case "[":
                csiMode = 2;
                csiSeq += s;
                break;
            case "c":
                csiMode = 0;
                resetFull();
                break;
            default:
                csiReset();
                return s;
            }
        } else if (csiMode == 2) {
            int c = s.codePointAt(0);
            if (c >= 0x30 && c <= 0x3f) {
                csiSeq += s;
            } else if (c >= 0x20 && c <= 0x2f) {
                csiMode = 3;
                csiSeq += s;
            } else if (c >= 0x40 && c <= 0x7e) {
                csiSeq += s;
                csiFinish();
            } else {
                csiReset();
                return s;
            }

        } else if (csiMode == 3) {
            int c = s.codePointAt(0);
            if (c >= 0x20 && c <= 0x2f) {
                csiSeq += s;
            } else if (c >= 0x40 && c <= 0x7e) {
                csiSeq += s;
                csiFinish();
            } else {
                csiReset();
                return s;
            }
        }
        return "";
    }

    public void beginningOfLine() {
        x = leftMargin;
    }

    public void beginningOfPage() {
        x = leftMargin;
        y = topMargin;
    }

    @Override
    public void clear() {
        print("\f");
    }

    public void clearAt(int x, int y) {
        printAt(x, y, " ");
    }

    public void clearLine(int y) {
        y = constrainY(y);
        if (y > 0 && y <= TextMode.PAGE_HEIGHT_MAX) {
            Arrays.fill(lineBuffer.get(y - 1), null);
            dirty(1, y);
            dirty(getLineWidth(), y);
            if (!useBuffer)
                redrawDirty();
        }
    }

    public void clearRegion(int x, int y, int width, int height) {
        if (x > getLineWidth() || y > getPageHeight())
            return;
        int x2 = Math.min(x + width - 1, getLineWidth());
        int y2 = Math.min(y + height - 1, getPageHeight());
        if (x2 < 1 || y2 < 1)
            return;
        int x1 = Math.max(1, x);
        int y1 = Math.max(1, y);
        // Char fillWith = new Char("*", Color.BLACK, Color.GREEN, Color.TRANSPARENT,
        // 0);
        for (int i = y1; i <= y2; i++) {
            Arrays.fill(lineBuffer.get(i - 1), x1 - 1, x2, null);
        }
        dirty(x1, y1);
        dirty(x2, y2);
        if (!useBuffer)
            redrawDirty();
    }

    private int constrainX(int x) {
        return x; // Math.min(LINE_WIDTH_HIRES, Math.max(1, x));
    }

    public int constrainY(int y) {
        return y; // Math.min(pageHeight, Math.max(1, y));
    }

    public int constrainYOrScroll(int y) {
        if (autoscroll) {
            if (y < 1) {
                scroll(y - 1);
                return 1;
            } else if (y > getPageHeight()) {
                scroll(y - getPageHeight());
                return getPageHeight();
            }
        }

        return y;// Math.min(pageHeight, Math.max(1, y));
    }

    private void csiFinish() {
        ControlSequences.applyCsi(this, csiSeq);
        csiReset();
    }

    private void csiReset() {
        csiMode = 0;
        csiSeq = null;
    }

    public void cycleMode(boolean adjustDisplayAspect) {
        textMode = textMode.nextMode();
        if (adjustDisplayAspect && screen != null)
            screen.setAspect(textMode.getAspect());
        dirty(1, 1);
        dirty(getLineWidth(), getPageHeight());
        if (!useBuffer)
            redrawDirty();
    }

    private void drawChar(int x, int y, Char c) {
        if (useBuffer) {
            dirty(x, y);
        } else if (c != null && textPage != null) {
            GraphicsContext context = textPage.getGraphicsContext2D();

            context.setFill(c.fill);
            context.setStroke(c.stroke);
            font.drawTextAt(context, (x - 1) * getCharWidth(), y * getCharHeight(), c.s,
                            textMode.getCharWidth() / textMode.getCharBoxSize(), c.mode, c.bg);
        }
    }

    public void drawCharCells() {
        if (screen != null) {
            GraphicsContext context = screen.getBackgroundContext();
            screen.clearBackground();
            double w = getCharWidth();
            double h = getCharHeight();
            context.save();
            context.setGlobalBlendMode(BlendMode.EXCLUSION);
            context.setFill(Color.WHITE.deriveColor(0.0, 1.0, 1.0, 0.1));
            for (int x = 0; x < getLineWidth(); x++) {
                for (int y = 0; y < getPageHeight(); y++) {
                    if ((x + y) % 2 == 0)
                        context.fillRect(x * w, y * h, w, h);
                }
            }
            context.restore();
        }
    }

    public Color getBackground(int x, int y) {
        Char c = null;
        if (x > 0 && x <= TextMode.LINE_WIDTH_MAX && y > 0 && y <= TextMode.PAGE_HEIGHT_MAX) {
            c = lineBuffer.get(y - 1)[x - 1];
        }
        Color bg = Color.TRANSPARENT;
        if (c != null && c.bg instanceof Color)
            bg = (Color) c.bg;
        else if (background instanceof Color)
            bg = (Color) background;
        return bg;
    }

    public boolean getBold() {
        return (videoAttrs & TextFont.ATTR_BRIGHT) != 0;
    }

    public String getChar(int x, int y) {
        Char c = null;
        if (x > 0 && x <= TextMode.LINE_WIDTH_MAX && y > 0 && y <= TextMode.PAGE_HEIGHT_MAX) {
            c = lineBuffer.get(y - 1)[x - 1];
        }
        if (c != null)
            return c.s;
        else
            return " ";
    }

    public double getCharHeight() {
        return textMode.getCharHeight();
    }

    public double getCharWidth() {
        return textMode.getCharWidth();
    }

    public Color getColor(int x, int y) {
        Char c = null;
        if (x > 0 && x <= TextMode.LINE_WIDTH_MAX && y > 0 && y <= TextMode.PAGE_HEIGHT_MAX) {
            c = lineBuffer.get(y - 1)[x - 1];
        }
        if (c != null)
            return c.fill;
        else
            return fill;
    }

    public TextFont getFont() {
        return font;
    }

    public boolean getItalics() {
        return (videoAttrs & TextFont.ATTR_ITALIC) != 0;
    }

    /**
     * @return the leftMargin
     */
    public int getLeftMargin() {
        return leftMargin;
    }

    public int getLineWidth() {
        return textMode.getLineWidth();
    }

    public int getPageHeight() {
        return textMode.getPageHeight();
    }

    public boolean getReverseVideo() {
        return (videoAttrs & TextFont.ATTR_INVERSE) != 0;
    }

    public TextMode getTextMode() {
        return textMode;
    }

    /**
     * @return the topMargin
     */
    public int getTopMargin() {
        return topMargin;
    }

    public int getVideoMode() {
        return videoAttrs;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isFilled(int x, int y) {
        return !getChar(x, y).equals(" ");
    }

    @Override
    public void layerToBack() {
        if (screen != null) {
            screen.moveToBack(this);
        }
    }

    @Override
    public void layerToFront() {
        if (screen != null) {
            screen.moveToFront(this);
        }
    }

    public void move(int deltaX, int deltaY) {
        x = constrainX(x + deltaX);
        y = constrainYOrScroll(y + deltaY);
    }

    public void moveHoriz(int dist) {
        x = constrainX(x + dist);
    }

    public void moveTo(int newX, int newY) {
        x = constrainX(newX);
        y = constrainYOrScroll(newY);
    }

    public void moveVert(int dist) {
        y = constrainYOrScroll(y + dist);
    }

    public void plot(int x, int y) {
        plot(x, y, (a, b) -> a | b);
    }

    public void plot(int x, int y, BiFunction<Integer, Integer, Integer> op) {
        int textX = (x) / 2 + 1;
        int textY = (y) / 2 + 1;
        int bitPos = (x + 1) % 2 + ((y + 1) % 2) * 2;
        String blockChar = BlocksAndBoxes.unicodeBlocks[1 << bitPos];
        // System.out.println(blockChar + ", " + bitPos + ", ("+ (x) + ", " + (y) + ")"+
        // ", (" + (textX) + ", " + (textY) + ")");
        String s = BlocksAndBoxes.blockComposeOrOverwrite(getChar(textX, textY), blockChar, op);
        // System.out.println("Merge '" + getChar(textX, textY) + "' + '" + blockChar +
        // "' = '" + s + "'");
        printAt(textX, textY, s);
    }

    public void print(String s) {
        addToCharBuffer(s);
    }

    public void print(String s, Color paint) {
        Color tmp = fill;
        fill = paint;
        addToCharBuffer(s);
        fill = tmp;
    }

    public void printAt(int atX, int atY, String s) {
        moveTo(atX, atY);
        print(s);
    }

    public void printAt(int atX, int atY, String s, Color ink) {
        moveTo(atX, atY);
        print(s, ink);
    }

    public void println() {
        print("\n");
    }

    public void println(String s) {
        print(s);
        print("\n");
    }

    public void redrawTextPage() {
        redrawTextPage(1, 1, getLineWidth(), getPageHeight());
        clean();
    }

    private void redrawTextPage(int x0, int y0, int x1, int y1) {
        /*
         * System.out.printf("redrawTextPage benchmark");
         * System.out.printf("  %5s %5s %7s %4s %5s %5s %5s%n", "ms", "chars",
         * "ms/char", "mode", "indir", "inv", "fake"); for (int m = -1; m < 8; m++) {
         * long t0 = System.currentTimeMillis(); int n = 0;
         */
        if (textPage == null)
            return;
        GraphicsContext context = textPage.getGraphicsContext2D();

        double px0 = (x0 - 1) * getCharWidth(), py0 = (y0 - 1) * getCharHeight();
        double px1 = x1 * getCharWidth(), py1 = y1 * getCharHeight();
        if (DEBUG_REDRAW)
            System.out.printf("redrawTextPage(): Area to clear: (%2f,%2f)–(%2f,%2f)%n", px0, py0, px1, py1);
        if (background != null && background != Color.TRANSPARENT) {
            context.setFill(background);
            context.fillRect(px0, py0, px1 - px0, py1 - py0);
        } else {
            context.clearRect(px0, py0, px1 - px0, py1 - py0);
        }
        for (int tmpY = y0; tmpY <= y1; tmpY++) {
            Char[] line = lineBuffer.get(tmpY - 1);
            for (int tmpX = x0; tmpX <= x1; tmpX++) {
                Char c = line[tmpX - 1];
                if (c != null) {
                    context.save();
                    context.setFill(c.fill);
                    context.setStroke(c.stroke);
                    Paint bg = c.bg == background ? null : c.bg;
                    font.drawTextNoClearAt(context, (tmpX - 1) * getCharWidth(), tmpY * getCharHeight(), c.s,
                                           textMode.getCharWidth() / textMode.getCharBoxSize(), c.mode/* m */, bg);
                    context.restore();
                    // n++;

                }
            }
        }
        /*
         * long t = System.currentTimeMillis() - t0; if (m >= 0)
         * System.out.printf("  %5d %5d %7.4f %4d %5b %5b %5b%n", t, n, ((double) t) /
         * n, m, (m & 3) != 0, (m & 1) != 0, (m & 4) != 0); } System.out.println();
         */
    }

    public void resetAttrs() {
        this.fill = DEFAULT_FILL;
        this.stroke = DEFAULT_STROKE;
        this.background = DEFAULT_BACKGROUND;
        this.videoAttrs = 0;
        this.csiSeq = null;
        this.csiMode = 0;
    }

    public void resetFull() {
        resetAttrs();
        beginningOfPage();
        this.autoscroll = true;
        this.textMode = DEFAULT_MODE;
        redrawTextPage();
    }

    public void restoreCursor() {
        x = savedX;
        y = savedY;
    }

    public void saveCursor() {
        savedX = x;
        savedY = y;
    }

    void scroll(int i) {
        while (i < 0) {
            scrollDown();
            i++;
        }
        while (i > 0) {
            scrollUp();
            i--;
        }
    }

    public void scrollDown() {
        Char[] remove = lineBuffer.remove(lineBuffer.size() - 1);
        Arrays.fill(remove, null);
        lineBuffer.add(0, remove);
        dirty(1, 1);
        dirty(getLineWidth(), getPageHeight());
        if (!useBuffer)
            redrawDirty();
    }

    public void scrollUp() {
        Char[] remove = lineBuffer.remove(0);
        Arrays.fill(remove, null);
        lineBuffer.add(remove);
        dirty(1, 1);
        dirty(getLineWidth(), getPageHeight());
        if (!useBuffer)
            redrawDirty();
    }

    public boolean setAutoScroll(boolean autoScroll) {
        boolean old = autoscroll;
        autoscroll = autoScroll;
        return old;
    }

    public void setBackground(int x, int y, Paint bg) {
        Char c = null;
        if (x > 0 && x <= TextMode.LINE_WIDTH_MAX && y > 0 && y <= TextMode.PAGE_HEIGHT_MAX) {
            c = lineBuffer.get(y - 1)[x - 1];
        }
        if (c != null) {
            c.bg = bg;
            drawChar(x, y, c);
        }
    }

    public void setBackground(Paint bgColor) {
        this.background = bgColor != null ? bgColor : DEFAULT_BACKGROUND;
    }

    public void setBold(boolean enabled) {
        if (enabled)
            videoAttrs |= TextFont.ATTR_BRIGHT;
        else
            videoAttrs &= ~TextFont.ATTR_BRIGHT;
    }

    public Char setChar(int x, int y, String s) {
        if (x > 0 && x <= TextMode.LINE_WIDTH_MAX && y > 0 && y <= TextMode.PAGE_HEIGHT_MAX) {
            Char c = new Char(s, fill, stroke, background, videoAttrs);
            lineBuffer.get(y - 1)[x - 1] = c;
            return c;
        }
        return null;
    }

    public void setColor(int x, int y, Color fill) {
        Char c = null;
        if (x > 0 && x <= TextMode.LINE_WIDTH_MAX && y > 0 && y <= TextMode.PAGE_HEIGHT_MAX) {
            c = lineBuffer.get(y - 1)[x - 1];
        }
        if (c != null) {
            c.fill = fill;
            drawChar(x, y, c);
        }
    }

    public void setFill(Color fill) {
        this.fill = fill != null ? fill : DEFAULT_FILL;
    }

    public void setFont(TextFont font) {
        this.font = font;
    }

    public void setInk(Color ink) {
        fill = ink != null ? ink : DEFAULT_FILL;
        stroke = ink != null ? ink : DEFAULT_STROKE;
    }

    public void setItalics(boolean enabled) {
        if (enabled)
            videoAttrs |= TextFont.ATTR_ITALIC;
        else
            videoAttrs &= ~TextFont.ATTR_ITALIC;
    }

    /**
     */
    public void setLeftMargin() {
        this.leftMargin = x;
    }

    /**
     * @param leftMargin
     *            the leftMargin to set
     */
    public void setLeftMargin(int leftMargin) {
        this.leftMargin = constrainX(leftMargin);
    }

    public void setReverseVideo(boolean enabled) {
        if (enabled)
            videoAttrs |= TextFont.ATTR_INVERSE;
        else
            videoAttrs &= ~TextFont.ATTR_INVERSE;
    }

    public void setStroke(Color stroke) {
        this.stroke = stroke != null ? stroke : DEFAULT_STROKE;
    }

    public void setTextMode(TextMode mode) {
        setTextMode(mode, false);
    }

    public void setTextMode(TextMode mode, boolean adjustDisplayAspect) {
        if (mode == null)
            throw new IllegalArgumentException();
        textMode = mode;
        if (adjustDisplayAspect && screen != null)
            screen.setAspect(textMode.getAspect());
        dirty(1, 1);
        dirty(getLineWidth(), getPageHeight());
        if (!useBuffer)
            redrawDirty();
    }

    public void setTopMargin() {
        this.topMargin = y;
    }

    /**
     * @param topMargin
     *            the topMargin to set
     */
    public void setTopMargin(int topMargin) {
        this.topMargin = constrainY(topMargin);
    }

    public void setVideoAttrDisabled(int attr) {
        videoAttrs &= ~attr;
    }

    public void setVideoAttrEnabled(int attr) {
        videoAttrs |= attr;
    }

    public void setVideoAttrs(int attr) {
        videoAttrs = attr;
    }

    public void unplot(int x, int y) {
        plot(x, y, (a, b) -> a & ~b);
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    private boolean isDirty() {
        return dirtyX0 <= dirtyX1 || dirtyY0 <= dirtyY1;
    }

    /**
     * Expand the dirty region (area that should be redrawn) to include the given
     * position
     *
     * @param x
     * @param y
     */
    private void dirty(int x, int y) {
        dirtyX0 = Math.max(Math.min(x, dirtyX0), 1);
        dirtyX1 = Math.min(Math.max(x, dirtyX1), getLineWidth());
        dirtyY0 = Math.max(Math.min(y, dirtyY0), 1);
        dirtyY1 = Math.min(Math.max(y, dirtyY1), getPageHeight());
    }

    /**
     * Redraw the part of the page that has changed since last redraw.
     */
    public void redrawDirty() {
        if (isDirty()) {
            if (DEBUG_REDRAW)
                System.out.printf("redrawDirty(): Dirty region is (%d,%d)–(%d,%d)%n", dirtyX0, dirtyY0, dirtyX1,
                                  dirtyY1);
            redrawTextPage(dirtyX0, dirtyY0, dirtyX1, dirtyY1);
            clean();
        }
    }

    /**
     * Mark the entire page as clean
     */
    private void clean() {
        dirtyX0 = Integer.MAX_VALUE;
        dirtyX1 = Integer.MIN_VALUE;
        dirtyY0 = Integer.MAX_VALUE;
        dirtyY1 = Integer.MIN_VALUE;
    }

    /**
     * With buffered printing, nothing is actually drawn until
     * {@link #redrawDirty()} or {@link #redrawTextPage()} is called.
     *
     * @param buffering
     *            Whether to use buffering
     */
    public void setBuffering(boolean buffering) {
        useBuffer = buffering;
    }

    /**
     * @return True if buffering is enabled
     * @see #setBuffering(boolean)
     */
    public boolean getBuffering() {
        return useBuffer;
    }
}
