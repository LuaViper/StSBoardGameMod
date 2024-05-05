package BoardGame.cards.BGPurple;

import BoardGame.actions.BGHaltAction;
import BoardGame.cards.AbstractBGCard;
import BoardGame.characters.BGWatcher;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGHalt extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGHalt");
    public static final String ID = "BGHalt";

    public BGHalt() {
        super("BGHalt", cardStrings.NAME, "purple/skill/halt", 0, cardStrings.DESCRIPTION, CardType.SKILL, BGWatcher.Enums.BG_PURPLE, CardRarity.COMMON, CardTarget.SELF);

        this.baseBlock=1;
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new BGHaltAction((AbstractCreature)p, this.block, this.magicNumber));
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
            upgradeMagicNumber(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGHalt();
    }
}



