package BoardGame.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import BoardGame.monsters.AbstractBGMonster;
import BoardGame.BoardGame;
import BoardGame.characters.BGIronclad;


import static BoardGame.BoardGame.makeCardPath;

public class BGGoldenTicket extends AbstractDynamicCard {

    /*
     * Wiki-page: https://github.com/daviscook477/BaseMod/wiki/Custom-Cards
     */

    // TEXT DECLARATION

    public static final String ID = BoardGame.makeID(BGGoldenTicket.class.getSimpleName());
    public static final String IMG = makeCardPath("Power.png");

    // /TEXT DECLARATION/


    // STAT DECLARATION

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.STATUS;
    public static final CardColor COLOR = BGIronclad.Enums.BG_RED;

    private static final int COST = -2;
    private static final int UPGRADE_COST = 1;

    private static final int MAGIC = 1;

    // /STAT DECLARATION/


    public BGGoldenTicket() {

        super(ID, IMG, COST, TYPE, COLOR, RARITY, TARGET);

        //this.tags.add(BaseModCardTags.FORM); //Tag your strike, defend and form cards so that they work correctly.

    }

    // Actions the card should do.
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}

    //Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            initializeDescription();
        }
    }
}
