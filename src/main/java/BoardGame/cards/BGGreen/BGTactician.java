package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import BoardGame.powers.BGVulnerablePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGTactician extends AbstractBGCard {
    public static final String ID = "BGTactician";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGTactician");

    public BGTactician() {
        super("BGTactician", cardStrings.NAME, "green/skill/tactician", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.NONE);
        this.baseMagicNumber = 2;
        this.magicNumber = this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {}

    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        this.cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
        return false;
    }

    public void triggerOnManualDiscard() {
        addToTop((AbstractGameAction)new GainEnergyAction(this.magicNumber));
        addToTop(new ExhaustSpecificCardAction(this, AbstractDungeon.player.discardPile,true));
    }

    public AbstractCard makeCopy() {
        return new BGTactician();
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}


