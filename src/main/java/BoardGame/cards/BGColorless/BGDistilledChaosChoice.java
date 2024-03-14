package BoardGame.cards.BGColorless;

import BoardGame.actions.BGXCostCardAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGRed.BGWhirlwind;
import BoardGame.characters.BGColorless;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.sql.Array;
import java.util.ArrayList;

public class BGDistilledChaosChoice extends AbstractBGAttackCardChoice {
    public static final String ID = "BGDistilledChaosChoice";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGDistilledChaosChoice");

    private ArrayList<AbstractCard> cards=new ArrayList<>();

    public BGDistilledChaosChoice(){
        this(null, null, null);
    }

    public BGDistilledChaosChoice(AbstractCard card1, AbstractCard card2, AbstractCard card3) {
        super("BGDistilledChaosChoice", cardStrings.NAME, "green/skill/alchemize", 0, cardStrings.DESCRIPTION, CardType.STATUS, BGColorless.Enums.CARD_COLOR, CardRarity.COMMON, CardTarget.NONE);

        this.baseMagicNumber=cost;
        this.magicNumber=cost;
        if(card1!=null){
            this.rawDescription = "";
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[0]+card1.name;
            cards.add(card1);
            if(card2!=null){
                this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[1]+card2.name;
                cards.add(card2);
                if(card3!=null) {
                    this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[1] + card3.name;
                    cards.add(card3);
                }
            }
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[2];
        }else{
            //browsing compendium?
            this.rawDescription = cardStrings.DESCRIPTION;
        }
        initializeDescription();
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
//        //TODO: complain very loudly if we're not in the middle of a ModalChoice screen
//        onChoseThisOption();
    }



    public void onChoseThisOption() {
        //for (AbstractCard card : cards){
        AbstractCard card = cards.get(0);
        if(card instanceof AbstractBGCard){
            ((AbstractBGCard)card).followUpCardChain=cards;
            ((AbstractBGCard)card).followUpCardChain.remove(0);
        }
        card.exhaustOnUseOnce = false;
        AbstractDungeon.player.limbo.group.add(card);
        card.current_y = -200.0F * Settings.scale;
        card.target_x = Settings.WIDTH / 2.0F + 200.0F * Settings.xScale;
        card.target_y = Settings.HEIGHT / 2.0F;
        card.targetAngle = 0.0F;
        card.lighten(false);
        card.drawScale = 0.12F;
        card.targetDrawScale = 0.75F;

        card.applyPowers();

        if (card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY) {
            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                if (target != null) {
                    card.calculateCardDamage(target);
                }
                addToTop((AbstractGameAction) new NewQueueCardAction(card, target, false, true));
                addToTop((AbstractGameAction) new UnlimboAction(card));
            };
            addToBot((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose a target for " + card.name + ".")); //TODO: localization
        } else {
            addToTop((AbstractGameAction) new NewQueueCardAction(card, null, false, true));
            addToTop((AbstractGameAction) new UnlimboAction(card));
        }
    }

//    @Override
//    public void optionSelected(AbstractPlayer p, AbstractMonster m, int i) {
//        //TODO: complain loudly
//    }


    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGDistilledChaosChoice();
    }
}

