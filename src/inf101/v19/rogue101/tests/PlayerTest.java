package inf101.v19.rogue101.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;

import inf101.v19.grid.GridDirection;
import inf101.v19.grid.ILocation;
import inf101.v19.rogue101.game.Game;
import inf101.v19.rogue101.game.IGame;
import inf101.v19.rogue101.map.GameMap;
import inf101.v19.rogue101.objects.IItem;
import inf101.v19.rogue101.objects.IPlayer;
import javafx.scene.input.KeyCode;

class PlayerTest {
    public static String TEST_MAP = "40 5\n" //
        + "########################################\n" //
        + "#...... ..C.R ......R.R......... ..R...#\n" //
        + "#.R@R...... ..........RC..R...... ... .#\n" //
        + "#... ..R........R......R. R........R.RR#\n" //
        + "########################################\n" //
	;

    @Test
    void testPlayer1() {
        // new game with our test map
        Game game = new Game(TEST_MAP);
        // pick (3,2) as the "current" position; this is where the player is on the
        // test map, so it'll set up the player and return it
        IPlayer player = (IPlayer) game.setCurrent(3, 2);

        // find players location
        ILocation loc = game.getLocation();
        // press "UP" key
        player.keyPressed(game, KeyCode.UP);
        // see that we moved north
        assertEquals(loc.go(GridDirection.NORTH), game.getLocation());
    }

}