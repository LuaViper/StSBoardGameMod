package BoardGame.cards.BGColorless;

import BoardGame.actions.BGUseShivAction;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGShivsUseExtraShiv extends AbstractBGAttackCardChoice {
    public static final String ID = "BGShivsUseExtraShiv";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGShivsUseExtraShiv");

    public BGShivsUseExtraShiv() {
        super("BGShivsUseExtraShiv", cardStrings.NAME, "colorless/attack/shiv", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.SPECIAL, AbstractCard.CardTarget.NONE);
        this.baseDamage=1;
        if(AbstractDungeon.player!=null) {
            AbstractPower accuracy = AbstractDungeon.player.getPower("BGAccuracy");
            if (accuracy != null) {
                this.damage = this.baseDamage + accuracy.amount;
            }
        }
        this.isDamageModified=(this.baseDamage!=this.damage);

    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void onChoseThisOption() {
        addToTop(new BGUseShivAction(true,false,0,"Choose a target for Shiv."));;
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGShivsUseExtraShiv();
    }
}

