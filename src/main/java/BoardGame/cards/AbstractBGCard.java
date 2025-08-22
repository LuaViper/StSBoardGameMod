
package BoardGame.cards;

import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.characters.BGColorless;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.relics.BGTheDieRelic;
import BoardGame.screen.TargetSelectScreen;
import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderFixSwitches;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

//class for cards which use artwork from the original game but custom colors.
public abstract class AbstractBGCard extends CustomCard
        //implements AlternateCardCostModifier
    {

    private static final Logger logger = LogManager.getLogger(AbstractCard.class.getName());
    //public CardType type; //AbstractCard already has a type

    public AbstractPlayer owner;

    public static HashMap<String, Texture> imgMap;
    public static HashMap<String, Texture> betaImgMap;

    //TODO: patch in protected "getCardAtlas" function to AbstractCard class so we don't have to make a 2nd copy of each atlas
    private static TextureAtlas cardAtlas;
    private static TextureAtlas oldCardAtlas;

    public AbstractBGCard originalCard=null;    //currently used only for Blasphemy, but that might change later
    public boolean copyOriginalCardAgain=false; //currently used only for Blasphemy. purgeOnUse must also be true for this to be processed.
    public AbstractBGCard copiedCard=null;
    public int copiedCardEnergyOnUse=-99;

    public boolean cannotBeCopied=false;
    public boolean ignoreFurtherCopies=false;

    public ArrayList<AbstractCard> followUpCardChain=null; //TODO: we can probably use this instead of BGCopyCardAction's tripleAttack flag

    //TODO: actually read and understand the SecondMagicNumber tutorial, we're currently just blindly copypasting here
    public int defaultSecondMagicNumber;        // Just like magic number, or any number for that matter, we want our regular, modifiable stat
    public int defaultBaseSecondMagicNumber;    // And our base stat - the number in it's base state. It will reset to that by default.
    public boolean upgradedDefaultSecondMagicNumber; // A boolean to check whether the number has been upgraded or not.
    public boolean isDefaultSecondMagicNumberModified; // A boolean to check whether the number has been modified or not, for coloring purposes. (red/green)






    public AbstractBGCard(final String id,
                          final String name,
                          final String img,
                          final int cost,
                          final String rawDescription,
                          final CardType type,
                          final CardColor color,
                          final CardRarity rarity,
                          final CardTarget target) {

        //super(id, name, "images/1024Portraits/"+img+".png", cost, rawDescription, type, color, rarity, target);
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
        //CustomCard tries to override this, so override it right back
        this.assetUrl=img;

        this.nonvolatileBaseCost =cost;

        isDefaultSecondMagicNumberModified = false;
        costModifiers=new HashMap<>();
    }

    static {
        imgMap = new HashMap<>();
        betaImgMap = new HashMap<>();
        cardAtlas = new TextureAtlas(Gdx.files.internal("cards/cards.atlas"));
        oldCardAtlas = new TextureAtlas(Gdx.files.internal("oldCards/cards.atlas"));
    }

    public void passthroughLoadCardImage(String img) {
        super.loadCardImage(img);
    }

    public void loadCardImage(String img) {
        this.portrait = cardAtlas.findRegion(img);
        this.jokePortrait=oldCardAtlas.findRegion(img);
    }

    protected Texture passthroughGetPortraitImage() {
        return super.getPortraitImage();
    }

    protected Texture getPortraitImage() {
        if (Settings.PLAYTESTER_ART_MODE || UnlockTracker.betaCardPref.getBoolean(this.cardID, false)) {
            this.portraitImg = ImageMaster.loadImage("images/1024PortraitsBeta/" + this.assetUrl + ".png");
        } else {
            this.portraitImg = ImageMaster.loadImage("images/1024Portraits/" + this.assetUrl + ".png");
            if (this.portraitImg == null) {
                this.portraitImg = ImageMaster.loadImage("images/1024PortraitsBeta/" + this.assetUrl + ".png");
            }
        }
        return this.portraitImg;
    }


    @SpirePatch(clz = AbstractCard.class, method = "getPrice",
            paramtypez = {AbstractCard.CardRarity.class})
    public static class AbstractCardGetPricePatch{
        @SpirePrefixPatch
        public static SpireReturn<Integer> getPrice(AbstractCard.CardRarity rarity) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                switch(rarity){
                    case COMMON:
                        return SpireReturn.Return(2);
                    case UNCOMMON:
                        return SpireReturn.Return(3);
                    case RARE:
                        return SpireReturn.Return(6);
                    default:
                        return SpireReturn.Return(9999);
                }
            }
            return SpireReturn.Continue();
        }
    }


    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        boolean canUse = super.canUse(p, m);
        if(this.isInAutoplay) {
            if (target == CardTarget.ENEMY || target == CardTarget.SELF_AND_ENEMY) {
                if (m == null || m.halfDead || m.isDead || m.isDying || m.isEscaping) {
                    //TODO: localization
                    this.cantUseMessage = "I don't have a target!";
                    return false;
                }
            }
        }
        return canUse;
    }


    public void setCostForTurn(int amt) {
        //TODO: complain extraordinarily loudly if amt!=0 -- new cost reduction system is incompatible
        if (this.costForTurn >= -1) {       //X-cost cards can be modified too.
            this.costForTurn = amt;
            if (this.costForTurn < 0) {
                this.costForTurn = 0;
            }

            if (this.costForTurn != this.cost) {
                this.isCostModifiedForTurn = true;
            }
        }
    }


    //getCost affects card display, but not the actual energy paid for it
    @SpirePatch(clz = AbstractCard.class, method = "getCost",
            paramtypez = {})
    public static class AbstractCardGetCostPatch {
        @SpirePrefixPatch
        public static SpireReturn<String> Prefix(AbstractCard __instance) {
            if(!(__instance instanceof AbstractBGCard))return SpireReturn.Continue();

            if(AbstractDungeon.player!=null) {
                if (AbstractDungeon.player.hasPower("BGConfusion")) {
                    AbstractPower p = AbstractDungeon.player.getPower("BGConfusion");
                    if (p.amount > -1) {
                        return SpireReturn.Return(Integer.toString(p.amount));
                    }
                }
            }
            if (__instance.cost == -1) {
                if(__instance.costForTurn!=-1)
                    return SpireReturn.Return(Integer.toString(__instance.costForTurn));
                else if(__instance.freeToPlay())
                    return SpireReturn.Return("0");
                else
                    return SpireReturn.Return("X");
            }

            if (__instance.freeToPlay())
                return SpireReturn.Return("0");

            return SpireReturn.Return(Integer.toString(__instance.costForTurn));
        }

    }

    public HashMap<String, CostModifier> costModifiers;
    public class CostModifier{
        int x;

    }

    public int nonvolatileBaseCost;
    public boolean receivesPowerDiscount=false;
    public boolean wasRetainedLastTurn=false;
    public boolean temporarilyCostsZero =false;
    public void onRetained(){
        super.onRetained();
        wasRetainedLastTurn=true;
        //applyRetainDiscount();
    }
    public void resetAttributes(){
        super.resetAttributes();
        wasRetainedLastTurn=false;
        temporarilyCostsZero=false;
        ignoreFurtherCopies=false;
    }
    public void onResetBeforeMoving() {
        wasRetainedLastTurn=false;
        temporarilyCostsZero=false;
        ignoreFurtherCopies=false;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        cost=nonvolatileBaseCost;
        costForTurn=nonvolatileBaseCost;
        if(receivesPowerDiscount){
            //TODO: surely there is SOME way to do this without resetting costForTurn here
            int discount=BGTheDieRelic.powersPlayedThisCombat;
            this.updateCost(-cost+ nonvolatileBaseCost -discount);
        }
        applyVolatileDiscounts();
    }
    public void applyVolatileDiscounts(){
        if(wasRetainedLastTurn){
            AbstractPower p = AbstractDungeon.player.getPower("BGEstablishmentPower");
            if(p!=null) {
                costForTurn -= p.amount;
                if(costForTurn<0)costForTurn=0;
                if(costForTurn!= nonvolatileBaseCost)isCostModifiedForTurn = true;
            }
        }
        if(temporarilyCostsZero){
            if(nonvolatileBaseCost>=-1) {   //don't affect cards that cost "-2" !
                costForTurn = 0;
                if (costForTurn != nonvolatileBaseCost) isCostModifiedForTurn = true;
            }
        }
    }

    protected void upgradeBaseCost(int newBaseCost) {
        super.upgradeBaseCost(newBaseCost);
        nonvolatileBaseCost=newBaseCost;
    }


        public AbstractCard makeStatEquivalentCopy(){
            AbstractCard card = super.makeStatEquivalentCopy();
            ((AbstractBGCard)card).wasRetainedLastTurn=this.wasRetainedLastTurn;
            return card;
        }




