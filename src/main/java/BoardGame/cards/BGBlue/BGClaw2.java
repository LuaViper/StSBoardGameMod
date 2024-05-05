package BoardGame.cards.BGBlue;

import BoardGame.actions.BGClawAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.ClawEffect;

//TODO: we've fixed BGClawAction to work on cards in limbo, but it might still not be compatible with Blasphemy 3x

public class BGClaw2 extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGClaw2");
    public static final String ID = "BGClaw2";


    public BGClaw2() {
        super("BGClaw2", cardStrings.NAME, "blue/attack/claw", 0, cardStrings.DESCRIPTION, CardType.ATTACK, BGDefect.Enums.BG_BLUE, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseDamage = 1;
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }


    public static int getClawPackCount(){
        int c=Math.round(BoardGame.BoardGame.clawPackCount);
        if(c<2)c=0;
        if(c>8)c=8;
        return c;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null)
            addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new ClawEffect(m.hb.cX, m.hb.cY, Color.CYAN, Color.WHITE), 0.1F));
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.NONE));
        addToBot((AbstractGameAction)new BGClawAction(this, this.magicNumber));


    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }



    public AbstractCard makeCopy() {
        return new BGClaw2();
    }
}

