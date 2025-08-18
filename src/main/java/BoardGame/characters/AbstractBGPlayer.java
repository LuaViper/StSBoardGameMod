package BoardGame.characters;

import BoardGame.cards.BGGreen.BGDoppelganger;
import BoardGame.powers.BGSurroundedPower;
import BoardGame.powers.ManualStartTurnPhasePower;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbInterface;

import java.util.Iterator;

public abstract class AbstractBGPlayer extends CustomPlayer {

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
    public boolean startTurnPhaseIsActive=true;
    public boolean stanceChangedThisTurn=false;
    //public int currentRow=0;

    public void applyStartOfTurnRelics() {
        super.applyStartOfTurnRelics();
        startTurnPhaseIsActive=true;
        shivsPlayedThisTurn=0;
        BGDoppelganger.cardsPlayedThisTurn.clear();
        stanceChangedThisTurn=false;
    }

    public static void checkEndPlayerStartTurnPhase(){
        if(!(AbstractDungeon.player instanceof AbstractBGPlayer))return;
        if(!((AbstractBGPlayer)AbstractDungeon.player).startTurnPhaseIsActive) return;
        for(AbstractPower p : AbstractDungeon.player.powers){
            if(p instanceof ManualStartTurnPhasePower){
                return;
            }
        }
        endPlayerStartTurnPhase();
    }

    public static void endPlayerStartTurnPhase(){
        ((AbstractBGPlayer)AbstractDungeon.player).startTurnPhaseIsActive=false;
        AbstractPower p=AbstractDungeon.player.getPower(BGSurroundedPower.POWER_ID);
        if(p!=null)((BGSurroundedPower)p).onEndPlayerStartTurnPhase();
    }

    public String getMultiSwapButtonUrl(){return "";}
    public AbstractBGPlayer(String name, PlayerClass setClass, String[] orbTextures, String orbVfxPath, String model, String animation) {
        super(name, setClass, orbTextures,
                orbVfxPath, model,
                animation);

        //AbstractPlayer expects potion slots to decrease at A11; override that here
        //TODO: code reuse; move to function
        if (AbstractDungeon.ascensionLevel >= 4)this.potionSlots--;
        if (AbstractDungeon.ascensionLevel >= 11)this.potionSlots++;
        this.potions.clear();
        int i;
        for (i = 0; i < this.potionSlots; i++)
            this.potions.add(new PotionSlot(i));
    }
    public AbstractBGPlayer(String name, AbstractPlayer.PlayerClass playerClass, EnergyOrbInterface energyOrbInterface, String model, String animation) {
        super(name, playerClass, energyOrbInterface, model, animation);

        //AbstractPlayer expects potion slots to decrease at A11; override that here
        //TODO: code reuse; move to function
        if (AbstractDungeon.ascensionLevel >= 4)this.potionSlots--;
        if (AbstractDungeon.ascensionLevel >= 11)this.potionSlots++;
        this.potions.clear();
        int i;
        for (i = 0; i < this.potionSlots; i++)
            this.potions.add(new PotionSlot(i));
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
