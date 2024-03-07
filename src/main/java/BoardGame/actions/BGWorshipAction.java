
package BoardGame.actions;

import BoardGame.cards.BGRed.BGWhirlwind;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGWorshipAction
        extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(BGWhirlwind.class.getName());
    private boolean dontExpendResources = false;
    private int energyOnUse = -1;
    private static final int extrahits=1;


    private AbstractPlayer p;


    public BGWorshipAction(AbstractPlayer p, boolean dontExpendResources, int energyOnUse) {
        this.p = p;
        this.dontExpendResources = dontExpendResources;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.energyOnUse = energyOnUse;
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

        if (effect > 0) {
            for (int i = effect - 1; i >= 0; i--) {
                addToTop((AbstractGameAction) new BGGainMiracleAction(1));
            }
            //logger.info("BGWhirlwindAction: subtract energy "+this.energyOnUse);
            if (!this.dontExpendResources) {
                this.p.energy.use(this.energyOnUse);
            }
        }

        this.isDone = true;
    }
}


