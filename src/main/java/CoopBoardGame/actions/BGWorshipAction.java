package CoopBoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class BGWorshipAction extends AbstractGameAction {

    private boolean dontExpendResources = false;
    private int energyOnUse = -1;
    private final int EXTRA_HITS = 1;
    private AbstractCard card;

    private AbstractPlayer p;

    public BGWorshipAction(
        AbstractPlayer p,
        boolean dontExpendResources,
        int energyOnUse,
        AbstractCard card
    ) {
        this.p = p;
        this.dontExpendResources = dontExpendResources;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.energyOnUse = energyOnUse;
        this.card = card;
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
            for (int i = effect - 1; i >= 0; i--) {
                addToTop((AbstractGameAction) new BGGainMiracleAction(1, card));
            }
            //logger.info("BGWhirlwindAction: subtract energy "+this.energyOnUse);
            if (!this.dontExpendResources) {
                this.p.energy.use(this.energyOnUse);
            }
        }

        this.isDone = true;
    }
}
