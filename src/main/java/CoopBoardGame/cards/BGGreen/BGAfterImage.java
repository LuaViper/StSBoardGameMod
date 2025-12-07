package CoopBoardGame.cards.BGGreen;

import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGSilent;
import CoopBoardGame.powers.BGAfterImagePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGAfterImage extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGAfterImage"
    );
    public static final String ID = "BGAfterImage";

    public BGAfterImage() {
        super(
            "BGAfterImage",
            cardStrings.NAME,
            "green/power/after_image",
            1,
            cardStrings.DESCRIPTION,
            CardType.POWER,
            BGSilent.Enums.BG_GREEN,
            CardRarity.RARE,
            CardTarget.SELF
        );
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(
            (AbstractGameAction) new ApplyPowerAction(
                (AbstractCreature) p,
                (AbstractCreature) p,
                (AbstractPower) new BGAfterImagePower((AbstractCreature) p, this.magicNumber),
                this.magicNumber
            )
        );
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    public AbstractCard makeCopy() {
        return new BGAfterImage();
    }
}
