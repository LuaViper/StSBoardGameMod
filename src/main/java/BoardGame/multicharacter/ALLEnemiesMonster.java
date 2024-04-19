package BoardGame.multicharacter;

import BoardGame.monsters.AbstractBGMonster;

public class ALLEnemiesMonster extends AbstractBGMonster implements MultiCreature {
    public ALLEnemiesMonster(){
        super("ALL","ALL",0,0,0,0,0,"all",0,0);
    }

    @Override
    public void takeTurn() {

    }

    @Override
    protected void getMove(int i) {

    }
}
