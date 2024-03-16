package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.characters.BGColorless;
import BoardGame.ui.EntropicBrewCourierRelicButton;
import BoardGame.ui.EntropicBrewPotionButton;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGTheCourierDiscardPurchase extends AbstractBGAttackCardChoice {
    public static final String ID = "BGTheCourierDiscardPurchase";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGTheCourierDiscardPurchase");
    EntropicBrewPotionButton potionButton;
    EntropicBrewCourierRelicButton relicButton;

    public BGTheCourierDiscardPurchase() {
        this((EntropicBrewPotionButton) null);
    }
    public BGTheCourierDiscardPurchase(EntropicBrewPotionButton potionButton) {
        super("BGTheCourierDiscardPurchase", cardStrings.NAME, "", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);
        this.potionButton=potionButton;
    }
    public BGTheCourierDiscardPurchase(EntropicBrewCourierRelicButton relicButton) {
        super("BGTheCourierDiscardPurchase", cardStrings.NAME, "", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);
        this.relicButton=relicButton;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        onChoseThisOption();
    }
    public void onChoseThisOption() {
        if(potionButton!=null)potionButton.die();
        if(relicButton != null ) relicButton.die();
    }


    public void upgrade() {
        if (!this.upgraded) {
            //TODO: maybe prevent compendium + symbols by removing upgradeName from these temp cards?
            upgradeName();
        }
    }

    public AbstractCard makeCopy() {
        return new BGTheCourierDiscardPurchase();
    }
}

