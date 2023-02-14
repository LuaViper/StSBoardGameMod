package BoardGame.relics;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGBirdFacedUrn extends AbstractBGRelic {
    public static final String ID = "BGBird Faced Urn";
    private static final int BLOCK_AMT = 1;

    public BGBirdFacedUrn() {
        super("BGBird Faced Urn", "bird_urn.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.SOLID);
    }
    public int getPrice() {return 9;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == AbstractCard.CardType.POWER) {
            flash();
            addToTop((AbstractGameAction)new GainBlockAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, BLOCK_AMT));
            addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        }
    }


    public AbstractRelic makeCopy() {
        return new BGBirdFacedUrn();
    }
}


