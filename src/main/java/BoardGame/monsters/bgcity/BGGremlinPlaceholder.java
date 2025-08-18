package BoardGame.monsters.bgcity;
 import BoardGame.monsters.BGDamageIcons;

import BoardGame.monsters.AbstractBGMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGGremlinPlaceholder extends AbstractBGMonster implements BGDamageIcons {
    public static final String ID = "BGGremlinPlaceholder";
    public static final String NAME = "Placeholder Gremlin";

    public BGGremlinPlaceholder() {
        super(NAME, "BGGremlinPlaceholder", 0, -999.0F, -999.0F, 200.0F, 310.0F, null, 35.0F, 0.0F);
        setHp(0);
        this.isDying=true;
    }

    @Override
    public void takeTurn() {

    }

    @Override
    protected void getMove(int num) {
        setMove((byte) 0, AbstractMonster.Intent.NONE);
    }
}
