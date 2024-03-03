package BoardGame.characters;

//TODO: does Recycle interact correctly with card cost changes?

import BoardGame.BoardGame;
import BoardGame.cards.BGBlue.BGDefend_Blue;
import BoardGame.cards.BGBlue.BGDualcast;
import BoardGame.cards.BGBlue.BGStrike_Blue;
import BoardGame.cards.BGBlue.BGZap;
import BoardGame.cards.BGGreen.BGDefend_Green;
import BoardGame.cards.BGGreen.BGNeutralize;
import BoardGame.cards.BGGreen.BGStrike_Green;
import BoardGame.cards.BGGreen.BGSurvivor;
import BoardGame.relics.*;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static BoardGame.BoardGame.*;

//Wiki-page https://github.com/daviscook477/BaseMod/wiki/Custom-Characters
//and https://github.com/daviscook477/BaseMod/wiki/Migrating-to-5.0
//All text (starting description and loadout, anything labeled TEXT[]) can be found in DefaultMod-character-Strings.json in the resources

public class BGDefect extends AbstractBGCharacter {
    public static final Logger logger = LogManager.getLogger(BoardGame.class.getName());

    // =============== CHARACTER ENUMERATORS =================

    public static class Enums {
        @SpireEnum
        public static PlayerClass BG_DEFECT;
        @SpireEnum(name = "BG_DEFECT_BLUE_COLOR") // These two HAVE to have the same absolutely identical name.
        public static AbstractCard.CardColor BG_BLUE;
        @SpireEnum(name = "BG_DEFECT_BLUE_COLOR")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    // =============== CHARACTER ENUMERATORS  =================


    // =============== BASE STATS =================

    public static final int ENERGY_PER_TURN = 3;
    public static final int STARTING_HP = 9;
    public static final int MAX_HP = 9;
    public static final int STARTING_GOLD = 5;
    public static final int CARD_DRAW = 5;
    public static final int ORB_SLOTS = 3;

    // =============== /BASE STATS/ =================


    // =============== STRINGS =================

    private static final String ID = makeID("BGDefect");
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(ID);
    private static final String[] NAMES = characterStrings.NAMES;
    private static final String[] TEXT = characterStrings.TEXT;

    // =============== /STRINGS/ =================


    // =============== TEXTURES OF BIG ENERGY ORB ===============

    public static final String[] orbTextures = {
            "BoardGameResources/images/char/theDefect/orb/layer1.png",
            "BoardGameResources/images/char/theDefect/orb/layer2.png",
            "BoardGameResources/images/char/theDefect/orb/layer3.png",
            "BoardGameResources/images/char/theDefect/orb/layer4.png",
            "BoardGameResources/images/char/theDefect/orb/layer5.png",
            "BoardGameResources/images/char/theDefect/orb/layer6.png",
            "BoardGameResources/images/char/theDefect/orb/layer1d.png",
            "BoardGameResources/images/char/theDefect/orb/layer2d.png",
            "BoardGameResources/images/char/theDefect/orb/layer3d.png",
            "BoardGameResources/images/char/theDefect/orb/layer4d.png",
            "BoardGameResources/images/char/theDefect/orb/layer5d.png",};

    // =============== /TEXTURES OF BIG ENERGY ORB/ ===============

    // =============== CHARACTER CLASS START =================

    public BGDefect(String name, PlayerClass setClass) {
        super(name, setClass, orbTextures,
                "BoardGameResources/images/char/theDefect/orb/vfx.png", null,
                "");



        // =============== TEXTURES, ENERGY, LOADOUT =================

//        initializeClass(null, // required call to load textures and setup energy/loadout.
//                // I left these in DefaultMod.java (Ctrl+click them to see where they are, Ctrl+hover to see what they read.)
//                THE_DEFAULT_SHOULDER_2, // campfire pose
//                THE_DEFAULT_SHOULDER_1, // another campfire pose
//                THE_DEFAULT_CORPSE, // dead corpse
        initializeClass((String)null, "images/characters/defect/shoulder2.png", "images/characters/defect/shoulder.png", "images/characters/defect/corpse.png",
                getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN)); // energy manager

        // =============== /TEXTURES, ENERGY, LOADOUT/ =================


        // =============== ANIMATIONS =================

        loadAnimation(
                BGDEFECT_SKELETON_ATLAS,
                BGDEFECT_SKELETON_JSON,
                1.0f);
        //loadAnimation("images/characters/ironclad/idle/skeleton.atlas", "images/characters/ironclad/idle/skeleton.json", 1.0F);
        // AnimationState.TrackEntry e = state.setAnimation(0, "animation", true);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTimeScale(0.6F);
        //e.setTime(e.getEndTime() * MathUtils.random());

        // =============== /ANIMATIONS/ =================


        // =============== TEXT BUBBLE LOCATION =================

        dialogX = (drawX + 0.0F * Settings.scale); // set location for text bubbles
        dialogY = (drawY + 220.0F * Settings.scale); // you can just copy these values

        // =============== /TEXT BUBBLE LOCATION/ =================


        BaseMod.MAX_HAND_SIZE=999;
    }

    // =============== /CHARACTER CLASS END/ =================

