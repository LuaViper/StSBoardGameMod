package CoopBoardGame.characters;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import java.util.ArrayList;
import java.util.Map;

/**
 * Shared utility class for building card pools filtered by color.
 * Used by BGCurse and BGColorless to avoid code duplication.
 */
public class CardPoolHelper {

    /**
     * Filters cards from the CardLibrary and adds matching cards to the provided pool.
     *
     * @param tmpPool The pool to add matching cards to
     * @param targetColor The card color to filter by
     * @return The updated pool with matching cards added
     */
    public static ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> tmpPool, AbstractCard.CardColor targetColor) {
        for (Map.Entry<String, AbstractCard> entry : CardLibrary.cards.entrySet()) {
            AbstractCard card = entry.getValue();
            String cardKey = entry.getKey();

            // Skip cards that don't match our target color
            if (!card.color.equals(targetColor)) {
                continue;
            }

            // Skip basic rarity cards
            if (card.rarity == AbstractCard.CardRarity.BASIC) {
                continue;
            }

            // Skip locked cards (except in daily runs)
            if (UnlockTracker.isCardLocked(cardKey) && !Settings.isDailyRun) {
                continue;
            }

            tmpPool.add(card);
        }

        return tmpPool;
    }
}
