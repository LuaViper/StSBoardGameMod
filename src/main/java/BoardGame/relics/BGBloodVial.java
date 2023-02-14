package BoardGame.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGBloodVial extends AbstractBGRelic {
    public static final String ID = "BGBlood Vial";

    public BGBloodVial() {
        super("BGBlood Vial", "blood_vial.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.CLINK);
    }
    public int getPrice() {return 6;}
    private static final int HEAL_AMOUNT = 1;

    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }


    public void atBattleStart() {
        flash();
        addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
        addToTop((AbstractGameAction)new HealAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)AbstractDungeon.player, 1, 0.0F));
    }


    public AbstractRelic makeCopy() {
        return new BGBloodVial();
    }
}


