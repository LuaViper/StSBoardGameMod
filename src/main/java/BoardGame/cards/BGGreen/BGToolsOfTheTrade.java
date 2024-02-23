package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import BoardGame.powers.BGWraithFormPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ToolsOfTheTradePower;

import java.util.ArrayList;

public class BGToolsOfTheTrade extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGToolsOfTheTrade");
    public static final String ID = "BGToolsOfTheTrade";

    public BGToolsOfTheTrade() {
        super("BGToolsOfTheTrade", cardStrings.NAME, "green/power/tools_of_the_trade", 1, cardStrings.DESCRIPTION, CardType.POWER, BGSilent.Enums.BG_GREEN, CardRarity.RARE, CardTarget.SELF);
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new ToolsOfTheTradePower((AbstractCreature)p, 1), 1));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }


    public AbstractCard makeCopy() {
        return new BGToolsOfTheTrade();
    }





}


