package BoardGame.cards.BGColorless;

import BoardGame.cards.AbstractBGAttackCardChoice;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGShivsDiscardExtraShiv extends AbstractBGAttackCardChoice {
    public static final String ID = "BGShivsDiscardExtraShiv";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGShivsDiscardExtraShiv");

    public BGShivsDiscardExtraShiv() {
        super("BGShivsDiscardExtraShiv", cardStrings.NAME, "green/skill/crippling_poison", -2, cardStrings.DESCRIPTION, CardType.STATUS, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
    }



    public void use(AbstractPlayer p, AbstractMonster m) {}



    public void onChoseThisOption() {
        AbstractRelic relic= AbstractDungeon.player.getRelic("BoardGame:BGShivs");
        if(relic!=null){
            relic.counter=relic.counter-1;
        }
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGShivsDiscardExtraShiv();
    }
}

