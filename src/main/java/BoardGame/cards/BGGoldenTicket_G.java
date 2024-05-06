package BoardGame.cards;

import BoardGame.BoardGame;
import BoardGame.characters.BGSilent;
import com.megacrit.cardcrawl.cards.AbstractCard;

//TODO: exact wording of Golden Ticket card seems to have changed just before printing -- check physical copies when available
//TODO: Golden Ticket is actually a TICKET card, not a POWER card

public class BGGoldenTicket_G extends BGGoldenTicket {
    public static final String ID = BoardGame.makeID("GoldenTicket_G");
    public static final CardColor COLOR = BGSilent.Enums.BG_GREEN;
    public BGGoldenTicket_G() {
        super(ID, COLOR);
    }
    public AbstractCard makeCopy() {
        return new BGGoldenTicket_G();
    }
}
