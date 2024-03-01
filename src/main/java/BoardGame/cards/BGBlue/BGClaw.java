package BoardGame.cards.BGBlue;
import BoardGame.actions.BGGainBlockIfDiscardCostsZeroAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.ClawEffect;

public class BGClaw extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGClaw");
    public static final String ID = "BGClaw";


    public BGClaw() {
        super("BGClaw", cardStrings.NAME, "blue/attack/claw", 0, cardStrings.DESCRIPTION, CardType.ATTACK, BGDefect.Enums.BG_BLUE, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseDamage = 1;
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }






    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new ClawEffect(m.hb.cX, m.hb.cY, Color.CYAN, Color.WHITE), 0.1F));
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.NONE));

    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(2);
        }
    }


    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * IsTopDiscardZeroCost();
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    public int IsTopDiscardZeroCost(){
        if(!AbstractDungeon.player.discardPile.group.isEmpty()) {
            AbstractCard c = AbstractDungeon.player.discardPile.group.get(AbstractDungeon.player.discardPile.group.size() - 1);
            if (c.cost == 0)
                return 1;
        }
        return 0;
    }

    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * IsTopDiscardZeroCost();
        super.calculateCardDamage(mo);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    public AbstractCard makeCopy() {
        return new BGClaw();
    }
}

