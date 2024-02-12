package BoardGame.cards.BGGreen;

import BoardGame.actions.BGFlechetteAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGBackstab extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGBackstab");
    public static final String ID = "BGBackstab";

    private AbstractMonster target;

    static Logger logger = LogManager.getLogger(BGBackstab.class.getName());

    public BGBackstab() {
        super("BGBackstab", cardStrings.NAME, "green/attack/backstab", 0, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ENEMY);
        this.baseDamage = 2;
        this.baseMagicNumber=2;
        this.magicNumber=this.baseMagicNumber;
        this.exhaust = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        this.target=m;
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
        }
    }

    public AbstractCard makeCopy() {
        return new BGBackstab();
    }


    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    public int IsTargetAtFullHP(AbstractMonster mo){
        return (mo.currentHealth>=mo.maxHealth) ? 1 : 0;
    }

    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * IsTargetAtFullHP(mo);
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void update() {
        super.update();
        if(AbstractDungeon.player!=null) {
            AbstractMonster mo = ReflectionHacks.getPrivate(AbstractDungeon.player, AbstractPlayer.class, "hoveredMonster");
            if(mo==null){
                this.target=null;
                this.applyPowers();
            }
        }
    }
}

