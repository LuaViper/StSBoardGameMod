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


//TODO: change potion rarity to either COMMON or UNCOMMON depending on whether there are 1 or 2 of them in the potion deck
public abstract class PotionHelperPatch {

    public static ArrayList<String> potionDeck =new ArrayList<String>();
    @SpirePatch(clz = PotionHelper.class, method = "getPotions",
            paramtypez = {AbstractPlayer.PlayerClass.class, boolean.class})
    public static class PotionHelperGetPotionsPatch {
        @SpirePrefixPatch
        public static SpireReturn<ArrayList<String>> getPotions() {
            if(CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if(potionDeck.isEmpty()) {
                    potionDeck = new ArrayList<>();
                    //...these are listed in the order they appear in Tabletop Simulator's deck search
                    potionDeck.add("BoardGame:BGBlock Potion");
                    potionDeck.add("BoardGame:BGEnergy Potion");
                    potionDeck.add("BoardGame:BGExplosive Potion");
                    potionDeck.add("BoardGame:BGFire Potion");
                    potionDeck.add("BoardGame:BGSwift Potion");
                    potionDeck.add("BoardGame:BGWeak Potion");
                    potionDeck.add("BoardGame:BGFearPotion");
                    potionDeck.add("BoardGame:BGSteroidPotion");
                    potionDeck.add("BoardGame:BGGamblersBrew");
                    potionDeck.add("BoardGame:BGBloodPotion");
                    potionDeck.add("BoardGame:BGGhostInAJar");
                    potionDeck.add("BoardGame:BGDistilledChaos");
                    potionDeck.add("BoardGame:BGEntropicBrew");
                    potionDeck.add("BoardGame:BGFairyPotion");
                    potionDeck.add("BoardGame:BGAttackPotion");
                    potionDeck.add("BoardGame:BGSkillPotion");
                    potionDeck.add("BoardGame:BGAncientPotion");
                    potionDeck.add("BoardGame:BGBlock Potion");
                    potionDeck.add("BoardGame:BGEnergy Potion");
                    potionDeck.add("BoardGame:BGExplosive Potion");
                    potionDeck.add("BoardGame:BGFire Potion");
                    potionDeck.add("BoardGame:BGSwift Potion");
                    potionDeck.add("BoardGame:BGWeak Potion");
                    potionDeck.add("BoardGame:BGFearPotion");
                    potionDeck.add("BoardGame:BGSteroidPotion");
                    potionDeck.add("BoardGame:BGLiquidMemories");
                    potionDeck.add("BoardGame:BGSneckoOil");
                    potionDeck.add("BoardGame:BGElixirPotion");
                    potionDeck.add("BoardGame:BGCunningPotion");
                    Collections.shuffle(potionDeck, new java.util.Random(cardRng.randomLong()));
                    BoardGame.BoardGame.logger.info("Shuffled potion deck successfully");
                    return SpireReturn.Continue();
                }
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
                    case "BoardGame:BGGamblersBrew":
                        return SpireReturn.Return((AbstractPotion) new BGGamblersBrew());
                    case "BoardGame:BGBloodPotion":
                        return SpireReturn.Return((AbstractPotion) new BGBloodPotion());
                    case "BoardGame:BGGhostInAJar":
                        return SpireReturn.Return((AbstractPotion) new BGGhostInAJar());
                    case "BoardGame:BGDistilledChaos":
                        return SpireReturn.Return((AbstractPotion) new BGDistilledChaos());
                    case "BoardGame:BGEntropicBrew":
                        return SpireReturn.Return((AbstractPotion) new BGEntropicBrew());
                    case "BoardGame:BGAttackPotion":
                        return SpireReturn.Return((AbstractPotion) new BGAttackPotion());
                    case "BoardGame:BGSkillPotion":
                        return SpireReturn.Return((AbstractPotion) new BGSkillPotion());
                    case "BoardGame:BGAncientPotion":
                        return SpireReturn.Return((AbstractPotion) new BGAncientPotion());
                    case "BoardGame:BGFairyPotion":
                        return SpireReturn.Return((AbstractPotion) new BGFairyPotion());
                    case "BoardGame:BGLiquidMemories":
                        return SpireReturn.Return((AbstractPotion) new BGLiquidMemories());
                    case "BoardGame:BGSneckoOil":
                        return SpireReturn.Return((AbstractPotion) new BGSneckoOil());
                    case "BoardGame:BGElixirPotion":
                        return SpireReturn.Return((AbstractPotion) new BGElixir());
                    case "BoardGame:BGCunningPotion":
                        return SpireReturn.Return((AbstractPotion) new BGCunningPotion());
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
