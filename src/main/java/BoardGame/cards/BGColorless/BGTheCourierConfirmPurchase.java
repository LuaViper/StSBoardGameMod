package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.characters.BGColorless;
import BoardGame.potions.BGEntropicBrew;
import BoardGame.relics.AbstractBGRelic;
import BoardGame.relics.BGMiracles;
import BoardGame.ui.EntropicBrewCourierRelicButton;
import BoardGame.ui.EntropicBrewPotionButton;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGTheCourierConfirmPurchase extends AbstractBGAttackCardChoice {
    public static final String ID = "BGTheCourierConfirmPurchase";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGTheCourierConfirmPurchase");

    EntropicBrewPotionButton potionButton;
    EntropicBrewCourierRelicButton relicButton;

    public BGTheCourierConfirmPurchase() {
        this((EntropicBrewCourierRelicButton) null);
    }
    public BGTheCourierConfirmPurchase(EntropicBrewCourierRelicButton relicButton) {
        super("BGTheCourierConfirmPurchase", cardStrings.NAME, "colorless/skill/fame_and_fortune", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);
        this.relicButton = relicButton;
        updateDescription();
    }

    public BGTheCourierConfirmPurchase(EntropicBrewPotionButton potionButton) {
        super("BGTheCourierConfirmPurchase", cardStrings.NAME, "colorless/skill/fame_and_fortune", -2, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.SPECIAL, CardTarget.NONE);
        this.potionButton = potionButton;
        updateDescription();
    }

    public void updateDescription() {
        if (AbstractDungeon.player==null || (potionButton==null && relicButton==null)) {
            this.rawDescription = cardStrings.DESCRIPTION;
        }else if(potionButton!=null) {
            if (AbstractDungeon.player.gold >= potionButton.realPotion.getPrice()) {
                this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0] + potionButton.realPotion.name + cardStrings.EXTENDED_DESCRIPTION[1] + potionButton.realPotion.getPrice() + cardStrings.EXTENDED_DESCRIPTION[2];
            } else {
                this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[3] + potionButton.realPotion.name + cardStrings.EXTENDED_DESCRIPTION[4] + potionButton.realPotion.getPrice() + cardStrings.EXTENDED_DESCRIPTION[5];
            }
        }else if(relicButton!=null){
            if (AbstractDungeon.player.gold >= relicButton.realRelic.getPrice()) {
                this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0] + relicButton.realRelic.name + cardStrings.EXTENDED_DESCRIPTION[1] + relicButton.realRelic.getPrice() + cardStrings.EXTENDED_DESCRIPTION[2];
            } else {
                this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[3] + relicButton.realRelic.name + cardStrings.EXTENDED_DESCRIPTION[4] + relicButton.realRelic.getPrice() + cardStrings.EXTENDED_DESCRIPTION[5];
            }
        }
        initializeDescription();
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        onChoseThisOption();
    }
    public void onChoseThisOption() {
        if(potionButton != null ) {
            potionButton.die();
            if (AbstractDungeon.player.gold >= potionButton.realPotion.getPrice()) {
                AbstractDungeon.player.loseGold(potionButton.realPotion.getPrice());
                if (BGEntropicBrew.countOpenPotionSlots() >= 1) {
                    addToTop((AbstractGameAction) new ObtainPotionAction(potionButton.realPotion));
                } else {
                    EntropicBrewPotionButton.SetupButton(potionButton.realPotion, false);
                }
            }
        }
        if(relicButton != null ){
            relicButton.die();
            if (AbstractDungeon.player.gold >= relicButton.realRelic.getPrice()) {
                AbstractDungeon.player.loseGold(relicButton.realRelic.getPrice());
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), relicButton.realRelic);
                if(relicButton.realRelic instanceof AbstractBGRelic) {
                    ((AbstractBGRelic)relicButton.realRelic).setupObtainedDuringCombat();
                }
            }
        }
    }


    public void upgrade() {
        if (!this.upgraded) {
            //TODO: maybe prevent compendium + symbols by removing upgradeName from these temp cards?
            upgradeName();
        }
    }

    public AbstractCard makeCopy() {
        return new BGTheCourierConfirmPurchase();
    }
}

