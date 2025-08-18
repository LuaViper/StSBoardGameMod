package BoardGame.patches.bossrewards;

import BoardGame.dungeons.AbstractBGDungeon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.ui.buttons.DynamicBanner;

import java.util.ArrayList;

public class SecondDynamicBanner {
    // note that "boss relic preview" at the MonsterRoomBoss reward screen isn't feasible as Orrery/Enchiridion could influence player's card pick

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("BossRelicSelectScreen");
    public static final String[] TEXT = uiStrings.TEXT;
    private static final String SELECT_MSG = TEXT[2];
    public static DynamicBanner banner;


    @SpirePatch2(clz = AbstractDungeon.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez={String.class, String.class, AbstractPlayer.class, ArrayList.class})
    public static class initializePatch {
        @SpirePostfixPatch
        public static void Foo() {
            SecondDynamicBanner.banner=new DynamicBanner();
        }
    }
    @SpirePatch2(clz = AbstractDungeon.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez={String.class, AbstractPlayer.class, SaveFile.class})
    public static class initializePatch2 {
        @SpirePostfixPatch
        public static void Foo() {
            SecondDynamicBanner.banner=new DynamicBanner();
        }
    }

    @SpirePatch2(clz = DynamicBanner.class, method = "appear",
            paramtypez={float.class,String.class})
    public static class AppearPatch {
        @SpirePostfixPatch
        public static void Foo(DynamicBanner __instance, float y, String label) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return;
            if(!label.equals(SELECT_MSG))return;
            if(__instance == SecondDynamicBanner.banner) return;
            //TODO: localization
            SecondDynamicBanner.banner.appear(y - 400.0F * Settings.scale,"Choose a Card");
        }
    }
    @SpirePatch2(clz = DynamicBanner.class, method = "appearInstantly",
            paramtypez={float.class,String.class})
    public static class AppearPatch2 {
        @SpirePostfixPatch
        public static void Foo(DynamicBanner __instance, float y, String label) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return;
            if(!label.equals(SELECT_MSG))return;
            if(__instance == SecondDynamicBanner.banner) return;
            //TODO: localization
            SecondDynamicBanner.banner.appearInstantly(y - 400.0F * Settings.scale,"Choose a Card");
        }
    }

    @SpirePatch2(clz = DynamicBanner.class, method = "hide",
            paramtypez={})
    public static class HidePatch {
        @SpirePostfixPatch
        public static void Foo(DynamicBanner __instance) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return;
            if(__instance == SecondDynamicBanner.banner) return;
            SecondDynamicBanner.banner.hide();
        }
    }

    @SpirePatch2(clz = DynamicBanner.class, method = "update",
            paramtypez={})
    public static class UpdatePatch {
        @SpirePostfixPatch
        public static void Foo(DynamicBanner __instance) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return;
            if(__instance == SecondDynamicBanner.banner) return;

            SecondDynamicBanner.banner.update();
        }
    }

    @SpirePatch2(clz = DynamicBanner.class, method = "render",
            paramtypez={SpriteBatch.class})
    public static class HideRelicBannerIfRelicsAlreadyTaken {
        @SpirePrefixPatch
        public static SpireReturn<Void> Foo(DynamicBanner __instance, SpriteBatch sb) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return SpireReturn.Continue();
            if(!(AbstractBGDungeon.getCurrRoom() instanceof TreasureRoomBoss))return SpireReturn.Continue();
            if(__instance == SecondDynamicBanner.banner) return SpireReturn.Continue();
            if(AbstractBGDungeon.screen!= AbstractDungeon.CurrentScreen.BOSS_REWARD)return SpireReturn.Continue();
            if (BossCardReward.Field.relicAlreadyTaken.get(AbstractDungeon.getCurrRoom())) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch2(clz = DynamicBanner.class, method = "render",
            paramtypez={SpriteBatch.class})
    public static class RenderPatch {
        @SpirePostfixPatch
        public static void Foo(DynamicBanner __instance, SpriteBatch sb) {
            if(!(CardCrawlGame.dungeon instanceof AbstractBGDungeon))return;
            if(__instance == SecondDynamicBanner.banner) return;
            if(AbstractDungeon.screen != AbstractDungeon.CurrentScreen.BOSS_REWARD) return;
            if(BossCardReward.Field.cardReward.get(AbstractBGDungeon.getCurrRoom())==null)return;

            SecondDynamicBanner.banner.render(sb);
        }
    }




}
