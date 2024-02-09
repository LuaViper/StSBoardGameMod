package BoardGame.relics;

import BoardGame.BoardGame;
import BoardGame.actions.BGUseShivAction;
import BoardGame.util.TextureLoader;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static BoardGame.BoardGame.makeRelicOutlinePath;
import static BoardGame.BoardGame.makeRelicPath;

public class BGShivs extends CustomRelic implements ClickableRelic {
    public static final String ID = BoardGame.makeID("BGShivs");
    private static final String IMGPATH="BGshivs.png";
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath(IMGPATH));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath(IMGPATH));

    public BGShivs() {
        super(ID, IMG, AbstractRelic.RelicTier.STARTER, AbstractRelic.LandingSound.CLINK);
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
    public int shivsPlayedThisTurn=0;

    @Override
    public void onRightClick() {// On right click
        if (!isObtained || !isPlayerTurn ) {
            // If it has been used this turn, or the player doesn't actually have the relic (i.e. it's on display in the shop room), or it's the enemy's turn
            return; // Don't do anything.
        }
        //final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGTheDieRelic.onRightClick");
        if(this.counter>0) {
            addToBot((AbstractGameAction) new BGUseShivAction(true, false, 0)); //can't discard shiv, +0 damage
        }
    }

    public void atTurnStart() {
        isPlayerTurn = true; // It's our turn!
        shivsPlayedThisTurn=0;
    }

    @Override
    public void onPlayerEndTurn() {
        isPlayerTurn = false; // Not our turn now.
        stopPulse();
    }


    @Override
    public void onVictory() {
        stopPulse(); // Don't keep pulsing past the victory screen/outside of combat.
        setCounter(0);
    }

}