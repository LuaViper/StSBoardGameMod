package BoardGame.relics;

import BoardGame.actions.BGUseMiracleAction;
import BoardGame.characters.BGWatcher;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import static BoardGame.BoardGame.makeRelicOutlinePath;
import static BoardGame.BoardGame.makeRelicPath;

public class BGMiracles extends AbstractBGRelic implements ClickableRelic {
    public static final String ID = "BoardGame:BGMiracles";
    private static final String IMGPATH="BGMiracles.png";
    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath(IMGPATH));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath(IMGPATH));

    public BGMiracles() {
        super(ID, "null image (will be fixed in relic constructor)", RelicTier.STARTER, LandingSound.MAGICAL);
        this.img = IMG;
        this.outlineImg=OUTLINE;
        setCounter(0);
    }

    public void setCounter(int setCounter) {
        this.counter = setCounter;
    }


    public void updateDescription(int accuracy) {
        this.description = getUpdatedDescription();
        //BoardGame.logger.info("updateDescription: "+this.description);
        this.tips.clear();this.tips.add(new PowerTip(this.name, this.description));initializeTips();
    }

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void atBattleStart() {
        if(AbstractDungeon.player instanceof BGWatcher) {
            //Prismatic Shard relic does not gain miracle at start of combat
            this.flash();
            this.counter = 1;
        }
    }





    public AbstractRelic makeCopy() {
        return new BGMiracles();
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
        if(this.counter>0) {
            addToBot((AbstractGameAction) new BGUseMiracleAction());
        }
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
        setCounter(0);
    }


    @SpirePatch2(clz = EnergyManager.class, method = "use",
            paramtypez = {int.class})
    public static class MiraclePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(int ___e) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)) {
                return SpireReturn.Continue();
            }

            int actualEnergy=___e;
            int miracleEnergy=0;
            if(___e > EnergyPanel.totalCount){
                actualEnergy=EnergyPanel.totalCount;
                miracleEnergy=___e-actualEnergy;
            }
            AbstractRelic relic= AbstractDungeon.player.getRelic("BoardGame:BGMiracles");
            if(relic!=null){
                relic.counter-=miracleEnergy;
                if(relic.counter<0)relic.counter=0; //this shouldn't happen...
            }
            EnergyPanel.useEnergy(actualEnergy);

            return SpireReturn.Return();
        }
    }



}