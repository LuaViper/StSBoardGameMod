package BoardGame.cards.BGPurple;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGTantrum extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGTantrum");
    public static final String ID = "BGTantrum";

    public BGTantrum() {
        super("BGTantrum", cardStrings.NAME, "purple/attack/tantrum", 1, cardStrings.DESCRIPTION, CardType.ATTACK, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.ENEMY);
        baseDamage = 2;
        baseMagicNumber=1;
        magicNumber=baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        //addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGInstantReboundPower((AbstractCreature)p), 1));
        for (int i = 0; i < this.magicNumber; i++)
            addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));


        //TODO: proper "isThisACopy" check
        if(!this.purgeOnUse) {
            this.purgeOnUse = true;
            AbstractCard copy = this.makeStatEquivalentCopy();
            addToBot(new MakeTempCardInDrawPileAction(copy, 1, false, true));
        }

        addToBot((AbstractGameAction) new ChangeStanceAction("BGWrath"));


    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(-1);
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGTantrum();
    }
}


