package CoopBoardGame.characters;

import CoopBoardGame.cards.BGGreen.BGDoppelganger;
import CoopBoardGame.powers.BGSurroundedPower;
import CoopBoardGame.powers.ManualStartTurnPhasePower;
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

public abstract class AbstractBGPlayer extends CustomPlayer {

    //TODO: maybe move PlayedThisTurn to TheDie relic
    public int shivsPlayedThisTurn = 0;
    public boolean startTurnPhaseIsActive = true;
    public boolean stanceChangedThisTurn = false;

    public void applyStartOfTurnRelics() {
        super.applyStartOfTurnRelics();
        startTurnPhaseIsActive = true;
        shivsPlayedThisTurn = 0;
        BGDoppelganger.cardsPlayedThisTurn.clear();
        stanceChangedThisTurn = false;
    }

    public static void checkEndPlayerStartTurnPhase() {
        if (!(AbstractDungeon.player instanceof AbstractBGPlayer)) return;
        if (!((AbstractBGPlayer) AbstractDungeon.player).startTurnPhaseIsActive) return;
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof ManualStartTurnPhasePower) {
                return;
            }
        }
        endPlayerStartTurnPhase();
    }

    public static void endPlayerStartTurnPhase() {
        ((AbstractBGPlayer) AbstractDungeon.player).startTurnPhaseIsActive = false;
        AbstractPower p = AbstractDungeon.player.getPower(BGSurroundedPower.POWER_ID);
        if (p != null) ((BGSurroundedPower) p).onEndPlayerStartTurnPhase();
    }

    public String getMultiSwapButtonUrl() {
        return "";
    }

    public AbstractBGPlayer(
        String name,
        PlayerClass setClass,
        String[] orbTextures,
        String orbVfxPath,
        String model,
        String animation
    ) {
        super(name, setClass, orbTextures, orbVfxPath, model, animation);
        //AbstractPlayer expects potion slots to decrease at A11; override that here
        //TODO: code reuse; move to function
        if (AbstractDungeon.ascensionLevel >= 4) this.potionSlots--;
        if (AbstractDungeon.ascensionLevel >= 11) this.potionSlots++;
        this.potions.clear();
        int i;
        for (i = 0; i < this.potionSlots; i++) this.potions.add(new PotionSlot(i));
    }

    public AbstractBGPlayer(
        String name,
        AbstractPlayer.PlayerClass playerClass,
        EnergyOrbInterface energyOrbInterface,
        String model,
        String animation
    ) {
        super(name, playerClass, energyOrbInterface, model, animation);
        //AbstractPlayer expects potion slots to decrease at A11; override that here
        //TODO: code reuse; move to function
        if (AbstractDungeon.ascensionLevel >= 4) this.potionSlots--;
        if (AbstractDungeon.ascensionLevel >= 11) this.potionSlots++;
        this.potions.clear();
        int i;
        for (i = 0; i < this.potionSlots; i++) this.potions.add(new PotionSlot(i));
    }

    public void nonInputReleaseCard() {
        for (AbstractOrb orb : this.orbs) {
            orb.hideEvokeValues();
        }

        ReflectionHacks.setPrivate(this, AbstractPlayer.class, "passedHesitationLine", false);

        this.inSingleTargetMode = false;
        this.isInKeyboardMode = false;
        GameCursor.hidden = false;

        ReflectionHacks.setPrivate(this, AbstractPlayer.class, "isUsingClickDragControl", false);
        this.isHoveringDropZone = false;
        this.isDraggingCard = false;
        ReflectionHacks.setPrivate(this, AbstractPlayer.class, "isHoveringCard", false);
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
        ReflectionHacks.setPrivate(this, AbstractPlayer.class, "touchscreenInspectCount", 0);
    }
}
