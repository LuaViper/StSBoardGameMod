package CoopBoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
public class BGWreathOfFlameAction extends AbstractGameAction {

    private boolean dontExpendResources = false;
    private int energyOnUse = -1;
    private final int EXTRA_HITS = 0;

    private AbstractPlayer p;

    public BGWreathOfFlameAction(AbstractPlayer p, boolean dontExpendResources, int energyOnUse) {
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
        effect += this.EXTRA_HITS;

        if (this.p.hasRelic("Chemical X")) {
            effect += 2;
            this.p.getRelic("Chemical X").flash();
        }

        if (effect > 0) {
            addToTop(new GainTemporaryStrengthIfNotCappedAction(p, effect));

            if (!this.dontExpendResources) {
                this.p.energy.use(this.energyOnUse);
            }
        }

        this.isDone = true;
    }
}
