package BoardGame.cards.BGGreen;

import BoardGame.actions.BGGainBlockIfDiscardAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGSilent;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.unique.EscapePlanAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGBlur extends AbstractBGCard {
    public static final String ID = "BGBlur";

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGBlur");

    public BGBlur() {
        super("BGBlur", cardStrings.NAME, "green/skill/blur", 1, cardStrings.DESCRIPTION, AbstractCard.CardType.SKILL, BGSilent.Enums.BG_GREEN, AbstractCard.CardRarity.UNCOMMON, AbstractCard.CardTarget.SELF);
        this.baseBlock = 2;
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, this.block));
        addToBot((AbstractGameAction)new BGGainBlockIfDiscardAction(this.magicNumber));
    }

    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (GameActionManager.totalDiscardedThisTurn > 0)
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();

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

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGBlur();
    }
}



