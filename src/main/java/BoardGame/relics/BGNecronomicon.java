package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.powers.BGDoubleAttackPower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

//TODO: if player has a die-altering relic, Necronomicon won't activate right away until roll is locked in. then, if player plays a card before roll is locked in, Necronomicon will activate after the card resolves, causing Double Attack to apply to the NEXT card.
public class BGNecronomicon extends AbstractBGRelic implements DieControlledRelic {
    public static final String ID = "BGNecronomicon";

    //TODOLATER: don't let Dolly's Mirror copy Necronomicon (exact wording is "FIRST attack THIS turn", which is incompatible with double copy rules)

    private boolean activated = true;

    public BGNecronomicon() {
        super("BGNecronomicon", "necronomicon.png", AbstractRelic.RelicTier.SPECIAL, AbstractRelic.LandingSound.FLAT);
    }
    public int getPrice() {return 9;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }




    public AbstractRelic makeCopy() {
        return new BGNecronomicon();
    }

    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

    public String getQuickSummary(){if(TheDie.monsterRoll==1)return "1 Double Attack";
    else return "";}
    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==1){
            activateDieAbility();
        }
    }


    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGDoubleAttackPower((AbstractCreature)AbstractDungeon.player, 1), 1));
        stopPulse();
    }

    @Override
    public void onRightClick() {// On right click
        if (!isObtained || !isPlayerTurn ) {
            return;
        }
        addToBot((AbstractGameAction)new BGActivateDieAbilityAction(this));
    }

    public void atTurnStart() {
        isPlayerTurn = true; // It's our turn!
    }

    @Override
    public void onPlayerEndTurn() {
        isPlayerTurn = false; // Not our turn now.
        stopPulse();
    }

    @Override
    public void onVictory() {
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
    }
}


