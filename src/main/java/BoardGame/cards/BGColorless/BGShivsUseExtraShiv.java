package BoardGame.cards.BGColorless;

import BoardGame.actions.BGUseShivAction;
import BoardGame.cards.AbstractAttackCardChoice;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGShivsUseExtraShiv extends AbstractAttackCardChoice {
    public static final String ID = "BGShivsUseExtraShiv";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGShivsUseExtraShiv");

    public BGShivsUseExtraShiv() {
        super("BGShivsUseExtraShiv", cardStrings.NAME, "colorless/attack/shiv", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.SPECIAL, AbstractCard.CardTarget.NONE);
        this.baseDamage=1;
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

