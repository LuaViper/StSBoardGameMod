package BoardGame.cards.BGBlue;
import BoardGame.actions.BGBeamCellConditionalVulnAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import BoardGame.orbs.BGDark;
import BoardGame.orbs.BGFrost;
import BoardGame.powers.BGVulnerablePower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGColdSnap extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGColdSnap");
    public static final String ID = "BGColdSnap";


    public BGColdSnap() {
        super("BGColdSnap", cardStrings.NAME, "blue/attack/cold_snap", 2, cardStrings.DESCRIPTION, CardType.ATTACK, BGDefect.Enums.BG_BLUE, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseDamage = 2;
    }




    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        addToBot((AbstractGameAction)new ChannelAction((AbstractOrb)new BGFrost()));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGColdSnap();
    }
}

