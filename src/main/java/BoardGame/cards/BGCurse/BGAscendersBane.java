package BoardGame.cards.BGCurse;

import BoardGame.actions.BGPainAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGCurse;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.SetDontTriggerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGAscendersBane extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGAscendersBane");
    public static final String ID = "BGAscendersBane";

    public BGAscendersBane() {
        super("BGAscendersBane", cardStrings.NAME, "curse/ascenders_bane", -2, cardStrings.DESCRIPTION, CardType.CURSE, BGCurse.Enums.BG_CURSE, CardRarity.CURSE, CardTarget.NONE);
        this.isEthereal = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {}

    public void upgrade() {}

    public AbstractCard makeCopy() {
        return new BGAscendersBane();
    }
}

