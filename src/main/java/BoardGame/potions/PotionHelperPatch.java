package BoardGame.potions;

import BoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.random.Random;

import java.util.ArrayList;
import java.util.Collections;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.cardRng;


public abstract class PotionHelperPatch {

    public static ArrayList<String> potionDeck =new ArrayList<String>();
    @SpirePatch(clz = PotionHelper.class, method = "getPotions",
            paramtypez = {AbstractPlayer.PlayerClass.class, boolean.class})
    public static class PotionHelperGetPotionsPatch {
        @SpirePrefixPatch
        public static SpireReturn<ArrayList<String>> getPotions() {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                potionDeck = new ArrayList<>();
                potionDeck.add("BoardGame:BGBlock Potion");
                potionDeck.add("BoardGame:BGEnergy Potion");
                potionDeck.add("BoardGame:BGExplosive Potion");
                potionDeck.add("BoardGame:BGFire Potion");
                potionDeck.add("BoardGame:BGSwift Potion");
                potionDeck.add("BoardGame:BGWeak Potion");
                potionDeck.add("BoardGame:BGFearPotion");
                potionDeck.add("BoardGame:BGSteroidPotion");
                potionDeck.add("BoardGame:BGSteroidPotion");
                potionDeck.add("BoardGame:BGBloodPotion");
                //potionDeck.add("BoardGame:BGFairyPotion");
                potionDeck.add("BoardGame:BGBlock Potion");
                potionDeck.add("BoardGame:BGEnergy Potion");
                potionDeck.add("BoardGame:BGExplosive Potion");
                potionDeck.add("BoardGame:BGFire Potion");
                potionDeck.add("BoardGame:BGSwift Potion");
                potionDeck.add("BoardGame:BGWeak Potion");
                potionDeck.add("BoardGame:BGFearPotion");
                potionDeck.add("BoardGame:BGSteroidPotion");
                potionDeck.add("BoardGame:BGSneckoOil");
                potionDeck.add("BoardGame:BGElixirPotion");
                Collections.shuffle(potionDeck, new java.util.Random(cardRng.randomLong()));
                return SpireReturn.Continue();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = PotionHelper.class, method = "getRandomPotion",
            paramtypez = {Random.class})
    public static class PotionHelperGetRandomPotionPatch {
        @SpirePrefixPatch
        public static SpireReturn<AbstractPotion> getRandomPotion(Random rng) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                String randomKey = potionDeck.remove(0);
                //TODO: don't put potion on the bottom of the deck until it's been used!
                potionDeck.add(randomKey);

                return SpireReturn.Return(PotionHelper.getPotion(randomKey));
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = PotionHelper.class, method = "getRandomPotion",
            paramtypez = {})
    public static class PotionHelperGetRandomPotionPatch2 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractPotion> getRandomPotion() {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                String randomKey = potionDeck.remove(0);
                //TODO: don't put potion on the bottom of the deck until it's been used!
                potionDeck.add(randomKey);

                return SpireReturn.Return(PotionHelper.getPotion(randomKey));
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch(clz = PotionHelper.class, method = "isAPotion",
            paramtypez = {String.class})
    public static class PotionHelperIsAPotionPatch {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> isAPotion(String key) {
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if (potionDeck.contains(key))
                    return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = PotionHelper.class, method = "getPotion",
            paramtypez = {String.class})
    public static class PotionHelperGetPotionPatch {
        @SpirePrefixPatch
        public static SpireReturn<AbstractPotion> getPotion(String name) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                switch (name) {
                    case "BoardGame:BGBlock Potion":
                        return SpireReturn.Return((AbstractPotion) new BGBlockPotion());
                    case "BoardGame:BGEnergy Potion":
                        return SpireReturn.Return((AbstractPotion) new BGEnergyPotion());
                    case "BoardGame:BGSwift Potion":
                        return SpireReturn.Return((AbstractPotion) new BGSwiftPotion());
                    case "BoardGame:BGExplosive Potion":
                        return SpireReturn.Return((AbstractPotion) new BGExplosivePotion());
                    case "BoardGame:BGFire Potion":
                        return SpireReturn.Return((AbstractPotion) new BGFirePotion());
                    case "BoardGame:BGWeak Potion":
                        return SpireReturn.Return((AbstractPotion) new BGWeakenPotion());
                    case "BoardGame:BGFearPotion":
                        return SpireReturn.Return((AbstractPotion) new BGFearPotion());
                    case "BoardGame:BGSteroidPotion":
                        return SpireReturn.Return((AbstractPotion) new BGSteroidPotion());
                    case "BoardGame:BGBloodPotion":
                        return SpireReturn.Return((AbstractPotion) new BGBloodPotion());
//                    case "BoardGame:BGFairyPotion":
//                        return SpireReturn.Return((AbstractPotion) new BGFairyPotion());
                    case "BoardGame:BGSneckoOil":
                        return SpireReturn.Return((AbstractPotion) new BGSneckoOil());
                    case "BoardGame:BGElixirPotion":
                        return SpireReturn.Return((AbstractPotion) new BGElixir());
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "returnRandomPotion",
            paramtypez = {AbstractPotion.PotionRarity.class, boolean.class})
    public static class AbstractDungeonReturnRandomPotionPatch {
        @SpirePrefixPatch
        public static SpireReturn<AbstractPotion> returnRandomPotion(AbstractPotion.PotionRarity rarity, boolean limited) {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                AbstractPotion temp = PotionHelper.getRandomPotion();
                return SpireReturn.Return(temp);
            }
            return SpireReturn.Continue();
        }
    }

}
