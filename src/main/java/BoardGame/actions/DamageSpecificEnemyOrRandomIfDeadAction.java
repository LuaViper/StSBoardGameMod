package BoardGame.actions;


 import BoardGame.screen.TargetSelectScreen;
 import basemod.BaseMod;
 import com.megacrit.cardcrawl.actions.AbstractGameAction;
 import com.megacrit.cardcrawl.actions.common.DamageAction;
 import com.megacrit.cardcrawl.cards.DamageInfo;
 import com.megacrit.cardcrawl.core.AbstractCreature;
 import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DamageSpecificEnemyOrRandomIfDeadAction extends AbstractGameAction {

    private DamageInfo info;
    private AbstractCreature originaltarget;

    public DamageSpecificEnemyOrRandomIfDeadAction(AbstractCreature originaltarget, DamageInfo info, AbstractGameAction.AttackEffect effect) {
        this.info = info;
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
        this.attackEffect = effect;
        this.originaltarget = originaltarget;
    }


    public void update() {
        if (originaltarget == null || originaltarget.halfDead || originaltarget.isDead || originaltarget.isDying || originaltarget.isEscaping) {
            this.target = (AbstractCreature) AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
        } else {
            this.target = originaltarget;
        }
        if (this.target != null) {
            addToTop(new DamageAction(this.target, this.info, this.attackEffect));
        }



        this.isDone = true;
    }
}

