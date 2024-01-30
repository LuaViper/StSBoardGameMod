package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO: "from any player" / "from ALL players"
public class BGPanacea extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGPanacea");
    public static final String ID = "BGPanacea";

    public BGPanacea() {
        super("BGPanacea", cardStrings.NAME, "colorless/skill/panacea", 0, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);


        this.selfRetain = true;
        this.exhaust = true;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)p, (AbstractCreature)p, "BGWeakened"));
        addToBot((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)p, (AbstractCreature)p, "BGVulnerable"));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGPanacea();
    }
}


