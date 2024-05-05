package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.powers.NilrysCodexCompatible;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BGMercuryHourglass
        extends AbstractBGRelic implements DieControlledRelic, NilrysCodexCompatible {
    public static final String ID = "BGMercury Hourglass";
    private static final int DMG = 1;

    public BGMercuryHourglass() {
        super("BGMercury Hourglass", "hourglass.png", AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.CLINK);
    }
    public int getPrice() {return 6;}

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }



    public AbstractRelic makeCopy() {
        return new BGMercuryHourglass();
    }


    public String getQuickSummary(){if(TheDie.monsterRoll==1 || TheDie.monsterRoll==2)return "1 AoE Damage";
    else return "";}
    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==1 || TheDie.finalRelicRoll==2){
            activateDieAbility();
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToBot((AbstractGameAction)new DamageAllEnemiesAction(AbstractDungeon.player,


                DamageInfo.createDamageMatrix(DMG, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_LIGHT));

    }

    public void Trigger2Ability(){
        activateDieAbility();
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


