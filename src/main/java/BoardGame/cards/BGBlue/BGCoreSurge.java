package BoardGame.cards.BGBlue;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import BoardGame.orbs.BGLightning;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class BGCoreSurge extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGCoreSurge");
    public static final String ID = "BGCoreSurge";


    public BGCoreSurge() {
        super("BGCoreSurge", cardStrings.NAME, "blue/attack/core_surge", 1, cardStrings.DESCRIPTION, CardType.ATTACK, BGDefect.Enums.BG_BLUE, CardRarity.RARE, CardTarget.ENEMY);
        this.baseDamage = 3;
        this.selfRetain = true;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)p, (AbstractCreature)p, "BGWeakened"));
        addToBot((AbstractGameAction)new RemoveSpecificPowerAction((AbstractCreature)p, (AbstractCreature)p, "BGVulnerable"));
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGCoreSurge();
    }
}
