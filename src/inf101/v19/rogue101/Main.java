package inf101.v19.rogue101;

import inf101.v19.gfx.Screen;
import inf101.v19.gfx.gfxmode.ITurtle;
import inf101.v19.gfx.textmode.Printer;
import inf101.v19.gfx.textmode.TextMode;
import inf101.v19.rogue101.game.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Main extends Application {
    // you might want to tune these options
    public static final boolean MAIN_USE_BACKGROUND_GRID = true;
    public static final boolean MAP_AUTO_SCALE_ITEM_DRAW = true;
    public static final boolean MAP_DRAW_ONLY_DIRTY_CELLS = false;
    public static final TextMode MAIN_TEXT_MODE = TextMode.MODE_80X25;

    public static final boolean DEBUG_TIME = false;

    public static final int LINE_MAP_BOTTOM = 20;
    public static final int LINE_STATUS = 21;
    public static final int LINE_MSG1 = 22;
    public static final int LINE_MSG2 = 23;
    public static final int LINE_MSG3 = 24;
    public static final int LINE_DEBUG = 25;
    public static final int COLUMN_MAP_END = 40;
    public static final int COLUMN_RIGHTSIDE_START = 41;

    public static void main(String[] args) {
        launch(args);
    }

    private Screen screen;
    private ITurtle painter;
    private Printer printer;

    private Game game;

    private boolean grid = MAIN_USE_BACKGROUND_GRID;
    private boolean autoNextTurn = false;
    private Timeline bigStepTimeline;
    private Timeline smallStepTimeline;

    private void setup() {
        //
        game = new Game(screen, painter, printer);
        game.draw();

        //
        bigStepTimeline = new Timeline();
        bigStepTimeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame kf = new KeyFrame(Duration.millis(1000), (ActionEvent event) -> {
                if (autoNextTurn) {
                    doTurn();
                }
            });
        bigStepTimeline.getKeyFrames().add(kf);
        // bigStepTimeline.playFromStart();

        //
        smallStepTimeline = new Timeline();
        smallStepTimeline.setCycleCount(1);
        kf = new KeyFrame(Duration.millis(1), (ActionEvent event) -> {
                doTurn();
            });
        smallStepTimeline.getKeyFrames().add(kf);

        // finally, start game
        doTurn();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        screen = Screen.startPaintScene(primaryStage, Screen.CONFIG_PIXELS_STEP_SCALED); // Screen.CONFIG_SCREEN_FULLSCREEN_NO_HINT);

        printer = screen.createPrinter();
        painter = screen.createPainter();
        printer.setTextMode(MAIN_TEXT_MODE, true);

        // Font with emojis – need separate download
        // printer.setFont(Printer.FONT_SYMBOLA);

        if (grid)
            printer.drawCharCells();
        printer.setAutoScroll(false);
        screen.setKeyPressedHandler(this::dispatchKeystroke);
        /*
         * screen.setKeyTypedHandler((KeyEvent event) -> { if (event.getCharacter() !=
         * KeyEvent.CHAR_UNDEFINED) { printer.print(event.getCharacter()); return true;
         * } return false; });
         */
        setup();

        primaryStage.show();

    }

    public void doTurn() {
        long t = System.currentTimeMillis();
        boolean waitForPlayer = game.doTurn();
        if (DEBUG_TIME)
            System.out.println("doTurn() took " + (System.currentTimeMillis() - t) + "ms");
        long t2 = System.currentTimeMillis();
        game.draw();
        printer.redrawDirty();
        if (DEBUG_TIME) {
            System.out.println("draw() took " + (System.currentTimeMillis() - t2) + "ms");
            System.out.println("doTurn()+draw() took " + (System.currentTimeMillis() - t) + "ms");
            System.out.println("waiting for player? " + waitForPlayer);
        }
        if (!waitForPlayer)
            smallStepTimeline.playFromStart(); // this will kickstart a new turn in a few milliseconds
    }

    public static String BUILTIN_MAP = "40 20\n" //
        + "########################################\n" //
        + "#...... ..C.R ......R.R......... ..R...#\n" //
        + "#.R@R...... ..........RC..R...... ... .#\n" //
        + "#.......... ..R......R.R..R........R...#\n" //
        + "#R. R......... R..R.........R......R.RR#\n" //
        + "#... ..R........R......R. R........R.RR#\n" //
        + "###############################....R..R#\n" //
        + "#. ...R..C. ..R.R..........C.RC....... #\n" //
        + "#..C.....R..... ........RR R..R.....R..#\n" //
        + "#...R..R.R..............R .R..R........#\n" //
        + "#.R.....R........RRR.......R.. .C....R.#\n" //
        + "#.C.. ..R.  .....R.RC..C....R...R..C. .#\n" //
        + "#. R..............R R..R........C.....R#\n" //
        + "#........###############################\n" //
        + "# R.........R...C....R.....R...R.......#\n" //
        + "# R......... R..R........R......R.RR..##\n" //
        + "#. ..R........R.....R.  ....C...R.RR...#\n" //
        + "#....RC..R........R......R.RC......R...#\n" //
        + "#.C.... ..... ......... .R..R....R...R.#\n" //
        + "########################################\n" //
	;

    private boolean dispatchKeystroke(KeyEvent event) {
        KeyCode code = event.getCode();
        if (event.isShortcutDown()) {
            if (code == KeyCode.Q) {
                System.exit(0);
            } else if (code == KeyCode.R) {
                printer.cycleMode(true);
                if (grid)
                    printer.drawCharCells();
                game.draw();
                printer.redrawDirty();
                return true;
            } else if (code == KeyCode.A) {
                screen.cycleAspect();
                if (grid)
                    printer.drawCharCells();
                return true;
            } else if (code == KeyCode.G) {
                grid = !grid;
                if (grid)
                    printer.drawCharCells();
                else
                    screen.clearBackground();
                return true;
            } else if (code == KeyCode.F) {
                screen.setFullScreen(!screen.isFullScreen());
                return true;
            } else if (code == KeyCode.L) {
                printer.redrawTextPage();
                return true;
            }
        } else if (code == KeyCode.ENTER) {
            try {
                doTurn();
            } catch (Exception e) {
                printer.printAt(1, 25, "Exception: " + e.getMessage(), Color.RED);
                e.printStackTrace();
            }
            return true;
        } else {
            try {
                game.keyPressed(code);
                doTurn();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter writer = new PrintWriter(sw);
                    e.printStackTrace(writer);
                    writer.close();
                    String trace = sw.toString().split("\n")[0];
                    game.displayDebug("Exception: " + trace);
                } catch (Exception e2) {
                    System.err.println("Also got this exception trying to display the previous one");
                    e2.printStackTrace();
                    game.displayDebug("Exception: " + e.getMessage());
                }
            }
            printer.redrawDirty();
            return true;
        }
        return false;
    }
}
