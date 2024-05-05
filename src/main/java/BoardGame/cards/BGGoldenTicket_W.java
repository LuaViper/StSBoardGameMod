package BoardGame.cards;

import BoardGame.BoardGame;
import BoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.cards.AbstractCard;

//TODO: exact wording of Golden Ticket card seems to have changed just before printing -- check physical copies when available
//TODO: Golden Ticket is actually a TICKET card, not a POWER card

public class BGGoldenTicket_W extends BGGoldenTicket {
    public static final String ID = BoardGame.makeID("GoldenTicket_W");
    public static final CardColor COLOR = BGWatcher.Enums.BG_PURPLE;
    public BGGoldenTicket_W()  {
        super(ID, COLOR);
    }
    public AbstractCard makeCopy() {
        return new BGGoldenTicket_W();
    }
}
