package inf101.v19.rogue101.game;

/**
 * An exception to be thrown when an moving object tries to do an illegal move,
 * for example to a position outside the map.
 *
 * @author larsjaffke
 *
 */
public class IllegalMoveException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 7641529271996915740L;

    public IllegalMoveException(String message) {
        super(message);
    }
}
