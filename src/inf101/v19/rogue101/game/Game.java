package inf101.v19.rogue101.game;

import inf101.v19.gfx.Screen;
import inf101.v19.gfx.gfxmode.ITurtle;
import inf101.v19.gfx.gfxmode.TurtlePainter;
import inf101.v19.gfx.textmode.Printer;
import inf101.v19.grid.GridDirection;
import inf101.v19.grid.IGrid;
import inf101.v19.grid.ILocation;
import inf101.v19.rogue101.Main;
import inf101.v19.rogue101.examples.Carrot;
import inf101.v19.rogue101.examples.Rabbit;
import inf101.v19.rogue101.map.GameMap;
import inf101.v19.rogue101.map.IGameMap;
import inf101.v19.rogue101.map.IMapView;
import inf101.v19.rogue101.map.MapReader;
import inf101.v19.rogue101.objects.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.Supplier;

public class Game implements IGame {
    /**
     * All the IActors that have things left to do this turn
     */
    private List<IActor> actors = Collections.synchronizedList(new ArrayList<>());
    /**
     * For fancy solution to factory problem
     */
    private Map<String, Supplier<IItem>> itemFactories = new HashMap<>();
    /**
     * Useful random generator
     */
    private Random random = new Random();
    /**
     * The game map. {@link IGameMap} gives us a few more details than
     * {@link IMapView} (write access to item lists); the game needs this but
     * individual items don't.
     */
    private IGameMap map;
    private IActor currentActor;
    private ILocation currentLocation;
    private int movePoints = 0;
    private final ITurtle painter;
    private final Printer printer;
    private int numPlayers = 0;
    private List<Carrot> carrots = new ArrayList<>();

    public Game(Screen screen, ITurtle painter, Printer printer) {
        this.painter = painter;
        this.printer = printer;

        // TODO: (*very* optional) for advanced factory technique, use
        // something like "itemFactories.put("R", () -> new Rabbit());"
        // must be done *before* you read the map

        // NOTE: in a more realistic situation, we will have multiple levels (one map
        // per level), and (at least for a Roguelike game) the levels should be
        // generated
        //
        // inputGrid will be filled with single-character strings indicating what (if
        // anything)
        // should be placed at that map square
        IGrid<String> inputGrid = MapReader.readFile("maps/level1.txt");
        if (inputGrid == null) {
            System.err.println("Map not found – falling back to builtin map");
            inputGrid = MapReader.readString(Main.BUILTIN_MAP);
        }
        this.map = new GameMap(inputGrid.getArea());
        for (ILocation loc : inputGrid.locations()) {
            IItem item = createItem(inputGrid.get(loc));
            if (item != null) {
                map.add(loc, item);
            }
        }

    }

    public Game(String mapString) {
        printer = new Printer(1280, 720);
        painter = new TurtlePainter(1280, 720);
        IGrid<String> inputGrid = MapReader.readString(mapString);
        this.map = new GameMap(inputGrid.getArea());
        for (ILocation loc : inputGrid.locations()) {
            IItem item = createItem(inputGrid.get(loc));
            if (item != null) {
                map.add(loc, item);
            }
        }
    }

    @Override
    public void addItem(IItem item) {
        map.add(currentLocation, item);
    }

    @Override
    public void addItem(String sym) {
        IItem item = createItem(sym);
        if (item != null)
            map.add(currentLocation, item);
    }

    @Override
    public ILocation attack(GridDirection dir, IItem target) {
        ILocation loc = map.getNeighbour(getLocation(), dir);
        if (!map.has(loc, target)) throw new IllegalMoveException("Target isn't there!");

        // TODO: implement attack

        map.clean(loc);

        if (target.isDestroyed()) {
            return move(dir);
        } else {
            movePoints--;
            return currentLocation;
        }
    }

    /**
     * Begin a new game turn, or continue to the previous turn
     *
     * @return True if the game should wait for more user input
     */
    public boolean doTurn() {
        doCarrotPlanting(20);
        do {
            if (actors.isEmpty()) {
                // System.err.println("new turn!");

                // no one in the queue, we're starting a new turn!
                // first collect all the actors:
                beginTurn();
            }

            // Do the Carrots turns:
            for (Carrot carrotItem : carrots){
                carrotItem.doTurn(this);
            }

            // process actors one by one; for the IPlayer, we return and wait for keypresses
            // Possible TODO: for INonPlayer, we could also return early (returning
            // *false*), and then insert a little timer delay between each non-player move
            // (the timer
            // is already set up in Main)
            while (!actors.isEmpty()) {
                // get the next player or non-player in the queue
                currentActor = actors.remove(0);
                if (currentActor.isDestroyed()) // skip if it's dead
                    continue;
                currentLocation = map.getLocation(currentActor);
                if (currentLocation == null) {
                    displayDebug("doTurn(): Whoops! Actor has disappeared from the map: " + currentActor);
                }
                movePoints = 1; // everyone gets to do one thing

                if (currentActor instanceof INonPlayer) {
                    // computer-controlled players do their stuff right away
                    ((INonPlayer) currentActor).doTurn(this);
                    // remove any dead items from current location
                    map.clean(currentLocation);
                } else if (currentActor instanceof IPlayer) {
                    if (currentActor.isDestroyed()) {
                        // a dead human player gets removed from the game
                        // TODO: you might want to be more clever here
                        displayMessage("YOU DIE!!!");
                        map.remove(currentLocation, currentActor);
                        currentActor = null;
                        currentLocation = null;
                    } else {
                        // For the human player, we need to wait for input, so we just return.
                        // Further keypresses will cause keyPressed() to be called, and once the human
                        // makes a move, it'll lose its movement point and doTurn() will be called again
                        //
                        // NOTE: currentActor and currentLocation are set to the IPlayer (above),
                        // so the game remembers who the player is whenever new keypresses occur. This
                        // is also how e.g., getLocalItems() work – the game always keeps track of
                        // whose turn it is.
                        return true;
                    }
                } else {
                    displayDebug("doTurn(): Hmm, this is a very strange actor: " + currentActor);
                }
            }
        } while (numPlayers > 0); // we can safely repeat if we have players, since we'll return (and break out of
        // the loop) once we hit the player
        return true;
    }

