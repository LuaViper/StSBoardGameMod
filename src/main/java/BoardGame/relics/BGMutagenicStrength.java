package BoardGame.relics;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;

public class BGMutagenicStrength extends AbstractBGRelic {
    public static final String ID = "BGMutagenicStrength";

    public BGMutagenicStrength() {
        super("BGMutagenicStrength", "mutagen.png", AbstractRelic.RelicTier.SPECIAL, AbstractRelic.LandingSound.CLINK);
    }
    public int getPrice() {return 6;}
    private static final int STR_AMT = 1;

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void atBattleStart() {
        flash();
        addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new StrengthPower((AbstractCreature)AbstractDungeon.player, STR_AMT), STR_AMT));
        addToTop((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, (AbstractPower)new LoseStrengthPower((AbstractCreature)AbstractDungeon.player, STR_AMT), STR_AMT));





        addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
    }


    public AbstractRelic makeCopy() {
        return new BGMutagenicStrength();
    }
}


