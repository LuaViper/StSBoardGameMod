package CoopBoardGame.cards.BGBlue;

import CoopBoardGame.actions.BGBarrageAction;
import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGDefect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGBarrage extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGBarrage"
    );
    public static final String ID = "BGBarrage";

    public BGBarrage() {
        super(
            "BGBarrage",
            cardStrings.NAME,
            "blue/attack/barrage",
            1,
            cardStrings.DESCRIPTION,
            CardType.ATTACK,
            BGDefect.Enums.BG_BLUE,
            CardRarity.COMMON,
            CardTarget.ENEMY
        );
        this.baseDamage = 1;
        this.baseMagicNumber = 0;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(
            (AbstractGameAction) new BGBarrageAction(
                (AbstractCreature) m,
                new DamageInfo((AbstractCreature) p, this.damage, DamageInfo.DamageType.NORMAL),
                this.magicNumber
            )
        );
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGBarrage();
    }
}
