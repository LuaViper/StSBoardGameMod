package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import BoardGame.monsters.bgending.BGSpireSpear;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGShieldSpear_Spear extends AbstractBGCard {
    public static final String ID = "BGShieldSpear_Spear";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGShieldSpear_Spear");

    public BGShieldSpear_Spear() {
        super("BGShieldSpear_Spear", cardStrings.NAME, "curse/necronomicurse", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);

        this.exhaust = true;

    }


    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void onChoseThisOption() {
        if(AbstractDungeon.getMonsters()!=null){
            if(AbstractDungeon.getMonsters().monsters.size()==2) {
                if (AbstractDungeon.getMonsters().monsters.get(0) instanceof BGSpireSpear) {
                    ((BGSpireSpear) AbstractDungeon.getMonsters().monsters.get(0)).facingAttack();
                }
            }
        }
    }

    public void upgrade() {
    }


    public AbstractCard makeCopy() {
        return new BGShieldSpear_Spear();
    }
}


