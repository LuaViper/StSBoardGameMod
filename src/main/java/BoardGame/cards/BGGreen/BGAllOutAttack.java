package BoardGame.cards.BGGreen;

import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGIronclad;
import BoardGame.characters.BGSilent;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

public class BGAllOutAttack extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGAllOutAttack");
    public static final String ID = "BGAllOutAttack";

    public BGAllOutAttack() {
        super("BGAllOutAttack", cardStrings.NAME, "green/attack/all_out_attack", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.ATTACK, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.ALL_ENEMY);
        this.baseDamage = 2;
        this.isMultiDamage = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAllEnemiesAction((AbstractCreature)p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.SLASH_HEAVY));
        addToBot((AbstractGameAction)new DiscardAction((AbstractCreature)p, (AbstractCreature)p, 1, false));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGAllOutAttack();
    }
}