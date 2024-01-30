package BoardGame.cards.BGRed;

import BoardGame.cards.BGStatus.BGDazed;
import BoardGame.characters.BGIronclad;
import BoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO: "to any player"
public class BGPowerThrough extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGPower Through"); public static final String ID = "BGPower Through";

    public BGPowerThrough() {
        super("BGPower Through", cardStrings.NAME, "red/skill/power_through", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);










        this.baseBlock = 3;
        this.cardsToPreview = (AbstractCard)new BGDazed();
    }


    public void use(AbstractPlayer p, AbstractMonster m) {

        addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, this.block));
        addToBot((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new BGDazed(), 1, false, true));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGPowerThrough();
    }
}


