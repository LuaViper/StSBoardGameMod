package BoardGame.multicharacter;

import BoardGame.monsters.AbstractBGMonster;

public class NullMonster extends AbstractBGMonster implements BGMultiCreature {
    public NullMonster(){
        super("NULL","NULL",0,0,0,0,0,"null",0,0);
    }

    @Override
    public void takeTurn() {

    }

    @Override
    protected void getMove(int i) {

    }
}
