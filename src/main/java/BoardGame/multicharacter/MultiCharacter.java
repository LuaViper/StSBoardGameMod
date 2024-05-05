package BoardGame.multicharacter;

import BoardGame.BoardGame;
import BoardGame.cards.BGRed.BGStrike_Red;
import BoardGame.characters.AbstractBGPlayer;
import BoardGame.characters.BGIronclad;
import BoardGame.multicharacter.patches.AbstractScenePatches;
import BoardGame.multicharacter.patches.ContextPatches;
import BoardGame.multicharacter.patches.HandLayoutHelper;
import BoardGame.relics.BGBurningBlood;
import BoardGame.relics.BGTheDieRelic;
import BoardGame.ui.OverlayMenuPatches;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO: oops we forgot Watcher's add-miracle-to-hand-at-start-of-combat relic

//TODO LATER: clear debuffs when combat ends so Weak doesn't affect card reward numbers displayed? not sure how this happened?

//REMINDER: players act from bottom lane to top lane, but monsters act from top lane to bottom lane

public class MultiCharacter extends AbstractBGPlayer {
//public class MultiCharacter extends CustomPlayer {
    public static final Logger logger = LogManager.getLogger(MultiCharacter.class.getName());

    public static class Enums {
        @SpireEnum
        public static AbstractPlayer.PlayerClass BG_MULTICHARACTER;

        @SpireEnum(name = "BG_BOARDGAME_COLOR")
        public static AbstractCard.CardColor CARD_COLOR;

        @SpireEnum(name = "BG_BOARDGAME_COLOR")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    public ArrayList<AbstractPlayer> subcharacters = new ArrayList<>();
    public static HandLayoutHelper handLayoutHelper = new HandLayoutHelper();

