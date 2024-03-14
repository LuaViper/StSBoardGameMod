package BoardGame.cards;

import BoardGame.BoardGame;
import BoardGame.characters.BGColorless;
import BoardGame.characters.BGWatcher;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static BoardGame.BoardGame.makeCardPath;
import static com.megacrit.cardcrawl.core.CardCrawlGame.languagePack;

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
