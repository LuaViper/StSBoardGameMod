package BoardGame.cards.BGBlue;
import BoardGame.actions.BGBeamCellConditionalVulnAction;
import BoardGame.actions.BGGoForTheEyesConditionalWeakAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import BoardGame.powers.BGVulnerablePower;
import BoardGame.powers.BGWeakPower;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGGoForTheEyes extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGGoForTheEyes");
    public static final String ID = "BGGoForTheEyes";


    public BGGoForTheEyes() {
        super("BGGoForTheEyes", cardStrings.NAME, "blue/attack/go_for_the_eyes", 0, cardStrings.DESCRIPTION, CardType.ATTACK, BGDefect.Enums.BG_BLUE, CardRarity.COMMON, CardTarget.ENEMY);
        this.baseDamage = 1;
    }


    public void triggerOnGlowCheck() {
        this
                .glowColor = shouldGlow() ? AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy() : AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
    }

    private boolean shouldGlow() {
        if(!upgraded)
            if(TheDie.monsterRoll==4 || TheDie.monsterRoll==5 || TheDie.monsterRoll==6)
                return true;

        return false;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        if(this.upgraded)
            addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)p, (AbstractPower)new BGWeakPower((AbstractCreature)m, 1, false), 1));
        else
            addToBot((AbstractGameAction)new BGGoForTheEyesConditionalWeakAction(p,m));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGGoForTheEyes();
    }
}

