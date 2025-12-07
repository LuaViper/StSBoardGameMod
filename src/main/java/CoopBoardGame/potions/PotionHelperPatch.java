package CoopBoardGame.potions;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.cardRng;

import CoopBoardGame.dungeons.AbstractBGDungeon;
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

//TODO: change potion rarity to either COMMON or UNCOMMON depending on whether there are 1 or 2 of them in the potion deck
public abstract class PotionHelperPatch {

    public static ArrayList<String> potionDeck = new ArrayList<String>();

    @SpirePatch(
        clz = PotionHelper.class,
        method = "getPotions",
        paramtypez = { AbstractPlayer.PlayerClass.class, boolean.class }
    )
    public static class PotionHelperGetPotionsPatch {

        @SpirePrefixPatch
        public static SpireReturn<ArrayList<String>> getPotions() {
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if (potionDeck.isEmpty()) {
                    potionDeck = new ArrayList<>();
                    //...these are listed in the order they appear in Tabletop Simulator's deck search
                    potionDeck.add("CoopBoardGame:BGBlock Potion");
                    potionDeck.add("CoopBoardGame:BGEnergy Potion");
                    potionDeck.add("CoopBoardGame:BGExplosive Potion");
                    potionDeck.add("CoopBoardGame:BGFire Potion");
                    potionDeck.add("CoopBoardGame:BGSwift Potion");
                    potionDeck.add("CoopBoardGame:BGWeak Potion");
                    potionDeck.add("CoopBoardGame:BGFearPotion");
                    potionDeck.add("CoopBoardGame:BGSteroidPotion");
                    potionDeck.add("CoopBoardGame:BGGamblersBrew");
                    potionDeck.add("CoopBoardGame:BGBloodPotion");
                    potionDeck.add("CoopBoardGame:BGGhostInAJar");
                    potionDeck.add("CoopBoardGame:BGDistilledChaos");
                    potionDeck.add("CoopBoardGame:BGEntropicBrew");
                    potionDeck.add("CoopBoardGame:BGFairyPotion");
                    potionDeck.add("CoopBoardGame:BGAttackPotion");
                    potionDeck.add("CoopBoardGame:BGSkillPotion");
                    potionDeck.add("CoopBoardGame:BGAncientPotion");
                    potionDeck.add("CoopBoardGame:BGBlock Potion");
                    potionDeck.add("CoopBoardGame:BGEnergy Potion");
                    potionDeck.add("CoopBoardGame:BGExplosive Potion");
                    potionDeck.add("CoopBoardGame:BGFire Potion");
                    potionDeck.add("CoopBoardGame:BGSwift Potion");
                    potionDeck.add("CoopBoardGame:BGWeak Potion");
                    potionDeck.add("CoopBoardGame:BGFearPotion");
                    potionDeck.add("CoopBoardGame:BGSteroidPotion");
                    potionDeck.add("CoopBoardGame:BGLiquidMemories");
                    potionDeck.add("CoopBoardGame:BGSneckoOil");
                    potionDeck.add("CoopBoardGame:BGElixirPotion");
                    potionDeck.add("CoopBoardGame:BGCunningPotion");
                    Collections.shuffle(potionDeck, new java.util.Random(cardRng.randomLong()));
                    CoopBoardGame.CoopBoardGame.logger.info("Shuffled potion deck successfully");
                    return SpireReturn.Continue();
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = PotionHelper.class, method = "getRandomPotion", paramtypez = { Random.class })
    public static class PotionHelperGetRandomPotionPatch {

        @SpirePrefixPatch
        public static SpireReturn<AbstractPotion> getRandomPotion(Random rng) {
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                String randomKey = potionDeck.remove(0);
                //TODO: don't put potion on the bottom of the deck until it's been used!
                potionDeck.add(randomKey);

                return SpireReturn.Return(PotionHelper.getPotion(randomKey));
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = PotionHelper.class, method = "getRandomPotion", paramtypez = {})
    public static class PotionHelperGetRandomPotionPatch2 {

        @SpirePrefixPatch
        public static SpireReturn<AbstractPotion> getRandomPotion() {
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                String randomKey = potionDeck.remove(0);
                //TODO: don't put potion on the bottom of the deck until it's been used!
                potionDeck.add(randomKey);

                return SpireReturn.Return(PotionHelper.getPotion(randomKey));
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = PotionHelper.class, method = "isAPotion", paramtypez = { String.class })
    public static class PotionHelperIsAPotionPatch {

        @SpirePrefixPatch
        public static SpireReturn<Boolean> isAPotion(String key) {
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                if (potionDeck.contains(key)) return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = PotionHelper.class, method = "getPotion", paramtypez = { String.class })
    public static class PotionHelperGetPotionPatch {

        @SpirePrefixPatch
        public static SpireReturn<AbstractPotion> getPotion(String name) {
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                switch (name) {
                    case "CoopBoardGame:BGBlock Potion":
                        return SpireReturn.Return((AbstractPotion) new BGBlockPotion());
                    case "CoopBoardGame:BGEnergy Potion":
                        return SpireReturn.Return((AbstractPotion) new BGEnergyPotion());
                    case "CoopBoardGame:BGSwift Potion":
                        return SpireReturn.Return((AbstractPotion) new BGSwiftPotion());
                    case "CoopBoardGame:BGExplosive Potion":
                        return SpireReturn.Return((AbstractPotion) new BGExplosivePotion());
                    case "CoopBoardGame:BGFire Potion":
                        return SpireReturn.Return((AbstractPotion) new BGFirePotion());
                    case "CoopBoardGame:BGWeak Potion":
                        return SpireReturn.Return((AbstractPotion) new BGWeakenPotion());
                    case "CoopBoardGame:BGFearPotion":
                        return SpireReturn.Return((AbstractPotion) new BGFearPotion());
                    case "CoopBoardGame:BGSteroidPotion":
                        return SpireReturn.Return((AbstractPotion) new BGSteroidPotion());
                    case "CoopBoardGame:BGGamblersBrew":
                        return SpireReturn.Return((AbstractPotion) new BGGamblersBrew());
                    case "CoopBoardGame:BGBloodPotion":
                        return SpireReturn.Return((AbstractPotion) new BGBloodPotion());
                    case "CoopBoardGame:BGGhostInAJar":
                        return SpireReturn.Return((AbstractPotion) new BGGhostInAJar());
                    case "CoopBoardGame:BGDistilledChaos":
                        return SpireReturn.Return((AbstractPotion) new BGDistilledChaos());
                    case "CoopBoardGame:BGEntropicBrew":
                        return SpireReturn.Return((AbstractPotion) new BGEntropicBrew());
                    case "CoopBoardGame:BGAttackPotion":
                        return SpireReturn.Return((AbstractPotion) new BGAttackPotion());
                    case "CoopBoardGame:BGSkillPotion":
                        return SpireReturn.Return((AbstractPotion) new BGSkillPotion());
                    case "CoopBoardGame:BGAncientPotion":
                        return SpireReturn.Return((AbstractPotion) new BGAncientPotion());
                    case "CoopBoardGame:BGFairyPotion":
                        return SpireReturn.Return((AbstractPotion) new BGFairyPotion());
                    case "CoopBoardGame:BGLiquidMemories":
                        return SpireReturn.Return((AbstractPotion) new BGLiquidMemories());
                    case "CoopBoardGame:BGSneckoOil":
                        return SpireReturn.Return((AbstractPotion) new BGSneckoOil());
                    case "CoopBoardGame:BGElixirPotion":
                        return SpireReturn.Return((AbstractPotion) new BGElixir());
                    case "CoopBoardGame:BGCunningPotion":
                        return SpireReturn.Return((AbstractPotion) new BGCunningPotion());
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
        clz = AbstractDungeon.class,
        method = "returnRandomPotion",
        paramtypez = { AbstractPotion.PotionRarity.class, boolean.class }
    )
    public static class AbstractDungeonReturnRandomPotionPatch {

        @SpirePrefixPatch
        public static SpireReturn<AbstractPotion> returnRandomPotion(
            AbstractPotion.PotionRarity rarity,
            boolean limited
        ) {
            if (CardCrawlGame.dungeon instanceof AbstractBGDungeon) {
                AbstractPotion temp = PotionHelper.getRandomPotion();
                return SpireReturn.Return(temp);
            }
            return SpireReturn.Continue();
        }
    }
}
