package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.ShakeScreenAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.DieDieDieEffect;

public class BGDieDieDie extends AbstractBGCard {
    public static final String ID = "BGDieDieDie";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("Die Die Die");

    public BGDieDieDie() {
        super("BGDieDieDie", cardStrings.NAME, "green/attack/die_die_die", 1, cardStrings.DESCRIPTION, CardType.ATTACK, BGSilent.Enums.BG_GREEN, CardRarity.RARE, CardTarget.ALL_ENEMY);
        this.exhaust = true;
        this.baseDamage = 3;
        this.isMultiDamage = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new BorderLongFlashEffect(Color.LIGHT_GRAY)));
        addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new DieDieDieEffect(), 0.7F));
        addToBot((AbstractGameAction)new ShakeScreenAction(0.0F, ScreenShake.ShakeDur.MED, ScreenShake.ShakeIntensity.HIGH));
        addToBot((AbstractGameAction)new DamageAllEnemiesAction((AbstractCreature)p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGDieDieDie();
    }
}


