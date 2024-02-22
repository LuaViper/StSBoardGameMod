package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.screen.TargetSelectScreen;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGTheBoot extends AbstractBGRelic implements DieControlledRelic {
    public BGTheBoot() {
        super("BGTheBoot", "boot.png", RelicTier.COMMON, LandingSound.HEAVY);
    }
    public int getPrice() {return 5;}

    public String getQuickSummary(){
        if(TheDie.monsterRoll==4||TheDie.monsterRoll==5||TheDie.monsterRoll==6)return "1 Damage";
    else return "";}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }



    public AbstractRelic makeCopy() {
        return new BGTheBoot();
    }




    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==4 || TheDie.finalRelicRoll==5 || TheDie.finalRelicRoll==6){
            activateDieAbility();
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));

        TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
            addToBot((AbstractGameAction) new DamageAction(target,
                    new DamageInfo(AbstractDungeon.player,1, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        };
        addToBot((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for The Boot (1 damage)."));
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


