package CoopBoardGame.characters;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import java.util.ArrayList;

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
        return CardPoolHelper.getCardPool(tmpPool, Enums.BG_CURSE);
    }
}
