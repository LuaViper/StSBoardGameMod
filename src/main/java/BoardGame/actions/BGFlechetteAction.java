package BoardGame.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.ThrowDaggerEffect;

public class BGFlechetteAction extends AbstractGameAction {
    private DamageInfo info;
    private int magicNumber;

    public BGFlechetteAction(AbstractCreature target, DamageInfo info, int magicNumber) {
        this.duration = Settings.ACTION_DUR_XFAST;
        this.info = info;
        this.actionType = AbstractGameAction.ActionType.BLOCK;
        this.target = target;
        this.magicNumber=magicNumber;
    }

    public void update() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.type == AbstractCard.CardType.SKILL) {
                addToTop((AbstractGameAction)new DamageAction(this.target, this.info, true));
                if (this.target != null && this.target.hb != null)
                    addToTop((AbstractGameAction)new VFXAction((AbstractGameEffect)new ThrowDaggerEffect(this.target.hb.cX, this.target.hb.cY)));
            }
        }
        for(int i=0;i<magicNumber;i+=1){
            addToTop((AbstractGameAction)new DamageAction(this.target, this.info, true));
            if (this.target != null && this.target.hb != null)
                addToTop((AbstractGameAction)new VFXAction((AbstractGameEffect)new ThrowDaggerEffect(this.target.hb.cX, this.target.hb.cY)));
        }
        this.isDone = true;
    }
}


/* Location:              C:\Program Files (x86)\Steam\steamapps\common\SlayTheSpire\desktop-1.0.jar!\com\megacrit\cardcrawl\action\\unique\FlechetteAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */