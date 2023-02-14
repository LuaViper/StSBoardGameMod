package BoardGame.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class BGLantern
        extends AbstractBGRelic {
    public static final String ID = "BGLantern";

    public BGLantern() {
        super("BGLantern", "lantern.png", AbstractRelic.RelicTier.COMMON, AbstractRelic.LandingSound.SOLID);
        this.energyBased = true;
    }
    public int getPrice() {return 6;}
    private static final int ENERGY_AMT = 1; private boolean firstTurn = true;

    public String getUpdatedDescription() {
        if (AbstractDungeon.player != null) {
            return setDescription(AbstractDungeon.player.chosenClass);
        }
        return setDescription((AbstractPlayer.PlayerClass)null);
    }


    private String setDescription(AbstractPlayer.PlayerClass c) {
        return this.DESCRIPTIONS[0] + this.DESCRIPTIONS[1] + LocalizedStrings.PERIOD;
    }


    public void updateDescription(AbstractPlayer.PlayerClass c) {
        this.description = setDescription(c);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }


    public void atPreBattle() {
        this.firstTurn = true;
    }


    public void atTurnStart() {
        if (this.firstTurn) {
            flash();
            addToTop((AbstractGameAction)new GainEnergyAction(1));
            addToTop((AbstractGameAction)new RelicAboveCreatureAction((AbstractCreature)AbstractDungeon.player, this));
            this.firstTurn = false;
        }
    }


    public AbstractRelic makeCopy() {
        return new BGLantern();
    }
}


