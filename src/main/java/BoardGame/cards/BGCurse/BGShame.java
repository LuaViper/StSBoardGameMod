package BoardGame.cards.BGCurse;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGCurse;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SetDontTriggerAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO: Shame eats Metallicize -- it probably shouldn't

public class BGShame extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGShame");
    public static final String ID = "BGShame";

    public BGShame() {
        super("BGShame", cardStrings.NAME, "curse/shame", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.CURSE, BGCurse.Enums.BG_CURSE, AbstractCard.CardRarity.CURSE, AbstractCard.CardTarget.NONE);

    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.dontTriggerOnUseCard) {
            addToTop((AbstractGameAction) new LoseBlockAction(AbstractDungeon.player,AbstractDungeon.player,1));
        }
    }



    public void triggerWhenDrawn() {
        addToBot((AbstractGameAction)new SetDontTriggerAction(this, false));
    }


    public void triggerOnEndOfTurnForPlayingCard() {
        this.dontTriggerOnUseCard = true;
        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this, true));
    }





    public void upgrade() {}



    public AbstractCard makeCopy() {
        return new BGShame();
    }
}


