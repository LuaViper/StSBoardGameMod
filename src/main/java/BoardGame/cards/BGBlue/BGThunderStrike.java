package BoardGame.cards.BGBlue;
import BoardGame.actions.BGBlizzardAction;
import BoardGame.actions.BGThunderStrikeAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGThunderStrike extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGThunderStrike");
    public static final String ID = "BGThunderStrike";

    public BGThunderStrike() {
        super("BGThunderStrike", cardStrings.NAME, "blue/attack/thunder_strike", 3, cardStrings.DESCRIPTION, CardType.ATTACK, BGDefect.Enums.BG_BLUE, CardRarity.RARE, CardTarget.ALL_ENEMY);
        this.baseDamage=4;
        this.isMultiDamage = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new BGThunderStrikeAction(AbstractDungeon.player,this.multiDamage));

    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
        }
    }

    public AbstractCard makeCopy() {
        return new BGThunderStrike();
    }
}