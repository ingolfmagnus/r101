package inf101.v19.rogue101.map;

import java.util.List;

import inf101.v19.gfx.gfxmode.ITurtle;
import inf101.v19.gfx.textmode.Printer;
import inf101.v19.grid.ILocation;
import inf101.v19.rogue101.objects.IItem;

/**
 * Extra map methods that are for the game class only!
 *
 * @author anya
 *
 */
public interface IGameMap extends IMapView {

    /**
     * Draw the map
     *
     * @param painter
     * @param printer
     */
    void draw(ITurtle painter, Printer printer);

    /**
     * Get a modifiable list of items
     *
     * @param loc
     * @return
     */
    List<IItem> getAllModifiable(ILocation loc);

    /**
     * Remove any destroyed items at the given location (items where {@link IItem#isDestroyed()} is true)
     *
     * @param loc
     */
    void clean(ILocation loc);

    /**
     * Remove an item
     * @param loc
     * @param item
     */
    void remove(ILocation loc, IItem item);

}
