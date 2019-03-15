package inf101.v19.gfx.gfxmode;

import inf101.v19.gfx.IPaintLayer;
import javafx.scene.paint.Paint;

public interface IPainter extends IPaintLayer {

    /**
     * Restore graphics settings previously stored by {@link #save()}.
     *
     * @return {@code this}, for sending more draw commands
     */
    IPainter restore();

    /**
     * Store graphics settings.
     *
     * @return {@code this}, for sending more draw commands
     */
    IPainter save();

    /**
     * Set colour used to drawing and filling.
     *
     * @param ink A colour or paint
     * @return {@code this}, for sending more draw commands
     */
    IPainter setInk(Paint ink);

    /**
     * Start drawing a shape.
     *
     * @return An IShape for sending shape drawing commands
     */
    IShape shape();

    /**
     * Start drawing with a turtle.
     *
     * @return An ITurtle for sending turtle drawing commands
     */
    ITurtle turtle();

    /**
     * @return Current ink, as set by {@link #setInk(Paint)}
     */
    Paint getInk();

}
