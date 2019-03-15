package inf101.v19.rogue101.events;

import inf101.v19.rogue101.game.IGame;
import inf101.v19.rogue101.objects.IItem;

/**
 * An “event” is something that happens in the game, typically due to an actor
 * taking some action (but could also be used to transmit user input)
 * <p>
 * Storing the particular action and it's associated data in an object means
 * that we can extend our system with many different kinds of actions – and have
 * many different reactions to those actions – without having to all of the
 * "game rule specific" stuff to all our interfaces and classes.
 * <p>
 * Our event objects let you store an extra (arbitrary) piece of data, giving
 * more information about what happened. An event handler can also update this
 * information, which is a possible way to report back to whomever caused the
 * event in the first place. The event objects also contain an event name, and
 * source/targets of the event (where relevant).
 * <p>
 * This system is fairly simplistic, and you're not expected to make use of it.
 *
 * @author anya
 *
 * @param <T>
 *            Type of the extra data
 */
public interface IEvent<T> {
    /**
     * @return Extra data stored in this event
     */
    T getData();

    /**
     * @return The name of this event
     */
    String getEventName();

    /**
     * Not all events need to be connected to the game, but you can use this if you
     * need it (e.g., for showing a message, or adding something to the map).
     * <p>
     * The result might be null.
     *
     * @return The game associated with this event, or null.
     */
    IGame getGame();

    /**
     * The source is the item that “caused” the event
     * <p>
     * Could be null if the source is unknown or not relevant.
     *
     * @return The source of this event
     */
    IItem getSource();

    /**
     * The target is the item that is affected by the event
     * <p>
     * Could be null if the target is unknown or not relevant.
     *
     * @return The target of this event, or null
     */
    IItem getTarget();

    /**
     * @param value
     *            Extra data to store in this event
     */
    void setData(T value);
}