    public static ArrayList<AbstractPlayer> getSubcharacters() {
        if (ContextPatches.originalBGMultiCharacter == null){
            if(AbstractDungeon.player instanceof MultiCharacter){
                ContextPatches.originalBGMultiCharacter=AbstractDungeon.player;
            }
        }
        if (ContextPatches.originalBGMultiCharacter != null)
            return ((MultiCharacter) ContextPatches.originalBGMultiCharacter).subcharacters;
        if (AbstractDungeon.currMapNode != null) {
            if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                logger.warn("tried to BGMultiCharacter.getSubcharacters, but ContextPatches.originalBGMultiCharacter==null, time to panic!");
            }
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

    public MultiCharacter(String name, AbstractPlayer.PlayerClass setClass) {
        super(name, setClass, orbTextures, "BoardGameResources/images/char/defaultCharacter/orb/vfx.png", null, "");
        if(BoardGame.ENABLE_TEST_FEATURES)TEXT[0]=TEXT[3];
        this.blockTextColor = new Color(0.9F, 0.9F, 0.9F, 0.0F);
        this.blockScale = 1.0F;
        initializeClass((String) null, "images/characters/ironclad/shoulder2.png", "images/characters/ironclad/shoulder.png", "images/characters/ironclad/corpse.png",
                getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN));
        loadAnimation("images/characters/ironclad/idle/skeleton.atlas", "images/characters/ironclad/idle/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTimeScale(0.6F);
        this.dialogX = this.drawX + 0.0F * Settings.scale;
        this.dialogY = this.drawY + 220.0F * Settings.scale;
        //BaseMod.MAX_HAND_SIZE = 999; //TODO: move to a starting relic

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

        //TODO: "if(BoardGame.USE_BOARDGAME_RULES)" or inverse
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
        return (AbstractPlayer) new MultiCharacter(this.name, this.chosenClass);
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


    public void preBattlePrep() {
        super.preBattlePrep();
        if (handLayoutHelper.currentHand < 0) handLayoutHelper.changeHand(0);

        for (AbstractPlayer c : this.subcharacters) {
            ContextPatches.pushPlayerContext(c);
            c.preBattlePrep();
            ContextPatches.popPlayerContext();
        }
        AbstractScenePatches.AbstractSceneExtraInterface.gridBackground.get(AbstractDungeon.scene).resetGridAtStartOfCombat();
    }

    public void applyPreCombatLogic() {
        //don't super
        for (AbstractPlayer c : this.subcharacters) {
            ContextPatches.pushPlayerContext(c);
            c.applyPreCombatLogic();
            ContextPatches.popPlayerContext();
        }
    }


    public void applyStartOfCombatLogic() {
        super.applyStartOfCombatLogic();
        for (AbstractPlayer c : this.subcharacters) {
            ContextPatches.pushPlayerContext(c);
            c.applyStartOfCombatLogic();
            ContextPatches.popPlayerContext();
        }
    }

    public void applyStartOfCombatPreDrawLogic() {
        super.applyStartOfCombatPreDrawLogic();
        for (AbstractPlayer c : this.subcharacters) {
            ContextPatches.pushPlayerContext(c);
            c.applyStartOfCombatPreDrawLogic();
            ContextPatches.popPlayerContext();
        }
    }


    public void applyStartOfTurnRelics() {
        super.applyStartOfTurnRelics();
        this.shivsPlayedThisTurn = 0;
        this.stanceChangedThisTurn = false;
        for (AbstractPlayer c : this.subcharacters) {
            ContextPatches.pushPlayerContext(c);
            c.applyStartOfTurnRelics();
            ContextPatches.popPlayerContext();
        }
    }


    public void updateInput(){
        super.updateInput();
        for (AbstractPlayer c : this.subcharacters) {
            if(MultiCreature.Field.currentRow.get(c)==handLayoutHelper.currentHand){
                c.updateInput();
            }else{

            }
        }
    }



    public void combatUpdate() {
        //super.combatUpdate();
        for (AbstractPlayer c : this.subcharacters) {
            ContextPatches.pushPlayerContext(c);
            c.combatUpdate();
            ContextPatches.popPlayerContext();
        }
    }

    public void update() {
        //super.update();
        handLayoutHelper.update();
        for (AbstractPlayer c : this.subcharacters) {
            ContextPatches.pushPlayerContext(c);
            c.update();
            ContextPatches.popPlayerContext();
        }
    }

    public void showHealthBar(){
        //super.showHealthBar();
        for (AbstractPlayer c : this.subcharacters) {
            ContextPatches.pushPlayerContext(c);
            c.showHealthBar();
            ContextPatches.popPlayerContext();
        }
    }
    public void hideHealthBar(){
        //super.hideHealthBar();
        for (AbstractPlayer c : this.subcharacters) {
            ContextPatches.pushPlayerContext(c);
            c.hideHealthBar();
            ContextPatches.popPlayerContext();
        }
    }

    public void render(SpriteBatch sb) {
        //super.render(sb);
        for (int i = subcharacters.size() - 1; i >= 0; i -= 1) {
            ContextPatches.pushPlayerContext(subcharacters.get(i));
            subcharacters.get(i).render(sb);
            ContextPatches.popPlayerContext();
        }
    }


    public void renderHand(SpriteBatch sb) {
        if (handLayoutHelper.currentHand >= 0) {
            for (int i = handLayoutHelper.currentHand + subcharacters.size() - 1; i >= handLayoutHelper.currentHand; i -= 1) {
                //BoardGame.logger.info("???   " + i + "   " + i % subcharacters.size());
                AbstractPlayer c = subcharacters.get(i % subcharacters.size());
                ContextPatches.pushPlayerContext(c);
                c.renderHand(sb);
                ContextPatches.popPlayerContext();
            }
        }
    }

    @Override
    public void updateOrb(int orbCount){
        for(AbstractPlayer c : MultiCharacter.getSubcharacters()){
            c.updateOrb(orbCount);
        }
    }
    @Override
    public void renderOrb(SpriteBatch sb, boolean enabled, float current_x, float current_y) {
        //do nothing
    }

    @Override
    public void loseBlock(){
        for(AbstractPlayer c : MultiCharacter.getSubcharacters()) {
            ContextPatches.pushPlayerContext(c);
            if (!AbstractDungeon.player.hasPower("Barricade") && !AbstractDungeon.player.hasPower("Blur"))
                if (!AbstractDungeon.player.hasRelic("Calipers")) {
                    AbstractDungeon.player.loseBlock();
                } else {
                    AbstractDungeon.player.loseBlock(15);
                }
            ContextPatches.popPlayerContext();
        }
    }


    @SpirePatch2(clz = AbstractPlayer.class, method = "renderCardHotKeyText", paramtypez = {SpriteBatch.class})
    public static class RenderCardHotKeyTextPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractPlayer __instance) {
            if (CardCrawlGame.chosenCharacter == Enums.BG_MULTICHARACTER) {
                if (MultiCharacter.getSubcharacters().size() != 1) {
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }



    public void onVictory() {
        //don't super
        for (AbstractPlayer c : this.subcharacters) {
            ContextPatches.pushPlayerContext(c);
            c.onVictory();
            ContextPatches.popPlayerContext();
        }
    }




}
