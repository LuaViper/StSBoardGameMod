package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.powers.BGTrigger2DieAbilityPower;
import BoardGame.powers.BGTriggerCharonsAshesPower;
import BoardGame.powers.NilrysCodexCompatible;
import BoardGame.screen.TargetSelectScreen;
import BoardGame.thedie.TheDie;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

//TODO: don't stop flashing if we click Ashes while another wildcard power is active
public class BGCharonsAshes extends AbstractBGRelic implements DieControlledRelic, ClickableRelic, NilrysCodexCompatible {
    public BGCharonsAshes() {
        super("BGCharonsAshes", "ashes.png", RelicTier.RARE, LandingSound.MAGICAL);
    }
    public int getPrice() {return 7;}

    public String getQuickSummary(){
        if(TheDie.monsterRoll==1||TheDie.monsterRoll==2)return "Exhaust / 2 Damage";
    else return "";}

    public String getUpdatedDescription() {
        BoardGame.BoardGame.logger.info("BGCalipers.getUpdatedDescription...");
        String desc = this.DESCRIPTIONS[0];
        desc+=DieControlledRelic.RIGHT_CLICK_TO_ACTIVATE;   //TODO LATER: "on cooldown" message, if possible
        return desc;
    }

    public AbstractRelic makeCopy() {
        return new BGCharonsAshes();
    }

    public boolean isAvailable=false;


    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==1 || TheDie.finalRelicRoll==2){
            //flash();
            //addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGTriggerCharonsAshesPower((AbstractCreature)AbstractDungeon.player)));
        }

    }

    public void activateDieAbility(){
        if(!AbstractDungeon.player.hand.isEmpty()) {
            flash(); // Flash
            stopPulse();
            isAvailable=false;

            addToBot((AbstractGameAction) new ExhaustAction(1, false));
            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                addToBot((AbstractGameAction) new DamageAction(target,
                        new DamageInfo(AbstractDungeon.player, 2, DamageInfo.DamageType.THORNS),
                        AbstractGameAction.AttackEffect.FIRE));
            };
            //TODO: localization
            addToBot((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose a target for Charon's Ashes (2 damage)."));
        }else{
            //AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, "I don't have any cards to Exhaust.", true));
        }
    }

    public void Trigger2Ability() {
        activateDieAbility();
    }
    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

    @Override
    public void onRightClick() {// On right click
        if (!isObtained || !isPlayerTurn) {
            return;
        }
        if(AbstractDungeon.player.hasPower("BGTriggerCharonsAshesPower")) {
            addToTop(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, "BGTriggerCharonsAshesPower"));
            activateDieAbility();
        }else {   //TODO: if player rolls 1 with Dolly's Mirror, ashes can be activated twice. which activation do we do first? (changing this will require a major logic rework)
            addToBot((AbstractGameAction) new BGActivateDieAbilityAction(this));
        }
    }

    public void atTurnStart() {
        isPlayerTurn = true; // It's our turn!
    }

    @Override
    public void onPlayerEndTurn() {
        isPlayerTurn = false; // Not our turn now.
        isAvailable=false;
        /* Unused Up */ { this.grayscale = false; this.usedUp=false; getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}
        stopPulse();
    }

    @Override
    public void onVictory() {
        isAvailable=false;
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
        /* Unused Up */ { this.grayscale = false; this.usedUp=false; this.description = getUpdatedDescription();this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();}
    }




}


