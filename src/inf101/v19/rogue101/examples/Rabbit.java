package inf101.v19.rogue101.examples;

import inf101.v19.gfx.gfxmode.ITurtle;
import inf101.v19.grid.GridDirection;
import inf101.v19.rogue101.game.IGame;
import inf101.v19.rogue101.objects.IItem;
import inf101.v19.rogue101.objects.INonPlayer;

import java.util.Collections;
import java.util.List;

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
