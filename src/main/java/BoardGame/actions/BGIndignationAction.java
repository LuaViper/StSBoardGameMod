package BoardGame.actions;

import BoardGame.powers.BGVulnerablePower;
import BoardGame.screen.TargetSelectScreen;
import BoardGame.thedie.TheDie;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.PotionBounceEffect;

public class BGIndignationAction extends AbstractGameAction {
    private boolean upgraded;
    private AbstractCreature m;
    public BGIndignationAction(boolean upgraded, AbstractCreature m) {
        this.upgraded=upgraded;
        this.m=m;
    }



    public void update() {
        if (AbstractDungeon.player.stance.ID.equals("BGWrath")) {
            if(this.upgraded) {
                for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters)
                    addToBot((AbstractGameAction) new ApplyPowerAction((AbstractCreature) mo, (AbstractCreature) AbstractDungeon.player, (AbstractPower) new BGVulnerablePower((AbstractCreature) mo, 1, false), 1, true, AbstractGameAction.AttackEffect.NONE));
            }else{
                if(m!=null){
                    addToBot((AbstractGameAction) new ApplyPowerAction((AbstractCreature) m, (AbstractCreature) AbstractDungeon.player, (AbstractPower) new BGVulnerablePower((AbstractCreature) m, 1, false), 1, true, AbstractGameAction.AttackEffect.NONE));
                }else{
                    TargetSelectScreen.TargetSelectAction tssAction = (target) -> {
                        if(target!=null) {
                            addToBot((AbstractGameAction) new ApplyPowerAction((AbstractCreature) target, (AbstractCreature) AbstractDungeon.player, (AbstractPower) new BGVulnerablePower((AbstractCreature) target, 1, false), 1, true, AbstractGameAction.AttackEffect.NONE));
                        }
                    };
                    //TODO: localization
                    addToTop((AbstractGameAction) new TargetSelectScreenAction(tssAction, "Choose a target for Indignation."));

                }
            }
        } else {
            addToBot(new ChangeStanceAction("BGWrath"));
        }
        this.isDone = true;
    }
}
