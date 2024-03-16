package BoardGame.cards.BGColorless;

import BoardGame.actions.BGGainMiracleAction;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.characters.BGColorless;
import BoardGame.ui.EntropicBrewPotionButton;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.SpotlightPlayerEffect;

import java.util.ArrayList;

public class BGTheCourierPotion extends AbstractBGAttackCardChoice {
    public static final String ID = "BGTheCourierPotion";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGTheCourierPotion");

    public BGTheCourierPotion() {
        super("BGTheCourierPotion", cardStrings.NAME, "green/skill/alchemize", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        onChoseThisOption();
    }
    public void onChoseThisOption() {
        AbstractPotion p = AbstractDungeon.returnRandomPotion(true);
        EntropicBrewPotionButton button=EntropicBrewPotionButton.SetupButton(p,true);
        ArrayList<AbstractCard> stanceChoices = new ArrayList<>();
        stanceChoices.add(new BGTheCourierConfirmPurchase(button));
        stanceChoices.add(new BGTheCourierDiscardPurchase(button));
        addToBot((AbstractGameAction)new ChooseOneAction(stanceChoices));


    }


    public void upgrade() {
        if (!this.upgraded) {
            //TODO: maybe prevent compendium + symbols by removing upgradeName from these temp cards?
            upgradeName();
        }
    }

    public AbstractCard makeCopy() {
        return new BGTheCourierPotion();
    }
}

