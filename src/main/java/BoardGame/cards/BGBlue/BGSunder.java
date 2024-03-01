package BoardGame.cards.BGBlue;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import BoardGame.relics.BGTheDieRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.defect.SunderAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;

public class BGSunder extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGSunder");
    public static final String ID = "BGSunder";

    public BGSunder() {
        super("BGSunder", cardStrings.NAME, "blue/attack/sunder", 3, cardStrings.DESCRIPTION, CardType.ATTACK, BGDefect.Enums.BG_BLUE, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.baseDamage = 5;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            addToBot((AbstractGameAction)new VFXAction((AbstractGameEffect)new WeightyImpactEffect(m.hb.cX, m.hb.cY)));
            addToBot((AbstractGameAction)new WaitAction(0.8F));
        }
        addToBot((AbstractGameAction)new SunderAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), 3));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
        }
    }

    public AbstractCard makeCopy() {
        return new BGSunder();
    }
}
