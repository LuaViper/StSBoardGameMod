package BoardGame.cards.BGPurple;

import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGColorless.BGBecomeAlmighty;
import BoardGame.cards.BGColorless.BGFameAndFortune;
import BoardGame.cards.BGColorless.BGLiveForever;
import BoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class BGWish extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGWish");
    public static final String ID = "BGWish";

    public BGWish() {
        super("BGWish", cardStrings.NAME, "purple/skill/wish", 3, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.RARE, CardTarget.SELF);
        this.exhaust = true;
        this.baseBlock=10;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> stanceChoices = new ArrayList<>();
        stanceChoices.add(new BGBecomeAlmighty());
        AbstractCard lf=new BGLiveForever();
        stanceChoices.add(lf);
        stanceChoices.add(new BGFameAndFortune(this));
        if (this.upgraded)
            for (AbstractCard c : stanceChoices)
                c.upgrade();
        lf.applyPowers();
        addToBot((AbstractGameAction)new ChooseOneAction(stanceChoices));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.upgradeBlock(5);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGWish();
    }
}
