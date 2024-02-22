package BoardGame.cards.BGRed;

import BoardGame.cards.AbstractBGAttackCardChoice;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.IronWaveEffect;

public class BGIronWaveSpear extends AbstractBGAttackCardChoice {
    public static final String ID = "BGIronWaveSpear";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGIronWaveSpear");

    public BGIronWaveSpear() {
        super("BGIronWaveSpear", cardStrings.NAME, "colorless/skill/wrath", -2, cardStrings.DESCRIPTION, AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.NONE);
        this.baseDamage = 2;
        this.baseBlock = 1;
    }


//    public void setTargets(AbstractPlayer p, AbstractMonster m){
//        this.p=p;
//        this.m=m;
//    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        this.p=p;this.m=m;
        this.onChoseThisOption();
    }






    public void onChoseThisOption() {
        //final Logger logger = LogManager.getLogger(BoardGame.class.getName());
        //logger.info("BGIronWaveSpear: "+this.damage+" "+this.block);
        applyPowers();
        calculateCardDamage(this.m);
        addToTop((AbstractGameAction) new DamageAction((AbstractCreature) this.m, new DamageInfo((AbstractCreature) this.p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
        addToBot((AbstractGameAction) new WaitAction(0.1F));
        if (this.p != null && this.m != null) {
            addToBot((AbstractGameAction) new VFXAction((AbstractGameEffect) new IronWaveEffect(this.p.hb.cX, this.p.hb.cY, this.m.hb.cX), 0.5F));
        }
        addToBot((AbstractGameAction) new GainBlockAction((AbstractCreature) this.p, (AbstractCreature) this.p, this.block));
    }



    public void upgrade() {}


    public AbstractCard makeCopy() {
        return new BGIronWaveSpear();
    }
}


