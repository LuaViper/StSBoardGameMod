
package BoardGame.cards;
import BoardGame.dungeons.AbstractBGDungeon;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

//class for cards which use artwork from the original game but custom colors.
public abstract class AbstractBGCard extends CustomCard {

    private static final Logger logger = LogManager.getLogger(AbstractCard.class.getName());
    public CardType type;

    public static HashMap<String, Texture> imgMap;
    public static HashMap<String, Texture> betaImgMap;

    //TODO: patch in protected "getCardAtlas" function to AbstractCard class so we don't have to make a 2nd copy of each atlas
    private static TextureAtlas cardAtlas;
    private static TextureAtlas oldCardAtlas;

    public AbstractBGCard copiedCard=null;
    public int copiedCardEnergyOnUse=-99;

    public boolean cannotBeCopied=false;

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

        isDefaultSecondMagicNumberModified = false;
    }

    static {
        imgMap = new HashMap<>();
        betaImgMap = new HashMap<>();
        cardAtlas = new TextureAtlas(Gdx.files.internal("cards/cards.atlas"));
        oldCardAtlas = new TextureAtlas(Gdx.files.internal("oldCards/cards.atlas"));
    }

    public void loadCardImage(String img) {
        this.portrait = cardAtlas.findRegion(img);
        this.jokePortrait=oldCardAtlas.findRegion(img);
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


    public void setCostForTurn(int amt) {
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


}