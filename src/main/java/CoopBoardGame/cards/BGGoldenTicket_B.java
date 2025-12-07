package CoopBoardGame.cards;

import CoopBoardGame.CoopBoardGame;
import CoopBoardGame.characters.BGDefect;
import com.megacrit.cardcrawl.cards.AbstractCard;

//TODO: exact wording of Golden Ticket card seems to have changed just before printing -- check physical copies when available
//TODO: Golden Ticket is actually a TICKET card, not a POWER card

public class BGGoldenTicket_B extends BGGoldenTicket {

    public static final String ID = CoopBoardGame.makeID("GoldenTicket_B");
    public static final CardColor COLOR = BGDefect.Enums.BG_BLUE;

    public BGGoldenTicket_B() {
        super(ID, COLOR);
    }

    public AbstractCard makeCopy() {
        return new BGGoldenTicket_B();
    }
}
