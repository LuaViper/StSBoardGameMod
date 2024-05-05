package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;
import BoardGame.powers.BGApotheosisPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGApotheosis extends AbstractBGCard {
    public static final String ID = "BGApotheosis";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGApotheosis");

    public BGApotheosis() {
        super("BGApotheosis", cardStrings.NAME, "colorless/skill/apotheosis", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.POWER, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.NONE);









        this.exhaust = true;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {

        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGApotheosisPower((AbstractCreature)p, 1), 0));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGApotheosis();
    }
}


