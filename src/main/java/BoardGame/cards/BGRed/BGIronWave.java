package BoardGame.cards.BGRed;
import BoardGame.actions.BGChooseOneAttackAction;
import BoardGame.cards.AbstractBGAttackCardChoice;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import com.badlogic.gdx.math.MathUtils;
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
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.IronWaveEffect;

import java.util.ArrayList;

//TODO: make sure this doesn't crash if played targetless via Havoc etc
public class BGIronWave extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGIron Wave");
    public static final String ID = "BGIron Wave";

    public BGIronWave() {
        super("BGIron Wave", cardStrings.NAME, "red/attack/iron_wave", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.ENEMY);

        this.baseDamage = 1;
        this.baseBlock = 1;
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
        this.defaultBaseSecondMagicNumber=1;
        this.defaultSecondMagicNumber=this.defaultBaseSecondMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!this.upgraded) {
            addToBot((AbstractGameAction) new DamageAction((AbstractCreature) m, new DamageInfo((AbstractCreature) p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
            addToBot((AbstractGameAction) new WaitAction(0.1F));
            if (p != null && m != null) {
                addToBot((AbstractGameAction) new VFXAction((AbstractGameEffect) new IronWaveEffect(p.hb.cX, p.hb.cY, m.hb.cX), 0.5F));
            }
            addToBot((AbstractGameAction) new GainBlockAction((AbstractCreature) p, (AbstractCreature) p, this.block));
        }else{
            //TODO: can we get BGIronWaveShield and BGIronWaveSpear to apply their modifiers to the targeted enemy?
            ArrayList<AbstractBGAttackCardChoice> attackChoices = new ArrayList<>();
            BGIronWaveSpear spear=new BGIronWaveSpear();
            spear.applyPowers();
            spear.calculateCardDamage(m);
            BGIronWaveShield shield=new BGIronWaveShield();
            shield.applyPowers();
            shield.calculateCardDamage(m);
            attackChoices.add(spear);
            attackChoices.add(shield);
            addToBot((AbstractGameAction)new BGChooseOneAttackAction(attackChoices,p,m));
        }
    }

    public void applyPowers() {
        super.applyPowers();
        int actualBaseDamage=baseDamage;
        int actualDamage=damage;
        boolean actualIsDamageModified=isDamageModified;

        baseDamage=defaultBaseSecondMagicNumber;

        super.applyPowers();
        defaultSecondMagicNumber=damage;
        isDefaultSecondMagicNumberModified=isDamageModified;

        baseDamage=actualBaseDamage;
        damage=actualDamage;
        isDamageModified=isDamageModified;





    }


    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        int actualBaseDamage=baseDamage;
        int actualDamage=damage;
        boolean actualIsDamageModified=isDamageModified;

        baseDamage=defaultBaseSecondMagicNumber;

        super.calculateCardDamage(mo);
        defaultSecondMagicNumber=damage;
        isDefaultSecondMagicNumberModified=isDamageModified;

        baseDamage=actualBaseDamage;
        damage=actualDamage;
        isDamageModified=isDamageModified;

    }

    protected void applyPowersToBlock() {
        super.applyPowersToBlock();
        float tmp=this.baseMagicNumber;
        for (AbstractPower p : AbstractDungeon.player.powers)
            tmp = p.modifyBlock(tmp, this);
        for (AbstractPower p : AbstractDungeon.player.powers)
            tmp = p.modifyBlockLast(tmp);
        if (this.baseMagicNumber != MathUtils.floor(tmp))
            this.isMagicNumberModified = true;
        if (tmp < 0.0F)
            tmp = 0.0F;
        this.magicNumber = MathUtils.floor(tmp);
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            upgradeBlock(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGIronWave();
    }
}


