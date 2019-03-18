package inf101.v19.rogue101.tests;

import inf101.v19.grid.ILocation;
import inf101.v19.rogue101.examples.Carrot;
import inf101.v19.rogue101.examples.Rabbit;
import inf101.v19.rogue101.map.GameMap;
import inf101.v19.rogue101.objects.Dust;
import inf101.v19.rogue101.objects.IItem;
import inf101.v19.rogue101.objects.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameMapTest {

    @Test
    void testSortedCheckRabbit() {
        GameMap gameMap = new GameMap(20, 20);
        ILocation location = gameMap.getLocation(10, 10);
        gameMap.add(location, new Dust());
        Rabbit theRabbit = new Rabbit();
        gameMap.add(location, theRabbit);
        gameMap.add(location, new Carrot());
        List<IItem> cellItems = gameMap.getAll(location);
        assertEquals(cellItems.get(0), theRabbit);
    }

    @Test
    void testSortedCheckPlayerAndDust() {
        GameMap gameMap = new GameMap(20, 20);
        ILocation location = gameMap.getLocation(10, 10);
        Dust theDust = new Dust();
        gameMap.add(location, theDust);
        Player thePlayer = new Player();
        gameMap.add(location, thePlayer);
        gameMap.add(location, new Carrot());
        List<IItem> cellItems = gameMap.getAll(location);
        // Player on top
        assertEquals(cellItems.get(0), thePlayer);
        // Dust on bottom
        assertEquals(cellItems.get(2), theDust);

    }

}
