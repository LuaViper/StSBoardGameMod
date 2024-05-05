package BoardGame.cards.BGPurple;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO: activated status (wasRetainedLastTurn) does not work with Play Twice effects. should it?
public class BGWindmillStrike extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGWindmillStrike");
    public static final String ID = "BGWindmillStrike";

    public BGWindmillStrike() {
        super("BGWindmillStrike", cardStrings.NAME, "purple/attack/windmill_strike", 2, cardStrings.DESCRIPTION, CardType.ATTACK, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.ENEMY);

        this.selfRetain=true;
        this.baseDamage=2;
        this.baseMagicNumber=3;
        this.magicNumber=this.baseMagicNumber;

        this.tags.add(AbstractCard.CardTags.STRIKE);
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        if(wasRetainedLastTurn) this.baseDamage += this.magicNumber;
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        if(wasRetainedLastTurn) this.baseDamage += this.magicNumber;
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }



    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(2);
        }
    }

    public AbstractCard makeCopy() {
        return new BGWindmillStrike();
    }
}



