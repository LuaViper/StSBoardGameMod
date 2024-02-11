package BoardGame.cards.BGColorless;

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

public class BGDuality2Block extends AbstractAttackCardChoice {
    public static final String ID = "BGDuality2Block";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDuality2Block");

    public BGDuality2Block() {
        super("BGDuality2Block", cardStrings.NAME, "purple/skill/defend", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.SPECIAL, AbstractCard.CardTarget.NONE);
        this.baseBlock=2;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void onChoseThisOption() {
        addToBot((AbstractGameAction)new GainBlockAction(AbstractDungeon.player,2));
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGDuality2Block();
    }
}

