package inf101.v19.rogue101.events;

import inf101.v19.rogue101.game.IGame;
import inf101.v19.rogue101.objects.IItem;

/**
 * Example implementation of events – could be used to have more complex
 * behaviour than just attack/defend/get damaged/pickup/drop.
 *
 * @author anya
 *
 * @param <T>
 *            Relevant extra data for this particular event
 */
public class GameEvent<T> implements IEvent<T> {
    public static final String ATTACK_FAILURE = "attackFailure";
    public static final String ATTACK_SUCCESS = "attackSuccess";
    public static final String DEFEND_FAILURE = "defendFailure";
    public static final String DEFEND_SUCCESS = "defendSuccess";
    public static final String EATEN = "eaten";

    /**
     * Create and send events for an attack.
     * <p>
     * Both attacker and defender will receive appropriate events (ATTACK_* or
     * DEFEND_*), depending on who “won”. The amount of damage is available through
     * {@link #getData()}.
     * <p>
     * Attacker will be sent the "damage" that was actually done (as returned by
     * defender's event handler)
     * <p>
     * Methods such as these could sensible be placed in IGame/Game.
     *
     * @param success
     *            True if attack succeeded (attacker "won")
     * @param attacker
     * @param defender
     * @param damage
     */
    public static void triggerAttack(boolean success, IItem attacker, IItem defender, int damage) {
        if (success) {
            Integer result = defender
                .handleEvent(new GameEvent<Integer>(DEFEND_FAILURE, null, attacker, defender, damage));
            if (result != null)
                damage = result;
            attacker.handleEvent(new GameEvent<Integer>(ATTACK_SUCCESS, null, attacker, defender, damage));
        } else {
            attacker.handleEvent(new GameEvent<Integer>(ATTACK_FAILURE, null, attacker, defender, 0));
            defender.handleEvent(new GameEvent<Integer>(DEFEND_SUCCESS, null, attacker, defender, 0));
        }
    }

    private final String name;
    private final IItem source;
    private final IItem target;
    private T value;

    private final IGame game;

    /**
     * Create a new game event
     *
     * @param name
     *            The name is used when checking which event this is / determine its
     *            “meaning”
     * @param game
     *            The game, or <code>null</code> if unknown/not relevant
     * @param source
     *            The item that caused the event, or <code>null</code> if
     *            unknown/not relevant
     * @param target
     *            The item that receives the event, or <code>null</code> if
     *            unknown/not relevant
     * @param value
     *            Arbitrary extra data
     */
    public GameEvent(String name, IGame game, IItem source, IItem target, T value) {
        this.name = name;
        this.game = game;
        this.source = source;
        this.target = target;
        this.value = value;
    }

    /**
     * Create a new game event
     *
     * @param name
     *            The name is used when checking which event this is / determine its
     *            “meaning”
     * @param source
     *            The item that caused the event, or <code>null</code> if
     *            unknown/not relevant
     */
    public GameEvent(String name, IItem source) {
        this(name, null, source, null, null);
    }

    /**
     * Create a new game event
     *
     * @param name
     *            The name is used when checking which event this is / determine its
     *            “meaning”
     * @param source
     *            The item that caused the event, or <code>null</code> if
     *            unknown/not relevant
     * @param value
     *            Arbitrary extra data
     */
    public GameEvent(String name, IItem source, T value) {
        this(name, null, source, null, value);
    }

    @Override
    public T getData() {
        return value;
    }

    @Override
    public String getEventName() {
        return name;
    }

    @Override
    public IGame getGame() {
        return game;
    }

    @Override
    public IItem getSource() {
        return source;
    }

    @Override
    public IItem getTarget() {
        return target;
    }

    @Override
    public void setData(T value) {
        this.value = value;
    }

}
