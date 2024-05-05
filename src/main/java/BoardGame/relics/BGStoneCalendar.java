package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.screen.TargetSelectScreen;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGStoneCalendar extends AbstractBGRelic implements DieControlledRelic {
    public BGStoneCalendar() {
        super("BGStoneCalendar", "calendar.png", RelicTier.COMMON, LandingSound.HEAVY);
    }
    public int getPrice() {return 7;}

    public String getQuickSummary(){
        if(TheDie.monsterRoll==4)return "4 Damage";
    else return "";}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }



    public AbstractRelic makeCopy() {
        return new BGStoneCalendar();
    }




    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==4){
            activateDieAbility();
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
            addToBot((AbstractGameAction) new DamageAction(target,
                    new DamageInfo(AbstractDungeon.player,4, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        };
        addToBot((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for Stone Calendar (4 damage)."));
    }

    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

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


