package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractAttackCardChoice;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGGremlinHornDrawACard extends AbstractAttackCardChoice {
    public static final String ID = "BGGremlinHornDrawACard";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGGremlinHornDrawACard");

    public BGGremlinHornDrawACard() {
        super("BGGremlinHornDrawACard", cardStrings.NAME, "colorless/skill/finesse", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.SPECIAL, AbstractCard.CardTarget.NONE);
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void onChoseThisOption() {
        addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, 1));
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGGremlinHornDrawACard();
    }
}

