package BoardGame.cards.BGRed;

import BoardGame.cards.AbstractBGCard;
import BoardGame.cards.BGStatus.BGDazed;
import BoardGame.characters.BGIronclad;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.VerticalAuraEffect;

public class BGAnger extends AbstractBGCard {
    public static final String ID = "BGAnger";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGAnger");

    public BGAnger() {
        super("BGAnger", cardStrings.NAME, "red/attack/anger", 0, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.ENEMY);









        this.baseDamage = 1;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {


        //addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGInstantReboundPower((AbstractCreature)p), 1));
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        addToBot((AbstractGameAction)new VFXAction((AbstractCreature)p, (AbstractGameEffect)new VerticalAuraEffect(Color.FIREBRICK, p.hb.cX, p.hb.cY), 0.0F));
        //addToBot((AbstractGameAction)new MakeTempCardInDiscardAction(makeStatEquivalentCopy(), 1));   //vanilla anger - do not use

        //TODO: proper "isThisACopy" check
        if(!this.purgeOnUse) {
            this.purgeOnUse = true;
            AbstractCard copy = this.makeStatEquivalentCopy();
            addToBot(new MakeTempCardInDrawPileAction(copy, 1, false, true));
        }
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGAnger();
    }
}


