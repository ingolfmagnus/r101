package inf101.v19.rogue101.objects;

import inf101.v19.gfx.gfxmode.ITurtle;
import inf101.v19.gfx.textmode.BlocksAndBoxes;
import inf101.v19.rogue101.game.IGame;

public class Wall implements IItem {
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
        return 10;
    }

    @Override
    public int getMaxHealth() {
        return 1000;
    }

    @Override
    public String getName() {
        return "wall";
    }

    @Override
    public int getSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getSymbol() {
        return BlocksAndBoxes.BLOCK_FULL;
    }

    @Override
    public int handleDamage(IGame game, IItem source, int amount) {
        hp -= amount;
        return amount;
    }
}
