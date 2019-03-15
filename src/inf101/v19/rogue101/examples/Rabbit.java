package inf101.v19.rogue101.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import inf101.v19.gfx.gfxmode.ITurtle;
import inf101.v19.grid.GridDirection;
import inf101.v19.rogue101.game.IGame;
import inf101.v19.rogue101.objects.IItem;
import inf101.v19.rogue101.objects.INonPlayer;

public class Rabbit implements INonPlayer {
    private int food = 0;
    private int hp = getMaxHealth();

    @Override
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
        List<GridDirection> possibleMoves = new ArrayList<>();
        for (GridDirection dir : GridDirection.FOUR_DIRECTIONS) {
            if (game.canGo(dir))
                possibleMoves.add(dir);
        }
        if (!possibleMoves.isEmpty()) {
            Collections.shuffle(possibleMoves);
            game.move(possibleMoves.get(0));
        }
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
        return "rabbit";
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public String getSymbol() {
        return hp > 0 ? "R" : "¤";
    }

    @Override
    public int handleDamage(IGame game, IItem source, int amount) {
        hp -= amount;
        return amount;
    }

}
