
package BoardGame.actions;

import BoardGame.cards.BGRed.BGWhirlwind;
import BoardGame.powers.BGPoisonPower;
import BoardGame.powers.BGWeakPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGMalaiseAction
        extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(BGWhirlwind.class.getName());
    public int[] multiDamage;
    private boolean dontExpendResources = false;
    private int energyOnUse = -1;
    private int extrahits=0;

    private DamageInfo.DamageType damageType;

    private AbstractPlayer p;
    private AbstractCreature m;

    public BGMalaiseAction(AbstractPlayer p, AbstractCreature m, boolean dontExpendResources, int energyOnUse, int extrahits) {
        this.p = p;
        this.m=m;
        this.dontExpendResources = dontExpendResources;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.energyOnUse = energyOnUse;
        this.extrahits=extrahits;
    }


    public void update() {
        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }
        effect += this.extrahits;

        if (this.p.hasRelic("Chemical X")) {
            effect += 2;
            this.p.getRelic("Chemical X").flash();
        }

        if(effect>0) {
            addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.m, (AbstractCreature) this.p, (AbstractPower) new BGPoisonPower((AbstractCreature) this.m, this.p, effect), effect));
            addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.m, (AbstractCreature) this.p, (AbstractPower) new BGWeakPower((AbstractCreature) this.m, effect, false), effect));

            if (!this.dontExpendResources) {
                this.p.energy.use(this.energyOnUse);
            }
        }

        this.isDone = true;
    }
}


