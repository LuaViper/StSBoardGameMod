package BoardGame.cards.BGColorless;

import BoardGame.actions.BGGainMiracleAction;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.SpotlightPlayerEffect;

public class BGFameAndFortune extends AbstractBGAttackCardChoice {
    public static final String ID = "BGFameAndFortune";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGFameAndFortune");
    private AbstractCard wishCard;

    public BGFameAndFortune() {
        this(null);
    }
    public BGFameAndFortune(AbstractCard wishCard) {
        super("BGFameAndFortune", cardStrings.NAME, "colorless/skill/fame_and_fortune", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);
        this.baseMagicNumber=4;
        magicNumber=baseMagicNumber;
        this.wishCard=wishCard;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        onChoseThisOption();
    }
    public void onChoseThisOption() {
        AbstractDungeon.effectList.add(new RainingGoldEffect(this.magicNumber * 20, true));
        AbstractDungeon.effectsQueue.add(new SpotlightPlayerEffect());
        addToBot((AbstractGameAction)new BGGainMiracleAction(this.magicNumber,wishCard));
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
        return new BGFameAndFortune(null);
    }
}

