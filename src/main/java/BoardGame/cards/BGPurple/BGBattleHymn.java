package BoardGame.cards.BGPurple;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import BoardGame.powers.BGBattleHymnPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGBattleHymn extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGBattleHymn");
    public static final String ID = "BGBattleHymn";

    public BGBattleHymn() {
        super("BGBattleHymn", cardStrings.NAME, "purple/power/battle_hymn", 1, cardStrings.DESCRIPTION, CardType.POWER, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.SELF);
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new ApplyPowerAction(p, p, new BGBattleHymnPower(p, magicNumber), magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGBattleHymn();
    }
}
