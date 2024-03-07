package BoardGame.characters;

import BoardGame.cards.BGRed.BGBash;
import BoardGame.cards.BGRed.BGDefend_Red;
import BoardGame.cards.BGRed.BGStrike_Red;
import BoardGame.relics.BGBurningBlood;
import BoardGame.relics.BGTheDieRelic;
import basemod.abstracts.CustomPlayer;
import basemod.animations.G3DJAnimation;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbInterface;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

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
    public boolean mayhemActive=false;
    public AbstractBGCharacter(String name, PlayerClass setClass, String[] orbTextures, String orbVfxPath, String model, String animation) {
        super(name, setClass, orbTextures,
                orbVfxPath, model,
                animation);
    }
    public AbstractBGCharacter(String name, AbstractPlayer.PlayerClass playerClass, EnergyOrbInterface energyOrbInterface, String model, String animation) {
        super(name, playerClass, energyOrbInterface, model, animation);
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
