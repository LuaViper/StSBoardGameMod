package CoopBoardGame.characters;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import java.util.ArrayList;

public class BGColorless {

    public static class Enums {

        @SpireEnum
        public static AbstractPlayer.PlayerClass BG_COLORLESS_PLAYERCLASS;

        @SpireEnum(name = "Colorless (Board Game)") // These two HAVE to have the same absolutely identical name.
        public static AbstractCard.CardColor CARD_COLOR;

        @SpireEnum(name = "Colorless (Board Game)")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    public ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> tmpPool) {
        return CardPoolHelper.getCardPool(tmpPool, Enums.CARD_COLOR);
    }
}
