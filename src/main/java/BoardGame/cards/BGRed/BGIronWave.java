package BoardGame.cards.BGRed;
import BoardGame.actions.ChooseOneAttackAction;
import BoardGame.cards.AbstractAttackCardChoice;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
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

import java.util.ArrayList;

//TODO: make sure this doesn't crash if played targetless via Havoc etc
//TODO: can we drop the Naive keyword if we use the SecondMagicNumber property or whatever?
public class BGIronWave extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGIron Wave");
    public static final String ID = "BGIron Wave";

    public BGIronWave() {
        super("BGIron Wave", cardStrings.NAME, "red/attack/iron_wave", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.ENEMY);

        this.baseDamage = 1;
        this.baseBlock = 1;
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
            ArrayList<AbstractAttackCardChoice> attackChoices = new ArrayList<>();
            attackChoices.add(new BGIronWaveSpear());
            attackChoices.add(new BGIronWaveShield());
            addToBot((AbstractGameAction)new ChooseOneAttackAction(attackChoices,p,m));
        }
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            //upgradeDamage(1);
            //upgradeBlock(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }


    public AbstractCard makeCopy() {
        return new BGIronWave();
    }
}


