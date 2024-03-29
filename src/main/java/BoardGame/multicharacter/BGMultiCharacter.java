package BoardGame.multicharacter;

import BoardGame.BoardGame;
import BoardGame.cards.BGRed.BGStrike_Red;
import BoardGame.characters.AbstractBGCharacter;
import BoardGame.characters.BGIronclad;
import BoardGame.multicharacter.patches.ContextPatches;
import BoardGame.multicharacter.patches.HandLayoutHelper;
import BoardGame.relics.BGBurningBlood;
import BoardGame.relics.BGTheDieRelic;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.devcommands.hand.Hand;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//REMINDER: players act from bottom lane to top lane, but monsters act from top lane to bottom lane
//TODO: UX - display BGMultiCharacter's hand as a combination of all subcharacters' hands??

public class BGMultiCharacter extends AbstractBGCharacter {
    public static final Logger logger = LogManager.getLogger(BGMultiCharacter.class.getName());

    public static class Enums {
        @SpireEnum
        public static AbstractPlayer.PlayerClass BG_MULTICHARACTER;

        @SpireEnum(name = "BG_BOARDGAME_COLOR")
        public static AbstractCard.CardColor CARD_COLOR;

        @SpireEnum(name = "BG_BOARDGAME_COLOR")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    public ArrayList<AbstractBGCharacter> subcharacters = new ArrayList<>();
    public static HandLayoutHelper handLayoutHelper = new HandLayoutHelper();

    public static ArrayList<AbstractBGCharacter> getSubcharacters() {
        if (ContextPatches.originalBGMultiCharacter != null)
            return ((BGMultiCharacter) ContextPatches.originalBGMultiCharacter).subcharacters;
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            logger.warn("tried to BGMultiCharacter.getSubcharacters, but ContextPatches.originalBGMultiCharacter==null, time to panic!");
        }
        return new ArrayList<>();
    }

    public static final int ENERGY_PER_TURN = 0;

    public static final int STARTING_HP = 9;

    public static final int MAX_HP = 9;

    public static final int STARTING_GOLD = 3;

    public static final int CARD_DRAW = 0;

    public static final int ORB_SLOTS = 0;

    private static final String ID = BoardGame.makeID("BGMultiCharacter");

    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(ID);

    private static final String[] NAMES = characterStrings.NAMES;

    private static final String[] TEXT = characterStrings.TEXT;

    public static final String[] orbTextures = new String[]{
            "BoardGameResources/images/char/defaultCharacter/orb/layer1.png", "BoardGameResources/images/char/defaultCharacter/orb/layer2.png", "BoardGameResources/images/char/defaultCharacter/orb/layer3.png", "BoardGameResources/images/char/defaultCharacter/orb/layer4.png", "BoardGameResources/images/char/defaultCharacter/orb/layer5.png", "BoardGameResources/images/char/defaultCharacter/orb/layer6.png", "BoardGameResources/images/char/defaultCharacter/orb/layer1d.png", "BoardGameResources/images/char/defaultCharacter/orb/layer2d.png", "BoardGameResources/images/char/defaultCharacter/orb/layer3d.png", "BoardGameResources/images/char/defaultCharacter/orb/layer4d.png",
            "BoardGameResources/images/char/defaultCharacter/orb/layer5d.png"};

    protected Color blockTextColor;

    protected float blockScale;

