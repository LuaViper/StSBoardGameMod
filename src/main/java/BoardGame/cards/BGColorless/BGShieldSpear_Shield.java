package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import BoardGame.monsters.bgending.BGSpireShield;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGShieldSpear_Shield extends AbstractBGCard {
    public static final String ID = "BGShieldSpear_Shield";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGShieldSpear_Shield");

    public BGShieldSpear_Shield() {
        super("BGShieldSpear_Shield", cardStrings.NAME, "curse/normality", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);

        this.exhaust = true;

    }


    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void onChoseThisOption() {
        if(AbstractDungeon.getMonsters()!=null){
            if(AbstractDungeon.getMonsters().monsters.size()==2) {
                if (AbstractDungeon.getMonsters().monsters.get(1) instanceof BGSpireShield) {
                    ((BGSpireShield) AbstractDungeon.getMonsters().monsters.get(1)).facingAttack();
                }
            }
        }
    }

    public void upgrade() {
    }


    public AbstractCard makeCopy() {
        return new BGShieldSpear_Shield();
    }
}


