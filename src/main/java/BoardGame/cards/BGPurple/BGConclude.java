package BoardGame.cards.BGPurple;
import BoardGame.actions.BGCrushJointsAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import BoardGame.powers.BGConclusionPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;

public class BGConclude extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGConclude");
    public static final String ID = "BGConclude";

    public BGConclude() {
        super("BGConclude", cardStrings.NAME, "purple/attack/conclude", 1, cardStrings.DESCRIPTION, CardType.ATTACK, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);

        this.baseDamage=1;
        this.baseMagicNumber=2;
        this.magicNumber=this.baseMagicNumber;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        for(int i=0;i<this.magicNumber;i+=1) {
            addToBot((AbstractGameAction) new SFXAction("ATTACK_HEAVY"));
            addToBot((AbstractGameAction) new VFXAction((AbstractCreature) p, (AbstractGameEffect) new CleaveEffect(), 0.1F));
            addToBot((AbstractGameAction)new DamageAllEnemiesAction((AbstractCreature)p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));
        }
        addToBot((AbstractGameAction)new ApplyPowerAction(p, p, new BGConclusionPower(p, 0), 0));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public AbstractCard makeCopy() {
        return new BGConclude();
    }
}



