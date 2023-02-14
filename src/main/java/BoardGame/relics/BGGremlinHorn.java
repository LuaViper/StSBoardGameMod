package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.actions.ChooseOneAttackAction;
import BoardGame.cards.BGColorless.BGGremlinHornDrawACard;
import BoardGame.cards.BGColorless.BGGremlinHornGainEnergy;
import BoardGame.cards.AbstractAttackCardChoice;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class BGGremlinHorn extends AbstractBGRelic implements DieControlledRelic {
    public BGGremlinHorn() {
        super("BGGremlin Horn", "gremlin_horn.png", AbstractRelic.RelicTier.UNCOMMON, AbstractRelic.LandingSound.HEAVY);
        this.energyBased = true;
    }
    public int getPrice() {return 8;}

    public String getUpdatedDescription() {
        if (AbstractDungeon.player != null) {
            return setDescription(AbstractDungeon.player.chosenClass);
        }
        return setDescription((AbstractPlayer.PlayerClass)null);
    }
    public static final String ID = "Gremlin Horn";

    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[0];
    }


    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }


//    public void onMonsterDeath(AbstractMonster m) {
//        if (m.currentHealth == 0 && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
//            flash();
//            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)m, this));
//            addToBot((AbstractGameAction)new GainEnergyAction(1));
//            addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, 1));
//        }
//    }


    public AbstractRelic makeCopy() {
        return new BGGremlinHorn();
    }




    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==4){
            flash();
            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            addToBot((AbstractGameAction)new DrawCardAction((AbstractCreature)AbstractDungeon.player, 1));
        }else if(TheDie.finalRelicRoll==5){
            flash();
            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            addToBot((AbstractGameAction)new GainEnergyAction(1));
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        ArrayList<AbstractAttackCardChoice> attackChoices = new ArrayList<>();
        attackChoices.add(new BGGremlinHornDrawACard());
        attackChoices.add(new BGGremlinHornGainEnergy());
        addToBot((AbstractGameAction)new ChooseOneAttackAction(attackChoices,null,null));
        stopPulse();
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


