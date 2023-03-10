package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractAttackCardChoice;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGGremlinHornGainEnergy extends AbstractAttackCardChoice {
    public static final String ID = "BGGremlinHornGainEnergy";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGGremlinHornGainEnergy");

    public BGGremlinHornGainEnergy() {
        super("BGGremlinHornGainEnergy", cardStrings.NAME, "blue/skill/double_energy", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE);
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void onChoseThisOption() {
        addToBot((AbstractGameAction)new GainEnergyAction(1));
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGGremlinHornGainEnergy();
    }
}

