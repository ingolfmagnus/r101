package inf101.v19.rogue101.objects;

import inf101.v19.gfx.gfxmode.ITurtle;
import inf101.v19.grid.GridDirection;
import inf101.v19.rogue101.examples.Carrot;
import inf101.v19.rogue101.game.IGame;
import javafx.scene.input.KeyCode;

import java.util.Collections;
import java.util.List;

public class Player implements IPlayer {
    private int food = 0;
    private int hp = getMaxHealth();
    private IItem myItem;


    public void doTurn(IGame game) {
        if (food == 0)
            hp--;
        else
            food--;
        if (hp < 1)
            return;

        for (IItem item : game.getLocalItems()) {
            if (item instanceof Carrot) {
                System.out.println("found carrot!");
                int eaten = item.handleDamage(game, this, 5);
                if (eaten > 0) {
                    System.out.println("ate carrot worth " + eaten + "!");
                    food += eaten;
                    game.displayMessage("You hear a faint crunching (" + getName() + " eats " + item.getArticle() + " "
                                        + item.getName() + ")");
                    return;
                }
            }
        }
        // TODO: prøv forskjellige varianter her
        List<GridDirection> possibleMoves = game.getPossibleMoves();
        if (!possibleMoves.isEmpty()) {
            Collections.shuffle(possibleMoves);
            GridDirection bestDir = possibleMoves.get(0);
            for (GridDirection dir : possibleMoves)
                for (IItem item : game.getMap().getItems(game.getMap().go(game.getLocation(), dir)))
                    if (item instanceof Carrot)
                        bestDir = dir;
            game.move(bestDir);
        }
    }

    /**
     * React to keypress
     * @param game The game engine object
     * @param key The key that has been pressed
     */
    @Override
    public void keyPressed(IGame game, KeyCode key) {
        if (key == KeyCode.LEFT) {
            tryToMove(game, GridDirection.WEST);

        }
        else if (key == KeyCode.UP) {
            tryToMove(game, GridDirection.NORTH);

        }
        else if (key == KeyCode.RIGHT) {
            tryToMove(game, GridDirection.EAST);

        }
        else if (key == KeyCode.DOWN) {
            tryToMove(game, GridDirection.SOUTH);

        }
        else if (key == KeyCode.PAGE_UP) {
            pickUp(game);

        }
        else if (key == KeyCode.PAGE_DOWN) {
            drop(game);

        }
        showStatus(game);
    }

    /**
     * Show status/health info
     * @param game The game engine object
     */
    private void showStatus(IGame game) {
        StringBuilder s = new StringBuilder();
        if (hp > 0)
            s.append("Player health: " + hp + "/" + getMaxHealth());
        else if (hp == 0)
            s.append("Player LOW HEALTH: " + hp + "/" + getMaxHealth());
        else
            s.append("Player died!");

        if (myItem == null) {
            s.append(" [NO ITEMS]");
        } else {
            s.append(" ["+myItem.getName()+"]");
        }
        game.displayStatus(s.toString());
    }

    /**
     * Try to move in the indicated direction
     * @param game
     * @param direction
     * @return True on successful move
     */
    private boolean tryToMove(IGame game, GridDirection direction) {
        boolean canMove = game.getPossibleMoves().contains(direction);
        if (canMove)
            game.move(direction);
        else {
            game.displayMessage("Ouch!");
            if(hitWall(game, direction))
                hp--;
        }
        return canMove;
    }

    /**
     * Check if we hit a wall in a direction
     * @param game The game engine object
     * @param direction The desired direction
     * @return True if a Wall was found
     */
    private boolean hitWall(IGame game, GridDirection direction) {
        boolean foundWall = false;
        List<IItem> hinders = game.getMap().getItems(game.getLocation(direction));
        for (IItem item : hinders)
            foundWall |= item instanceof Wall;
        return foundWall;
    }

    void pickUp(IGame game) {
        if (myItem != null) {
            game.displayMessage("You already carry a " + myItem.getName());
            return;
        }

        List<IItem> items = game.getLocalItems();
        for (IItem item : items)
            if (!(item instanceof Dust)) {
                myItem = game.pickUp(item);
                if (myItem == null)
                    game.displayMessage("Pickup failed!");
                else
                    game.displayMessage("You picked up a " + myItem.getName());
                break;
            }
    }

    void drop(IGame game) {
        if (myItem == null) {
            game.displayMessage("You have nothing to drop");
            return;
        }
        game.drop(myItem);
        myItem = null;
    }

    @Override
    public boolean draw(ITurtle painter, double w, double h) {
        return false;
    }

    @Override
    public int getAttack() {
        return 1000;
    }

    @Override
    public int getCurrentHealth() {
        return hp;
    }

    @Override
    public int getDamage() {
        return 1000;
    }

    @Override
    public int getDefence() {
        return 1000;
    }

    @Override
    public int getMaxHealth() {
        return 10;
    }

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public String getSymbol() {
        return hp > 0 ? "@" : "o";
    }

    @Override
    public int handleDamage(IGame game, IItem source, int amount) {
        hp -= amount;
        return amount;
    }
}
