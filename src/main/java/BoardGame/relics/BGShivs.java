package BoardGame.relics;

import BoardGame.BoardGame;
import BoardGame.actions.BGActivateDieAbilityAction;
import BoardGame.powers.BGMayhemPower;
import BoardGame.powers.BGTriggerAnyDieAbilityPower;
import BoardGame.thedie.TheDie;
import BoardGame.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static BoardGame.BoardGame.makeRelicOutlinePath;
import static BoardGame.BoardGame.makeRelicPath;

public class BGShivs extends CustomRelic {
    public static final String ID = BoardGame.makeID("BGShivs");
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("BGshivs.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("BGshivs.png"));

    public BGShivs() {

        super(ID, IMG, OUTLINE, RelicTier.STARTER, LandingSound.CLINK);
        setCounter(0);
    }

    public void setCounter(int setCounter) {
        this.counter = setCounter;
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


//    public void atBattleStart() {
//        //flash();
//        TheDie.roll();
//        addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new BGTheDiePower((AbstractCreature)AbstractDungeon.player, 1), TheDie.initialRoll));
//    }





    public AbstractRelic makeCopy() {
        return new BGShivs();
    }

    private boolean isPlayerTurn = false; // We should make sure the relic is only activateable during our turn, not the enemies'.

//    @Override
//    public void onRightClick() {// On right click
//        if (!isObtained || !isPlayerTurn ) {
//            // If it has been used this turn, the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
//            return; // Don't do anything.
//        }
//        //final Logger logger = LogManager.getLogger(BoardGame.class.getName());
//        //logger.info("BGTheDieRelic.onRightClick");
//        addToBot((AbstractGameAction)new BGActivateDieAbilityAction(this));
//
//    }
//
//    public void atTurnStart() {
//        isPlayerTurn = true; // It's our turn!
//    }
//
//    @Override
//    public void onPlayerEndTurn() {
//        TheDie.forceLockInRoll=true;
//        isPlayerTurn = false; // Not our turn now.
//        stopPulse();
//    }
//
//
//    @Override
//    public void onVictory() {
//        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
//    }

}