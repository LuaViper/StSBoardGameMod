package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
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
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGSlice extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGSlice");
    public static final String ID = "BGSlice";

    private AbstractMonster target;

    static Logger logger = LogManager.getLogger(BGSlice.class.getName());
    public BGSlice() {
        super("BGSlice", cardStrings.NAME, "green/attack/slice", 0, cardStrings.DESCRIPTION, CardType.ATTACK, BGSilent.Enums.BG_GREEN, CardRarity.COMMON, CardTarget.ENEMY);



        this.baseDamage = 1;
        this.baseMagicNumber = 1;
        this.magicNumber = this.baseMagicNumber;
    }

    public static int checkShivs(){
        int total=0;
        AbstractRelic shivs=AbstractDungeon.player.getRelic("BoardGame:BGShivs");
        if(shivs!=null){
            if(shivs.counter>0) {
                total=1;
            }
        }

        return total;
    }
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.target=m;
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));

    }

    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * checkShivs();

        super.applyPowers();

        this.baseDamage = realBaseDamage;

        this.isDamageModified = (this.damage != this.baseDamage);


    }


    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * checkShivs();

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

//    @Override
//    public void update() {
//        super.update();
//        if(AbstractDungeon.player!=null) {
//            AbstractMonster mo = ReflectionHacks.getPrivate(AbstractDungeon.player, AbstractPlayer.class, "hoveredMonster");
//            if(mo==null){
//                this.target=null;
//                this.applyPowers();
//            }
//        }
//    }

    public AbstractCard makeCopy() {
        return new BGSlice();
    }
}


