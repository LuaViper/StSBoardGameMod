package BoardGame.relics;
import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.powers.*;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGNilrysCodex extends AbstractBGRelic implements DieControlledRelic, NilrysCodexCompatible {
    public BGNilrysCodex() {
        super("BGNilry's Codex", "codex.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.MAGICAL);
    }
    public int getPrice() {return 7;}


    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }
    public static final String ID = "BGNilry's Codex";

    public String getQuickSummary(){ if(TheDie.monsterRoll==2)return "Draw 1";
    else if(TheDie.monsterRoll==4)return "Copy a 2 relic";
    else return "";}
    public AbstractRelic makeCopy() {
        return new BGNilrysCodex();
    }


    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==2){
            activateDieAbility();
        }
        if(TheDie.finalRelicRoll==4){
            flash();
            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGTrigger2DieAbilityPower((AbstractCreature)AbstractDungeon.player)));
        }
    }

    public void activateDieAbility(){
        flash();
        addToTop((AbstractGameAction) new DrawCardAction(AbstractDungeon.player, 1) );
        stopPulse();
    }

    public void Trigger2Ability(){
        activateDieAbility();
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