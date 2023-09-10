package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.PutOnDeckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGThinkingAhead extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGThinking Ahead");
    public static final String ID = "BGThinking Ahead";

    public BGThinkingAhead() {
        super("BGThinking Ahead", cardStrings.NAME, "colorless/skill/thinking_ahead", 0, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.NONE);









        this.baseMagicNumber=2;
        this.magicNumber=this.baseMagicNumber;
        this.exhaust = true;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)p, this.magicNumber));
        if (AbstractDungeon.player.hand.size() > 0) {
            addToBot((AbstractGameAction)new PutOnDeckAction((AbstractCreature)p, (AbstractCreature)p, 1, false));
        }
    }


    public AbstractCard makeCopy() {
        return new BGThinkingAhead();
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}


