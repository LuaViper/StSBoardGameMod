
package BoardGame.actions;

import BoardGame.cards.BGRed.BGWhirlwind;
import BoardGame.powers.BGConfusionPower;
import BoardGame.powers.WeakVulnCancel;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;
import com.megacrit.cardcrawl.vfx.combat.WhirlwindEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BGWhirlwindAction
        extends AbstractGameAction {

    private static final Logger logger = LogManager.getLogger(BGWhirlwind.class.getName());
    public int[] multiDamage;
    private boolean dontExpendResources = false;
    private int energyOnUse = -1;
    private int extrahits=0;

    private DamageInfo.DamageType damageType;

    private AbstractPlayer p;


    public BGWhirlwindAction(AbstractPlayer p, int[] multiDamage, DamageInfo.DamageType damageType, boolean dontExpendResources, int energyOnUse, int extrahits) {
        this.p = p;
        this.multiDamage=multiDamage;
        this.dontExpendResources = dontExpendResources;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.energyOnUse = energyOnUse;
        this.extrahits=extrahits;
        this.damageType = damageType;
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
                //as a consequence, actions are added in reverse order from the original card
                addToTop((AbstractGameAction) new DamageAllEnemiesAction((AbstractCreature) this.p, this.multiDamage, this.damageType, AbstractGameAction.AttackEffect.NONE, true));
                addToTop((AbstractGameAction) new VFXAction((AbstractCreature) this.p, (AbstractGameEffect) new CleaveEffect(), 0.0F));
                addToTop((AbstractGameAction) new SFXAction("ATTACK_HEAVY"));
                if (i == 0) {
                    addToTop((AbstractGameAction) new VFXAction((AbstractGameEffect) new WhirlwindEffect(), 0.0F));
                    addToTop((AbstractGameAction) new SFXAction("ATTACK_WHIRLWIND"));
                }
            }
            //logger.info("BGWhirlwindAction: subtract energy "+this.energyOnUse);
            if (!this.dontExpendResources) {
                this.p.energy.use(this.energyOnUse);
            }
        }else{
            addToTop((AbstractGameAction)new DamageAllEnemiesAction(this.p,0, WeakVulnCancel.WEAKVULN_ZEROHITS, AbstractGameAction.AttackEffect.NONE));
        }
        this.isDone = true;
    }
}


