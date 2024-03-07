
package BoardGame.actions;

import BoardGame.cards.BGRed.BGWhirlwind;
import BoardGame.powers.WeakVulnCancel;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGSkewerAction
        extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(BGWhirlwind.class.getName());
    public int[] multiDamage;
    private boolean dontExpendResources = false;
    private int energyOnUse = -1;
    private int extrahits=0;

    private DamageInfo.DamageType damageType;

    private AbstractPlayer p;
    private AbstractMonster m;
    private int damage;
    private DamageInfo.DamageType damageTypeForTurn;


    public BGSkewerAction(AbstractPlayer p, AbstractMonster m, int damage, DamageInfo.DamageType damageTypeForTurn, boolean dontExpendResources, int energyOnUse, int extrahits) {
        this.multiDamage = multiDamage;
        this.damageType = damageType;
        this.p = p;
        this.m=m;
        this.damage = damage;
        this.dontExpendResources = dontExpendResources;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.damageTypeForTurn = damageTypeForTurn;
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

        if (effect > 0) {
            for (int i = effect - 1; i >= 0; i--) {
                //damage needs to be addToTop instead of addToBot, otherwise weakvuln checks will fail
                addToTop((AbstractGameAction)new DamageAction((AbstractCreature)this.m, new DamageInfo((AbstractCreature)this.p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
            }
            if (!this.dontExpendResources) {
                this.p.energy.use(this.energyOnUse);
            }
        }else{
            addToTop((AbstractGameAction)new DamageAction((AbstractCreature)this.m, new DamageInfo((AbstractCreature)this.p, 0, WeakVulnCancel.WEAKVULN_ZEROHITS), AbstractGameAction.AttackEffect.NONE));
        }
        this.isDone = true;
    }
}


