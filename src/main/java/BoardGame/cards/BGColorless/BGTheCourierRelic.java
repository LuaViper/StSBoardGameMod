package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.characters.BGColorless;
import BoardGame.relics.BGDiscardedOldCoin;
import BoardGame.relics.BGOldCoin;
import BoardGame.ui.EntropicBrewCourierRelicButton;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public class BGTheCourierRelic extends AbstractBGAttackCardChoice {
    public static final String ID = "BGTheCourierRelic";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGTheCourierRelic");

    public BGTheCourierRelic() {
        super("BGTheCourierRelic", cardStrings.NAME, "colorless/skill/transmutation", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        onChoseThisOption();
    }
    public void onChoseThisOption() {
        //TODO: this is a During Turn action, so lock die roll
        AbstractRelic r = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.COMMON);    //TODO LATER: random relic tier, in case we're searching vanilla relics
        if(r instanceof BGOldCoin) {
            AbstractRelic r2 = new BGDiscardedOldCoin();
            //TODO LATER: Shivs and Miracles should probably use instantObtain too
            r2.instantObtain(AbstractDungeon.player, AbstractDungeon.player.relics.size(), true);
            r2.flash();
            r = AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.COMMON);    //TODO LATER: random relic tier, in case we're searching vanilla relics
        }


        EntropicBrewCourierRelicButton button=EntropicBrewCourierRelicButton.SetupButton(r);
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
        return new BGTheCourierRelic();
    }
}

