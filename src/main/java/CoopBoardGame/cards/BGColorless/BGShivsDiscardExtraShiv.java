package CoopBoardGame.cards.BGColorless;

import CoopBoardGame.cards.AbstractBGAttackCardChoice;
import CoopBoardGame.characters.BGColorless;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGShivsDiscardExtraShiv extends AbstractBGAttackCardChoice {

    public static final String ID = "BGShivsDiscardExtraShiv";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGShivsDiscardExtraShiv"
    );

    public BGShivsDiscardExtraShiv() {
        super(
            "BGShivsDiscardExtraShiv",
            cardStrings.NAME,
            "green/skill/crippling_poison",
            -2,
            cardStrings.DESCRIPTION,
            CardType.STATUS,
            BGColorless.Enums.CARD_COLOR,
            CardRarity.SPECIAL,
            CardTarget.NONE
        );
        this.isInAutoplay = true; //bandage fix for extra shivs triggering dice lock
    }

    public void use(AbstractPlayer p, AbstractMonster m) {}

    public void onChoseThisOption() {
        AbstractRelic relic = AbstractDungeon.player.getRelic("CoopBoardGame:BGShivs");
        if (relic != null) {
            relic.counter = relic.counter - 1;
        }
    }

    public void upgrade() {}

    public AbstractCard makeCopy() {
        return new BGShivsDiscardExtraShiv();
    }
}
