//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package BoardGame.powers;

import BoardGame.actions.BGToggleDiscardingAtEndOfTurnFlagAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

//TODO: check if this still interacts properly with end-of-turn Mayhem
public class BGDarkEmbracePower extends AbstractPower {




    public static final String POWER_ID = "BGDark Embrace";
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Dark Embrace");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    public BGDarkEmbracePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = "BGDark Embrace";
        this.owner = owner;
        this.amount = amount;
        this.updateDescription();
        this.loadRegion("darkembrace");
    }

    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[0];
        } else {
            this.description = DESCRIPTIONS[1] + this.amount + DESCRIPTIONS[2];
        }

    }

    public void onExhaust(AbstractCard card) {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.flash();

            //It turns out the end-of-turn discard action resolves at a curious point where
            // endTurnQueued, isEndingTurn, AND actionManager.turnHasEnded are all false!

            //if(AbstractDungeon.player.endTurnQueued){
            //if(AbstractDungeon.player.isEndingTurn){
            //if (AbstractDungeon.actionManager.turnHasEnded) {
            if(BGToggleDiscardingAtEndOfTurnFlagAction.Field.discardingCardsAtEndOfTurn.get(AbstractDungeon.actionManager)){
                //end turn: discard hand first, then draw
                this.addToBot(new DrawCardAction(this.owner, this.amount));
            }else{
                //not end turn: draw before whatever caused us to exhaust enters discard pile
                this.addToTop(new DrawCardAction(this.owner, this.amount));
            }
        }

    }




}
