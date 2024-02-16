package BoardGame.cards.BGGreen;

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
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGBane extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGBane");
    public static final String ID = "BGBane";

    private AbstractMonster target;

    static Logger logger = LogManager.getLogger(BGBane.class.getName());
    public BGBane() {
        super("BGBane", cardStrings.NAME, "green/attack/bane", 1, cardStrings.DESCRIPTION, CardType.ATTACK, BGSilent.Enums.BG_GREEN, CardRarity.UNCOMMON, CardTarget.ENEMY);



        this.baseDamage = 2;
        this.baseMagicNumber = 2;
        this.magicNumber = this.baseMagicNumber;
    }

    public static int checkTargetPoison(AbstractMonster target){
        if(target==null)return 0;
        AbstractPower poison=target.getPower("BGPoison");
        int total=0;
        if (poison != null && poison.amount > 0) {
            total=1;
        }
        return total;
    }
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.target=m;
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        //this.baseDamage += this.magicNumber * 0;  //TODO: why did we have to comment this out again?

        super.applyPowers();

        this.baseDamage = realBaseDamage;

        this.isDamageModified = (this.damage != this.baseDamage);
    }


    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * checkTargetPoison(mo);

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
        return new BGBane();
    }
}