    public BGMultiCharacter(String name, AbstractPlayer.PlayerClass setClass) {
        super(name, setClass, orbTextures, "BoardGameResources/images/char/defaultCharacter/orb/vfx.png", null, "");
        this.blockTextColor = new Color(0.9F, 0.9F, 0.9F, 0.0F);
        this.blockScale = 1.0F;
        initializeClass((String) null, "images/characters/ironclad/shoulder2.png", "images/characters/ironclad/shoulder.png", "images/characters/ironclad/corpse.png", getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(0));
        loadAnimation("images/characters/ironclad/idle/skeleton.atlas", "images/characters/ironclad/idle/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTimeScale(0.6F);
        this.dialogX = this.drawX + 0.0F * Settings.scale;
        this.dialogY = this.drawY + 220.0F * Settings.scale;
        BaseMod.MAX_HAND_SIZE = 999;
    }

    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(NAMES[0], TEXT[0], 9, 9, 0, 3, 0, (AbstractPlayer) this, getStartingRelics(), getStartingDeck(), false);
    }

    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        logger.info("Begin loading starter Deck Strings");
        retVal.add("BGStrike_R");
        retVal.add("BGStrike_R");
        retVal.add("BGStrike_R");
        retVal.add("BGStrike_R");
        retVal.add("BGStrike_R");
        retVal.add("BGDefend_R");
        retVal.add("BGDefend_R");
        retVal.add("BGDefend_R");
        retVal.add("BGDefend_R");
        retVal.add("BGBash");
        return retVal;
    }

    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(BGTheDieRelic.ID);
        retVal.add(BGBurningBlood.ID);
        retVal.add("BGRing of the Snake");
        retVal.add("BoardGame:BGShivs");
        retVal.add("BGCrackedCore");
        retVal.add("BoardGame:BGMiracles");
        UnlockTracker.markRelicAsSeen(BGTheDieRelic.ID);
        UnlockTracker.markRelicAsSeen(BGBurningBlood.ID);
        UnlockTracker.markRelicAsSeen("BGRing of the Snake");
        UnlockTracker.markRelicAsSeen("BoardGame:BGShivs");
        UnlockTracker.markRelicAsSeen("BGCrackedCore");
        UnlockTracker.markRelicAsSeen("BoardGame:BGMiracles");
        return retVal;
    }

    protected void initializeStarterRelics(AbstractPlayer.PlayerClass chosenClass) {
        ArrayList<String> relics = getStartingRelics();
        if (ModHelper.isModEnabled("Cursed Run")) {
            relics.clear();
            relics.add("Cursed Key");
            relics.add("Darkstone Periapt");
            relics.add("Du-Vu Doll");
        }
        if (ModHelper.isModEnabled("ControlledChaos"))
            relics.add("Frozen Eye");
        int index = 0;
        for (String s : relics) {
            if (s.equals(BGTheDieRelic.ID)) {
                RelicLibrary.getRelic(s).makeCopy().instantObtain((AbstractPlayer) this, index, false);
                index++;
            }
        }
        AbstractDungeon.relicsToRemoveOnStart.addAll(relics);
    }

    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA("BLUNT_HEAVY", MathUtils.random(-0.2F, 0.2F));
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, true);
    }

    public String getCustomModeCharacterButtonSoundKey() {
        return "BLUNT_FAST";
    }

    public int getAscensionMaxHPLoss() {
        return 1;
    }

    public AbstractCard.CardColor getCardColor() {
        return BGIronclad.Enums.BG_RED;
    }

    public Color getCardTrailColor() {
        return BoardGame.BG_IRONCLAD_RED;
    }

    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontRed;
    }

    public String getLocalizedCharacterName() {
        return NAMES[0];
    }

    public AbstractCard getStartCardForEvent() {
        return (AbstractCard) new BGStrike_Red();
    }

    public String getTitle(AbstractPlayer.PlayerClass playerClass) {
        return NAMES[1];
    }

    public AbstractPlayer newInstance() {
        return (AbstractPlayer) new BGMultiCharacter(this.name, this.chosenClass);
    }

    public Color getCardRenderColor() {
        return BoardGame.BG_IRONCLAD_RED;
    }

    public Color getSlashAttackColor() {
        return BoardGame.BG_IRONCLAD_RED;
    }

    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
                AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.POISON, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.POISON, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.SLASH_HEAVY,
                AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.BLUNT_LIGHT, AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.BLUNT_LIGHT, AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.BLUNT_HEAVY,
                AbstractGameAction.AttackEffect.BLUNT_LIGHT};
    }

    public String getSpireHeartText() {
        return TEXT[1];
    }

    public String getVampireText() {
        return TEXT[2];
    }

    public void applyStartOfTurnRelics() {
        super.applyStartOfTurnRelics();
        this.shivsPlayedThisTurn = 0;
        this.stanceChangedThisTurn = false;
    }


    public void preBattlePrep() {
        super.preBattlePrep();
        if (handLayoutHelper.currentHand < 0) handLayoutHelper.changeHand(0);
        for (AbstractPlayer c : this.subcharacters) {
            c.preBattlePrep();
        }
    }

    public void updateInput(){
        super.updateInput();
        for (AbstractPlayer c : this.subcharacters) {
            if(((AbstractBGCharacter)c).currentRow==handLayoutHelper.currentHand){
                c.updateInput();
            }else{
               // ((AbstractBGCharacter)c).nonInputReleaseCard();

//                ReflectionHacks.setPrivate(c,AbstractPlayer.class,"passedHesitationLine",false);
//                c.inSingleTargetMode=false;
//
//                boolean tmpJCL= InputHelper.justClickedLeft;
//                boolean tmpJCR= InputHelper.justClickedRight;
//                boolean tmpJRCR= InputHelper.justReleasedClickRight;
//                boolean tmpJRCL= InputHelper.justReleasedClickLeft;
//                boolean tmpIMD= InputHelper.isMouseDown;
//                int tmpMX=InputHelper.mX;
//                int tmpMY=InputHelper.mY;
//                InputHelper.justClickedLeft=false;
//                InputHelper.justClickedRight=false;
//                InputHelper.justReleasedClickLeft=true;
//                InputHelper.justReleasedClickRight=true;
//                InputHelper.isMouseDown=false;
//                InputHelper.mX=0;
//                InputHelper.mY=Settings.HEIGHT;
//                c.isDraggingCard=false;
//                c.updateInput();
//                InputHelper.justClickedLeft=tmpJCL;
//                InputHelper.justClickedRight=tmpJCR;
//                InputHelper.justReleasedClickLeft=tmpJRCL;
//                InputHelper.justReleasedClickRight=tmpJRCL;
//                InputHelper.isMouseDown=tmpIMD;
//                InputHelper.mX=tmpMX;
//                InputHelper.mY=tmpMY;


            }
        }
    }



    public void combatUpdate() {
        for (AbstractPlayer c : this.subcharacters) {
            c.combatUpdate();
        }
    }

    public void update() {
        handLayoutHelper.update();
        for (AbstractPlayer c : this.subcharacters) {
            c.update();
        }
    }

    public void render(SpriteBatch sb) {
        for (int i = subcharacters.size() - 1; i >= 0; i -= 1) {
            subcharacters.get(i).render(sb);
        }
    }

    public void renderHand(SpriteBatch sb) {
        if (handLayoutHelper.currentHand >= 0) {
            for (int i = handLayoutHelper.currentHand + subcharacters.size() - 1; i >= handLayoutHelper.currentHand; i -= 1) {
                //BoardGame.logger.info("???   " + i + "   " + i % subcharacters.size());
                AbstractPlayer c = subcharacters.get(i % subcharacters.size());
                c.renderHand(sb);
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "renderCardHotKeyText", paramtypez = {SpriteBatch.class})
    public static class RenderCardHotKeyTextPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractPlayer __instance) {
            if (CardCrawlGame.chosenCharacter == Enums.BG_MULTICHARACTER) {
                if (BGMultiCharacter.getSubcharacters().size() != 1) {
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }


}
