package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import BoardGame.powers.BGInvinciblePlayerPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGApparition extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGGhostly");
    public static final String ID = "BGGhostly";

    public BGApparition() {
        super("BGGhostly", cardStrings.NAME, "colorless/skill/apparition", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);

        this.exhaust = true;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        //addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new IntangiblePlayerPower((AbstractCreature)p, 1), 1));
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGInvinciblePlayerPower((AbstractCreature)p, 1), 0));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
            this.isEthereal = false;
        }
    }


    public AbstractCard makeCopy() {
        return new BGApparition();
    }
}


