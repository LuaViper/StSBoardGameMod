package BoardGame.relics;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BGDuVuDoll extends AbstractBGRelic {
    public static final String ID = "BGDu-Vu Doll";

    public BGDuVuDoll() {
        super("BGDu-Vu Doll", "duvuDoll.png", AbstractRelic.RelicTier.RARE, AbstractRelic.LandingSound.MAGICAL);
    }
    public int getPrice() {return 7;}
    private static final int STR_AMT = 1;

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void onCardDraw(AbstractCard card) {
        if (card.type == AbstractCard.CardType.CURSE) {
            flash();
            addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new StrengthPower((AbstractCreature)AbstractDungeon.player, STR_AMT), STR_AMT));
            addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new LoseStrengthPower((AbstractCreature)AbstractDungeon.player, STR_AMT), STR_AMT));
            addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        }
    }


    public AbstractRelic makeCopy() {
        return new BGDuVuDoll();
    }
}