    // Starting description and loadout
    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(NAMES[0], TEXT[0],
                STARTING_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this, getStartingRelics(),
                getStartingDeck(), false);
    }

    // Starting Deck
    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();

        logger.info("Begin loading starter Deck Strings");

        retVal.add(BGStrike_Blue.ID);
        retVal.add(BGStrike_Blue.ID);
        retVal.add(BGStrike_Blue.ID);
        retVal.add(BGStrike_Blue.ID);
        retVal.add(BGDefend_Blue.ID);
        retVal.add(BGDefend_Blue.ID);
        retVal.add(BGDefend_Blue.ID);
        retVal.add(BGDefend_Blue.ID);
        retVal.add(BGZap.ID);
        retVal.add(BGDualcast.ID);

        return retVal;
    }

    // Starting Relics
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();

        logger.info("getStartingRelics: "+BGTheDieRelic.ID+" + BGCrackedCore");
        retVal.add(BGTheDieRelic.ID);
        retVal.add(BGCrackedCore.ID);

        // Mark relics as seen - makes it visible in the compendium immediately
        // If you don't have this it won't be visible in the compendium until you see them in game
        UnlockTracker.markRelicAsSeen(BGTheDieRelic.ID);
        UnlockTracker.markRelicAsSeen(BGCrackedCore.ID);

        return retVal;
    }

    // character Select screen effect
    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA("ATTACK_MAGIC_BEAM_SHORT", MathUtils.random(-0.2F, 0.2F));
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);

    }

    // character Select on-button-press sound effect
    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_MAGIC_BEAM_SHORT";
    }

    // Should return how much HP your maximum HP reduces by when starting a run at
    // Ascension 14 or higher. (ironclad loses 5, defect and silent lose 4 hp respectively)
    @Override
    public int getAscensionMaxHPLoss() {
        return 1;
    }

    // Should return the card color enum to be associated with your character.
    @Override
    public AbstractCard.CardColor getCardColor() {
        return Enums.BG_BLUE;
    }

    // Should return a color object to be used to color the trail of moving cards
    @Override
    public Color getCardTrailColor() {
        return Color.SKY.cpy();
    }

    // Should return a BitmapFont object that you can use to customize how your
    // energy is displayed from within the energy orb.
    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    // Should return class name as it appears in run history screen.
    @Override
    public String getLocalizedCharacterName() {
        return NAMES[0];
    }

    //Which card should be obtainable from the Match and Keep event?
    @Override
    public AbstractCard getStartCardForEvent() {
        return new BGStrike_Blue();
    }

    // The class name as it appears next to your player name in-game
    @Override
    public String getTitle(PlayerClass playerClass) {
        return NAMES[1];
    }

    // Should return a new instance of your character, sending name as its name parameter.
    @Override
    public AbstractPlayer newInstance() {
        return new BGDefect(name, chosenClass);
    }

    // Should return a Color object to be used to color the miniature card images in run history.
    @Override
    public Color getCardRenderColor() {
        return Color.SKY;
    }

    // Should return a Color object to be used as screen tint effect when your
    // character attacks the heart.

    public Color getSlashAttackColor() {
        return Color.SKY;
    }

    // Should return an AttackEffect array of any size greater than 0. These effects
    // will be played in sequence as your character's finishing combo on the heart.
    // Attack effects are the same as used in DamageAction and the like.
    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
                AbstractGameAction.AttackEffect.SLASH_HEAVY,
                AbstractGameAction.AttackEffect.FIRE,
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL,
                AbstractGameAction.AttackEffect.SLASH_HEAVY,
                AbstractGameAction.AttackEffect.FIRE,
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL
        };
    }

    // Should return a string containing what text is shown when your character is
    // about to attack the heart. For example, the defect is "NL You charge your
    // core to its maximum..."
    @Override
    public String getSpireHeartText() {
        return TEXT[1];
    }

    // The vampire events refer to the base game characters as "brother", "sister",
    // and "broken one" respectively.This method should return a String containing
    // the full text that will be displayed as the first screen of the vampire event.
    @Override
    public String getVampireText() {
        return TEXT[2];
    }


    protected Color blockTextColor = new Color(0.9F, 0.9F, 0.9F, 0.0F);
    protected float blockScale = 1.0F;


    public Texture getCutsceneBg() {
        return ImageMaster.loadImage("images/scenes/blueBg.jpg");
    }
    public List<CutscenePanel> getCutscenePanels() {
        List<CutscenePanel> panels = new ArrayList();
        panels.add(new CutscenePanel("images/scenes/defect1.png", "ATTACK_MAGIC_BEAM_SHORT"));
        panels.add(new CutscenePanel("images/scenes/defect2.png"));
        panels.add(new CutscenePanel("images/scenes/defect3.png"));
        return panels;
    }


    public void applyStartOfTurnRelics() {
        super.applyStartOfTurnRelics();
        this.shivsPlayedThisTurn=0;
    }

    //TODO: move addBlock to CustomBoardGameCreature class (which itself will require various sweeping changes to implement)
    public void addBlock(int blockAmount) {
        float tmp = blockAmount;

        if (this.isPlayer) {
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                tmp = r.onPlayerGainedBlock(tmp);
            }
            if (tmp > 0.0F) {
                for (AbstractPower p : this.powers) {
                    p.onGainedBlock(tmp);
                }
            }
        }
        boolean effect = false;
        if (this.currentBlock == 0) {
            effect = true;
        }
        for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            for (AbstractPower p : m.powers) {
                tmp = p.onPlayerGainedBlock(tmp);
            }
        }
        this.currentBlock += MathUtils.floor(tmp);


//        if (this.currentBlock >= 99 && this.isPlayer) {
//            UnlockTracker.unlockAchievement("IMPERVIOUS");
//        }


        if (this.currentBlock > 20) {
            this.currentBlock = 20;
        }


        if (this.currentBlock == 20 && this.isPlayer) {
            //UnlockTracker.unlockAchievement("BG_BARRICADED");
        }

        if (effect && this.currentBlock > 0) {
            gainBlockAnimation();
        } else if (blockAmount > 0 &&
                blockAmount > 0) {
            Color tmpCol = Settings.GOLD_COLOR.cpy();
            tmpCol.a = this.blockTextColor.a;
            this.blockTextColor = tmpCol;
            this.blockScale = 5.0F;
        }
    }







}
