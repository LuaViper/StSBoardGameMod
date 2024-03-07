
package BoardGame.actions;

import BoardGame.cards.BGRed.BGWhirlwind;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGReinforcedBodyAction
        extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(BGWhirlwind.class.getName());
    public int[] multiDamage;
    private boolean dontExpendResources = false;
    private int energyOnUse = -1;
    private boolean upgraded=false;

    private DamageInfo.DamageType damageType;

    private AbstractPlayer p;

    int blockBonus=0;

    public BGReinforcedBodyAction(AbstractPlayer p, boolean dontExpendResources, int energyOnUse, boolean upgraded, int blockBonus) {
        this.p = p;
        this.dontExpendResources = dontExpendResources;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.energyOnUse = energyOnUse;
        this.upgraded=upgraded;
        this.blockBonus=blockBonus;
    }


    public void update() {
        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }
        if(!this.upgraded) {
            if(effect<=0) {
                this.isDone = true;
                return;
            }else{
                effect=effect+1;
            }
        }
        effect=effect+blockBonus;

        //TODO LATER: allow card to be played for 0 if player has Chemical X relic (requires changing canUse checks as well)
        if (this.p.hasRelic("Chemical X")) {
            effect += 2;
            this.p.getRelic("Chemical X").flash();
        }

        if (effect > 0) {
            addToTop((AbstractGameAction)new GainBlockAction(p,effect));
            if(upgraded)
                addToTop((AbstractGameAction)new GainBlockAction(p,effect));
            if (!this.dontExpendResources) {
                this.p.energy.use(this.energyOnUse);
            }
        }else{
            //do nothing; this is not an Attack card so we don't need to WEAKVULN
        }
        this.isDone = true;
    }
}


