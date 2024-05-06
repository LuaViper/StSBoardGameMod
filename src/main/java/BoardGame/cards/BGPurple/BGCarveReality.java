package BoardGame.cards.BGPurple;
import BoardGame.actions.CheckAfterUseCardAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

//TODO: if first target is killed AND only one target remains, targetscreen autoselects!

public class BGCarveReality extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGCarveReality");
    public static final String ID = "BGCarveReality";

    public BGCarveReality() {
        super("BGCarveReality", cardStrings.NAME, "purple/attack/carve_reality", 2, cardStrings.DESCRIPTION, CardType.ATTACK, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.ENEMY);

        this.baseDamage=3;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DamageAction((AbstractCreature)m, new DamageInfo((AbstractCreature)p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));
        TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
            if(target!=m) {
                UseCardAction fakeShivAction = new UseCardAction(this, target);
                this.calculateCardDamage(target);
                addToTop((AbstractGameAction) new CheckAfterUseCardAction(this, fakeShivAction));
                addToTop((AbstractGameAction) new DamageAction((AbstractCreature) target, new DamageInfo((AbstractCreature) AbstractDungeon.player, this.damage, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                //reminder: order of operations is now DamageAction -> (proc weak/vuln) -> CheckAfterUseCardAction
            }
        };
        //TODO: localization
        addToBot((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Left-click second target, or right-click to pass.", true));
    }



    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGCarveReality();
    }
}



