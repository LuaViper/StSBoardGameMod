package BoardGame.multicharacter;

import BoardGame.BoardGame;
import BoardGame.cards.BGRed.BGStrike_Red;
import BoardGame.characters.AbstractBGCharacter;
import BoardGame.characters.BGIronclad;
import BoardGame.relics.BGBurningBlood;
import BoardGame.relics.BGTheDieRelic;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGMultiCharacter extends AbstractBGCharacter {
  public static final Logger logger = LogManager.getLogger(BoardGame.class.getName());
  
  public static class Enums {
    @SpireEnum
    public static AbstractPlayer.PlayerClass BG_MULTICHARACTER;
    
    @SpireEnum(name = "BG_BOARDGAME_COLOR")
    public static AbstractCard.CardColor CARD_COLOR;
    
    @SpireEnum(name = "BG_BOARDGAME_COLOR")
    public static CardLibrary.LibraryType LIBRARY_COLOR;
  }
  
  public ArrayList<AbstractBGCharacter> subcharacters = new ArrayList<>();
  
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
  
  public static final String[] orbTextures = new String[] { 
      "BoardGameResources/images/char/defaultCharacter/orb/layer1.png", "BoardGameResources/images/char/defaultCharacter/orb/layer2.png", "BoardGameResources/images/char/defaultCharacter/orb/layer3.png", "BoardGameResources/images/char/defaultCharacter/orb/layer4.png", "BoardGameResources/images/char/defaultCharacter/orb/layer5.png", "BoardGameResources/images/char/defaultCharacter/orb/layer6.png", "BoardGameResources/images/char/defaultCharacter/orb/layer1d.png", "BoardGameResources/images/char/defaultCharacter/orb/layer2d.png", "BoardGameResources/images/char/defaultCharacter/orb/layer3d.png", "BoardGameResources/images/char/defaultCharacter/orb/layer4d.png", 
      "BoardGameResources/images/char/defaultCharacter/orb/layer5d.png" };
  
  protected Color blockTextColor;
  
  protected float blockScale;
  
  public BGMultiCharacter(String name, AbstractPlayer.PlayerClass setClass) {
    super(name, setClass, orbTextures, "BoardGameResources/images/char/defaultCharacter/orb/vfx.png", null, "");
    this.blockTextColor = new Color(0.9F, 0.9F, 0.9F, 0.0F);
    this.blockScale = 1.0F;
    initializeClass((String)null, "images/characters/ironclad/shoulder2.png", "images/characters/ironclad/shoulder.png", "images/characters/ironclad/corpse.png", getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(0));
    loadAnimation("images/characters/ironclad/idle/skeleton.atlas", "images/characters/ironclad/idle/skeleton.json", 1.0F);
    AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
    e.setTimeScale(0.6F);
    this.dialogX = this.drawX + 0.0F * Settings.scale;
    this.dialogY = this.drawY + 220.0F * Settings.scale;
    BaseMod.MAX_HAND_SIZE = 999;
  }
  
  public CharSelectInfo getLoadout() {
    return new CharSelectInfo(NAMES[0], TEXT[0], 9, 9, 0, 3, 0, (AbstractPlayer)this, getStartingRelics(), getStartingDeck(), false);
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
        RelicLibrary.getRelic(s).makeCopy().instantObtain((AbstractPlayer)this, index, false);
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
    return (AbstractCard)new BGStrike_Red();
  }
  
  public String getTitle(AbstractPlayer.PlayerClass playerClass) {
    return NAMES[1];
  }
  
  public AbstractPlayer newInstance() {
    return (AbstractPlayer)new BGMultiCharacter(this.name, this.chosenClass);
  }
  
  public Color getCardRenderColor() {
    return BoardGame.BG_IRONCLAD_RED;
  }
  
  public Color getSlashAttackColor() {
    return BoardGame.BG_IRONCLAD_RED;
  }
  
  public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
    return new AbstractGameAction.AttackEffect[] { 
        AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.POISON, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.POISON, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.SLASH_HEAVY, 
        AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.BLUNT_LIGHT, AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.BLUNT_LIGHT, AbstractGameAction.AttackEffect.BLUNT_HEAVY, AbstractGameAction.AttackEffect.BLUNT_HEAVY, 
        AbstractGameAction.AttackEffect.BLUNT_LIGHT };
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
    for (int i = this.subcharacters.size() - 1; i >= 0; ) {
      ((AbstractBGCharacter)this.subcharacters.get(i)).preBattlePrep();
      i--;
    } 
  }
  
  public void render(SpriteBatch sb) {
    for (int i = this.subcharacters.size() - 1; i >= 0; ) {
      ((AbstractBGCharacter)this.subcharacters.get(i)).render(sb);
      i--;
    } 
  }
  
  public void renderHand(SpriteBatch sb) {
    for (int i = this.subcharacters.size() - 1; i >= 0; ) {
      ((AbstractBGCharacter)this.subcharacters.get(i)).renderHand(sb);
      i--;
    } 
  }
  
  public void addBlock(int blockAmount) {
    float tmp = blockAmount;
    if (this.isPlayer) {
      for (AbstractRelic r : AbstractDungeon.player.relics)
        tmp = r.onPlayerGainedBlock(tmp); 
      if (tmp > 0.0F)
        for (AbstractPower p : this.powers)
          p.onGainedBlock(tmp);  
    } 
    boolean effect = false;
    if (this.currentBlock == 0)
      effect = true; 
    for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
      for (AbstractPower p : m.powers)
        tmp = p.onPlayerGainedBlock(tmp); 
    } 
    this.currentBlock += MathUtils.floor(tmp);
    if (this.currentBlock > 20)
      this.currentBlock = 20; 
    if (this.currentBlock != 20 || this.isPlayer);
    if (effect && this.currentBlock > 0) {
      gainBlockAnimation();
    } else if (blockAmount > 0 && blockAmount > 0) {
      Color tmpCol = Settings.GOLD_COLOR.cpy();
      tmpCol.a = this.blockTextColor.a;
      this.blockTextColor = tmpCol;
      this.blockScale = 5.0F;
    } 
  }
}


/* Location:              C:\Spire dev\BoardGame.jar!\BoardGame\multicharacter\BGMultiCharacter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */