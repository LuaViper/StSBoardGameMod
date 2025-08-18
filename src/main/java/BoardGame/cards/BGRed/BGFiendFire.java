package BoardGame.cards.BGRed;
import BoardGame.actions.BGFiendFireAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGFiendFire extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGFiendFire");
    public static final String ID = "BGFiend Fire";

    public BGFiendFire() {
        super("BGFiend Fire", cardStrings.NAME, "red/attack/fiend_fire", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.ENEMY);










        this.baseDamage = 1;
        this.exhaust = true;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new BGFiendFireAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn)));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGFiendFire();
    }
}
