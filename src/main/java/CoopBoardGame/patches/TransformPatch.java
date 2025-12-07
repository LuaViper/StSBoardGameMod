package CoopBoardGame.patches;

import static CoopBoardGame.characters.BGCurse.Enums.BG_CURSE;

import CoopBoardGame.dungeons.AbstractBGDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import java.util.ArrayList;

/**
 * Patches to modify card transformation behavior in Board Game mode.
 *
 * In Board Game mode, transformations use the reward deck instead of all available cards,
 * and certain cards (BGAscendersBane, BG_CURSE cards) are excluded from transformation options.
 */
public class TransformPatch {

    /**
     * Gets all cards that can be transformed, excluding bottled cards and BG_CURSE cards.
     *
     * @return CardGroup containing all transformable cards from the player's deck
     */
    public static CardGroup getTransformableCards() {
        CardGroup purgeableCards = CardGroup.getGroupWithoutBottledCards(
            AbstractDungeon.player.masterDeck.getPurgeableCards()
        );
        CardGroup transformableCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

        for (AbstractCard card : purgeableCards.group) {
            if (card.color != BG_CURSE) {
                transformableCards.group.add(card);
            }
        }
        return transformableCards;
    }

    /**
     * Prevents BGAscendersBane from being purged (removed from deck).
     * This card acts like the base game's AscendersBane curse.
     */
    @SpirePatch2(clz = CardGroup.class, method = "getPurgeableCards")
    public static class BGAscendersBanePurgePatch {

        @SpirePostfixPatch
        public static CardGroup Postfix(CardGroup __result) {
            CardGroup filteredCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

            for (AbstractCard card : __result.group) {
                // Other non-purgeable cards have already been filtered by vanilla logic
                if (!card.cardID.equals("BGAscendersBane")) {
                    filteredCards.group.add(card);
                }
            }
            return filteredCards;
        }
    }

    /**
     * Modifies the transform animation to use Board Game reward decks.
     *
     * Note: returnTrulyRandomCardFromAvailable is used for the visual animation.
     * The actual transform result is determined by AbstractBGDungeon.DrawFromRewardDeck.
     */
    @SpirePatch2(
        clz = AbstractDungeon.class,
        method = "returnTrulyRandomCardFromAvailable",
        paramtypez = { AbstractCard.class, Random.class }
    )
    public static class TransformAnimationPatch {

        @SpirePrefixPatch
        private static SpireReturn<AbstractCard> Foo(AbstractCard prohibited, Random rng) {
            // Only modify behavior in Board Game dungeons
            if (!(CardCrawlGame.dungeon instanceof AbstractBGDungeon)) {
                return SpireReturn.Continue();
            }

            ArrayList<AbstractCard> availableCards = new ArrayList<>();

            // Add all cards from the normal reward deck, excluding the prohibited card
            for (AbstractCard card : AbstractBGDungeon.rewardDeck.group) {
                if (!card.cardID.equals(prohibited.cardID)) {
                    availableCards.add(card);
                }
            }

            // Add all cards from the rare reward deck, excluding the prohibited card
            for (AbstractCard card : AbstractBGDungeon.rareRewardDeck.group) {
                if (!card.cardID.equals(prohibited.cardID)) {
                    availableCards.add(card);
                }
            }

            // Select a random card from the combined pool
            int randomIndex = rng.random(availableCards.size() - 1);
            AbstractCard selectedCard = availableCards.get(randomIndex);

            return SpireReturn.Return(selectedCard.makeCopy());
        }
    }
}
