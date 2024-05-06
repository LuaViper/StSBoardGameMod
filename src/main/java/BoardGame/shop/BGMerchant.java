package BoardGame.shop;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AnimatedNpc;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.shop.Merchant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class BGMerchant {
    public static final Logger logger = LogManager.getLogger(BGMerchant.class.getName());
    @SpirePatch(clz = Merchant.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez = {float.class,float.class,int.class})
    public static class MerchantConstructorPatch{
        public MerchantConstructorPatch(){}
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(Merchant merchant, float x, float y, int newShopScreen,
                                               @ByRef ArrayList<AbstractCard> ___cards1[], @ByRef ArrayList<AbstractCard> ___cards2[], @ByRef ArrayList<String> ___idleMessages[],
                                          @ByRef float[] ___speechTimer, @ByRef float[]___modX, @ByRef float[]___modY, @ByRef int[]___shopScreen) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
            //if(false){
                BoardGame.BoardGame.logger.info("Rolling BG MERCHANT CARDS");
                merchant.anim = new AnimatedNpc(Merchant.DRAW_X + 256.0F * Settings.scale, AbstractDungeon.floorY + 30.0F * Settings.scale, "images/npcs/merchant/skeleton.atlas", "images/npcs/merchant/skeleton.json", "idle");

                merchant.hb = new Hitbox(360.0F * Settings.scale, 300.0F * Settings.scale);
                if(___cards1[0]==null){
                    ___cards1[0]=new ArrayList<>();
                }
                if(___cards2[0]==null){
                    ___cards2[0]=new ArrayList<>();
                }
                if(___idleMessages[0]==null){
                    ___idleMessages[0]=new ArrayList<>();
                }


                AbstractCard c;

                c = AbstractDungeon.getCard(AbstractCard.CardRarity.COMMON);
                ___cards1[0].add(c);

                c = AbstractDungeon.getCard(AbstractCard.CardRarity.COMMON);
                ___cards1[0].add(c);

                c = AbstractDungeon.getCard(AbstractCard.CardRarity.COMMON);
                ___cards1[0].add(c);

                c = AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.COMMON);
                ___cards1[0].add(c);

                c = AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.COMMON);
                ___cards1[0].add(c);

                c = AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.COMMON);
                ___cards2[0].add(c);

                if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                    if (AbstractDungeon.id.equals("TheEnding")) {
                        Collections.addAll(___idleMessages[0], Merchant.ENDING_TEXT);
                    } else {
                        Collections.addAll(___idleMessages[0], Merchant.TEXT);
                        logger.info("Added " + Merchant.TEXT.length + " to idleMessages[0]");
                    }
                }


                ___speechTimer[0] = 1.5F;
                ___modX[0] = x;
                ___modY[0] = y;

                merchant.hb.move(Merchant.DRAW_X + (250.0F + x) * Settings.scale, Merchant.DRAW_Y + (130.0F + y) * Settings.scale);
                ___shopScreen[0] = newShopScreen;
                AbstractDungeon.shopScreen.init(___cards1[0], ___cards2[0]);

                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
//        @SpirePostfixPatch
//        public static SpireReturn<Void> Postfix(Merchant merchant, float x, float y, int newShopScreen,
//                                               @ByRef ArrayList<String>[] ___idleMessages,
//                                               @ByRef float[] ___speechTimer, @ByRef float[]___modX, @ByRef float[]___modY, @ByRef int[]___shopScreen) {
//
//            return SpireReturn.Continue();
//        }
    }








}
