package BoardGame.cards.BGPurple;
import BoardGame.actions.BGBouncingFlaskAction;
import BoardGame.actions.TargetSelectScreenAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import BoardGame.powers.WeakVulnCancel;
import BoardGame.screen.TargetSelectScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import com.megacrit.cardcrawl.vfx.combat.PotionBounceEffect;

//TODO: does Ragnarok decrease its damage correctly if it was initially targeted on a Vulnerable enemy before switching targets?
public class BGRagnarok extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGRagnarok");
    public static final String ID = "BGRagnarok";

    public BGRagnarok() {
        super("BGRagnarok", cardStrings.NAME, "purple/attack/ragnarok", 3, cardStrings.DESCRIPTION, CardType.ATTACK, BGWatcher.Enums.BG_PURPLE, CardRarity.RARE, CardTarget.ENEMY);

        this.baseDamage=1;
        baseMagicNumber=4;  //5 hits total; 1st hit from playing the targeted card
        magicNumber=baseMagicNumber;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        followupAttack(m);
        for(int i=0;i<magicNumber;i+=1) {
            TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                followupAttack(target);
            };
            addToBot((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose the next target for Ragnarok."));
        }
    }

    public void followupAttack(AbstractMonster t) {
        if (t != null) {
            this.calculateCardDamage((AbstractMonster) t);

            addToTop(new DamageAction(t, new DamageInfo((AbstractCreature) AbstractDungeon.player, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.NONE));
            addToTop((AbstractGameAction) new SFXAction("ORB_LIGHTNING_EVOKE", 0.1F));
            addToTop((AbstractGameAction) new VFXAction((AbstractGameEffect) new LightningEffect(t.hb.cX, t.hb.cY)));

        }
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
        return new BGRagnarok();
    }
}



