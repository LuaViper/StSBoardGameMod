package BoardGame.relics;
import BoardGame.BoardGame;
import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.characters.AbstractBGCharacter;
import BoardGame.monsters.DieControlledMoves;
import BoardGame.powers.BGMayhemPower;
import BoardGame.powers.BGTheDiePower;
import BoardGame.powers.BGTriggerAnyDieAbilityPower;
import BoardGame.thedie.TheDie;
import BoardGame.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static BoardGame.BoardGame.makeRelicOutlinePath;
import static BoardGame.BoardGame.makeRelicPath;

//TODO: if we roll a 6, don't allow Incense Burner to be triggered a second time
public class BGTheDieRelic extends CustomRelic implements DieControlledRelic {
    public static final String ID = BoardGame.makeID("BGTheDieRelic");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("BGloadedDie.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("BGloadedDie.png"));

    public boolean tookDamageThisTurn=false;

    final Logger logger = LogManager.getLogger(BGTheDieRelic.class.getName());
    public String getQuickSummary(){if(TheDie.monsterRoll==4 || TheDie.monsterRoll==5)return "1 #yBlock";
        else if(TheDie.monsterRoll==6)return "Copy any die relic";
        else return "";}

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
        this.isObtained=true;
        TheDie.forceLockInRoll=false;
        //logger.info("fLIR false");
        TheDie.roll();
        this.description = getUpdatedDescription();
    }

    public void onUseCard(AbstractCard card, UseCardAction action) {
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            //mayhem fix
            //TODO: mayhem fix is still wrong -- player should have the chance to lock the roll + activate relics before playing mayhem (some cards change depending on roll)
            if(!card.isInAutoplay){
                TheDie.forceLockInRoll = true;
                lockRollAndActivateDieRelics();
            }
        }
    }

    public void onUsePotion(){
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            TheDie.forceLockInRoll = true;
            lockRollAndActivateDieRelics();
        }
    }

    public void onUsePower(){
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            TheDie.forceLockInRoll = true;
            lockRollAndActivateDieRelics();
        }
    }



    public void lockRollAndActivateDieRelics(){
        //TODO: all "start of turn" powers and relics are supposed to activate AFTER the roll is locked in
        if(TheDie.finalRelicRoll<=0) {
            TheDie.finalRelicRoll = TheDie.monsterRoll;
            for (AbstractRelic relic : AbstractDungeon.player.relics) {
                if (relic instanceof DieControlledRelic) {
                    ((DieControlledRelic) relic).checkDieAbility();
                }
            }
            description = getUpdatedDescription();

//            AbstractPower p = AbstractDungeon.player.getPower("BGMayhemPower"); //TODO: maybe implement diecontrolled
//            logger.info("Mayhem check: "+p);
//            if(p!=null){
//                ((BGMayhemPower)p).onRollLockedIn();
//            }
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
            // If it has been used this turn, or the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
            return; // Don't do anything.
        }
        //final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGTheDieRelic.onRightClick");
        addToBot((AbstractGameAction)new BGActivateDieAbilityAction(this));

    }

    public void atTurnStart() {
        isPlayerTurn = true; // It's our turn!
        tookDamageThisTurn=false;
    }

    @Override
    public void onPlayerEndTurn() {
        TheDie.forceLockInRoll=true;
        lockRollAndActivateDieRelics();
        isPlayerTurn = false; // Not our turn now.
        stopPulse();
    }


    @Override
    public void onVictory() {
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
    }

}


