package BoardGame.characters;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class BGCurse {
    public static class Enums {
        @SpireEnum
        public static AbstractPlayer.PlayerClass BG_CURSE_PLAYERCLASS;
        @SpireEnum(name = "Curses (Board Game)") // These two HAVE to have the same absolutely identical name.
        public static AbstractCard.CardColor BG_CURSE;
        @SpireEnum(name = "Curses (Board Game)")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    public ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> tmpPool) {
        AbstractCard.CardColor color = BGCurse.Enums.BG_CURSE;
        Iterator var3 = CardLibrary.cards.entrySet().iterator();

        while(true) {
            Map.Entry c;
            AbstractCard card;
            do {
                do {
                    do {
                        if (!var3.hasNext()) {
                            return tmpPool;
                        }

                        c = (Map.Entry)var3.next();
                        card = (AbstractCard)c.getValue();
                    } while(!card.color.equals(color));
                } while(card.rarity == AbstractCard.CardRarity.BASIC);
            } while(UnlockTracker.isCardLocked((String)c.getKey()) && !Settings.isDailyRun);

            tmpPool.add(card);
        }
    }
}