//    @Override
//    public void calculateCardDamage(AbstractMonster mo) {
//        super.calculateCardDamage(mo);
//
//    }

    @SpirePatch2(clz = CardGroup.class, method = "resetCardBeforeMoving",   //TODO: consider moving this patch to Outmaneuver
            paramtypez={AbstractCard.class})
    public static class resetCardBeforeMovingPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCard c) {
            if(c instanceof AbstractBGCard){
                ((AbstractBGCard)c).onResetBeforeMoving();
            }
        }
    }



    public void displayUpgrades() { // Display the upgrade - when you click a card to upgrade it
        super.displayUpgrades();
        if (upgradedDefaultSecondMagicNumber) { // If we set upgradedDefaultSecondMagicNumber = true in our card.
            defaultSecondMagicNumber = defaultBaseSecondMagicNumber; // Show how the number changes, as out of combat, the base number of a card is shown.
            isDefaultSecondMagicNumberModified = true; // Modified = true, color it green to highlight that the number is being changed.
        }

    }

    public void upgradeDefaultSecondMagicNumber(int amount) { // If we're upgrading (read: changing) the number. Note "upgrade" and NOT "upgraded" - 2 different things. One is a boolean, and then this one is what you will usually use - change the integer by how much you want to upgrade.
        defaultBaseSecondMagicNumber += amount; // Upgrade the number by the amount you provide in your card.
        defaultSecondMagicNumber = defaultBaseSecondMagicNumber; // Set the number to be equal to the base value.
        upgradedDefaultSecondMagicNumber = true; // Upgraded = true - which does what the above method does.
    }




    //TODO: consider moving this to either DistilledChaos or one of the DoubleCardPlayPowers

    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard",
            paramtypez={AbstractCard.class, AbstractMonster.class, int.class})
    public static class FollowUpCardChainPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard c) {
            if(c instanceof AbstractBGCard){
                AbstractBGCard oldCard = (AbstractBGCard)c;
                if(oldCard.followUpCardChain!=null && !oldCard.followUpCardChain.isEmpty()){
                    AbstractCard card=oldCard.followUpCardChain.get(0);
                    if(card instanceof AbstractBGCard){
                        ((AbstractBGCard)card).followUpCardChain=oldCard.followUpCardChain;
                        ((AbstractBGCard)card).followUpCardChain.remove(0);
                        if (card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY) {
                            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                                if (target != null) {
                                    card.calculateCardDamage(target);
                                }
                                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new NewQueueCardAction(card, target, true, true));
                                AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new UnlimboAction(card,card.exhaust));
                            };
                            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose a target for " + card.name + ".")); //TODO: localization
                        } else {
                            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new NewQueueCardAction(card, null, true, true));
                            AbstractDungeon.actionManager.addToBottom((AbstractGameAction) new UnlimboAction(card,card.exhaust));
                        }
                    }
                }
            }
        }
    }
