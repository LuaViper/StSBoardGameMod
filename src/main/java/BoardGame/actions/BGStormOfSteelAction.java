package BoardGame.actions;


import BoardGame.patches.DiscardInOrderOfEnergyCostPatch;
import BoardGame.powers.BGAfterImagePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGStormOfSteelAction extends AbstractGameAction {
    private AbstractPlayer p;

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("GamblingChipAction");

    public static final String[] TEXT = uiStrings.TEXT;

    private int bonus;

    public BGStormOfSteelAction(AbstractCreature source, int bonus) {
        setValues((AbstractCreature)AbstractDungeon.player, source, -1);
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.bonus=bonus;
    }


    public void update() {
        if (this.duration == 0.5F) {
            AbstractDungeon.handCardSelectScreen.open("Discard (Storm of Steel)", 99, true, true);
            addToBot((AbstractGameAction)new WaitAction(0.25F));
            tickDuration();
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            int total=AbstractDungeon.handCardSelectScreen.selectedCards.group.size()+bonus;
            if(total>0) {
                addToTop((AbstractGameAction) new BGGainShivAction(total));
                DiscardInOrderOfEnergyCostPatch.sortByCost(AbstractDungeon.handCardSelectScreen.selectedCards,false);
                for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                    AbstractDungeon.player.hand.moveToDiscardPile(c);
                    GameActionManager.incrementDiscard(false);
                    c.triggerOnManualDiscard();
                }
            }
            if (!AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty()) {
                //That did NOT trigger DiscardAction's AfterImage call, so do that now
                AbstractPower pw=AbstractDungeon.player.getPower("BoardGame:BGAfterImagePower");
                if(pw!=null){
                    ((BGAfterImagePower)pw).onDiscardAction();
                }
            }
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }
        tickDuration();
    }
}


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\SlayTheSpire\desktop-1.0.jar!\com\megacrit\cardcrawl\action\\unique\GamblingChipAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */