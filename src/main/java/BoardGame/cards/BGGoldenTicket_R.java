package BoardGame.cards;

import BoardGame.BoardGame;
import BoardGame.characters.BGIronclad;
import BoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.cards.AbstractCard;

//TODO: exact wording of Golden Ticket card seems to have changed just before printing -- check physical copies when available
//TODO: Golden Ticket is actually a TICKET card, not a POWER card

public class BGGoldenTicket_R extends BGGoldenTicket {

    public static final String ID = BoardGame.makeID("GoldenTicket_R");
    public static final CardColor COLOR = BGIronclad.Enums.BG_RED;
    public BGGoldenTicket_R() {
        super(ID, COLOR);
    }
    public AbstractCard makeCopy() {
        return new BGGoldenTicket_R();
    }
}
