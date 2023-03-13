
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
import com.megacrit.cardcrawl.helpers.ImageMaster;
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


}