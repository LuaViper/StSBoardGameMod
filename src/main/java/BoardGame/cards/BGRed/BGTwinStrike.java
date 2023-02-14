package BoardGame.cards.BGRed;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BGTwinStrike extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGTwin Strike");
    public static final String ID = "BGTwin Strike";
    private boolean isFirstAttack;
    public BGTwinStrike() {
        super("BGTwin Strike", cardStrings.NAME, "red/attack/twin_strike", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.ENEMY);


        this.isFirstAttack=false;

        this.baseDamage = 1;
        this.baseMagicNumber=0;
        this.magicNumber = this.baseMagicNumber;
        this.tags.add(AbstractCard.CardTags.STRIKE);
    }


    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        if(this.isFirstAttack) this.baseDamage += this.magicNumber;
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }



    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
//        final Logger logger = LogManager.getLogger(BoardGame.class.getName());
//        logger.info("BGTwinStrike Damage:"+this.damage);
//        logger.info("BGTwinStrike Magic Number:"+this.magicNumber);
//        logger.info("BGTwinStrike First Attack:"+this.isFirstAttack);
        if(this.isFirstAttack) {
            this.baseDamage += this.magicNumber;
        }
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        //int hit1=this.damage+this.magicNumber;

        this.isFirstAttack=true;
        int realDamage=this.damage;
        this.damage += this.magicNumber;
        calculateCardDamage(m);
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        this.isFirstAttack=false;
        this.damage = realDamage;
        calculateCardDamage(m);
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            //upgradeMagicNumber(1);
            //this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            //initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGTwinStrike();
    }
}


