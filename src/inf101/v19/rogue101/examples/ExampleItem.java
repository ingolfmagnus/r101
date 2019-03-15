package inf101.v19.rogue101.examples;

import inf101.v19.gfx.gfxmode.ITurtle;
import inf101.v19.rogue101.game.IGame;
import inf101.v19.rogue101.objects.IItem;

public class ExampleItem implements IItem {
    private int hp = getMaxHealth();

    @Override
    public boolean draw(ITurtle painter, double w, double h) {
        return false;
    }

    @Override
    public int getCurrentHealth() {
        return hp;
    }

    @Override
    public int getDefence() {
        return 0;
    }

    @Override
    public int getMaxHealth() {
        return 1;
    }

    @Override
    public String getName() {
        return "strange model of an item";
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public String getSymbol() {
        return "X";
    }

    @Override
    public int handleDamage(IGame game, IItem source, int amount) {
        hp -= amount;
        return amount;
    }

}
