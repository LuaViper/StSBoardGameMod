package BoardGame.shop;

import BoardGame.cards.BGGoldenTicket;
import BoardGame.dungeons.AbstractBGDungeon;
import BoardGame.dungeons.BGExordium;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.OnSaleTag;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StoreRelic;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class BGShopScreen {
    final static float fake_draw_start_x = Settings.WIDTH * 0.16F;
    @SpirePatch(clz = ShopScreen.class, method = "initCards",
            paramtypez = {} )
    public static class ShopScreenInitCardsPatch {
        @SpirePrefixPatch
        private static SpireReturn<Void> initCards(ShopScreen __instance, @ByRef OnSaleTag[] ___saleTag) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                int tmp = (int) (Settings.WIDTH - fake_draw_start_x * 2.0F - AbstractCard.IMG_WIDTH_S * 5.0F) / 4;

                float padX = (int) (tmp + AbstractCard.IMG_WIDTH_S);

                int i;
                for (i = 0; i < __instance.coloredCards.size(); i++) {
                    float tmpPrice = AbstractCard.getPrice(((AbstractCard) __instance.coloredCards.get(i)).rarity);

                    AbstractCard c = __instance.coloredCards.get(i);
                    c.price = (int) tmpPrice;
                    c.current_x = (Settings.WIDTH / 2);
                    c.target_x = fake_draw_start_x + AbstractCard.IMG_WIDTH_S / 2.0F + padX * i;

                    for (AbstractRelic r : AbstractDungeon.player.relics) {
                        r.onPreviewObtainCard(c);
                    }
                }


                for (i = 0; i < __instance.colorlessCards.size(); i++) {
                    float tmpPrice = AbstractCard.getPrice(((AbstractCard) __instance.colorlessCards.get(i)).rarity);

                    AbstractCard c = __instance.colorlessCards.get(i);

                    c.price = (int) tmpPrice;
                    c.current_x = (Settings.WIDTH / 2);
                    c.target_x = fake_draw_start_x + AbstractCard.IMG_WIDTH_S / 2.0F + padX * i;

                    for (AbstractRelic r : AbstractDungeon.player.relics) {
                        r.onPreviewObtainCard(c);
                    }
                }


                //AbstractCard saleCard = __instance.coloredCards.get(AbstractDungeon.merchantRng.random(0, 4));
                AbstractCard saleCard = new BGGoldenTicket();   //IGNORE MEEEEEE
                saleCard.price = 9999;
                ___saleTag[0] = new OnSaleTag(saleCard);



                //TODO: investigate if we can access original setStartingCardPositions somehow
                fake_setStartingCardPositions(__instance);


                return SpireReturn.Return();
            }


            return SpireReturn.Continue();
        }
    }

    private static void fake_setStartingCardPositions(ShopScreen instance) {
        int tmp = (int)(Settings.WIDTH - fake_draw_start_x * 2.0F - AbstractCard.IMG_WIDTH_S * 5.0F) / 4;

        float padX = (int)(tmp + AbstractCard.IMG_WIDTH_S) + 10.0F * Settings.scale;
        int i;
        for (i = 0; i < instance.coloredCards.size(); i++) {
            ((AbstractCard)instance.coloredCards.get(i)).updateHoverLogic();
            ((AbstractCard)instance.coloredCards.get(i)).targetDrawScale = 0.75F;
            ((AbstractCard)instance.coloredCards.get(i)).current_x = fake_draw_start_x + AbstractCard.IMG_WIDTH_S / 2.0F + padX * i;
            ((AbstractCard)instance.coloredCards.get(i)).target_x = fake_draw_start_x + AbstractCard.IMG_WIDTH_S / 2.0F + padX * i;
            ((AbstractCard)instance.coloredCards.get(i)).target_y = 9999.0F * Settings.scale;
            ((AbstractCard)instance.coloredCards.get(i)).current_y = 9999.0F * Settings.scale;
        }

        for (i = 0; i < instance.colorlessCards.size(); i++) {
            ((AbstractCard)instance.colorlessCards.get(i)).updateHoverLogic();
            ((AbstractCard)instance.colorlessCards.get(i)).targetDrawScale = 0.75F;
            ((AbstractCard)instance.colorlessCards.get(i)).current_x = fake_draw_start_x + AbstractCard.IMG_WIDTH_S / 2.0F + padX * i;
            ((AbstractCard)instance.colorlessCards.get(i)).target_x = fake_draw_start_x + AbstractCard.IMG_WIDTH_S / 2.0F + padX * i;
            ((AbstractCard)instance.colorlessCards.get(i)).target_y = 9999.0F * Settings.scale;
            ((AbstractCard)instance.colorlessCards.get(i)).current_y = 9999.0F * Settings.scale;
        }
    }


    @SpirePatch(clz = ShopScreen.class, method = "initRelics",
            paramtypez = {} )
    public static class ShopScreenInitRelicsPatch {
        @SpirePostfixPatch
        private static void initRelics(ShopScreen __instance, @ByRef ArrayList<StoreRelic>[] ___relics) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                (___relics[0]).get(0).price -= 1;
            }
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "init",
            paramtypez = {ArrayList.class, ArrayList.class} )
    public static class ShopScreenInitPatch{
        @SpirePostfixPatch
        private static void init(ShopScreen __instance, ArrayList<AbstractCard> coloredCards, ArrayList<AbstractCard> colorlessCards){
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if(AbstractBGDungeon.ascensionLevel<8)
                    __instance.actualPurgeCost = 3;
                else
                    __instance.actualPurgeCost = 4;
            }
        }
    }


    @SpirePatch2(clz = ShopScreen.class, method = "purchaseCard",
            paramtypez = {AbstractCard.class} )
    public static class purchaseCardPatch {
         @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> Insert(ShopScreen __instance, AbstractCard hoveredCard){
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                AbstractBGDungeon.removeCardFromRewardDeck(hoveredCard);
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SoundMaster.class,"play");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }

        }
    }


    @SpirePatch2(clz = ShopScreen.class, method = "updatePurgeCard")
    public static class CardRemovalTooltipPatch {
        @SpireInsertPatch(
                locator= Locator.class,
                localvars={}
        )
        public static SpireReturn<Void> Insert(){
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon){
                //TODO: localization
                TipHelper.renderGenericTip(InputHelper.mX - 360.0F * Settings.scale, InputHelper.mY - 70.0F * Settings.scale, ShopScreen.LABEL[0], "Remove a card from your deck.");
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(TipHelper.class,"renderGenericTip");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }

        }
    }



}
