package CoopBoardGame.actions;

import CoopBoardGame.orbs.BGLightning;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class BGTempestAction extends AbstractGameAction {

    public int[] multiDamage;
    private boolean dontExpendResources = false;
    private int energyOnUse = -1;
    private int extrahits = 0;

    private AbstractPlayer p;

    public BGTempestAction(
        AbstractPlayer p,
        AbstractMonster m,
        int damage,
        DamageInfo.DamageType damageTypeForTurn,
        boolean dontExpendResources,
        int energyOnUse,
        int extrahits
    ) {
        this.p = p;
        this.dontExpendResources = dontExpendResources;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.energyOnUse = energyOnUse;
        this.extrahits = extrahits;
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
                BGLightning lightning = new BGLightning();
                addToTop((AbstractGameAction) new BGChannelAction((AbstractOrb) lightning));
            }
            if (!this.dontExpendResources) {
                this.p.energy.use(this.energyOnUse);
            }
        } else {
            //do nothing; this is not an Attack card so we don't need to WEAKVULN
        }
        this.isDone = true;
    }
}
