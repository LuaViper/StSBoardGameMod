package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractAttackCardChoice;
import BoardGame.cards.BGStatus.BGDazed;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGSneckoEyeGainDazed extends AbstractAttackCardChoice {
    public static final String ID = "BGSneckoEyeGainDazed";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGSneckoEyeGainDazed");

    public BGSneckoEyeGainDazed() {
        super("BGSneckoEyeGainDazed", cardStrings.NAME, "red/attack/reckless_charge", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.SPECIAL, AbstractCard.CardTarget.NONE);
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void onChoseThisOption() {
        addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 1, false, true));
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGSneckoEyeGainDazed();
    }
}

