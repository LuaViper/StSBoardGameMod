package BoardGame.cards.BGBlue;
import BoardGame.actions.BGGainBlockIfDiscardCostsZeroAction;
import BoardGame.actions.BGGainEnergyIfThreeOrbsAction;
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
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGChargeBattery extends AbstractBGCard {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGChargeBattery");
    public static final String ID = "BGChargeBattery";

    public BGChargeBattery() {
        super("BGChargeBattery", cardStrings.NAME, "blue/skill/charge_battery", 1, cardStrings.DESCRIPTION, CardType.SKILL, BGDefect.Enums.BG_BLUE, CardRarity.COMMON, CardTarget.SELF);
        this.baseBlock = 2;
        this.baseMagicNumber=1;
        this.magicNumber=this.baseMagicNumber;
    }

//  TODO: MAYBE glow if we have 3+ orbs

//    public void triggerOnGlowCheck() {
//        this
//                .glowColor = shouldGlow() ? AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy() : AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
//    }
//
//    private boolean shouldGlow() {
//        if(!AbstractDungeon.player.discardPile.group.isEmpty()) {
//            AbstractCard c = AbstractDungeon.player.discardPile.group.get(AbstractDungeon.player.discardPile.group.size() - 1);
//            if (c.cost == 0)
//                return true;
//        }
//        return false;
//    }



    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot((AbstractGameAction)new GainBlockAction((AbstractCreature)p, (AbstractCreature)p, this.block));
        addToBot((AbstractGameAction)new BGGainEnergyIfThreeOrbsAction());
    }

    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(1);
        }
    }

    public AbstractCard makeCopy() {
        return new BGChargeBattery();
    }
}

