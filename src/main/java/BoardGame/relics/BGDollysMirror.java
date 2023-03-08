
package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.powers.BGTriggerAnyDieAbilityPower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class BGDollysMirror extends AbstractBGRelic implements DieControlledRelic  {
    public static final String ID = "BGDollysMirror";
    private boolean cardSelected = true;

    public BGDollysMirror() {
        super("BGDollysMirror", "mirror.png", AbstractRelic.RelicTier.SHOP, AbstractRelic.LandingSound.SOLID);
    }
    public int getPrice() {return 7;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    public String getQuickSummary(){ if(TheDie.monsterRoll==1)return "Copy any die relic";
    return "";}
    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==1){
            activateDieAbility();
        }
    }
    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGTriggerAnyDieAbilityPower((AbstractCreature)AbstractDungeon.player)));
    }
    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

    @Override
    public void onRightClick() {// On right click
        //Don't actually do anything when right-clicked, since we'd be spending a die ability to activate a die ability
//        //final Logger logger = LogManager.getLogger(BoardGame.class.getName());
//        //logger.info("Relic.onRightClick");
//        addToBot((AbstractGameAction)new BGActivateDieAbilityAction(this));
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

    public AbstractRelic makeCopy() {
        return new BGDollysMirror();
    }
}


