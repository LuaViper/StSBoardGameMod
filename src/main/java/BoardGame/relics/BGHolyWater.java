package BoardGame.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BGHolyWater extends AbstractBGRelic implements ClickableRelic {
    public BGHolyWater() {
        super("BGHolyWater", "holy_water.png", AbstractRelic.RelicTier.BOSS, AbstractRelic.LandingSound.MAGICAL);
//        this.counter=2;
//        setCounter(this.counter);
    }
    public static final String ID = "BGHolyWater";

    public String getUpdatedDescription() {
        String desc = this.DESCRIPTIONS[0];
        if (this.usedUp) desc += DieControlledRelic.USED_THIS_COMBAT;
        else desc += DieControlledRelic.RIGHT_CLICK_TO_ACTIVATE;
        return desc;
    }

    public void setCounter(int setCounter) {
        this.counter = setCounter;
        if (this.counter <= 0) {
            /* Used Up (Combat) */
            {
                this.grayscale = true;
                this.usedUp = true;
                this.description = getUpdatedDescription();
                this.tips.clear();
                this.tips.add(new PowerTip(this.name, this.description));
                initializeTips();
            }
            stopPulse();
            this.counter = -2;
        }
    }

    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.
    public void atBattleStartPreDraw() {
        //TODO: probably should call UnusedUp here too (weird things happen if game is reloaded while relic is "used").  other relics too?
        this.counter=2;
        setCounter(this.counter);
    }

    @Override
    public void onRightClick() {// On right click
        if (!isObtained || !isPlayerTurn || this.counter<=0) {
            // If it has been used this turn, or the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
            return; // Don't do anything.
        }

        if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) { // Only if you're in combat
            //usedThisTurn = true; // Set relic as "Used this turn"
            flash(); // Flash

            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            addToBot((AbstractGameAction)new GainEnergyAction(1));

            this.counter-=1;
            setCounter(this.counter);
        }
    }

    public void atTurnStart() {
        isPlayerTurn = true; // It's our turn!
        if (this.counter>0) beginLongPulse();     // Pulse while the player can click on it.
    }

    @Override
    public void onPlayerEndTurn() {
        isPlayerTurn = false; // Not our turn now.
        stopPulse();
    }


    public AbstractRelic makeCopy() {
        return new BGHolyWater();
    }

    @Override
    public void onVictory() {
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
        /* Unused Up */
        {
            this.grayscale = false;
            this.usedUp = false;
            this.description = getUpdatedDescription();
            this.tips.clear();
            this.tips.add(new PowerTip(this.name, this.description));
            initializeTips();
        }
    }

}


