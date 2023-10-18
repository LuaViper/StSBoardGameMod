package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGColorless;

import BoardGame.powers.BGFreeCardPower;
import BoardGame.powers.BGSadisticPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.MadnessAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGMadness extends AbstractBGCard {
    //TODO: card was buffed to cost 0.  not sure what upgrade does now
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGMadness");
    public static final String ID = "BGMadness";

    public BGMadness() {
        super("BGMadness", cardStrings.NAME, "colorless/skill/madness", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGColorless.Enums.CARD_COLOR, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);





        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;


    }


    public void use(AbstractPlayer p, AbstractMonster m) {

        addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGFreeCardPower((AbstractCreature)p, this.magicNumber), this.magicNumber));

    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }


    public AbstractCard makeCopy() {
        return new BGMadness();
    }
}