    /**
     * With a probability, plant a Carrot
     * @param percentProbability
     */
    private void doCarrotPlanting(int percentProbability) {
        Random R = new Random();
        if (R.nextInt(100) < percentProbability) {
            ILocation randomLoc = null;
            do {
                int randomX = R.nextInt(map.getWidth());
                int randomY = R.nextInt(map.getHeight());
                randomLoc = map.getLocation(randomX, randomY);
            } while (map.getItems(randomLoc).size() != 0);
            Carrot newCarrot = new Carrot();
            map.add(randomLoc, newCarrot);
        }
    }

    /**
     * Go through the map and collect all the actors.
     */
    private void beginTurn() {
        numPlayers = 0;
        // Collect carrots each turn
        carrots.clear();
        // this extra fancy iteration over each map location runs *in parallel* on
        // multicore systems!
        // that makes some things more tricky, hence the "synchronized" block and
        // "Collections.synchronizedList()" in the initialization of "actors".
        // NOTE: If you want to modify this yourself, it might be a good idea to replace
        // "parallelStream()" by "stream()", because weird things can happen when many
        // things happen
        // at the same time! (or do INF214 or DAT103 to learn about locks and threading)
        map.getArea().stream().forEach((loc) -> { // will do this for each location in map
                List<IItem> list = map.getAllModifiable(loc); // all items at loc
                Iterator<IItem> li = list.iterator(); // manual iterator lets us remove() items
                while (li.hasNext()) { // this is what "for(IItem item : list)" looks like on the inside
                    IItem item = li.next();
                    if (item.getCurrentHealth() < 0) {
                        // normally, we expect these things to be removed when they are destroyed, so
                        // this shouldn't happen
                        synchronized (this) {
                            formatDebug("beginTurn(): found and removed leftover destroyed item %s '%s' at %s%n",
                                        item.getName(), item.getSymbol(), loc);
                        }
                        li.remove();
                        map.remove(loc, item); // need to do this too, to update item map
                    } else if (item instanceof IPlayer) {
                        actors.add(0, (IActor) item); // we let the human player go first
                        synchronized (this) {
                            numPlayers++;
                        }
                    } else if (item instanceof IActor) {
                        actors.add((IActor) item); // add other actors to the end of the list
                    } else if (item instanceof Carrot) {
                        carrots.add((Carrot)item);
                    }
                }
            });
    }

    @Override
    public boolean canGo(GridDirection dir) {
        return map.canGo(currentLocation, dir);
    }

    @Override
    public IItem createItem(String sym) {
        switch (sym) {
        case "#":
            return new Wall();
        case ".":
            return new Dust();
        case "R":
            return new Rabbit();
        case "C":
            return new Carrot();
        case "@":
            return new Player();
        case " ":
            return null;
        default:
            // alternative/advanced method
            Supplier<IItem> factory = itemFactories.get(sym);
            if (factory != null) {
                return factory.get();
            } else {
                System.err.println("createItem: Don't know how to create a '" + sym + "'");
                return null;
            }
        }
    }

    @Override
    public void displayDebug(String s) {
        printer.clearLine(Main.LINE_DEBUG);
        printer.printAt(1, Main.LINE_DEBUG, s, Color.DARKRED);
        System.err.println(s);
    }

    @Override
    public void displayMessage(String s) {
        // it should be safe to print to lines Main.LINE_MSG1, Main.LINE_MSG2,
        // Main.LINE_MSG3
        // TODO: you can save the last three lines, and display/scroll them
        printer.clearLine(Main.LINE_MSG1);
        printer.printAt(1, Main.LINE_MSG1, s);
        System.out.println("Message: «" + s + "»");
    }

    @Override
    public void displayStatus(String s) {
        printer.clearLine(Main.LINE_STATUS);
        printer.printAt(1, Main.LINE_STATUS, s);
        System.out.println("Status: «" + s + "»");
    }

