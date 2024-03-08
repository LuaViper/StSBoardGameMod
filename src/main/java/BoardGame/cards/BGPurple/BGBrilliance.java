package BoardGame.cards.BGPurple;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import BoardGame.powers.BGConclusionPower;
import BoardGame.powers.WeakVulnCancel;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

//TODO: are any other for-loop multi-hit cards missing WEAKVULN_ZEROHITS?

public class BGBrilliance extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGBrilliance");
    public static final String ID = "BGBrilliance";

    public BGBrilliance() {
        super("BGBrilliance", cardStrings.NAME, "purple/attack/brilliance", 1, cardStrings.DESCRIPTION, CardType.ATTACK, BGWatcher.Enums.BG_PURPLE, CardRarity.RARE, CardTarget.ENEMY);

        this.baseDamage=2;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        //TODO: do we need to move this to an action since a relic is involved??
        int count=0;
        AbstractRelic r = p.getRelic("BoardGame:BGMiracles");
        if (r != null) {
            count=r.counter;
        }

        if(count>0){
            for (int i = 0; i < count; i += 1) {
                addToBot((AbstractGameAction) new DamageAction((AbstractCreature) m, new DamageInfo((AbstractCreature) p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
                addToBot((AbstractGameAction) new WaitAction(0.15F));
            }
        }else{
            addToBot((AbstractGameAction) new DamageAction((AbstractCreature) m, new DamageInfo((AbstractCreature) p, 0, WeakVulnCancel.WEAKVULN_ZEROHITS), AbstractGameAction.AttackEffect.NONE));
        }

    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGBrilliance();
    }
}



