package BoardGame.powers;

//TODO: see if we can get the green poison HP bar thing working with this one too

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.PoisonLoseHpAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class BGPoisonPower extends AbstractBGPower {
    public static final String POWER_ID = "BGPoison";

    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("BoardGame:BGPoison");

    public static final String NAME = powerStrings.NAME;

    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private AbstractCreature source;

    public BGPoisonPower(AbstractCreature owner, AbstractCreature source, int poisonAmt) {
        this.name = NAME;
        this.ID = "BGPoison";
        this.owner = owner;
        this.source = source;
        this.amount = poisonAmt;
        //TODO: physical token limits -- also this should be 30 across ALL enemies
        if (this.amount >= 30)
            this.amount = 30;
        updateDescription();
        loadRegion("poison");
        this.type = AbstractPower.PowerType.DEBUFF;
        this.isTurnBased = true;
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_POISON", 0.05F);
    }

    public void updateDescription() {
        if (this.owner == null || this.owner.isPlayer) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[2] + this.amount + DESCRIPTIONS[1];
        }
    }

    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
//        if (this.amount > 29 && AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.THE_SILENT)
//            UnlockTracker.unlockAchievement("CATALYST");
        //TODO: physical token limits -- also this should be 30 across ALL enemies
        if(this.amount>30)
            this.amount=30;
    }

    public void atStartOfTurn() {
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT &&
                !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            flashWithoutSound();
            //note that we're calling vanilla PoisonLoseHpAction here --
            // it only checks for vanilla poison, so it won't decrement BGpoison, and it still uses the damage amount we pass it
            addToBot((AbstractGameAction)new PoisonLoseHpAction(this.owner, this.source, this.amount, AbstractGameAction.AttackEffect.POISON));
        }
    }
}