//
//    //private static final String FILENAME="BoardGameResources/images/512/colorless_bg_skill.png";
//    private static final Texture SKILL_COLORLESS = ((ReflectionHacks.RMethod)ReflectionHacks.privateStaticMethod(CustomCard.class,"getTextureFromString",String.class))
//            .invoke(null, "BoardGameResources/images/512/colorless_bg_skill.png");
//
//    @Override
//    public Texture getCardBg() {
//        BoardGame.BoardGame.logger.info("getCardBg"+(String)((this.type==CardType.STATUS) ? " !!!!!!" : ""));
//        return(this.type==CardType.STATUS) ? SKILL_COLORLESS : super.getCardBg();
//    }
//
////    @Override         //CustomCard doesn't actually use this
////    public TextureAtlas.AtlasRegion getCardBgAtlas() {
////        return(this.type==CardType.STATUS) ? ImageMaster.CARD_SKILL_BG_SILHOUETTE : super.getCardBgAtlas();
////    }

    @SpirePatch2(clz= RenderFixSwitches.RenderBgSwitch.class, method="Prefix",
            paramtypez={AbstractCard.class, SpriteBatch.class, float.class, float.class, Color.class})
    public static class StatusCardColorPatch {
        @SpireInsertPatch(
                locator = AbstractBGCard.StatusCardColorPatch.Locator.class,
                localvars = {"texture"}
        )
        public static SpireReturn<?> Insert(AbstractCard _____instance, @ByRef Texture[] ___texture) {
            if (_____instance instanceof AbstractBGCard && _____instance.type==CardType.STATUS) {
                if (BaseMod.getSkillBgTexture(BGColorless.Enums.CARD_COLOR) == null) {
                    BaseMod.saveSkillBgTexture(BGColorless.Enums.CARD_COLOR, ImageMaster.loadImage(BaseMod.getSkillBg(BGColorless.Enums.CARD_COLOR)));
                }
                ___texture[0] = BaseMod.getSkillBgTexture(BGColorless.Enums.CARD_COLOR);
                return SpireReturn.Continue();
                //Note: region will still be set to ImageMaster.CARD_SKILL_BG_BLACK at first, but it will quickly be overwritten since texture!=null
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(ImageMaster.class, "CARD_SKILL_BG_BLACK");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }


//    @Override
//    public int getAlternateResource(AbstractCard card) {
//        if(AbstractDungeon.player instanceof AbstractBGCharacter) {
//            return (((AbstractBGCharacter)AbstractDungeon.player).currentMultiEnergy);
//        }
//        return -1;
//    }
//    @Override
//    public boolean prioritizeAlternateCost(AbstractCard card) {
//        return true;
//    }
//    @Override
//    public boolean canSplitCost(AbstractCard card) {
//        return false;
//    }
//    @Override
//    public int spendAlternateCost(AbstractCard card, int costToSpend) {
//        int resource = -1;
//        if(AbstractDungeon.player instanceof AbstractBGCharacter) {
//            resource = (((AbstractBGCharacter)AbstractDungeon.player).currentMultiEnergy);
//        }
//        if (resource > costToSpend) {
//            ((AbstractBGCharacter)AbstractDungeon.player).currentMultiEnergy-=costToSpend;
//            costToSpend = 0;
//        } else if (resource > 0) {
//            ((AbstractBGCharacter)AbstractDungeon.player).currentMultiEnergy-=resource;
//            costToSpend -= resource;
//        }
//        return costToSpend;
//    }

    @SpirePatch(
            clz=AbstractCard.class,
            method=SpirePatch.CLASS
    )
    public static class Field
    {
        public static SpireField<AbstractCreature> rowTargetCreature = new SpireField<>(()->null);
    }


}