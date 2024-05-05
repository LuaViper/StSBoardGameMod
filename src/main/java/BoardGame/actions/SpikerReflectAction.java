package BoardGame.actions;

import BoardGame.powers.BGSpikerProccedPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class SpikerReflectAction extends AbstractGameAction {
    private AbstractCreature spiker;
    private int dmgAmt;

    public SpikerReflectAction(AbstractCreature spiker,int dmgAmt) {
        this.spiker = spiker;
        this.dmgAmt = dmgAmt;
    }


    public void update() {
        if(!(spiker.halfDead || spiker.isDying || spiker.isDead)){
            //spiker not dead == use procced power system, same as weak/vulnerable
            AbstractDungeon.actionManager.addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.spiker, (AbstractCreature) this.spiker, (AbstractPower) new BGSpikerProccedPower((AbstractCreature) this.spiker, this.dmgAmt, false), this.dmgAmt));
        }else {
            //spiker dead == deal the damage immediately

            if (!this.spiker.hasPower("BGSpikerProccedPower")) {
                //(but make sure we only deal it once)
                AbstractDungeon.actionManager.addToTop((AbstractGameAction) new ApplyPowerAction((AbstractCreature) this.spiker, (AbstractCreature) this.spiker, (AbstractPower) new BGSpikerProccedPower((AbstractCreature) this.spiker, this.dmgAmt, false), this.dmgAmt));
                AbstractDungeon.actionManager.addToTop((AbstractGameAction) new DamageAction((AbstractCreature) AbstractDungeon.player, new DamageInfo(this.spiker, this.dmgAmt,
                        DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL, true));
                AbstractDungeon.actionManager.addToTop((AbstractGameAction) new RemoveSpecificPowerAction(this.spiker, this.spiker, "BGSpikerProcced"));
            }

        }
        this.isDone = true;
    }
}