    public void draw() {
        map.draw(painter, printer);
    }

    @Override
    public boolean drop(IItem item) {
        if (item != null) {
            map.add(currentLocation, item);
            return true;
        } else
            return false;
    }

    @Override
    public void formatDebug(String s, Object... args) {
        displayDebug(String.format(s, args));
    }

    @Override
    public void formatMessage(String s, Object... args) {
        displayMessage(String.format(s, args));
    }

    @Override
    public void formatStatus(String s, Object... args) {
        displayStatus(String.format(s, args));
    }

    @Override
    public int getHeight() {
        return map.getHeight();
    }

    @Override
    public List<IItem> getLocalItems() {
        return map.getItems(currentLocation);
    }

    @Override
    public ILocation getLocation() {
        return currentLocation;
    }

    @Override
    public ILocation getLocation(GridDirection dir) {
        if (currentLocation.canGo(dir))
            return currentLocation.go(dir);
        else
            return null;
    }

    /**
     * Return the game map. {@link IGameMap} gives us a few more details than
     * {@link IMapView} (write access to item lists); the game needs this but
     * individual items don't.
     */
    @Override
    public IMapView getMap() {
        return map;
    }

    @Override
    public List<GridDirection> getPossibleMoves() {
        List<GridDirection> possibleMoves = new ArrayList<>();
        for (GridDirection dir : GridDirection.FOUR_DIRECTIONS) {
            if (canGo(dir))
                possibleMoves.add(dir);
        }
        return possibleMoves;
    }

    @Override
    public List<ILocation> getVisible() {
        // TODO: maybe replace 3 by some sort of visibility range obtained from
        // currentActor?
        return map.getNeighbourhood(currentLocation, 3);
    }

    @Override
    public int getWidth() {
        return map.getWidth();
    }

    public void keyPressed(KeyCode code) {
        // only an IPlayer/human can handle keypresses, and only if it's the human's
        // turn
        if (currentActor instanceof IPlayer) {
            ((IPlayer) currentActor).keyPressed(this, code); // do your thing
            if (movePoints <= 0)
                doTurn(); // proceed with turn if we're out of moves
        }
    }

    @Override
    public ILocation move(GridDirection dir) {
        if (movePoints < 1)
            throw new IllegalMoveException("You're out of moves!");
        ILocation newLoc = map.go(currentLocation, dir);
        map.remove(currentLocation, currentActor);
        map.add(newLoc, currentActor);
        currentLocation = newLoc;
        movePoints--;
        return currentLocation;
    }

    @Override
    public IItem pickUp(IItem item) {
        if (item != null && map.has(currentLocation, item)) {
            // TODO: bruk getAttack()/getDefence() til å avgjøre om man får til å plukke opp
            // tingen
            // evt.: en IActor kan bare plukkes opp hvis den har få/ingen helsepoeng igjen
            map.remove(currentLocation, item);
            return item;
        } else {
            return null;
        }
    }

    @Override
    public ILocation rangedAttack(GridDirection dir, IItem target) {
        return currentLocation;
    }

    @Override
    public ITurtle getPainter() {
        return painter;
    }

    @Override
    public Printer getPrinter() {
        return printer;
    }

    @Override
    public int[] getFreeTextAreaBounds() {
        int[] area = new int[4];
        area[0] = getWidth() + 1;
        area[1] = 1;
        area[2] = printer.getLineWidth();
        area[3] = printer.getPageHeight() - 5;
        return area;
    }

    @Override
    public void clearFreeTextArea() {
        printer.clearRegion(getWidth() + 1, 1, printer.getLineWidth() - getWidth(), printer.getPageHeight() - 5);
    }

    @Override
    public void clearFreeGraphicsArea() {
        painter.as(GraphicsContext.class).clearRect(getWidth() * printer.getCharWidth(), 0,
                                                    painter.getWidth() - getWidth() * printer.getCharWidth(),
                                                    (printer.getPageHeight() - 5) * printer.getCharHeight());
    }

    @Override
    public double[] getFreeGraphicsAreaBounds() {
        double[] area = new double[4];
        area[0] = getWidth() * printer.getCharWidth();
        area[1] = 0;
        area[2] = painter.getWidth();
        area[3] = getHeight() * printer.getCharHeight();
        return area;
    }

    @Override
    public IActor getActor() {
        return currentActor;
    }

    public ILocation setCurrent(IActor actor) {
        currentLocation = map.getLocation(actor);
        if (currentLocation != null) {
            currentActor = actor;
            movePoints = 1;
        }
        return currentLocation;
    }

    public IActor setCurrent(ILocation loc) {
        List<IActor> list = map.getActors(loc);
        if (!list.isEmpty()) {
            currentActor = list.get(0);
            currentLocation = loc;
            movePoints = 1;
        }
        return currentActor;
    }

    public IActor setCurrent(int x, int y) {
        return setCurrent(map.getLocation(x, y));
    }

    @Override
    public Random getRandom() {
        return random;
    }
}
