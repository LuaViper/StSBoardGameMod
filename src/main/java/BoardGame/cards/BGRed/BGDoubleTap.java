//TODO: currently, copied cards are played after the original card (VG) instead of before the original card (BG)
//TODO: NYI: Double Tap CAN be copied, but CANNOT be PlayedTwice

package BoardGame.cards.BGRed;

import BoardGame.powers.BGDoubleAttackPower;
import BoardGame.powers.BGDoubleTapPower;
import BoardGame.characters.BGIronclad;
import BoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
public class BGDoubleTap extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDouble Tap");
    public static final String ID = "BGDouble Tap";

    public BGDoubleTap() {
        super("BGDouble Tap", cardStrings.NAME, "red/skill/double_tap", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.SELF);

        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

//    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
//        boolean canUse = super.canUse(p, m);
//        if (!canUse) {
//            return false;
//        }
//
//        if (p.hasPower("BGDouble Tap")) {
//            this.cantUseMessage = cardStrings.UPGRADE_DESCRIPTION;
//            return false;
//        }
//        return canUse;
//    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        //addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGDoubleTapPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGDoubleAttackPower((AbstractCreature)p, this.magicNumber), this.magicNumber));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }


    public AbstractCard makeCopy() {
        return new BGDoubleTap();
    }
}


