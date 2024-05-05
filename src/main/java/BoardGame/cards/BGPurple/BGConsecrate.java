package BoardGame.cards.BGPurple;

import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

public class BGConsecrate extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGConsecrate");
    public static final String ID = "BGConsecrate";

    public BGConsecrate() {
        super("BGConsecrate", cardStrings.NAME, "purple/attack/consecrate", 0, cardStrings.DESCRIPTION, CardType.ATTACK, BGWatcher.Enums.BG_PURPLE, CardRarity.COMMON, CardTarget.ALL_ENEMY);
        this.baseDamage = 1;
        this.isMultiDamage = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new SFXAction("ATTACK_HEAVY"));
        addToBot((AbstractGameAction)new VFXAction((AbstractCreature)p, (AbstractGameEffect)new CleaveEffect(), 0.1F));
        addToBot((AbstractGameAction)new DamageAllEnemiesAction((AbstractCreature)p,
                this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGConsecrate();
    }
}

