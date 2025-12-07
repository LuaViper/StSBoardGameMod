package CoopBoardGame.cards.BGPurple;

import CoopBoardGame.cards.AbstractBGCard;
import CoopBoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGTranquility extends AbstractBGCard {

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(
        "CoopBoardGame:BGTranquility"
    );
    public static final String ID = "BGTranquility";

    public BGTranquility() {
        super(
            "BGTranquility",
            cardStrings.NAME,
            "purple/skill/tranquility",
            1,
            cardStrings.DESCRIPTION,
            CardType.SKILL,
            BGWatcher.Enums.BG_PURPLE,
            CardRarity.COMMON,
            CardTarget.SELF
        );
        this.exhaust = true;
        this.selfRetain = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction) new ChangeStanceAction("BGCalm"));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    public AbstractCard makeCopy() {
        return new BGTranquility();
    }
}
