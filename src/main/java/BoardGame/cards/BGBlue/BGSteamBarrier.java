package BoardGame.cards.BGBlue;
import BoardGame.actions.BGGainBlockIfDiscardCostsZeroAction;
import BoardGame.actions.BGGainBlockIfShivAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGDefect;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGSteamBarrier extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGSteamBarrier");
    public static final String ID = "BGSteamBarrier";

    public BGSteamBarrier() {
        super("BGSteamBarrier", cardStrings.NAME, "blue/skill/steam_barrier", 0, cardStrings.DESCRIPTION, CardType.SKILL, BGDefect.Enums.BG_BLUE, CardRarity.COMMON, CardTarget.SELF);
        this.baseBlock = 1;
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }

    protected void applyPowersToBlock() {
        super.applyPowersToBlock();
        float tmp=this.baseMagicNumber;
        for (AbstractPower p : AbstractDungeon.player.powers)
            tmp = p.modifyBlock(tmp, this);
        for (AbstractPower p : AbstractDungeon.player.powers)
            tmp = p.modifyBlockLast(tmp);
        if (this.baseMagicNumber != MathUtils.floor(tmp))
            this.isMagicNumberModified = true;
        if (tmp < 0.0F)
            tmp = 0.0F;
        this.magicNumber = MathUtils.floor(tmp);
    }

    public void triggerOnGlowCheck() {
        this
                .glowColor = shouldGlow() ? AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy() : AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
    }

    private boolean shouldGlow() {
        if(!AbstractDungeon.player.discardPile.group.isEmpty()) {
            AbstractCard c = AbstractDungeon.player.discardPile.group.get(AbstractDungeon.player.discardPile.group.size() - 1);
            if (c.cost == 0)
                return true;
        }
        return false;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, this.block));
        addToBot((AbstractGameAction)new BGGainBlockIfDiscardCostsZeroAction(this.magicNumber));
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGSteamBarrier();
    }
}

