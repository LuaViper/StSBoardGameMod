package BoardGame.relics;

import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.actions.BGChooseOneAttackAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.cards.BGColorless.BGDuality2Block;
import BoardGame.cards.BGColorless.BGDuality2Damage;
import BoardGame.powers.NilrysCodexCompatible;
import BoardGame.screen.TargetSelectScreen;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

//TODO: get a second or third opinion on whether we named the Yin/Yang option cards appropriately

public class BGDuality extends AbstractBGRelic implements DieControlledRelic, NilrysCodexCompatible {
    public BGDuality() {
        super("BGDuality", "duality.png", RelicTier.COMMON, LandingSound.MAGICAL);
    }
    public int getPrice() {return 8;}


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }
    public static final String ID = "BGDuality";

    public String getQuickSummary(){ if(TheDie.monsterRoll==2)return "2 Block";
    else if(TheDie.monsterRoll==4)return "2 Damage";
    else return "";}
    public AbstractRelic makeCopy() {
        return new BGDuality();
    }


    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==2){
            activateDieAbility2();
        }
        if(TheDie.finalRelicRoll==4){
            activateDieAbility4();
        }
    }

    public void activateDieAbility(){
        flash();
        addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        ArrayList<AbstractBGAttackCardChoice> attackChoices = new ArrayList<>();
        attackChoices.add(new BGDuality2Block());
        attackChoices.add(new BGDuality2Damage());
        addToBot((AbstractGameAction)new BGChooseOneAttackAction(attackChoices,null,null));
        stopPulse();
    }

    public void Trigger2Ability(){
        activateDieAbility2();
    }

    public void activateDieAbility2(){
        flash();
        addToTop((AbstractGameAction) new GainBlockAction(AbstractDungeon.player, 2) );
        stopPulse();
    }

    public void activateDieAbility4(){
        flash();
        TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
            if(target==null)return;
            addToBot((AbstractGameAction) new DamageAction(target,
                    new DamageInfo(AbstractDungeon.player,2, DamageInfo.DamageType.THORNS),
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        };
        addToBot((AbstractGameAction)new TargetSelectScreenAction(tssAction,"Choose a target for Duality (2 damage)."));
        stopPulse();
    }



    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

    public void onRightClick() {// On right click
        if (!isObtained || !isPlayerTurn ) {
            // If it has been used this turn, or the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
            return; // Don't do anything.
        }
        //final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGTheDieRelic.onRightClick");
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