package inf101.v19.rogue101.objects;

import inf101.v19.rogue101.game.IGame;

/**
 * An actor controlled by the computer
 *
 * @author anya
 *
 */
public interface INonPlayer extends IActor {
    /**
     * Do one turn for this non-player
     * <p>
     * This INonPlayer will be the game's current actor ({@link IGame#getActor()})
     * for the duration of this method call.
     *
     * @param game
     *            Game, for interacting with the world
     */
    void doTurn(IGame game);
}
