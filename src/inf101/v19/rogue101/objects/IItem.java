package inf101.v19.rogue101.objects;

import inf101.v19.gfx.gfxmode.ITurtle;
import inf101.v19.rogue101.events.IEvent;
import inf101.v19.rogue101.game.IGame;

/**
 * An {@link IItem} is something that can be placed on the map or into a
 * container of items.
 * <p>
 * An {@link IActor} is a special case of {@link IItem} used for things that are
 * “alive”, such as the player or AI-controlled monsters/NPCs.
 * <p>
 * By default, all items have a hit point / health system (they can be damaged),
 * and can be picked up. So you could potentially destroy an item or pick up a
 * monster.
 *
 * @author anya
 */
public interface IItem extends Comparable<IItem> {
    @Override
    default int compareTo(IItem other) {
        return Integer.compare(getSize(), other.getSize());
    }

    /**
     * Draw this item on the screen.
     * <p>
     * The turtle-painter will be positioned in the centre of the cell. You should
     * avoid drawing outside the cell (size is indicated by the <code>w</code> and
     * <code>h</code> parameters, so you can move ±w/2 and ±h/2 from the initial
     * position). If you want to start in the lower left corner of the cell, you can
     * start by doing <code>painter.jump(-w/2,h/2)</code> ((0,0) is in the top-left
     * corner of the screen, so negative X points left and positive Y points down).
     * <p>
     * If this method returns <code>true</code>, the game will <em>not</em> print
     * {@link #getSymbol()} in the cell.
     * <p>
     * All calls to <code>painter.save()</code> must be matched by a call to
     * <code>painter.restore()</code>.
     *
     * @param painter
     *            A turtle-painter for drawing
     * @param w
     *            The width of the cell
     * @param h
     *            The height of the cell
     * @return False if the letter from {@link #getSymbol()} should be drawn instead
     */
    default boolean draw(ITurtle painter, double w, double h) {
        return false;
    }

    /**
     * @return "a" or "an", depending on the name
     */
    default String getArticle() {
        return "a";
    }

    /**
     * Get current remaining health points.
     * <p>
     * An object's <em>health points</em> determines how much damage it can take
     * before it is destroyed / broken / killed.
     *
     * @return Current health points for this item
     */
    int getCurrentHealth();

    /**
     * The defence score determines how hard an object/actor is to hit or grab.
     *
     * @return Defence score of this object
     */
    int getDefence();

    /**
     * Get item health as a 0.0..1.0 proportion.
     *
     * <li><code>getHealth() >= 1.0</code> means perfect condition
     * <li><code>getHealth() <= 0.0</code> means broken or dead
     * <li><code>0.0 < getHealth() < 1.0</code> means partially damaged
     *
     * @return Health, in the range 0.0 to 1.0
     */
    default double getHealthStatus() {
        return getMaxHealth() > 0 ? getCurrentHealth() / getMaxHealth() : 0;
    }

    /**
     * Get maximum health points.
     *
     * An object's <em>health points</em> determines how much damage it can take
     * before it is destroyed / broken / killed.
     *
     * @return Max health points for this item
     */
    int getMaxHealth();

    /**
     * Get a (user-friendly) name for the item
     * <p>
     * Used for things like <code>"You see " + getArticle() + " " + getName()</code>
     *
     * @return Item's name
     */
    String getName();

    /**
     * Get a map symbol used for printing this item on the screen.
     * <p>
     * This is usually the same as {@link #getSymbol()}, but could also include
     * special control characters for changing the text colour, for example.
     *
     *
     * @return A string to be displayed for this item on the screen (should be only
     *         one column wide when printed)
     * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#Colors">ANSI
     *      escape code (on Wikipedia)</a>
     */
    default String getPrintSymbol() {
        return getSymbol();
    }

    /**
     * Get the size of the object.
     * <p>
     * The size determines how much space an item will use if put into a container.
     *
     * @return Size of the item
     */
    int getSize();

    /**
     * Get the map symbol of this item.
     * <p>
     * The symbol can be used on a text-only map, or when loading a map from text.
     * <p>
     * The symbol should be a single Unicode codepoint (i.e.,
     * <code>getSymbol().codePointCount(0, getSymbol().length()) == 1</code>). In
     * most cases this means that the symbol should be a single character (i.e.,
     * getSymbol().length() == 1); but there are a few Unicode characters (such as
     * many emojis and special symbols) that require two Java <code>char</code>s.
     *
     * @return A single-codepoint string with the item's symbol
     */
    String getSymbol();

    /**
     * Inform the item that it has been damaged
     *
     * @param game
     *            The game
     * @param source
     *            The item (usually an IActor) that caused the damage
     * @param amount
     *            How much damage the item should take
     * @return Amount of damage actually taken (could be less than
     *         <code>amount</code> due to armour/protection effects)
     */
    int handleDamage(IGame game, IItem source, int amount);

    /**
     * Inform the item that something has happened.
     *
     * @param event
     *            An object describing the event.
     * @return
     */
    default <T> T handleEvent(IEvent<T> event) {
        return event.getData();
    }

    /**
     * @return True if this item has been destroyed, and should be removed from the map
     */
    default boolean isDestroyed() {
        return getCurrentHealth() < 0;
    }
}
