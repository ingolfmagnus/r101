package inf101.v19.rogue101.game;

import java.util.List;
import java.util.Random;

import inf101.v19.gfx.gfxmode.ITurtle;
import inf101.v19.gfx.textmode.Printer;
import inf101.v19.grid.GridDirection;
import inf101.v19.grid.ILocation;
import inf101.v19.rogue101.map.IMapView;
import inf101.v19.rogue101.objects.IItem;
import inf101.v19.rogue101.objects.IActor;
import inf101.v19.rogue101.objects.INonPlayer;
import inf101.v19.rogue101.objects.IPlayer;

/**
 * Game interface
 * <p>
 * The game has a map and a current {@link IActor} (the player or non-player
 * whose turn it currently is). The game also knows the current location of the
 * actor. Most methods that deal with the map will use this current location –
 * they are meant to be used by the current actor for exploring or interacting
 * with its surroundings.
 * <p>
 * In other words, you should avoid calling most of these methods if you're not
 * the current actor. You know you're the current actor when you're inside your
 * {@link IPlayer#keyPressed()} or {@link INonPlayer#doTurn()} method.
 *
 * @author anya
 *
 */
public interface IGame {
    /**
     * Add an item to the current location
     * <p>
     * If the item is an actor, it won't move until the next turn.
     *
     * @param item
     */
    void addItem(IItem item);

    /**
     * Add a new item (identified by symbol) to the current location
     * <p>
     * If the item is an actor, it won't move until the next turn.
     *
     * @param sym
     */
    void addItem(String sym);

    /**
     * Perform an attack on the target.
     * <p>
     * Will use your {@link IActor#getAttack()} against the target's
     * {@link IItem#getDefence()}, and then possiblyu find the damage you're dealing
     * with {@link IActor#getDamage()} and inform the target using
     * {@link IItem#handleDamage(IGame, IItem, int)}
     *
     * @param dir
     *            Direction
     * @param target
     *            A target item, which should be in the neighbouring square in the
     *            given direction
     * @return Your new location if the attack resulted in you moving
     */
    ILocation attack(GridDirection dir, IItem target);

    /**
     * @param dir
     * @return True if it's possible to move in the given direction
     */
    boolean canGo(GridDirection dir);

    /**
     * Create a new item based on a text symbol
     * <p>
     * The item won't be added to the map unless you call {@link #addItem(IItem)}.
     *
     * @param symbol
     * @return The new item
     */
    IItem createItem(String symbol);

    /**
     * Displays a message in the debug area on the screen (bottom line)
     *
     * @param s
     *            A message
     */
    void displayDebug(String s);

    /**
     * Displays a message in the message area on the screen (below the map and the
     * status line)
     *
     * @param s
     *            A message
     */
    void displayMessage(String s);

    /**
     * Displays a status message in the status area on the screen (right below the
     * map)
     *
     * @param s
     *            A message
     */
    void displayStatus(String s);

    /**
     * Displays a message in the message area on the screen (below the map and the
     * status line)
     *
     * @param s
     *            A message
     * @see String#format(String, Object...)
     */
    void formatDebug(String s, Object... args);

    /**
     * Displays a formatted message in the message area on the screen (below the map
     * and the status line)
     *
     * @param s
     *            A message
     * @see String#format(String, Object...)
     */
    void formatMessage(String s, Object... args);

    /**
     * Displays a formatted status message in the status area on the screen (right
     * below the map)
     *
     * @param s
     *            A message
     * @see String#format(String, Object...)
     */
    void formatStatus(String s, Object... args);

    /**
     * Pick up an item
     * <p>
     * This should be used by IActors who want to pick up an item and carry it. The
     * item will be returned if picking it succeeded (the actor <em>might</em> also
     * make a mistake and pick up the wrong item!).
     *
     * @param item
     *            An item, should be in the current map location
     * @return The item that was picked up (normally <code>item</code>), or
     *         <code>null</code> if it failed
     */
    IItem pickUp(IItem item);

    /**
     * Drop an item
     * <p>
     * This should be used by IActors who are carrying an item and want to put it on
     * the ground. Check the return value to see if it succeeded.
     *
     * @param item
     *            An item, should be carried by the current actor and not already be
     *            on the map
     * @return True if the item was placed on the map, false means you're still
     *         holding it
     */
    boolean drop(IItem item);

