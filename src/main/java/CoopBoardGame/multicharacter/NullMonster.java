package CoopBoardGame.multicharacter;

import CoopBoardGame.monsters.AbstractBGMonster;

public class NullMonster extends AbstractBGMonster {

    public NullMonster() {
        super("NULL", "NULL", 0, 0, 0, 0, 0, "null", 0, 0);
    }

    @Override
    public void takeTurn() {}

    @Override
    protected void getMove(int i) {}
}
