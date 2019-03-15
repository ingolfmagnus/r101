package inf101.v19.rogue101.tests;

import static org.junit.jupiter.api.Assertions.fail;

import inf101.v19.grid.ILocation;
import inf101.v19.rogue101.map.GameMap;
import org.junit.jupiter.api.Test;

class GameMapTest {

    @Test
    void testSortedAdd() {
        GameMap gameMap = new GameMap(20, 20);
        ILocation location = gameMap.getLocation(10, 10);
        // TODO:
        fail("Not yet implemented");
    }

}