    /**
     * Clear the unused graphics area (you can fill it with whatever you want!)
     */
    void clearFreeGraphicsArea();

    /**
     * Clear the unused text area (you can fill it with whatever you want!)
     */
    void clearFreeTextArea();

    /**
     * Get the bounds of the free graphics area.
     * <p>
     * You can fill this with whatever you want, using {@link #getPainter()} and
     * {@link #clearFreeGraphicsArea()}.
     *
     * @return Array of coordinates; ([0],[1]) are the top-left corner, and
     *         ([2],[3]) are the bottom-right corner (exclusive).
     */
    double[] getFreeGraphicsAreaBounds();

    /**
     * Get the bounds of the free texxt area.
     * <p>
     * You can fill this with whatever you want, using {@link #getPrinter()} and
     * {@link #clearFreeTextArea()}.
     * <p>
     * You'll probably want to use something like:
     *
     * <pre>
     * int[] bounds = getFreeTextArea();
     * int x = bounds[0];
     * int y = bounds[1];
     * game.getPrinter().printAt(x, y++, "Hello");
     * game.getPrinter().printAt(x, y++, "Do you have any carrot cake?", Color.ORANGE);
     * </pre>
     *
     * @return Array of column/line numbers; ([0],[1]) is the top-left corner, and
     *         ([2],[3]) is the bottom-right corner (inclusive).
     */
    int[] getFreeTextAreaBounds();

    /**
     * See {@link #getFreeGraphicsAreaBounds()}, {@link #clearFreeGraphicsArea()}.
     *
     * @return A Turtle, for painting graphics
     */
    ITurtle getPainter();

    /**
     * See {@link #getFreeTextAreaBounds()}, {@link #clearFreeTextArea()}.
     *
     * @return A printer, for printing text
     */
    Printer getPrinter();

    /**
     * @return The height of the map
     */
    int getHeight();

    /**
     * @return A list of the non-actor items at the current map location
     */
    List<IItem> getLocalItems();

    /**
     * Get the current actor's location.
     * <p>
     * You should only call this from an IActor that is currently doing its move.
     *
     * @return Location of the current actor
     */
    ILocation getLocation();

    /**
     * Get the current actor
     * <p>
     * You can check if it's your move by doing game.getActor()==this.
     *
     * @return The current actor (i.e., the (IPlayer/INonPlayer) player who's turn it currently is)
     */
    IActor getActor();

    /**
     * Get the neighbouring map location in direction <code>dir</code>
     * <p>
     * Same as <code>getLocation().go(dir)</code>
     *
     * @param dir
     *            A direction
     * @return A location, or <code>null</code> if the location would be outside the
     *         map
     */
    ILocation getLocation(GridDirection dir);

    /**
     * @return The map
     */
    IMapView getMap();

    /**
     * @return A list of directions we can move in, for use with
     *         {@link #move(GridDirection)}
     */
    List<GridDirection> getPossibleMoves();

    /**
     * Get a list of all locations that are visible from the current location.
     * <p>
     * The location list is sorted so that nearby locations come earlier in the
     * list. E.g., if <code>l = getVisible()<code> and <code>i < j</code>then
     * <code>getLocation().gridDistanceTo(l.get(i)) < getLocation().gridDistanceTo(l.get(j))</code>
     *
     * @return A list of grid cells visible from the {@link #getLocation()}
     */
    List<ILocation> getVisible();

    /**
     * @return Width of the map
     */
    int getWidth();

    /**
     * Move the current actor in the given direction.
     * <p>
     * The new location will be returned.
     *
     * @param dir
     * @return A new location
     * @throws IllegalMoveException
     *             if moving in that direction is illegal
     */
    ILocation move(GridDirection dir);

    /**
     * Perform a ranged attack on the target.
     * <p>
     * Rules for this are up to you!
     *
     * @param dir
     *            Direction
     * @param target
     *            A target item, which should in some square in the given direction
     * @return Your new location if the attack resulted in you moving (unlikely)
     */
    ILocation rangedAttack(GridDirection dir, IItem target);

    /**
     * @return A random generator
     */
    Random getRandom();
}
