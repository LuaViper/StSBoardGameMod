package BoardGame.cards.BGPurple;
import BoardGame.actions.BGWorshipAction;
import BoardGame.actions.BGXCostCardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.actions.watcher.NotStanceCheckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.EmptyStanceEffect;

public class BGEmptyMind extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGEmptyMind");
    public static final String ID = "BGEmptyMind";

    public BGEmptyMind() {
        super("BGEmptyMind", cardStrings.NAME, "purple/skill/empty_mind", 1, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.UNCOMMON, CardTarget.SELF);

        this.baseMagicNumber=2;
        this.magicNumber=this.baseMagicNumber;
    }


    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new DrawCardAction(this.magicNumber));
        addToBot((AbstractGameAction)new NotStanceCheckAction("Neutral", (AbstractGameAction)new VFXAction((AbstractGameEffect)new EmptyStanceEffect(p.hb.cX, p.hb.cY), 0.1F)));
        addToBot((AbstractGameAction)new ChangeStanceAction("Neutral"));
    }


    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }


    public AbstractCard makeCopy() {
        return new BGEmptyMind();
    }
}


