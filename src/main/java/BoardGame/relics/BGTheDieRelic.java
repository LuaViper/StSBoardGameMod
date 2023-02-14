package BoardGame.relics;
import BoardGame.BoardGame;
import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.powers.BGTheDiePower;
import BoardGame.powers.BGTriggerAnyDieAbilityPower;
import BoardGame.thedie.TheDie;
import BoardGame.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static BoardGame.BoardGame.makeRelicOutlinePath;
import static BoardGame.BoardGame.makeRelicPath;

public class BGTheDieRelic extends CustomRelic implements DieControlledRelic {
    public static final String ID = BoardGame.makeID("BGTheDieRelic");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("BGloadedDie.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("BGloadedDie.png"));

    public BGTheDieRelic() {
        //super(ID, IMG, OUTLINE, RelicTier.SPECIAL, LandingSound.SOLID);
        super(ID, IMG, OUTLINE, RelicTier.STARTER, LandingSound.MAGICAL);

    }

    public String getUpdatedDescription() {
//        if(TheDie.monsterRoll>0){
//            return this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + TheDie.monsterRoll + this.DESCRIPTIONS[2];
//        }
        return this.DESCRIPTIONS[0];
    }


//    public void atBattleStart() {
//        //flash();
//        TheDie.roll();
//        addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGTheDiePower((AbstractCreature)AbstractDungeon.player, 1), TheDie.initialRoll));
//    }

    public void atTurnStartPostDraw() {
    //public void atTurnStart() {
        TheDie.roll();
        this.description = getUpdatedDescription();
        addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGTheDiePower((AbstractCreature)AbstractDungeon.player, TheDie.initialRoll), TheDie.initialRoll));
        //TODO: roll-changing abilities go here; delay lock roll until then
        lockRollAndActivateDieRelics();
        this.description = getUpdatedDescription();
        this.isObtained=true;
    }

    public void lockRollAndActivateDieRelics(){
        TheDie.finalRelicRoll=TheDie.monsterRoll;
        for(AbstractRelic relic : AbstractDungeon.player.relics){
            if(relic instanceof DieControlledRelic){
                ((DieControlledRelic) relic).checkDieAbility();
            }
        }


    }

    public void checkDieAbility(){
        if(TheDie.finalRelicRoll==4 || TheDie.finalRelicRoll==5){
            activateDieAbility();
        }
        if(TheDie.finalRelicRoll==6){
            flash();
            addToBot((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGTriggerAnyDieAbilityPower((AbstractCreature)AbstractDungeon.player)));
        }
    }

    public void activateDieAbility(){
        flash();
        addToTop((AbstractGameAction) new GainBlockAction(AbstractDungeon.player, 1) );
        stopPulse();
    }

    public AbstractRelic makeCopy() {
        return new BGTheDieRelic();
    }

    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

    @Override
    public void onRightClick() {// On right click
        if (!isObtained || !isPlayerTurn ) {
            // If it has been used this turn, the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
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


