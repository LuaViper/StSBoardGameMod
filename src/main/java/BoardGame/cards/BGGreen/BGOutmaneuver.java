package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO: activated status does not work with Play Twice effects. should it?

public class BGOutmaneuver extends AbstractBGCard {
    public static final String ID = "BGOutmaneuver";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGOutmaneuver");

    public BGOutmaneuver() {
        super("BGOutmaneuver", cardStrings.NAME, "green/skill/outmaneuver", 0, cardStrings.DESCRIPTION, CardType.SKILL, BGSilent.Enums.BG_GREEN, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber = 2;
        this.magicNumber = this.baseMagicNumber;
        this.selfRetain=true;
    }


    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (this.wasRetainedLastTurn)
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
    }
    public void use(AbstractPlayer p, AbstractMonster m) {
        if(this.wasRetainedLastTurn){
            addToBot((AbstractGameAction)new GainEnergyAction(this.magicNumber));
        }
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
        return new BGOutmaneuver();
    }
}

