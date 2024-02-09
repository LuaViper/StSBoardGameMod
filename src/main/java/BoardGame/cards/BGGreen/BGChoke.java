package BoardGame.cards.BGGreen;
import BoardGame.actions.BGPainAction;
import BoardGame.characters.BGSilent;
import BoardGame.cards.AbstractBGCard;
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
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGChoke extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGChoke");
    public static final String ID = "BGChoke";

    private AbstractMonster target;

    static Logger logger = LogManager.getLogger(BGChoke.class.getName());
    public BGChoke() {
        super("BGChoke", cardStrings.NAME, "green/attack/choke", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ENEMY);



        this.baseDamage = 3;
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

    public static int countTargetDebuffs(AbstractMonster target){
        //logger.info("Choke countTargetDebuffs target "+target);
        if(target==null)return 0;
        AbstractPower weak=target.getPower("BGWeakened");
        AbstractPower poison=target.getPower("BGPoisonPower");
        int total=0;
        //logger.info("Weak:"+weak);
        if(weak!=null && weak.amount>0){        //strictly speaking >0 check shouldn't be necessary for weak/poison.  in theory.
            total+=weak.amount;
        }
        if (poison != null && poison.amount > 0) {
            total+=poison.amount;
        }
        return total;
    }
    public void use(AbstractPlayer p, AbstractMonster m) {
        //logger.info("Choke use");
        this.target=m;
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));
//        if(this.target!=null) {
//            this.rawDescription = cardStrings.DESCRIPTION;
//        }
//        initializeDescription();
    }

    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        //this.baseDamage += this.magicNumber * 0;

        super.applyPowers();

        this.baseDamage = realBaseDamage;

        this.isDamageModified = (this.damage != this.baseDamage);


    }


    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * countTargetDebuffs(mo);

        super.calculateCardDamage(mo);

        this.baseDamage = realBaseDamage;

        this.isDamageModified = (this.damage != this.baseDamage);

    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
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

    public AbstractCard makeCopy() {
        return new BGChoke();
    }
}


