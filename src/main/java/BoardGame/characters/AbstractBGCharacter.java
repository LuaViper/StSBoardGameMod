package BoardGame.characters;

import BoardGame.cards.BGRed.BGBash;
import BoardGame.cards.BGRed.BGDefend_Red;
import BoardGame.cards.BGRed.BGStrike_Red;
import BoardGame.relics.BGBurningBlood;
import BoardGame.relics.BGTheDieRelic;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomPlayer;
import basemod.animations.G3DJAnimation;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbInterface;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;
import java.util.Iterator;

import static BoardGame.BoardGame.CHAR_SELECT_BUTTON_IRONCLAD;

public abstract class AbstractBGCharacter extends CustomPlayer {

//    public static final String[] orbTextures = {
//            "BoardGameResources/images/char/defaultCharacter/orb/layer1.png",
//            "BoardGameResources/images/char/defaultCharacter/orb/layer2.png",
//            "BoardGameResources/images/char/defaultCharacter/orb/layer3.png",
//            "BoardGameResources/images/char/defaultCharacter/orb/layer4.png",
//            "BoardGameResources/images/char/defaultCharacter/orb/layer5.png",
//            "BoardGameResources/images/char/defaultCharacter/orb/layer6.png",
//            "BoardGameResources/images/char/defaultCharacter/orb/layer1d.png",
//            "BoardGameResources/images/char/defaultCharacter/orb/layer2d.png",
//            "BoardGameResources/images/char/defaultCharacter/orb/layer3d.png",
//            "BoardGameResources/images/char/defaultCharacter/orb/layer4d.png",
//            "BoardGameResources/images/char/defaultCharacter/orb/layer5d.png",};

    //TODO: maybe move PlayedThisTurn to TheDie relic
    public int shivsPlayedThisTurn=0;
    public boolean stanceChangedThisTurn=false;
    public int currentRow=0;
    public int savedCurrentEnergy=0;


    public String getMultiSwapButtonUrl(){return "";}
    public AbstractBGCharacter(String name, PlayerClass setClass, String[] orbTextures, String orbVfxPath, String model, String animation) {
        super(name, setClass, orbTextures,
                orbVfxPath, model,
                animation);
    }
    public AbstractBGCharacter(String name, AbstractPlayer.PlayerClass playerClass, EnergyOrbInterface energyOrbInterface, String model, String animation) {
        super(name, playerClass, energyOrbInterface, model, animation);
    }

    public void nonInputReleaseCard() {
        Iterator var1 = this.orbs.iterator();

        while (var1.hasNext()) {
            AbstractOrb o = (AbstractOrb) var1.next();
            o.hideEvokeValues();
        }

        ReflectionHacks.setPrivate(this,AbstractPlayer.class,"passedHesitationLine",false);

        this.inSingleTargetMode = false;
        if (!this.isInKeyboardMode) {
            GameCursor.hidden = false;
        }

        ReflectionHacks.setPrivate(this,AbstractPlayer.class,"isUsingClickDragControl" , false);
        this.isHoveringDropZone = false;
        this.isDraggingCard = false;
        ReflectionHacks.setPrivate(this,AbstractPlayer.class,"isHoveringCard" , false);
        if (this.hoveredCard != null) {
            if (this.hoveredCard.canUse(this, (AbstractMonster) null)) {
                this.hoveredCard.beginGlowing();
            }
            this.hoveredCard.untip();
            this.hoveredCard.hoverTimer = 0.25F;
            this.hoveredCard.unhover();
        }

        this.hoveredCard = null;
        this.hand.refreshHandLayout();
        ReflectionHacks.setPrivate(this,AbstractPlayer.class,"touchscreenInspectCount" , 0);

    }

//
//    @Override
//    public ArrayList<String> getStartingDeck() {
//        ArrayList<String> retVal = new ArrayList<>();
//
//        retVal.add(BGStrike_Red.ID);
//        retVal.add(BGStrike_Red.ID);
//        retVal.add(BGStrike_Red.ID);
//        retVal.add(BGStrike_Red.ID);
//        retVal.add(BGStrike_Red.ID);
//        retVal.add(BGDefend_Red.ID);
//        retVal.add(BGDefend_Red.ID);
//        retVal.add(BGDefend_Red.ID);
//        retVal.add(BGDefend_Red.ID);
//        retVal.add(BGBash.ID);
//
//        return retVal;
//    }
//
//
//    public ArrayList<String> getStartingRelics() {
//        ArrayList<String> retVal = new ArrayList<>();
//
//        retVal.add(BGTheDieRelic.ID);
//        UnlockTracker.markRelicAsSeen(BGTheDieRelic.ID);
//
//        return retVal;
//    }
}
