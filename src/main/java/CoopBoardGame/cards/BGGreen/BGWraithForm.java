package CoopBoardGame.cards.BGGreen;

import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGSilent;
import CoopBoardGame.powers.BGInvinciblePlayerPower;
import CoopBoardGame.powers.BGWraithFormPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGWraithForm extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGWraithForm"
    );
    public static final String ID = "BGWraithForm";

    public BGWraithForm() {
        super(
            "BGWraithForm",
            cardStrings.NAME,
            "green/power/wraith_form",
            3,
            cardStrings.DESCRIPTION,
            CardType.POWER,
            BGSilent.Enums.BG_GREEN,
            CardRarity.RARE,
            CardTarget.SELF
        );
        this.baseMagicNumber = 2;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(
            (AbstractGameAction) new ApplyPowerAction(
                (AbstractCreature) p,
                (AbstractCreature) p,
                (AbstractPower) new BGInvinciblePlayerPower((AbstractCreature) p, 1),
                1
            )
        );
        addToBot(
            (AbstractGameAction) new ApplyPowerAction(
                (AbstractCreature) p,
                (AbstractCreature) p,
                (AbstractPower) new BGWraithFormPower((AbstractCreature) p, this.magicNumber, this),
                this.magicNumber
            )
        );
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGWraithForm();
    }
}
