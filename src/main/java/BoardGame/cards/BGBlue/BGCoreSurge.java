package BoardGame.cards.BGBlue;

import BoardGame.actions.BGCoreSurgeAttackAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO: when playing solo, should this card's displayed damage ignore Weak?
public class BGCoreSurge extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGCoreSurge");
    public static final String ID = "BGCoreSurge";


    public BGCoreSurge() {
        super("BGCoreSurge", cardStrings.NAME, "blue/attack/core_surge", 1, cardStrings.DESCRIPTION, CardType.ATTACK, BGDefect.Enums.BG_BLUE, CardRarity.RARE, CardTarget.ENEMY);
        this.baseDamage = 3;
        this.selfRetain = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)p, (AbstractCreature)p, "BGWeakened"));
        addToBot((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)p, (AbstractCreature)p, "BGVulnerable"));
        //we can't recalculate card damage yet because RemoveSpecificPower hasn't run,
        // so we need a custom action to do it for us
        addToBot(new BGCoreSurgeAttackAction(this,p,m));
//        applyPowers();
//        calculateCardDamage(m);
//        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGCoreSurge();
    }
}
